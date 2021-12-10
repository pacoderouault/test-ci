package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class PolyphenHvarPrediction extends VariantPrediction{
    public PolyphenHvarPrediction(String prediction, Number score) {
        super(PredictionTools.POLYPHEN2_HVAR, prediction, score);
    }
}
