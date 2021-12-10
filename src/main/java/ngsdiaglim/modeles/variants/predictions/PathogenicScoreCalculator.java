package ngsdiaglim.modeles.variants.predictions;

import ngsdiaglim.modeles.variants.Annotation;

public class PathogenicScoreCalculator {

    public static Double getPathogenicScore(Annotation a) {
        Double score = 0d;

        // consequence
        if (a.getTranscriptConsequence() != null) {

            if (a.getTranscriptConsequence().getConsequence() != null) {
                score += a.getTranscriptConsequence().getConsequence().getWeight();
            }

            // ClinVar
            if (a.getTranscriptConsequence().getClinvarSign() != null) {
                double clinVarScore = 0d;
                for (String s : a.getTranscriptConsequence().getClinvarSign().split("&")) {
                    s = s.trim();
                    if (s.equalsIgnoreCase("benign")) {
                        clinVarScore += 1;
                    } else if (s.equalsIgnoreCase("likely benign")) {
                        clinVarScore += 0.5;
                    } else if (s.equalsIgnoreCase("likely pathogenic")) {
                        clinVarScore -= 0.5;
                    } else if (s.equalsIgnoreCase("pathogenic")) {
                        clinVarScore -= 1;
                    }
                }
                score += clinVarScore;
            }

            // Polyphen hdiv
            double polyphenHdiv = 0d;
            if (a.getTranscriptConsequence().getPolyphen2HdivPred() != null) {
                double toolScore = a.getTranscriptConsequence().getPolyphen2HdivPred().getScore().doubleValue();
                if (toolScore >= 0.957) {
                    polyphenHdiv -= 1;
                } else if (toolScore >= 0.453) {
                    polyphenHdiv -= 0.5;
                } else {
                    polyphenHdiv += 1;
                }
                score += polyphenHdiv;
            }

            // Polyphen hvar
            double polyphenHvar = 0d;
            if (a.getTranscriptConsequence().getPolyphen2HvarPred() != null) {
                double toolScore = a.getTranscriptConsequence().getPolyphen2HvarPred().getScore().doubleValue();
                if (toolScore >= 0.957) {
                    polyphenHvar -= 1;
                } else if (toolScore >= 0.453) {
                    polyphenHvar -= 0.5;
                } else {
                    polyphenHvar += 1;
                }
                score += polyphenHvar;
            }

            // sift
            double sift = 0d;
            if (a.getTranscriptConsequence().getSiftPred() != null) {
                if (a.getTranscriptConsequence().getSiftPred().getScore().doubleValue() <= 0.5) {
                    sift -= 1;
                } else {
                    sift += 1;
                }
                score += sift;
            }

            // cadd
            double cadd = 0d;
            if (a.getTranscriptConsequence().getCaddRawPred() != null) {
                double toolScore = a.getTranscriptConsequence().getCaddRawPred().getScore().doubleValue();
                if (toolScore >= 10) {
                    cadd -= 1;
                } else if (toolScore >= 1.5) {
                    cadd -= 0.5;
                } else {
                    cadd += 1;
                }
                score += cadd;
            }

            // revel
            double revel = 0d;
            if (a.getTranscriptConsequence().getRevelPred() != null) {
                if (a.getTranscriptConsequence().getRevelPred().getScore().doubleValue() >= 0.5) {
                    revel -= 1;
                } else {
                    revel += 1;
                }
                score += revel;
            }

            // gerp
            double gerp = 0d;
            if (a.getTranscriptConsequence().getGerpPred() != null) {
                if (a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() > 0) {
                    gerp -= 1;
                } else {
                    gerp += 1;
                }
                score += gerp;
            }
        }

        // gnomad
        double gnomad = 0d;
        if (a.getGnomADFrequencies().maxProperty() == null || a.getGnomADFrequencies().maxProperty().get().getAf() <= 0.001) {
            gnomad -= 1;
        }
        else if (a.getGnomADFrequencies().maxProperty().get().getAf() <= 0.01) {
            gnomad -= 0.5;
        }
        else {
            gnomad += 1;
        }
        score += gnomad;

        return score;
    }
}
