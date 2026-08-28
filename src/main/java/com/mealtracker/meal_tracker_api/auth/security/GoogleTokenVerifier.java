package com.mealtracker.meal_tracker_api.auth.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.oauth.client-id}") String googleClientId) {
        // This is the ONLY correct way to validate a Google ID token server-
        // side: verify its cryptographic signature against Google's own
        // public keys (which this library fetches and caches automatically),
        // AND check the "audience" claim matches YOUR app's client ID -
        // without the audience check, a token issued for a completely
        // different app could be replayed against your backend.
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    /**
     * Verifies the token and returns its payload if valid.
     * Returns null if invalid/expired/wrong audience - callers decide how
     * to respond. Never trust an unverified token's claims.
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;
        } catch (GeneralSecurityException | java.io.IOException | IllegalArgumentException e) {
            // IllegalArgumentException covers malformed token strings -
            // treat identically to "invalid" rather than letting a raw
            // exception leak up and produce a confusing 500.
            return null;
        }
    }
}
