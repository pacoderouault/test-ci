package ngsdiaglim.modeles.variants.populations;

import ngsdiaglim.enumerations.GnomadPopulation;

public class GnomAD {

    private GnomADData gnomadExomesData2_1_1;
    private GnomADData gnomadGenomesData2_1_1;

    public GnomADData getGnomadExomesData2_1_1() {return gnomadExomesData2_1_1;}

    public void setGnomadExomesData2_1_1(GnomADData gnomadExomesData2_1_1) {
        this.gnomadExomesData2_1_1 = gnomadExomesData2_1_1;
    }

    public GnomADData getGnomadGenomesData2_1_1() {return gnomadGenomesData2_1_1;}

    public void setGnomadGenomesData2_1_1(GnomADData gnomadGenomesData2_1_1) {
        this.gnomadGenomesData2_1_1 = gnomadGenomesData2_1_1;
    }

    public enum GnomadSource {
        EXOME, GENOME;
    }


    public GnomadPopulationFreq getMaxFrequency(GnomadSource source) {
        if (source.equals(GnomadSource.EXOME) && gnomadExomesData2_1_1 != null) {
            return gnomadExomesData2_1_1.getPopulationFrequency(GnomadPopulation.MAX_POP);
        }
        if (source.equals(GnomadSource.GENOME) && gnomadGenomesData2_1_1 != null) {
            return gnomadGenomesData2_1_1.getPopulationFrequency(GnomadPopulation.MAX_POP);
        }
        return null;
    }
}
