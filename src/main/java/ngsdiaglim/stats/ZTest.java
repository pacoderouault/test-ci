package ngsdiaglim.stats;

import ngsdiaglim.cnv.caller.CNVDetectionRobustZScore;
import ngsdiaglim.enumerations.CNVTypes;

public class ZTest {

    public static CNVTypes getCNVType(Double zscore, Double alpha) {

        if (zscore <= CNVDetectionRobustZScore.delThreshold) {
            return CNVTypes.DELETION;
        }
        else if (zscore >= CNVDetectionRobustZScore.dupThreshold) {
            return CNVTypes.DUPLICATION;
        }
        else {
            return CNVTypes.NORMAL;
        }
    }

    public static Boolean zTest(Double zScore) {
        return Math.abs(zScore) >= CNVDetectionRobustZScore.dupThreshold;
    }

    public static Boolean zTest(Double zScore, Double alpha) {
        return Math.abs(zScore) >= alpha;
    }


}
