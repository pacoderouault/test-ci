package ngsdiaglim.enumerations;

public enum CNVChartHeight {
    SMALL(230), MEDIUM(320), LARGE(450);

    private final double height;

    CNVChartHeight(double height) {
        this.height = height;
    }

    public double getHeight() {return height;}
}
