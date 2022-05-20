package ngsdiaglim.utils;

import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.analyse.ExternalVariation;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalDatabasesUtils {

    private static final String dbSnpURL = "https://www.ncbi.nlm.nih.gov/snp/";
    private static final String gnomadURL = "http://gnomad.broadinstitute.org/variant/";
    private static final String alamutURL = "http://localhost:10000/show?request=";
    private static final String hgmdURL = "http://www.hgmd.cf.ac.uk/ac/gene.php?gene=";
    private static final String ensemblURL37 = "http://grch37.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=";
    private static final String ensemblURL = "http://ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=";
    private static final String ensemblVariationURL37 = "http://grch37.ensembl.org/Homo_sapiens/Variation/Explore?db=core;r=";
    private static final String ensemblVariationURL = "http://ensembl.org/Homo_sapiens/Variation/Explore?db=core;r=";
    private static final String omimURL = "https://www.omim.org/search/?index=entry&sort=score+desc%2C+prefix_sort+desc&start=1&limit=10&search=";
    private static final String igvURL = "http://localhost:60151/load?genome=hg19&file=";
    private static final String googleURL = "https://www.google.com/search?q=";
    //    private static final String cosmicURL = "https://cancer.sanger.ac.uk/cosmic/mutation/overview?id=";
    private static final String cosmicSearchURL = "https://cancer.sanger.ac.uk/cosmic/search?q=";
    private static final String cosmicGenViewURL = "https://cancer.sanger.ac.uk/cosmic/gene/analysis?coords=AA%%3AAA&wgs=off&id=354503&ln=%1$s&start=%2$s&end=%3$s";
    private static final String litvarURL = "https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/LitVar/#!?query=";
    private static final String nuccore = "https://www.ncbi.nlm.nih.gov/nuccore/";
    private static final String varsomeURL = "https://varsome.com/variant";
    private static final String clinvarURL = "https://www.ncbi.nlm.nih.gov/clinvar/variation/";
    private static final String clinvarTermURL = "https://www.ncbi.nlm.nih.gov/clinvar?term=";
    private static final String lovdURL = "https://databases.lovd.nl/shared/variants/in_gene?search_geneid=\"%1$s\"&search_VariantOnTranscript/DNA=\"%2$s\"";
    private static final String oncoKBURL = "https://www.oncokb.org/gene/";
    private static final String pubmed = "https://pubmed.ncbi.nlm.nih.gov/";
    private static final String intogen = "https://www.intogen.org/search?gene=";
    private static final String thegencc = "https://search.thegencc.org/genes/HGNC:";
    private static final String franklin = "https://franklin.genoox.com/clinical-db/variant/snp/";

    /**
     *
     * @return An outlink to the dbsnp database from the Annotation
     */
    public static String getdbSnpLink(String id) {
        if (id == null) return null;
        return dbSnpURL + id;
    }

    /**
     *
     * @return An outlink to the dbsnp database from the Annotation
     */
    public static String getdbSnpLink(Annotation a) {
        if (a == null || a.getTranscriptConsequence() == null) return null;

        // try to find rsid
        if (a.getTranscriptConsequence().getExternalVariations() != null) {
            for (ExternalVariation av : a.getTranscriptConsequence().getExternalVariations(ExternalVariation.ExternalVariationDb.DBSNP)) {
                return av.getURL();
            }
        }

        // construct from hgvsc
        if (a.getTranscriptConsequence().getHgvsc() != null) {
            return dbSnpURL + "term=" + a.getTranscriptConsequence().getHgvsc();
        }

        // construct from position
        String positionTag = "";
        if (a.getGenome().equals(Genome.GRCh38)) {
            positionTag = "[POSITION_GRCH38]";
        } else {
            positionTag = "[POSITION_GRCH37]";
        }
        return dbSnpURL + "term=" + a.getGenomicVariant().getStart() + positionTag + "+AND+" + a.getGenomicVariant().getContigWithoutChr() + "[CHR]";
    }


    /**
     *
     * @return An outlink to GnomAD from the Annotation
     */
    public static String getGnomADLink(Annotation ann) {
        return gnomadURL + ann.getGenomicVariant().getContig() + "-" + ann.getGenomicVariant().getStart() + "-" + ann.getGenomicVariant().getRef() + "-" + ann.getGenomicVariant().getAlt();
    }


    public static String getClinVarLink(Annotation ann) {
        if (ann == null || ann.getTranscriptConsequence() == null) {
            return null;
        }

        if (ann.getTranscriptConsequence().getClinvarId() != null) {
            return clinvarURL + ann.getTranscriptConsequence().getClinvarId() + "/";
        }

        if (ann.getTranscriptConsequence().getHgvsc() != null) {
            return clinvarTermURL + ann.getTranscriptConsequence().getHgvsc();
        }

        String r = clinvarTermURL + ann.getGenomicVariant().getContigWithoutChr() + "[chr]+AND+" + ann.getGenomicVariant().getStart();
        if (ann.getGenome().equals(Genome.GRCh38)) {
            r += "[chrpos38]";
        } else {
            r += "[chrpos37]";
        }
        return r;
    }


    public static String getAlamutLink(String alamutQuery) {
        return alamutURL + alamutQuery;
    }

    /**
     * /example query : hg19:3:37025319T>G
     */
    public static String getAlamutGenomicQuery(Annotation ann) {
        String r = "";
        if (ann.getGenome().equals(Genome.GRCh38)) {
            r += "hg38:";
        } else {
            r += "hg19:";
        }
        return r + ann.getGenomicVariant().getContig() + ":" + ann.getGenomicVariant().getStart() + ann.getGenomicVariant().getRef() + ">" + ann.getGenomicVariant().getAlt();
    }

    /**
     * /example query : PMP22:c.464T>G
     */
    public static String getAlamutGeneQuery(Annotation ann) {
        if (ann == null || ann.getTranscriptConsequence() == null) return null;
        String r = "";
        if (ann.getGenome().equals(Genome.GRCh38)) {
            r += "hg38:";
        } else {
            r += "hg19:";
        }
        return r + ann.getGene().getGeneName()+ ":" + ann.getTranscriptConsequence().getHgvsc();
    }

    /**
     * /example query : NM_000249.3:c.464T>G
     */
    public static String getAlamutTranscriptQuery(Annotation ann) {
        if (ann == null || ann.getTranscriptConsequence() == null) return null;
        String r = "";
        if (ann.getGenome().equals(Genome.GRCh38)) {
            r += "hg38:";
        } else {
            r += "hg19:";
        }
        return r + ann.getTranscriptConsequence().getHgvsc();
    }

    public static String getAlamutQuery(Annotation ann) {
        if (ann == null || ann.getTranscriptConsequence() == null) return null;
        String r = "";
        if (ann.getGenome().equals(Genome.GRCh38)) {
            r += "hg38:";
        } else {
            r += "hg19:";
        }
        if (ann.getTranscriptConsequence().getHgvsc() != null) {
            return r +ann.getTranscriptConsequence().getHgvsc();
        } else {
            return r +ann.getGenomicVariant().getContig() + ":" + ann.getGenomicVariant().getStart() + ann.getGenomicVariant().getRef() + ">" + ann.getGenomicVariant().getAlt();
        }
    }

    public static String getHGMDLink(Annotation ann) {
        return hgmdURL + ann.getGene().getGeneName();
    }

    public static String getEnsemblLink(Annotation ann) {

        if (ann == null || ann.getTranscriptConsequence() == null) return null;

        if (ann.getTranscriptConsequence().getHgvsc() != null) {
            if (ann.getGenome().equals(Genome.GRCh38)) {
                return ensemblVariationURL + ";v="+ann.getTranscriptConsequence().getHgvsc();
            } else {
                return ensemblVariationURL37 + ";v="+ann.getTranscriptConsequence().getHgvsc();
            }
        }

        if (ann.getTranscriptConsequence().getExternalVariations() != null) {
            for (ExternalVariation av : ann.getTranscriptConsequence().getExternalVariations(ExternalVariation.ExternalVariationDb.DBSNP)) {
                if (ann.getGenome().equals(Genome.GRCh38)) {
                    return ensemblVariationURL + ";v=" + av.getId();
                } else {
                    return ensemblVariationURL37 + ";v=" + av.getId();
                }
            }
        }
        if (ann.getGenome().equals(Genome.GRCh38)) {
            return ensemblURL + ann.getGene().getGeneName();
        } else {
            return ensemblURL37 + ann.getGene().getGeneName();
        }
    }

    public static String getOMIMLink(Annotation ann) {
        return omimURL + ann.getGene().getGeneName();
    }

    public static String getGoogleLink(Annotation ann) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(ann.getGene().getGeneSetId()).append("\" (");
        sb.append("\"").append(ann.getTranscriptConsequence().getHgvsc()).append("\"");
        if (ann.getTranscriptConsequence().getHgvsp() != null && !ann.getTranscriptConsequence().getHgvsp().isEmpty()) {
            sb.append(" | \"").append(ann.getTranscriptConsequence().getHgvsp()).append("\"");
        }
        sb.append(")");

        return googleURL + sb;
    }


    public static String getCosmicLink(String cosmicId) {
        if (cosmicId == null) return null;
        return cosmicSearchURL + cosmicId;
    }


    public static String getCosmicLink(Annotation a) {
        if (a == null || a.getTranscriptConsequence() == null) return null;

        if (a.getTranscriptConsequence().getExternalVariations() != null) {
            for (ExternalVariation av : a.getTranscriptConsequence().getExternalVariations(ExternalVariation.ExternalVariationDb.COSMIC)) {
                return cosmicSearchURL + av.getId();
            }
        }

        return cosmicSearchURL + a.getTranscriptConsequence().getGeneName() + "+" + a.getTranscriptConsequence().getHgvsc();

    }

    public static String getCosmicGeneViewLink(Annotation ann) {
        if (ann.getTranscriptConsequence().getHgvsp() != null && !ann.getTranscriptConsequence().getHgvsp().isEmpty()) {
            String gene;
            if (ann.getGene() != null) {
                gene = ann.getGene().getGeneName();
            } else {
                gene = ann.getTranscriptConsequence().getGeneName();
            }
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(ann.getTranscriptConsequence().getHgvsp());
            if (m.find()) {
                String aa = m.group();
                int pos = Integer.parseInt(aa);
                int start = pos - 10;
                int end = pos + 10;
                return String.format(cosmicGenViewURL, gene, start, end);
            }
        }
        return null;
    }


    public static String getLovdLink(Annotation a) {
        if (a != null && a.getTranscriptConsequence() != null && a.getTranscriptConsequence().getHgvsc() != null) {
            String gene = a.getTranscriptConsequence().getGeneName();
            String hgvsc = a.getTranscriptConsequence().getProteinMutation();
            if (hgvsc != null) {
                String[] hgvscTks = hgvsc.split(":");
                if (hgvscTks.length > 1) {
                    return String.format(lovdURL, gene, hgvscTks[1]);
                }
            }
        }
        return null;
    }

    public static String getLitVarLink(Annotation a) {
        StringBuilder sb = new StringBuilder();
        if (a.getGene() != null) {
            sb.append(a.getGene().getGeneName()).append(" ");
        }

        if (a.getTranscriptConsequence().getHgvsp() != null && !a.getTranscriptConsequence().getHgvsp().isEmpty()) {
            sb.append(a.getTranscriptConsequence().getHgvsp());
        }
        else if (a.getTranscriptConsequence().getHgvsc() != null && !a.getTranscriptConsequence().getHgvsc().isEmpty()) {
            sb.append(a.getTranscriptConsequence().getHgvsc());
        }
        return litvarURL + sb;
    }

    public static String getNuccoreLink(String id) {
        return nuccore + id;
    }

    public static String getVarsomeLink(Annotation a) {
        if (a != null) {
            String url = varsomeURL;
            if (a.getGenome().equals(Genome.GRCh38)) {
                url += "/hg38/";
            } else {
                url += "/hg19/";
            }
            if (a.getTranscriptConsequence() != null && a.getTranscriptConsequence().getHgvsc() != null && !a.getTranscriptConsequence().getHgvsc().isEmpty()) {
                return url + a.getTranscriptConsequence().getHgvscWithoutVersion();
            } else {
                return url + a.getGenomicVariant().getContig() + " " + a.getGenomicVariant().getStart() + " . " + a.getGenomicVariant().getRef() + " " + a.getGenomicVariant().getAlt();
            }
        }
        return null;
    }

    public static String getOncoKBLink(Annotation a) {
        if (a != null && a.getTranscriptConsequence() != null) {
            return oncoKBURL + a.getTranscriptConsequence().getGeneName() + "/" + a.getTranscriptConsequence().getProteinMutationOneLetter();
        }
        return null;
    }

    public static String getPubmedLink(String pubmedId) {
        if (pubmedId == null) return null;
        return pubmed + pubmedId + "/";
    }

    public static String getIntogenLink(Annotation a) {
        if (a.getGene() != null) {
            return intogen + a.getGene().getGeneName();
        } else if (a.getTranscriptConsequence() != null) {
            return intogen + a.getTranscriptConsequence().getGeneName();
        }
        return null;
    }

    public static String getThegenccLink(Annotation a) {
        if (a.getTranscriptConsequence() != null && !StringUtils.isBlank(a.getTranscriptConsequence().getHgncId())) {
            return thegencc + a.getTranscriptConsequence().getHgncId();
        }
        return null;
    }

    public static String getFranklinLink(Annotation a) {
        String url = "";
        if (a.getGenome().equals(Genome.GRCh38)) {
            url = a.getVariant().getGrch38PositionVariant().getContig() + "-" +
                    a.getVariant().getGrch38PositionVariant().getContig() + "-" +
                    a.getVariant().getGrch38PositionVariant().getStart() + "-" +
                    a.getVariant().getGrch38PositionVariant().getRef() + "-" +
                    a.getVariant().getGrch38PositionVariant().getAlt() + "-" +
                    "-hg38";
        } else {
            if (a.getGenome().equals(Genome.GRCh38)) {
                url = a.getVariant().getGrch37PositionVariant().getContig() + "-" +
                        a.getVariant().getGrch37PositionVariant().getContig() + "-" +
                        a.getVariant().getGrch37PositionVariant().getStart() + "-" +
                        a.getVariant().getGrch37PositionVariant().getRef() + "-" +
                        a.getVariant().getGrch37PositionVariant().getAlt();
            }
        }
        return franklin + url;
    }
}
