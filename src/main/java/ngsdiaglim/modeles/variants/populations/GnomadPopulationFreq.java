package ngsdiaglim.modeles.variants.populations;

import ngsdiaglim.enumerations.GnomadPopulation;
import ngsdiaglim.utils.NumberUtils;

public class GnomadPopulationFreq {

    private final GnomadPopulation populationName;
    private final float af;
    private final int ac;
    private final int an;

    public GnomadPopulationFreq(GnomadPopulation populationName, float af, int ac, int an) {
        this.populationName = populationName;
        this.af = af;
        this.ac = ac;
        this.an = an;
    }

    public GnomadPopulation getPopulationName() {return populationName;}

    public float getAf() {return af;}

    public int getAc() {return ac;}

    public int getAn() {return an;}

    @Override
    public String toString() {
        if (Float.isNaN(af)) {
            return "";
        }
        return NumberUtils.round(af, 3) + " (" + ac + "/" + an + ")";
    }

}
