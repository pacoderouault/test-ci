package ngsdiaglim.modeles.variants.predictions;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SpliceAIPredictions {

    private SpliceAIPrediction donnorGainPred;
    private SpliceAIPrediction donnorLossPred;
    private SpliceAIPrediction acceptorGainPred;
    private SpliceAIPrediction acceptorLossPred;

    public SpliceAIPrediction getDonnorGainPred() {return donnorGainPred;}

    public void setDonnorGainPred(SpliceAIPrediction donnorGainPred) {
        this.donnorGainPred = donnorGainPred;
    }

    public SpliceAIPrediction getDonnorLossPred() {return donnorLossPred;}

    public void setDonnorLossPred(SpliceAIPrediction donnorLossPred) {
        this.donnorLossPred = donnorLossPred;
    }

    public SpliceAIPrediction getAcceptorGainPred() {return acceptorGainPred;}

    public void setAcceptorGainPred(SpliceAIPrediction acceptorGainPred) {
        this.acceptorGainPred = acceptorGainPred;
    }

    public SpliceAIPrediction getAcceptorLossPred() {return acceptorLossPred;}

    public void setAcceptorLossPred(SpliceAIPrediction acceptorLossPred) {
        this.acceptorLossPred = acceptorLossPred;
    }

    public SpliceAIPrediction getMostSeverePred() {
        SpliceAIPrediction mostSeverePred = null;
        float maxPvalue = 0f;
        if (acceptorGainPred != null) {
            mostSeverePred = acceptorGainPred;
        }
        if (acceptorLossPred != null && (mostSeverePred == null || acceptorLossPred.getScore() > maxPvalue)) {
            mostSeverePred = acceptorLossPred;
        }
        if (donnorGainPred != null && (mostSeverePred == null || donnorGainPred.getScore() > maxPvalue)) {
            mostSeverePred = donnorGainPred;
        }
        if (donnorLossPred != null && (mostSeverePred == null || donnorLossPred.getScore() > maxPvalue)) {
            mostSeverePred = donnorLossPred;
        }
        return mostSeverePred;
    }

    public static class SpliceAIPrediction {
        private final SpliceAISite site;
        private final int position;
        private final double score;
        private static final DecimalFormat df = new DecimalFormat("#.####");

        public SpliceAIPrediction(SpliceAISite site, int position, double score) {
            this.site = site;
            this.position = position;
            this.score = score;
            df.setRoundingMode(RoundingMode.CEILING);
        }

        public int getPosition() {return position;}

        public double getScore() {return score;}

        public String getPrintableScore() {
            return df.format(score);
        }

        public SpliceAISite getSite() {return site;}

        public String toString() {
            return site.getSite() + " = " + position + ":" + getPrintableScore();
        }
    }

    public enum SpliceAISite {
        DONNOR_GAIN("Donnor Gain"),
        DONNOR_LOSS("Donnor Loss"),
        ACCPETOR_GAIN("Acceptor Gain"),
        ACCEPTOR_LOSS("Acceptor Loss");

        private final String site;

        SpliceAISite(String s) {
            this.site = s;
        }

        public String getSite() {return site;}
    }
}
