package ngsdiaglim.modeles.analyse;

import ngsdiaglim.utils.ExternalDatabasesUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalVariation {

    private final ExternalVariationDb db;
    private final String id;
    private final static Pattern dbsnpPattern = Pattern.compile("rs\\d+");
    private final static Pattern cosmicPattern = Pattern.compile("COSM|COSV\\d+");

    public ExternalVariation(ExternalVariationDb db, String id) {
        this.db = db;
        this.id = id;
    }

    public ExternalVariationDb getDb() {return db;}

    public String getId() {return id;}

    public enum ExternalVariationDb {
        DBSNP, COSMIC
    }

    public static ExternalVariation parseString(String variationId) {
        Matcher dbsnpMatcher = dbsnpPattern.matcher(variationId);
        if (dbsnpMatcher.matches()) {
            return new ExternalVariation(ExternalVariationDb.DBSNP, variationId);
        }

        Matcher cosmicMatcher = cosmicPattern.matcher(variationId);
        if (cosmicMatcher.matches()) {
            return new ExternalVariation(ExternalVariationDb.COSMIC, variationId);
        }

        return null;
    }

    public String getURL() {
        if (db.equals(ExternalVariationDb.DBSNP)) {
            return ExternalDatabasesUtils.getdbSnpLink(id);
        } else if (db.equals(ExternalVariationDb.COSMIC)) {
            return ExternalDatabasesUtils.getCosmicLink(id);
        }
        return null;
    }
}
