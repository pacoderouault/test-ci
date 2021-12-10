package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class CaddPhredPrediction extends VariantPrediction{
    public CaddPhredPrediction(String prediction, Number score) {
        super(PredictionTools.CADD_PHRED, prediction, score);
    }
}
