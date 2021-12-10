package ngsdiaglim.modeles.biofeatures;

import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.utils.MathUtils;
import ngsdiaglim.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CoverageRegion extends Region {

    private Double meanDepth;
    private final List<Integer> depthValues = new ArrayList<>();
    private final CoverageQuality coverageQuality;

    public CoverageRegion(String contig, int start, int end, String name, CoverageQuality coverageQuality) {
        super(contig, start, end, name);
        this.coverageQuality = coverageQuality;
    }

    public CoverageQuality getCoverageQuality() {return coverageQuality;}

    public List<Integer> getDepthValues() {return depthValues;}

    public void addDepthValue(int d) {
        depthValues.add(d);
    }

//    public Double getMeanDepth() {return meanDepth;}

    public void setMeanDepth(Double meanDepth) {
        this.meanDepth = meanDepth;
    }

    public void extendsRegion(Integer depth) {
        setEnd(getEnd() + 1);
        addDepthValue(depth);
    }

    public double getAverageDepth() {
        if (meanDepth == null) meanDepth = MathUtils.meanOfInt(depthValues);
        return meanDepth;
    }

    public boolean isTouching(String contig, int pos) {
        return getContig().equalsIgnoreCase(contig) && (pos - getEnd()) == 1;
    }

    public String toIgvBed() {
        StringJoiner sj = new StringJoiner("\t");
        double mean = getAverageDepth();
        String color = null;
        String name = null;
        if (coverageQuality.equals(CoverageQuality.NO_COVERED)) {
            color = "255,0,0";
            name = "No Cov.";
        } else if (coverageQuality.equals(CoverageQuality.LOW_COVERAGE)) {
            color = "255,128,0";
            name = "Low Cov.";
        }
        name += " (" + getSize() + "pb; " + Math.round(mean) + "X)";
        sj.add(getContig())
                .add(String.valueOf(getStart())) // 0 based for igv
                .add(String.valueOf(getEnd()))
                .add(name)
                .add(String.valueOf(NumberUtils.round(mean, 2)))
                .add(".")
                .add(String.valueOf(getStart()))
                .add(String.valueOf(getEnd()))
                .add(color);

        return sj.toString();
    }

    @Override
    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof CoverageRegion)) return false;
//        if (!super.equals(o)) return false;
//
//        CoverageRegion that = (CoverageRegion) o;
        return super.equals(o);
//        return meanDepth != null ? meanDepth.equals(that.meanDepth) : that.meanDepth == null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return getContig() + " " + getStart() + " " + getEnd();
    }
}
