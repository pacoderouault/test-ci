package ngsdiaglim.cnv;

import ngsdiaglim.stats.Quartiles;

import java.util.List;

public class BoxplotData {

    private String name;
    private Quartiles quartiles;
    private Double mean;
    private long lowAmpliconsNb;
    private List<Integer> values;

    public BoxplotData(String name, Quartiles quartiles, Double mean, long lowAmpliconsNb, List<Integer> values) {
        this.name = name;
        this.quartiles = quartiles;
        this.mean = mean;
        this.lowAmpliconsNb = lowAmpliconsNb;
        this.values = values;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public Quartiles getQuartiles() { return quartiles; }

    public void setQuartiles(Quartiles quartiles) {
        this.quartiles = quartiles;
    }

    public Double getMean() { return mean; }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public long getLowAmpliconsNb() { return lowAmpliconsNb; }

    public void setLowAmpliconsNb(long lowAmpliconsNb) {
        this.lowAmpliconsNb = lowAmpliconsNb;
    }

    public List<Integer> getValues() { return values; }

    public void setValues(List<Integer> values) {
        this.values = values;
    }
}
