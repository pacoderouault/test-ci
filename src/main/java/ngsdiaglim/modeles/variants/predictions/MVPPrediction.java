package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class MVPPrediction extends VariantPrediction{
    public MVPPrediction(String prediction, Number score) {
        super(PredictionTools.MVP, prediction, score);
    }
}
