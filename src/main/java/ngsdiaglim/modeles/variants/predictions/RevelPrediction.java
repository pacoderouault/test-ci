package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class RevelPrediction extends VariantPrediction{
    public RevelPrediction(String prediction, Number score) {
        super(PredictionTools.REVEL, prediction, score);
    }
}
