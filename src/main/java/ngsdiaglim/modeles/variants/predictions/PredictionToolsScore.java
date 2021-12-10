package ngsdiaglim.modeles.variants.predictions;

import javafx.scene.paint.Color;

public class PredictionToolsScore {

    private static final Color red = Color.RED;
    private static final Color orange = Color.ORANGE;
    private static final Color green = Color.GREEN;
    private static final Color blue = Color.DEEPSKYBLUE;

    public static float scalePolyphenScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        return score;
    }

    public static float scaleSiftScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        return 1 - score;
    }

    public static float scaleCaddPhredScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        score -= 1;
        score = Math.min(score, 30);
        return score / 30;
    }

    public static float scaleRevelScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        return score;
    }

    public static float scaleGerpScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        float from = 12.3f;
        float to = 6.17f + from;
        return (score + from) / to;
    }

    public static float scaleSpliceAIScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        return score;
    }

    public static float scaleMVPScore(Float score) {
        if (score == null || score.isNaN()) return -1;
        return score;
    }

    public static float scalePopulationFrequencie(Float freq) {

        float maxFreqPatho = 0.005f;

        if (freq == null || freq.isNaN()) return -1;
        freq = Math.min(freq, maxFreqPatho);
        return 1 - freq / maxFreqPatho;
    }

    public static Color getPolyphenScoreColor(Float score) {
        if (score >= 0.957f) return red;
        else if (score >= 0.453f) return orange;
        else return blue;
    }

    public static Color getSiftScoreColor(Float score) {
        if (score <= 0.05f) return red;
        else return blue;
    }

    public static Color getCaddPhredScoreColor(Float score) {
        if (score >= 15f) return red;
        if (score >= 10f) return orange;
        else return blue;
    }

    public static Color getRevelScoreColor(Float score) {
        if (score >= 0.5f) return red;
        else return blue;
    }

    public static Color getGnomadFreqColor(Float freq) {
        if (freq < 0.01f) return red;
        else return blue;
    }

    public static Color getGerpColor(Float score) {
        if (score < 0) return blue;
        else return orange;
    }

    public static Color getSpliceAIColor(Float score) {
        if (score < 0.5) return blue;
        else if (score < 0.8) return orange;
        else return red;
    }

    public static Color getMVPColor(Float score) {
        if (score < 0.7) return blue;
        else return orange;
    }

}
