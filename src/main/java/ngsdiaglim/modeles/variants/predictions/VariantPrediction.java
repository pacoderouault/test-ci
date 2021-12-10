package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.enumerations.PredictionTools;

public class VariantPrediction {

    private final PredictionTools tool;
    private final String prediction;
    private final Number score;

    public VariantPrediction(PredictionTools tool, String prediction, Number score) {
        this.tool = tool;
        this.prediction = prediction;
        this.score = score;
    }

    public PredictionTools getTool() {return tool;}

    public String getPrediction() {return prediction;}

    public Number getScore() {return score;}

    @Override
    public String toString() {
        if (prediction != null && score != null) {
            return prediction + "(" + score + ")";
        } else if (prediction == null && score != null) {
            return String.valueOf(score);
        }
        else return prediction;
    }
}