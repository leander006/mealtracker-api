package com.mealtracker.meal_tracker_api.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.AuthResponse;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.GoogleAuthRequest;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.LoginRequest;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.SignupRequest;
import com.mealtracker.meal_tracker_api.auth.entity.User;
import com.mealtracker.meal_tracker_api.auth.entity.User.AuthProvider;
import com.mealtracker.meal_tracker_api.auth.repository.UserRepository;
import com.mealtracker.meal_tracker_api.auth.security.GoogleTokenVerifier;
import com.mealtracker.meal_tracker_api.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.info("Signup rejected - email already registered");
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .authProvider(AuthProvider.LOCAL)
                .name(request.name())
                .build();

        User saved = userRepository.save(user);
        log.info("New local user created, id={}", saved.getId());

        return issueTokenResponse(saved);
    }

    public AuthResponse login(LoginRequest request) throws InvalidCredentialsException {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getAuthProvider() != AuthProvider.LOCAL || user.getPasswordHash() == null) {
            // This account was created via Google - there's no password to
            // check. Same generic error message as a wrong password, so we
            // don't leak which accounts exist or how they were created.
            log.info("Local login attempted on a Google-only account, userId={}", user.getId());
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.info("Login failed - bad password, userId={}", user.getId());
            throw new InvalidCredentialsException();
        }

        log.info("Login succeeded, userId={}", user.getId());
        return issueTokenResponse(user);
    }

    @Transactional
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.idToken());
        if (payload == null) {
            throw new InvalidGoogleTokenException();
        }

        String googleId = payload.getSubject();          // Google's stable user ID
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // Match on googleId first (repeat login), fall back to email
        // (first-time Google login on an account that already exists,
        // e.g. they originally signed up with a password) - in that case
        // we LINK the Google identity to the existing account rather than
        // creating a duplicate. This is a deliberate product decision:
        // "same email = same person" is reasonable here, though worth
        // knowing it assumes email ownership is trustworthy, which Google
        // has already verified for us at this point.
        User user = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(email))
                .map(existing -> linkGoogleIdIfNeeded(existing, googleId))
                .orElseGet(() -> createGoogleUser(googleId, email, name));

        log.info("Google auth succeeded, userId={}", user.getId());
        return issueTokenResponse(user);
    }

    private User linkGoogleIdIfNeeded(User existing, String googleId) {
        if (existing.getGoogleId() == null) {
            existing.setGoogleId(googleId);
            return userRepository.save(existing);
        }
        return existing;
    }

    private User createGoogleUser(String googleId, String email, String name) {
        User user = User.builder()
                .email(email)
                .googleId(googleId)
                .authProvider(AuthProvider.GOOGLE)
                .name(name != null ? name : email)
                .build();
        User saved = userRepository.save(user);
        log.info("New Google user created, id={}", saved.getId());
        return saved;
    }

    private AuthResponse issueTokenResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(user.getId(), user.getName(), token);
    }
}
