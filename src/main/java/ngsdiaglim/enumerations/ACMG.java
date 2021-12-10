package ngsdiaglim.enumerations;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ACMG pathogenicity prediction
 */
public enum ACMG {
    PATHOGENIC("Pathogenic", 5),
    LIKELY_PATHOGENIC("Likely pathogenic", 4),
    UNCERTAIN_SIGNIGICANCE("Uncertain significance", 3),
    LIKELY_BENIN("Likely benign", 2),
    BENIN("Benign", 1);

    private final String name;
    private final Integer pathoValue;

    private static final Map<String, ACMG> NAME_ENUM_MAP;
    private static final Map<Integer, ACMG> PATHO_ENUM_MAP;

    private ACMG(String name, int pathoValue) {
        this.name = name;
        this.pathoValue = pathoValue;
    }

    static {
        Map<String,ACMG> map = new ConcurrentHashMap<>();
        for (ACMG instance : ACMG.values()) {
            map.put(instance.getName(),instance);
        }
        NAME_ENUM_MAP = Collections.unmodifiableMap(map);

        Map<Integer,ACMG> mapP = new ConcurrentHashMap<>();
        for (ACMG instance : ACMG.values()) {
            mapP.put(instance.getPathogenicityValue(),instance);
        }
        PATHO_ENUM_MAP = Collections.unmodifiableMap(mapP);
    }

    public String getName() {
        return name;
    }

    public Integer getPathogenicityValue() {
        return pathoValue;
    }

    public static ACMG getFromName(String name) {
        return NAME_ENUM_MAP.get(name);
    }

    public static ACMG getFromPathogenicityValue(Integer value) {
        return PATHO_ENUM_MAP.get(value);
    }

    public String toString() {
        return this.name;
    }
}
