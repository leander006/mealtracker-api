package com.mealtracker.meal_tracker_api.inference.service;

import com.mealtracker.meal_tracker_api.inference.client.GptPortionClient;
import com.mealtracker.meal_tracker_api.inference.client.MlServiceClient;
import com.mealtracker.meal_tracker_api.inference.client.UsdaMacroClient;
import com.mealtracker.meal_tracker_api.inference.dto.InferenceDtos.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InferenceService {

    private static final Logger log = LoggerFactory.getLogger(InferenceService.class);

    private final MlServiceClient mlServiceClient;
    private final GptPortionClient gptPortionClient;
    private final UsdaMacroClient usdaMacroClient;

    /**
     * The full scan pipeline, in order:
     *   1. classifier identifies the food (cheap, your own trained model)
     *   2. Claude estimates portion size for that KNOWN food (small, cheap prompt)
     *   3. USDA gives ground-truth macros scaled to that portion (deterministic, free)
     *
     * Each step narrows the problem before handing it to the next - this
     * is deliberate, not arbitrary ordering.
     */
    public ScanResponse scan(byte[] imageBytes, String filename, String imageUrl) {
        ClassificationResult classification = mlServiceClient.classify(imageBytes, filename);
        log.info("Classified as {} (confidence={})", classification.foodLabel(), classification.confidence());

        PortionEstimate portion = gptPortionClient.estimatePortion(imageBytes, classification.foodLabel());        log.info("Estimated portion: {}g", portion.estimatedPortionGrams());

        MacroLookupResult macros = usdaMacroClient.lookupMacros(
                classification.foodLabel(), portion.estimatedPortionGrams());

        return new ScanResponse(
                classification.foodLabel(),
                classification.confidence(),
                portion.estimatedPortionGrams(),
                macros,
                imageUrl
        );
    }
}
