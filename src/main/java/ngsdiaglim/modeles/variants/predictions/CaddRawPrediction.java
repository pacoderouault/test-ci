package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class CaddRawPrediction extends VariantPrediction{
    public CaddRawPrediction(String prediction, Number score) {
        super(PredictionTools.CADD_RAW, prediction, score);
    }
}
