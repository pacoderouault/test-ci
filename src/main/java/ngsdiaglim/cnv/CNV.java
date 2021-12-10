package ngsdiaglim.cnv;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.CNVTypes;

import java.util.List;

public class CNV {

    private CNVTypes cnvTypes;
    private String contig;
    private int start;
    private int end;
    private ObservableList<CovCopRegion> ampliconsList = FXCollections.observableArrayList();
    private int firstAmpliconIndex;
    private int lastAmpliconIndex;

    public CNV(CNVTypes cnvTypes, String contig, int start, int end, List<CovCopRegion> ampliconsList, int lastAmpliconIndex) {
        this.cnvTypes = cnvTypes;
        this.contig = contig;
        this.start = start;
        this.end = end;
        this.ampliconsList.setAll(ampliconsList);
        this.firstAmpliconIndex = lastAmpliconIndex - ampliconsList.size();
        this.lastAmpliconIndex = lastAmpliconIndex;
    }

    public CNVTypes getCnvTypes() { return cnvTypes; }

    public void setCnvTypes(CNVTypes cnvTypes) {
        this.cnvTypes = cnvTypes;
    }

    public String getContig() { return contig; }

    public void setContig(String contig) {
        this.contig = contig;
    }

    public int getStart() { return start; }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() { return end; }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getSize() {
        return end - start + 1;
    }

    public ObservableList<CovCopRegion> getAmpliconsList() { return ampliconsList; }

    public void setAmpliconsList(ObservableList<CovCopRegion> ampliconsList) {
        this.ampliconsList = ampliconsList;
    }

    public int getFirstAmpliconIndex() { return firstAmpliconIndex; }

    public void setFirstAmpliconIndex(int firstAmpliconIndex) {
        this.firstAmpliconIndex = firstAmpliconIndex;
    }

    public int getLastAmpliconIndex() { return lastAmpliconIndex; }

    public void setLastAmpliconIndex(int lastApmliconIndex) {
        this.lastAmpliconIndex = lastApmliconIndex;
    }
}
