package ngsdiaglim.enumerations;

import ngsdiaglim.App;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum VariantsTableColumns {

    INDEX("#"),
    HOTSPOT(App.getBundle().getString("analysisview.variants.table.hotspot")),
    CONTIG(App.getBundle().getString("analysisview.variants.table.contigCol")),
    POSITION(App.getBundle().getString("analysisview.variants.table.posCol")),
    REF(App.getBundle().getString("analysisview.variants.table.refCol")),
    ALT(App.getBundle().getString("analysisview.variants.table.altCol")),
    ACMG(App.getBundle().getString("analysisview.variants.table.acmgCol")),
    OCCURENCE(App.getBundle().getString("analysisview.variants.table.occurenceCol"), App.getBundle().getString("analysisview.variants.table.occurenceCol.help")),
    OCCURENCE_IN_RUN(App.getBundle().getString("analysisview.variants.table.occurenceRunCol"), App.getBundle().getString("analysisview.variants.table.occurenceRunCol.help")),
    DEPTH(App.getBundle().getString("analysisview.variants.table.depth")),
    VAF(App.getBundle().getString("analysisview.variants.table.vaf")),
    ALLELE_DEPTH(App.getBundle().getString("analysisview.variants.table.alleleDepth")),
    ALLELE_STRAND_DEPTH(App.getBundle().getString("analysisview.variants.table.alleleStrandDepth"), App.getBundle().getString("analysisview.variants.table.alleleStrandDepth.help")),
    GENE(App.getBundle().getString("analysisview.variants.table.gene")),
    GENES(App.getBundle().getString("analysisview.variants.table.genes")),
    PROTEIN(App.getBundle().getString("analysisview.variants.table.protein")),
    TRANSCRIPT(App.getBundle().getString("analysisview.variants.table.transcript")),
    EXON(App.getBundle().getString("analysisview.variants.table.exon")),
    INTRON(App.getBundle().getString("analysisview.variants.table.intron")),
    HGVSC(App.getBundle().getString("analysisview.variants.table.hgvsc")),
    HGVSP(App.getBundle().getString("analysisview.variants.table.hgvsp")),
    CONSEQUENCE(App.getBundle().getString("analysisview.variants.table.consequence")),
    CLIVAR_SIGN(App.getBundle().getString("analysisview.variants.table.clinvarSign")),
    POLYPHEN2_HVAR(App.getBundle().getString("analysisview.variants.table.polyphen2Hvar"), App.getBundle().getString("analysisview.variants.table.polyphen2Hvar.help")),
    POLYPHEN2_HDIV(App.getBundle().getString("analysisview.variants.table.polyphen2Hdiv"), App.getBundle().getString("analysisview.variants.table.polyphen2Hdiv.help")),
    SIFT(App.getBundle().getString("analysisview.variants.table.sift"), App.getBundle().getString("analysisview.variants.table.sift.help")),
    CADD(App.getBundle().getString("analysisview.variants.table.cadd"), App.getBundle().getString("analysisview.variants.table.cadd.help")),
    GERP(App.getBundle().getString("analysisview.variants.table.gerp"), App.getBundle().getString("analysisview.variants.table.gerp.help")),
    REVEL(App.getBundle().getString("analysisview.variants.table.revel"), App.getBundle().getString("analysisview.variants.table.revel.help")),
    MVP(App.getBundle().getString("analysisview.variants.table.mvp"), App.getBundle().getString("analysisview.variants.table.mvp.help")),
    SPLICE_AI(App.getBundle().getString("analysisview.variants.table.spliceAI"), App.getBundle().getString("analysisview.variants.table.spliceAI.help")),
    GNOMAD_MAX(App.getBundle().getString("analysisview.variants.table.gnomadMax"), App.getBundle().getString("analysisview.variants.table.gnomadMax.help")),
    EXTERNAL_VARIATIONS(App.getBundle().getString("analysisview.variants.table.externalVariations"))
    ;

    private static final Map<String, VariantsTableColumns> NAME_ENUM_MAP;
    private final String name;
    private final String desc;

    VariantsTableColumns(String s, String d) {
        name = s;
        desc = d;
    }

    VariantsTableColumns(String s) {
        name = s;
        desc = null;
    }

    static {
        Map<String,VariantsTableColumns> map = new ConcurrentHashMap<>();
        for (VariantsTableColumns instance : VariantsTableColumns.values()) {
            map.put(instance.getName(), instance);
        }
        NAME_ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public String getName() { return name; }
    public String getDesc() { return desc; }

    public static VariantsTableColumns getFromName(String name) {
        if (NAME_ENUM_MAP.containsKey(name)) {
            return NAME_ENUM_MAP.get(name);
        }
        return null;
    }
}
