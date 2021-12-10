package ngsdiaglim.controllers.charts;

public class PredictionGaugeLabel {

    private final float value;
    private final String text;

    public PredictionGaugeLabel(float value, String text) {
        this.value = value;
        this.text = text;
    }

    public float getValue() {return value;}

    public String getText() {return text;}
}
