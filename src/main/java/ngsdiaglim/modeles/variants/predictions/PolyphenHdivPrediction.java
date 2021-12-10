package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class PolyphenHdivPrediction extends VariantPrediction{
    public PolyphenHdivPrediction(String prediction, Number score) {
        super(PredictionTools.POLYPHEN2_HDIV, prediction, score);
    }
}
