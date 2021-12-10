package ngsdiaglim.modeles.parsers;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.enumerations.GnomadPopulation;
import ngsdiaglim.enumerations.PredictionTools;
import ngsdiaglim.enumerations.VepTag;
import ngsdiaglim.exceptions.NotBiallelicVariant;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.ExternalVariation;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.TranscriptConsequence;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.modeles.variants.predictions.DbscSNVPredictions;
import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class VCFParser {

    private final File vcfFile;
    private final AnalysisParameters params;
    private final Run run;
    private final VariantParserReportData variantParserReportData = new VariantParserReportData();
    private final ObservableList<Annotation> annotations = FXCollections.observableArrayList();
    private final static String vepFieldSplitter = "&";
    private final static String overlappingDeletionAllele = "*";

    public VCFParser(File vcfFile, AnalysisParameters params, Run run) {
        this.vcfFile = vcfFile;
        this.params = params;
        this.run = run;
    }

    public VariantParserReportData getVariantParserReportData() {return variantParserReportData;}

    public ObservableList<Annotation> getAnnotations() {return annotations;}

    public void parseVCF(boolean parseVep) throws IOException, NotBiallelicVariant, SQLException {
        if (vcfFile.exists()) {
            VCFReader reader = VCFUtils.getVCFReader(vcfFile);
            VepPredictionParser vepPredictionParser = new VepPredictionParser(reader.getHeader());

            for (VariantContext ctx : reader) {

                if (ctx.getGenotype(0).isNoCall() || ctx.getGenotype(0).isHomRef()) {
                    variantParserReportData.incrementNoCallFilteredCount();
                    continue;
                }
                if (ctx.getAlternateAllele(0).getBaseString().equals(overlappingDeletionAllele)) {
                    variantParserReportData.incrementOverlappingDeletionVariants();
                    continue;
                }
                if (!ctx.isBiallelic()) {
                    throw new NotBiallelicVariant("Not biallelic variant found  : " + ctx.getContig() + ":" + ctx.getStart() + "-" + ctx.getReference());
                }

                variantParserReportData.incrementNbVariants();
                Variant variant = DAOController.get().getVariantsDAO().getVariant(ctx.getContig(), ctx.getStart(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
                if (variant == null) {
                    long variant_id = DAOController.get().getVariantsDAO().addVariant(ctx.getContig(), ctx.getStart(), ctx.getEnd(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
                    variant = new Variant(variant_id, ctx.getContig(), ctx.getStart(), ctx.getEnd(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
                }

                Annotation annotation = variantContextToAnnotation(variant, ctx);


                Hotspot hotspot = params.getHotspotsSet() == null ? null : params.getHotspotsSet().getHotspot(annotation.getVariant());

                if (hotspot != null || annotation.getVaf() >= params.getMinVAF()) {

                    if (parseVep) {
                        annotation.getVariant().setHotspot(hotspot);
                        Object[] analysisIds = new Object[run.getAnalyses().size()];
                        for (int i = 0; i < run.getAnalyses().size(); i++) {
                            analysisIds[i] = (int) run.getAnalyses().get(i).getId();
                        }
                        variant.setOccurrenceInRun(DAOController.get().getVariantAnalysisDAO().countRunOccurrence(variant.getId(), analysisIds));
                        parseTranscriptsConsequences(annotation, ctx, vepPredictionParser, params);
//                        List<VepPredictionParser.VepPrediction> predictions = vepPredictionParser.getPredictions(ctx);
//                        HashSet<String> geneNames = new HashSet<>();
//                        for (VepPredictionParser.VepPrediction p : predictions) {
//                            TranscriptConsequence transcriptConsequence = parseVepPrediction(annotation, p, params);
//                            if (transcriptConsequence.getTranscript() != null) {
//                                annotation.addTranscriptConsequence(transcriptConsequence);
//                            }
//                            String geneName = transcriptConsequence.getGeneName();
//                            if (geneName != null && !StringUtils.isBlank(geneName)) {
//                                geneNames.add(geneName);
//
//                                // if gene correspond to gene in geneSet, set as Default gene for the annotation
//                                if (annotation.getGene() == null) {
//                                    Gene gene = params.getGeneSet().getGene(geneName);
//                                    if (gene != null) {
//                                        annotation.setGene(gene);
//                                    }
//                                }
//                            }
//                            annotation.setGeneNames(geneNames);
//                        }
//
//                        // set visible transcript consequence from preferred transcript
//                        if (!annotation.getTranscriptConsequences().isEmpty()) {
//                            for (Map.Entry<String, TranscriptConsequence> entry : annotation.getTranscriptConsequences().entrySet()) {
//                                if (entry.getValue().getTranscript().isPreferred()) {
//                                    annotation.setTranscriptConsequence(entry.getValue());
//                                    break;
//                                }
//                            }
////                        for (Map.Entry<String, TranscriptConsequence> entry : annotation.getTranscriptConsequences().entrySet()) {
////                            String geneName = entry.getValue().getGeneName();
////                            if (geneName != null) {
////                                Gene gene = params.getGeneSet().getGene(entry.getValue().getGeneName());
////                                if (gene != null && gene.getTranscriptPreferred() != null) {
////                                    if (gene.getTranscriptPreferred().equals(entry.getValue().getTranscript())) {
////                                        entry.getValue().getTranscript().setPreferred(true);
////                                        entry.getValue().getTranscript().setGene(gene);
////                                        annotation.setTranscriptConsequence(entry.getValue());
////                                    }
////                                }
////                            }
////                        }
//
////                        if (annotation.getGene() != null) {
////                            Transcript preferredTranscript = annotation.getGene().getTranscriptPreferred();
////                            if (preferredTranscript != null) {
////                                TranscriptConsequence transcriptConsequence = annotation.getTranscriptConsequences().get(preferredTranscript.getNameWithoutVersion());
////                                if (transcriptConsequence != null) {
////                                    annotation.setTranscriptConsequence(transcriptConsequence);
////                                }
////                            }
////                        }
//                        } else { // set empty Transcript consequence
//                            annotation.setTranscriptConsequence(new TranscriptConsequence(annotation));
//                        }
//                        if (annotation.getTranscriptConsequence() == null) {
//                            if (annotation.getTranscriptConsequences().isEmpty()) {
//                                annotation.setTranscriptConsequence(new TranscriptConsequence(annotation));
//                            } else {
//                                // set first transcript read
//                                annotation.setTranscriptConsequence(annotation.getTranscriptConsequences().entrySet().iterator().next().getValue());
//                            }
//                        }

                    }

                    annotations.add(annotation);
                } else {
                    variantParserReportData.incrementVafFilteredCount();
                }
            }
        }
    }


    public static Annotation variantContextToAnnotation(Variant variant, VariantContext ctx) {
        Annotation annotation = new Annotation(variant);
        Genotype genotype = ctx.getGenotype(0);
        int dp = genotype.getDP();
        annotation.setDepth(dp);
        int[] ad = getAlleleDepth(genotype);
        if (ad != null) {
            annotation.setReferenceDepth(ad[0]);
            annotation.setAlleleDepth(ad[1]);
        }
        Float af = getAF(genotype, ad);
        if (af != null) {
            annotation.setVaf(af);
        }
        int[] alleleStrandBias = getAlleleCountByStrand(ctx);
        if (alleleStrandBias != null) {
            annotation.setAlleleForwardCount(alleleStrandBias[0]);
            annotation.setAlleleReverseCount(alleleStrandBias[1]);
        }

        return annotation;
    }


    public static Annotation parseTranscriptsConsequences(Annotation annotation, VariantContext ctx, VepPredictionParser vepPredictionParser, AnalysisParameters params) {
        List<VepPredictionParser.VepPrediction> predictions = vepPredictionParser.getPredictions(ctx);
        HashSet<String> geneNames = new HashSet<>();
        for (VepPredictionParser.VepPrediction p : predictions) {
            TranscriptConsequence transcriptConsequence = parseVepPrediction(annotation, p, params);
            if (transcriptConsequence.getTranscript() != null) {
                annotation.addTranscriptConsequence(transcriptConsequence);
            }
            String geneName = transcriptConsequence.getGeneName();
            if (geneName != null && !StringUtils.isBlank(geneName)) {
                geneNames.add(geneName);

                // if gene correspond to gene in geneSet, set as Default gene for the annotation
                if (annotation.getGene() == null) {
                    Gene gene = params.getGeneSet().getGene(geneName);
                    if (gene != null) {
                        annotation.setGene(gene);
                    }
                }
            }
            annotation.setGeneNames(geneNames);
        }

        // set visible transcript consequence from preferred transcript
        if (!annotation.getTranscriptConsequences().isEmpty()) {
            for (Map.Entry<String, TranscriptConsequence> entry : annotation.getTranscriptConsequences().entrySet()) {
                if (entry.getValue().getTranscript().isPreferred()) {
                    annotation.setTranscriptConsequence(entry.getValue());
                    break;
                }
            }
//                        for (Map.Entry<String, TranscriptConsequence> entry : annotation.getTranscriptConsequences().entrySet()) {
//                            String geneName = entry.getValue().getGeneName();
//                            if (geneName != null) {
//                                Gene gene = params.getGeneSet().getGene(entry.getValue().getGeneName());
//                                if (gene != null && gene.getTranscriptPreferred() != null) {
//                                    if (gene.getTranscriptPreferred().equals(entry.getValue().getTranscript())) {
//                                        entry.getValue().getTranscript().setPreferred(true);
//                                        entry.getValue().getTranscript().setGene(gene);
//                                        annotation.setTranscriptConsequence(entry.getValue());
//                                    }
//                                }
//                            }
//                        }

//                        if (annotation.getGene() != null) {
//                            Transcript preferredTranscript = annotation.getGene().getTranscriptPreferred();
//                            if (preferredTranscript != null) {
//                                TranscriptConsequence transcriptConsequence = annotation.getTranscriptConsequences().get(preferredTranscript.getNameWithoutVersion());
//                                if (transcriptConsequence != null) {
//                                    annotation.setTranscriptConsequence(transcriptConsequence);
//                                }
//                            }
//                        }
        } else { // set empty Transcript consequence
            annotation.setTranscriptConsequence(new TranscriptConsequence(annotation));
        }
        if (annotation.getTranscriptConsequence() == null) {
            if (annotation.getTranscriptConsequences().isEmpty()) {
                annotation.setTranscriptConsequence(new TranscriptConsequence(annotation));
            } else {
                // set first transcript read
                annotation.setTranscriptConsequence(annotation.getTranscriptConsequences().entrySet().iterator().next().getValue());
            }
        }

        return annotation;
    }


    public static TranscriptConsequence parseVepPrediction(Annotation annotation, VepPredictionParser.VepPrediction vepPrediction, AnalysisParameters params) {

        TranscriptConsequence transcriptConsequence = new TranscriptConsequence(annotation);

        String feature = vepPrediction.getByCol(VepTag.Feature.getColumnName());

        if (feature != null) {
            // try if transcript is present in GeneSet
            Transcript transcript = params.getGeneSet().getTranscript(Transcript.getNameWithoutVersion(feature));
            if (transcript == null) {
                // create new transcript
                transcript = new Transcript(feature);
            }
            transcriptConsequence.setTranscript(transcript);
        }

        String consequence = vepPrediction.getByCol(VepTag.CONSEQUENCE.getColumnName());

        if (consequence != null) {
            EnsemblConsequence mostSevereConsequence = null;
            for (String cons : consequence.split(vepFieldSplitter)) {
                // Hack for java variable that can't starts with a number
                if (cons.equalsIgnoreCase("5_prime_UTR_variant")) {
                    cons = "PRIME_5_UTR_VARIANT";
                } else if (cons.equalsIgnoreCase("3_prime_UTR_variant")) {
                    cons = "PRIME_3_UTR_VARIANT";
                }
                EnsemblConsequence ensemblCons = EnsemblConsequence.fromString(cons.toUpperCase());
                if (ensemblCons != null) {
                    transcriptConsequence.addConsequence(ensemblCons);
                    if (mostSevereConsequence == null || ensemblCons.getWeight() > mostSevereConsequence.getWeight()) {
                        mostSevereConsequence = ensemblCons;
                    }
                }
            }
            if (mostSevereConsequence != null) {
                transcriptConsequence.setConsequence(mostSevereConsequence);
            }
        }

        String symbol = vepPrediction.getByCol(VepTag.SYMBOL.getColumnName());
        if (symbol != null) transcriptConsequence.setGeneName(symbol);

        String exon = vepPrediction.getByCol(VepTag.EXON.getColumnName());
        if (exon != null) transcriptConsequence.setExon(exon);

        String intron = vepPrediction.getByCol(VepTag.INTRON.getColumnName());
        if (intron != null) transcriptConsequence.setIntron(intron);

        String hgvsc = vepPrediction.getByCol(VepTag.HGVSc.getColumnName());
        if (hgvsc != null) transcriptConsequence.setHgvsc(hgvsc);

        String hgvsp = vepPrediction.getByCol(VepTag.HGVSp.getColumnName());
        if (hgvsp != null) transcriptConsequence.setHgvsp(hgvsp);

        String cDNAPosition = vepPrediction.getByCol(VepTag.cDNA_position.getColumnName());
        if (cDNAPosition != null) transcriptConsequence.setcDNAPosition(cDNAPosition);

        String cdsPosition = vepPrediction.getByCol(VepTag.CDS_position.getColumnName());
        if (cdsPosition != null) transcriptConsequence.setCdsPosition(cdsPosition);

        String proteinPosition = vepPrediction.getByCol(VepTag.Protein_position.getColumnName());
        if (proteinPosition != null) transcriptConsequence.setProteinPosition(proteinPosition);

        String codons = vepPrediction.getByCol(VepTag.Codons.getColumnName());
        if (codons != null) transcriptConsequence.setCodons(codons);

        String aminoAcids = vepPrediction.getByCol(VepTag.Amino_acids.getColumnName());
        if (aminoAcids != null) transcriptConsequence.setAminoAcids(aminoAcids);

        String existingVariations = vepPrediction.getByCol(VepTag.Existing_variation.getColumnName());
        if (existingVariations != null) {
            ObservableList<ExternalVariation> externalVariations = FXCollections.observableArrayList();
            for (String variationId : existingVariations.split(vepFieldSplitter)) {
                ExternalVariation externalVariation = ExternalVariation.parseString(variationId);
                if (externalVariation != null) {
                    externalVariations.add(externalVariation);
                }
            }
            if (!externalVariations.isEmpty()) transcriptConsequence.setExternalVariations(externalVariations);
        }


        String distance = vepPrediction.getByCol(VepTag.DISTANCE.getColumnName());
        if (NumberUtils.isInt(distance)) transcriptConsequence.setDistance(Integer.parseInt(distance));

        String protein = vepPrediction.getByCol(VepTag.ENSP.getColumnName());
        if (protein != null) transcriptConsequence.setProteinName(protein);

        String siftPreds = vepPrediction.getByCol(VepTag.SIFT_PRED.getColumnName());
        String siftScores = vepPrediction.getByCol(VepTag.SIFT_SCORE.getColumnName());
        if (siftPreds != null && siftScores != null) {
            String mostSevereSift = null;
            Float mostSevereScore = null;
            String[] siftPredsTks = siftPreds.split("&");
            String[] siftScoresTks = siftScores.split("&");
            if (siftPredsTks.length == siftScoresTks.length) {
                for (int i = 0; i < siftPredsTks.length; i++) {
                    if (!siftPredsTks[i].equals(".")) {
                        if (NumberUtils.isFloat(siftScoresTks[i])) {
                            float siftScore = Float.parseFloat(siftScoresTks[i]);
                            if (mostSevereScore == null || siftScore > mostSevereScore) {
                                mostSevereScore = siftScore;
                                mostSevereSift = siftPredsTks[i];
                            }
                        }
                    }
                }
            }
            if (mostSevereSift != null) {
                VariantPrediction siftPrediction = new VariantPrediction(PredictionTools.SIFT, mostSevereSift, mostSevereScore);
                transcriptConsequence.setSiftPred(siftPrediction);
            }
        }

        String polyphen2HvarPreds = vepPrediction.getByCol(VepTag.POLYPHEN_2HVAR_PRED.getColumnName());
        String polyphen2HvarScores = vepPrediction.getByCol(VepTag.POLYPHEN_2HVAR_SCORE.getColumnName());
        if (polyphen2HvarPreds != null && polyphen2HvarScores != null) {
            String mostSeverePred = null;
            Float mostSevereScore = null;
            String[] polyphen2HvarPredsTks = polyphen2HvarPreds.split("&");
            String[] polyphen2HvarScoresTks = polyphen2HvarScores.split("&");
            if (polyphen2HvarPredsTks.length == polyphen2HvarScoresTks.length) {
                for (int i = 0; i < polyphen2HvarPredsTks.length; i++) {
                    if (!polyphen2HvarPredsTks[i].equals(".")) {
                        if (NumberUtils.isFloat(polyphen2HvarScoresTks[i])) {
                            float score = Float.parseFloat(polyphen2HvarScoresTks[i]);
                            if (mostSevereScore == null || score > mostSevereScore) {
                                mostSevereScore = score;
                                mostSeverePred = polyphen2HvarPredsTks[i];
                            }
                        }
                    }
                }
            }
            if (mostSeverePred != null) {
                VariantPrediction polyphenHvarPrediction = new VariantPrediction(PredictionTools.POLYPHEN2_HVAR, mostSeverePred, mostSevereScore);
                transcriptConsequence.setPolyphen2HvarPred(polyphenHvarPrediction);
            }
        }

        String polyphen2HdivPreds = vepPrediction.getByCol(VepTag.POLYPHEN_2HDIV_PRED.getColumnName());
        String polyphen2HdivScores = vepPrediction.getByCol(VepTag.POLYPHEN_2HDIV_SCORE.getColumnName());
        if (polyphen2HdivPreds != null && polyphen2HdivScores != null) {
            String mostSeverePred = null;
            Float mostSevereScore = null;
            String[] polyphen2HdivPredsTks = polyphen2HdivPreds.split("&");
            String[] polyphen2HdivScoresTks = polyphen2HdivScores.split("&");
            if (polyphen2HdivPredsTks.length == polyphen2HdivScoresTks.length) {
                for (int i = 0; i < polyphen2HdivPredsTks.length; i++) {
                    if (!polyphen2HdivPredsTks[i].equals(".")) {
                        if (NumberUtils.isFloat(polyphen2HdivScoresTks[i])) {
                            float score = Float.parseFloat(polyphen2HdivScoresTks[i]);
                            if (mostSevereScore == null || score > mostSevereScore) {
                                mostSevereScore = score;
                                mostSeverePred = polyphen2HdivPredsTks[i];
                            }
                        }
                    }
                }
            }
            if (mostSeverePred != null) {
                VariantPrediction polyphenHdivPrediction = new VariantPrediction(PredictionTools.POLYPHEN2_HDIV, mostSeverePred, mostSevereScore);
                transcriptConsequence.setPolyphen2HdivPred(polyphenHdivPrediction);
            }
        }

        String gnomAD_AFR_AF = vepPrediction.getByCol(VepTag.gnomAD_AFR_AF.getColumnName());
        String gnomAD_AFR_AC = vepPrediction.getByCol(VepTag.gnomAD_AFR_AC.getColumnName());
        String gnomAD_AFR_AN = vepPrediction.getByCol(VepTag.gnomAD_AFR_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_AFR_AF) && NumberUtils.isInt(gnomAD_AFR_AC) && NumberUtils.isInt(gnomAD_AFR_AN)) {
            GnomadPopulationFreq afr = new GnomadPopulationFreq(
                    GnomadPopulation.AFR,
                    Float.parseFloat(gnomAD_AFR_AF),
                    Integer.parseInt(gnomAD_AFR_AC),
                    Integer.parseInt(gnomAD_AFR_AN)
                    );
            annotation.getGnomADFrequencies().setAfr(afr);
        }

        String gnomAD_AMR_AF = vepPrediction.getByCol(VepTag.gnomAD_AMR_AF.getColumnName());
        String gnomAD_AMR_AC = vepPrediction.getByCol(VepTag.gnomAD_AMR_AC.getColumnName());
        String gnomAD_AMR_AN = vepPrediction.getByCol(VepTag.gnomAD_AMR_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_AMR_AF) && NumberUtils.isInt(gnomAD_AMR_AC) && NumberUtils.isInt(gnomAD_AMR_AN)) {
            GnomadPopulationFreq amr = new GnomadPopulationFreq(
                    GnomadPopulation.AMR,
                    Float.parseFloat(gnomAD_AMR_AF),
                    Integer.parseInt(gnomAD_AMR_AC),
                    Integer.parseInt(gnomAD_AMR_AN)
            );
            annotation.getGnomADFrequencies().setAmr(amr);
        }

        String gnomAD_ASJ_AF = vepPrediction.getByCol(VepTag.gnomAD_ASJ_AF.getColumnName());
        String gnomAD_ASJ_AC = vepPrediction.getByCol(VepTag.gnomAD_ASJ_AC.getColumnName());
        String gnomAD_ASJ_AN = vepPrediction.getByCol(VepTag.gnomAD_ASJ_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_ASJ_AF) && NumberUtils.isInt(gnomAD_ASJ_AC) && NumberUtils.isInt(gnomAD_ASJ_AN)) {
            GnomadPopulationFreq asj = new GnomadPopulationFreq(
                    GnomadPopulation.ASJ,
                    Float.parseFloat(gnomAD_ASJ_AF),
                    Integer.parseInt(gnomAD_ASJ_AC),
                    Integer.parseInt(gnomAD_ASJ_AN)
            );
            annotation.getGnomADFrequencies().setAsj(asj);
        }

        String gnomAD_EAS_AF = vepPrediction.getByCol(VepTag.gnomAD_EAS_AF.getColumnName());
        String gnomAD_EAS_AC = vepPrediction.getByCol(VepTag.gnomAD_EAS_AC.getColumnName());
        String gnomAD_EAS_AN = vepPrediction.getByCol(VepTag.gnomAD_EAS_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_EAS_AF) && NumberUtils.isInt(gnomAD_EAS_AC) && NumberUtils.isInt(gnomAD_EAS_AN)) {
            GnomadPopulationFreq eas = new GnomadPopulationFreq(
                    GnomadPopulation.EAS,
                    Float.parseFloat(gnomAD_EAS_AF),
                    Integer.parseInt(gnomAD_EAS_AC),
                    Integer.parseInt(gnomAD_EAS_AN)
            );
            annotation.getGnomADFrequencies().setEas(eas);
        }

        String gnomAD_FIN_AF = vepPrediction.getByCol(VepTag.gnomAD_FIN_AF.getColumnName());
        String gnomAD_FIN_AC = vepPrediction.getByCol(VepTag.gnomAD_FIN_AC.getColumnName());
        String gnomAD_FIN_AN = vepPrediction.getByCol(VepTag.gnomAD_FIN_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_FIN_AF) && NumberUtils.isInt(gnomAD_FIN_AC) && NumberUtils.isInt(gnomAD_FIN_AN)) {
            GnomadPopulationFreq fin = new GnomadPopulationFreq(
                    GnomadPopulation.FIN,
                    Float.parseFloat(gnomAD_FIN_AF),
                    Integer.parseInt(gnomAD_FIN_AC),
                    Integer.parseInt(gnomAD_FIN_AN)
            );
            annotation.getGnomADFrequencies().setFin(fin);
        }

        String gnomAD_NFE_AF = vepPrediction.getByCol(VepTag.gnomAD_NFE_AF.getColumnName());
        String gnomAD_NFE_AC = vepPrediction.getByCol(VepTag.gnomAD_NFE_AC.getColumnName());
        String gnomAD_NFE_AN = vepPrediction.getByCol(VepTag.gnomAD_NFE_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_NFE_AF) && NumberUtils.isInt(gnomAD_NFE_AC) && NumberUtils.isInt(gnomAD_NFE_AN)) {
            GnomadPopulationFreq nfe = new GnomadPopulationFreq(
                    GnomadPopulation.NFE,
                    Float.parseFloat(gnomAD_NFE_AF),
                    Integer.parseInt(gnomAD_NFE_AC),
                    Integer.parseInt(gnomAD_NFE_AN)
            );
            annotation.getGnomADFrequencies().setNfe(nfe);
        }

        String gnomAD_SAS_AF = vepPrediction.getByCol(VepTag.gnomAD_SAS_AF.getColumnName());
        String gnomAD_SAS_AC = vepPrediction.getByCol(VepTag.gnomAD_SAS_AC.getColumnName());
        String gnomAD_SAS_AN = vepPrediction.getByCol(VepTag.gnomAD_SAS_AN.getColumnName());
        if (NumberUtils.isFloat(gnomAD_SAS_AF) && NumberUtils.isInt(gnomAD_SAS_AC) && NumberUtils.isInt(gnomAD_SAS_AN)) {
            GnomadPopulationFreq sas = new GnomadPopulationFreq(
                    GnomadPopulation.SAS,
                    Float.parseFloat(gnomAD_SAS_AF),
                    Integer.parseInt(gnomAD_SAS_AC),
                    Integer.parseInt(gnomAD_SAS_AN)
            );
            annotation.getGnomADFrequencies().setSas(sas);
        }

        annotation.getGnomADFrequencies().computeMaxGnomad();

        String clinvarSig = vepPrediction.getByCol(VepTag.CLIN_SIG.getColumnName());
        if (clinvarSig != null) {
            ObservableList<String> clinvarSigs = FXCollections.observableArrayList();
            clinvarSigs.addAll(Arrays.asList(clinvarSig.split(vepFieldSplitter)));
            if (!clinvarSigs.isEmpty()) transcriptConsequence.setClinvarSig(clinvarSigs);
        }

        String clinvarId = vepPrediction.getByCol(VepTag.clinvar_id.getColumnName());
        if (clinvarId != null) transcriptConsequence.setClinvarId(clinvarId);

        String pubmed = vepPrediction.getByCol(VepTag.PUBMED.getColumnName());
        if (pubmed != null) {
            ObservableList<String> pubmedList = FXCollections.observableArrayList();
            pubmedList.addAll(Arrays.asList(pubmed.split(vepFieldSplitter)));
            if (!pubmedList.isEmpty()) transcriptConsequence.setPubmedIds(pubmedList);
        }

        String caddRaw = vepPrediction.getByCol(VepTag.CADD_RAW.getColumnName());
        if (NumberUtils.isFloat(caddRaw)) {
            VariantPrediction caddRawPred = new VariantPrediction(PredictionTools.CADD_RAW, null, Float.parseFloat(caddRaw));
            transcriptConsequence.setCaddRawPred(caddRawPred);
        }

        String caddPhred = vepPrediction.getByCol(VepTag.CADD_PHRED.getColumnName());
        if (NumberUtils.isFloat(caddPhred)) {
            VariantPrediction caddPhredPred = new VariantPrediction(PredictionTools.CADD_PHRED, null, Float.parseFloat(caddPhred));
            transcriptConsequence.setCaddPhredPred(caddPhredPred);
        }

        String phastCons100way = vepPrediction.getByCol(VepTag.phastCons100way_vertebrate.getColumnName());
        if (NumberUtils.isFloat(phastCons100way)) {
            VariantPrediction phastCons100wayPred = new VariantPrediction(PredictionTools.PHASTCONS100WAY_VERTEBRATE, null, Float.parseFloat(phastCons100way));
            transcriptConsequence.setPhastCons100WayPred(phastCons100wayPred);
        }

        String phastCons30way = vepPrediction.getByCol(VepTag.phastCons30way_mammalian.getColumnName());
        if (NumberUtils.isFloat(phastCons30way)) {
            VariantPrediction phastCons30wayPred = new VariantPrediction(PredictionTools.PHASTCONS100WAY_VERTEBRATE, null, Float.parseFloat(phastCons30way));
            transcriptConsequence.setPhastCons30WayPred(phastCons30wayPred);
        }

        String phylop100way = vepPrediction.getByCol(VepTag.phyloP100way_vertebrate.getColumnName());
        if (NumberUtils.isFloat(phylop100way)) {
            VariantPrediction phylop100wayPred = new VariantPrediction(PredictionTools.PHYLOP100WAY_VERTEBRATE, null, Float.parseFloat(phylop100way));
            transcriptConsequence.setPhylop100WayPred(phylop100wayPred);
        }

        String phylop30way = vepPrediction.getByCol(VepTag.phyloP30way_mammalian.getColumnName());
        if (NumberUtils.isFloat(phylop30way)) {
            VariantPrediction phylop30wayPred = new VariantPrediction(PredictionTools.PHYLOP30WAY_MAMMALIAN, null, Float.parseFloat(phylop30way));
            transcriptConsequence.setPhylop30WayPred(phylop30wayPred);
        }

        String siphy = vepPrediction.getByCol(VepTag.SiPhy_29way_logOdds.getColumnName());
        if (NumberUtils.isFloat(siphy)) {
            VariantPrediction siphyPred = new VariantPrediction(PredictionTools.SIPHY, null, Float.parseFloat(siphy));
            transcriptConsequence.setSiphyPred(siphyPred);
        }

        String fathmm = vepPrediction.getByCol(VepTag.FATHMM_score.getColumnName());
        if (NumberUtils.isFloat(fathmm)) {
            String fathmmP = vepPrediction.getByCol(VepTag.FATHMM_pred.getColumnName());
            VariantPrediction fathmmPred = new VariantPrediction(PredictionTools.FATHMM, fathmmP, Float.parseFloat(fathmm));
            transcriptConsequence.setFathmmPred(fathmmPred);
        }

        String vest4 = vepPrediction.getByCol(VepTag.VEST4_score.getColumnName());
        if (NumberUtils.isFloat(vest4)) {
            VariantPrediction vest4Pred = new VariantPrediction(PredictionTools.VEST4, null, Float.parseFloat(vest4));
            transcriptConsequence.setVest4Pred(vest4Pred);
        }

        String mcap = vepPrediction.getByCol(VepTag.MCAP_score.getColumnName());
        if (NumberUtils.isFloat(mcap)) {
            String mcapP = vepPrediction.getByCol(VepTag.MCAP_pred.getColumnName());
            VariantPrediction mcapPred = new VariantPrediction(PredictionTools.MCAP, mcapP, Float.parseFloat(mcap));
            transcriptConsequence.setMcapPred(mcapPred);
        }

        String metaLR = vepPrediction.getByCol(VepTag.MetaLR_score.getColumnName());
        if (NumberUtils.isFloat(metaLR)) {
            String metaLRP = vepPrediction.getByCol(VepTag.MetaLR_pred.getColumnName());
            VariantPrediction metaLRPred = new VariantPrediction(PredictionTools.MetaLR, metaLRP, Float.parseFloat(metaLR));
            transcriptConsequence.setMetaLRPred(metaLRPred);
        }

        String metaSVM = vepPrediction.getByCol(VepTag.MetaSVM_score.getColumnName());
        if (NumberUtils.isFloat(metaSVM)) {
            String metaSVMP = vepPrediction.getByCol(VepTag.MetaSVM_pred.getColumnName());
            VariantPrediction metaSVMPred = new VariantPrediction(PredictionTools.MCAP, metaSVMP, Float.parseFloat(metaSVM));
            transcriptConsequence.setMetaSVMPred(metaSVMPred);
        }

        String dbscSNV_ADA = vepPrediction.getByCol(VepTag.dbscSNV_ADA.getColumnName());
        String dbscSNV_RF = vepPrediction.getByCol(VepTag.dbscSNV_RF.getColumnName());

        Float dbscSNV_ADA_score = null;
        if (NumberUtils.isFloat(dbscSNV_ADA)) dbscSNV_ADA_score = Float.parseFloat(dbscSNV_ADA);
        Float dbscSNV_RF_score = null;
        if (NumberUtils.isFloat(dbscSNV_RF)) dbscSNV_RF_score = Float.parseFloat(dbscSNV_RF);

        if (dbscSNV_ADA_score != null || dbscSNV_RF_score != null) {
            DbscSNVPredictions dbscSNV = new DbscSNVPredictions(dbscSNV_ADA_score, dbscSNV_RF_score);
            transcriptConsequence.setDbscSNVPreds(dbscSNV);
        }

        SpliceAIPredictions spliceAIPredictions = null;

        String spliceAI_pred_DP_AG = vepPrediction.getByCol(VepTag.SpliceAI_pred_DP_AG.getColumnName());
        String spliceAI_pred_DS_AG = vepPrediction.getByCol(VepTag.SpliceAI_pred_DS_AG.getColumnName());
        if (NumberUtils.isInt(spliceAI_pred_DP_AG) && NumberUtils.isFloat(spliceAI_pred_DS_AG)) {
            SpliceAIPredictions.SpliceAIPrediction spliceAI_AG = new SpliceAIPredictions.SpliceAIPrediction(
                    SpliceAIPredictions.SpliceAISite.ACCPETOR_GAIN,
                    Integer.parseInt(spliceAI_pred_DP_AG),
                    Float.parseFloat(spliceAI_pred_DS_AG)
            );
            spliceAIPredictions = new SpliceAIPredictions();
            spliceAIPredictions.setAcceptorGainPred(spliceAI_AG);
        }

        String spliceAI_pred_DP_AL = vepPrediction.getByCol(VepTag.SpliceAI_pred_DP_AL.getColumnName());
        String spliceAI_pred_DS_AL = vepPrediction.getByCol(VepTag.SpliceAI_pred_DS_AL.getColumnName());
        if (NumberUtils.isInt(spliceAI_pred_DP_AL) && NumberUtils.isFloat(spliceAI_pred_DS_AL)) {
            SpliceAIPredictions.SpliceAIPrediction spliceAI_AL = new SpliceAIPredictions.SpliceAIPrediction(
                    SpliceAIPredictions.SpliceAISite.ACCEPTOR_LOSS,
                    Integer.parseInt(spliceAI_pred_DP_AL),
                    Float.parseFloat(spliceAI_pred_DS_AL)
            );
            if (spliceAIPredictions == null) spliceAIPredictions = new SpliceAIPredictions();
            spliceAIPredictions.setAcceptorLossPred(spliceAI_AL);
        }

        String spliceAI_pred_DP_DG = vepPrediction.getByCol(VepTag.SpliceAI_pred_DP_DG.getColumnName());
        String spliceAI_pred_DS_DG = vepPrediction.getByCol(VepTag.SpliceAI_pred_DS_DG.getColumnName());
        if (NumberUtils.isInt(spliceAI_pred_DP_DG) && NumberUtils.isFloat(spliceAI_pred_DS_DG)) {
            SpliceAIPredictions.SpliceAIPrediction spliceAI_DG = new SpliceAIPredictions.SpliceAIPrediction(
                    SpliceAIPredictions.SpliceAISite.DONNOR_GAIN,
                    Integer.parseInt(spliceAI_pred_DP_DG),
                    Float.parseFloat(spliceAI_pred_DS_DG)
            );
            if (spliceAIPredictions == null) spliceAIPredictions = new SpliceAIPredictions();
            spliceAIPredictions.setDonnorGainPred(spliceAI_DG);
        }

        String spliceAI_pred_DP_DL = vepPrediction.getByCol(VepTag.SpliceAI_pred_DP_DL.getColumnName());
        String spliceAI_pred_DS_DL = vepPrediction.getByCol(VepTag.SpliceAI_pred_DS_DL.getColumnName());
        if (NumberUtils.isInt(spliceAI_pred_DP_DL) && NumberUtils.isFloat(spliceAI_pred_DS_DL)) {
            SpliceAIPredictions.SpliceAIPrediction spliceAI_DL = new SpliceAIPredictions.SpliceAIPrediction(
                    SpliceAIPredictions.SpliceAISite.DONNOR_LOSS,
                    Integer.parseInt(spliceAI_pred_DP_DL),
                    Float.parseFloat(spliceAI_pred_DS_DL)
            );
            if (spliceAIPredictions == null) spliceAIPredictions = new SpliceAIPredictions();
            spliceAIPredictions.setDonnorLossPred(spliceAI_DL);
        }

        if (spliceAIPredictions != null) {
            transcriptConsequence.setspliceAIPreds(spliceAIPredictions);
        }

        String gerp = vepPrediction.getByCol(VepTag.GERP_RS.getColumnName());
        if (NumberUtils.isFloat(gerp)) {
            VariantPrediction gerpPred = new VariantPrediction(PredictionTools.GERP, null, Float.valueOf(gerp));
            transcriptConsequence.setGerpPred(gerpPred);
        }

        String revel = vepPrediction.getByCol(VepTag.REVEL_score.getColumnName());
        if (NumberUtils.isFloat(revel)) {
            VariantPrediction revelPred = new VariantPrediction(PredictionTools.REVEL, null, Float.valueOf(revel));
            transcriptConsequence.setRevelPred(revelPred);
        }

        String mvpStr = vepPrediction.getByCol(VepTag.MVP_score.getColumnName());
        if (mvpStr != null) {
            Float mostSevereScore = null;
            String[] mvpTks = mvpStr.split("&");
            for (String mvpTk : mvpTks) {
                if (!mvpTk.equals(".")) {
                    if (NumberUtils.isFloat(mvpTk)) {
                        float score = Float.parseFloat(mvpTk);
                        if (mostSevereScore == null || score > mostSevereScore) {
                            mostSevereScore = score;
                        }
                    }
                }
            }

            if (mostSevereScore != null) {
                VariantPrediction mvpPrediction = new VariantPrediction(PredictionTools.MVP, null, mostSevereScore);
                transcriptConsequence.setMVPPred(mvpPrediction);
            }

        }

        return transcriptConsequence;
    }

    public static int[] getAlleleDepth(Genotype genotype) {
        if (genotype.hasAD()) {
            return genotype.getAD();
        }
        if (genotype.hasAnyAttribute("FRO") && genotype.hasAnyAttribute("FAO")) {
            return new int[] {
                    Integer.parseInt(genotype.getAnyAttribute("FAO").toString()),
                    Integer.parseInt(genotype.getAnyAttribute("FRO").toString())
            };
        }
        if (genotype.hasAnyAttribute("RO") && genotype.hasAnyAttribute("AO")) {
            return new int[] {
                    Integer.parseInt(genotype.getAnyAttribute("AO").toString()),
                    Integer.parseInt(genotype.getAnyAttribute("RO").toString())
            };
        }
        return null;
    }

    public static Float getAF(Genotype genotype, int[] ad) {
        if (genotype.hasAnyAttribute("AF")) {
            return Float.parseFloat(genotype.getAnyAttribute("AF").toString());
        }
        if (ad != null) {
            return ad[0] / (float) (ad[0] + ad[1]);
        }
        return null;
    }

    public static int[] getAlleleCountByStrand(VariantContext ctx) {
        Genotype genotype = ctx.getGenotype(0);
        if (genotype.hasAnyAttribute("FSAF") && genotype.hasAnyAttribute("FSAR")) {
            return new int[] {
                    Integer.parseInt(genotype.getAnyAttribute("FSAF").toString()),
                    Integer.parseInt(genotype.getAnyAttribute("FSAR").toString())
            };
        }
        if (genotype.hasAnyAttribute("SAF") && genotype.hasAnyAttribute("SAR")) {
            return new int[] {
                    Integer.parseInt(genotype.getAnyAttribute("SAF").toString()),
                    Integer.parseInt(genotype.getAnyAttribute("SAR").toString())
            };
        }
        if (ctx.hasAttribute("SAF") && ctx.hasAttribute("SAR")) {
            return new int[] {
                    Integer.parseInt(ctx.getAttribute("SAF").toString()),
                    Integer.parseInt(ctx.getAttribute("SAR").toString())
            };
        }
        return null;
    }

    public static class VariantParserReportData {

        private int variantsCount = 0;
        private int filteredVariantsCount = 0;
        private int vafFilteredCount = 0;
        private int noCallFilteredCount = 0;
        private int overlapingDeletionVariant = 0;

        public VariantParserReportData() {}

        public int getVariantsCount() {return variantsCount;}

        public int getFilteredVariantsCount() {return filteredVariantsCount;}

        public int getVafFilteredCount() {return vafFilteredCount;}

        public int getNoCallFilteredCount() {return noCallFilteredCount;}

        public int getOverlapingDeletionVariant() {return overlapingDeletionVariant;}

        public void incrementNbVariants() {
            variantsCount++;
        }

        public void incrementFilteredVariants() {
            filteredVariantsCount++;
        }

        public void incrementVafFilteredCount() {
            vafFilteredCount++;
            incrementFilteredVariants();
        }

        public void incrementNoCallFilteredCount() {
            noCallFilteredCount++;
            incrementFilteredVariants();
        }

        public void incrementOverlappingDeletionVariants() { overlapingDeletionVariant++; }
    }
}
