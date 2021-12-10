package ngsdiaglim.cnv.caller;

import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;

import java.util.List;

public abstract class CNVDetection {

    protected CovCopCNVData cnvData;
    protected List<CovCopRegion> amplicons;

    protected void clearOldCVNs() {
        cnvData.getSamples().values().forEach(s -> s.getCNV().clear());
    }

    abstract public void callCNVs();
    abstract protected void getCNVs();
}
