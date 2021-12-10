package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class SiftPrediction extends VariantPrediction {

    public SiftPrediction(String prediction, Number score) {
        super(PredictionTools.SIFT, prediction, score);
    }
}
