package ngsdiaglim.modeles.ciq;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;


public class CIQVariantDataSet {

    private final CIQHotspot ciqHotspot;
    private final ObservableList<CIQVariantRecord> ciqRecords = FXCollections.observableArrayList();
    private double min, max, mean, sd, cv;

    public CIQVariantDataSet(CIQHotspot ciqHotspot, List<CIQVariantRecord> records) {
        this.ciqHotspot = ciqHotspot;
        this.ciqRecords.setAll(records);
    }

    public CIQHotspot getCiqHotspot() {return ciqHotspot;}

    public ObservableList<CIQVariantRecord> getCiqRecords() {return ciqRecords;}

    public double getMin() {
        if (Double.isNaN(min)) {
            return ciqHotspot.getVafTarget();
        }
        return min;
    }

    public double getMax() {
        if (Double.isNaN(max)) {
            return ciqHotspot.getVafTarget();
        }
        return max;
    }

    public double getMean() {
        if (Double.isNaN(mean)) {
            return ciqHotspot.getVafTarget();
        }
        return mean;
    }

    public double getSd() {
        if (Double.isNaN(sd)) {
            return 0;
        }
        return sd;
    }

    public double getCv() {return cv;}

    public double getHighMaxValue() {
        if (Double.isNaN(mean) || Double.isNaN(sd)) {
            return ciqHotspot.getVafTarget();
        }
        return mean + 3 * sd;
    }

    public double getHighMinValue() {
        if (Double.isNaN(mean) || Double.isNaN(sd)) {
            return ciqHotspot.getVafTarget();
        }
        return mean - 3 * sd;
    }

    public double getLowMaxValue() {
        if (Double.isNaN(mean) || Double.isNaN(sd)) {
            return ciqHotspot.getVafTarget();
        }
        return mean + 2 * sd;
    }

    public double getLowMinValue() {
        if (Double.isNaN(mean) || Double.isNaN(sd)) {
            return ciqHotspot.getVafTarget();
        }
        return mean - 2 * sd;
    }

    public void computeStats() {
        DescriptiveStatistics stats = getVafStats();
        this.min = stats.getMin();
        this.max = stats.getMax();
        this.mean = stats.getMean();
        this.sd = stats.getStandardDeviation();
        this.cv = sd / mean;
    }

    private DescriptiveStatistics getVafStats() {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        ciqRecords.stream().filter(CIQVariantRecord::isAccepted).forEach(r -> stats.addValue(r.getVaf()));
        return stats;
    }

    public boolean isInsideSQ(float vaf) {
        return vaf >= mean - sd && vaf <= mean + sd;
    }

    public boolean isInside2SD(float vaf) {
        return vaf >= mean - 2 * sd && vaf <= mean + 2 * sd;
    }

    public boolean isInside3SD(float vaf) {
        return vaf >= mean - 3 * sd && vaf <= mean + 3 * sd;
    }

    public boolean isOutside2SD(float vaf) {
        return vaf < mean - 2 * sd || vaf > mean + 2 * sd;
    }
}
