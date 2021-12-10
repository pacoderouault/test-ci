package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class GerpPrediction extends VariantPrediction {

    public GerpPrediction(String prediction, Number score) {
        super(PredictionTools.GERP, prediction, score);
    }
}
