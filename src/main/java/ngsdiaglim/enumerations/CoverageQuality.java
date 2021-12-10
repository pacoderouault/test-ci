package ngsdiaglim.enumerations;

public enum CoverageQuality {
    LOW_COVERAGE,
    NO_COVERED;

    CoverageQuality() {
    }

    public String getName() {
        return name().replace("_", " ");
    }
}
