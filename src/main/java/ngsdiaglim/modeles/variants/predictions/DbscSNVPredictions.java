package ngsdiaglim.modeles.variants.predictions;

public class DbscSNVPredictions {

    private final Float adaScore;
    private final Float rfScore;

    public DbscSNVPredictions(Float adaScore, Float rfScore) {
        this.adaScore = adaScore;
        this.rfScore = rfScore;
    }

    public Float getAdaScore() {return adaScore;}

    public Float getRfScore() {return rfScore;}
}
