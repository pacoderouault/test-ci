package ngsdiaglim.stats;

import java.util.List;

public class Quartiles {

    private final Double q1;
    private final Double q2;
    private final Double q3;
    private final Double pl;
    private final Double pr;

    public Quartiles(Double q1, Double q2, Double q3) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        if (q1 != null) {
            pl = Math.max(0, q1 - (1.5 * (q3 - q1)));
            pr = q3 + (1.5 * (q3 - q1));
        }
        else {
            pl = null;
            pr = null;
        }
    }

    public Quartiles(Double q1, Double q2, Double q3, List<Integer> values) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        if (q1 != null) {
            pl = Math.max(values.get(0), q1 - (1.5 * (q3 - q1)));
            pr = Math.min(values.get(values.size() - 1), q3 + (1.5 * (q3 - q1)));
        }
        else {
            pl = null;
            pr = null;
        }
    }

    public Double getQ1() { return q1; }

    public Double getQ2() { return q2; }

    public Double getQ3() { return q3; }

    public Double getPl() { return pl; }

    public Double getPr() { return pr; }
}
