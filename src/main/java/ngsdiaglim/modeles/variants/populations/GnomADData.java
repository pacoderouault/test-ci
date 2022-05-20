package ngsdiaglim.modeles.variants.populations;

import ngsdiaglim.enumerations.GnomadPopulation;

import java.util.HashMap;

public class GnomADData {

    private final GnomAD.GnomadSource source;
    private final String version;
    private final HashMap<GnomadPopulation, GnomadPopulationFreq> populationsFrequencies = new HashMap<>();

    public GnomADData(GnomAD.GnomadSource source, String version) {
        this.source = source;
        this.version = version;
    }

    public GnomAD.GnomadSource getSource() {return source;}

    public String getVersion() {return version;}

    public HashMap<GnomadPopulation, GnomadPopulationFreq> getPopulationsFrequencies() {return populationsFrequencies;}

    public void addPopulationsFrequency(GnomadPopulation population, float af, int ac, int an) {
        populationsFrequencies.put(population, new GnomadPopulationFreq(population, af, ac, an));
    }

    public GnomadPopulationFreq getPopulationFrequency(GnomadPopulation pop) {
        return populationsFrequencies.getOrDefault(pop, null);
    }

}
