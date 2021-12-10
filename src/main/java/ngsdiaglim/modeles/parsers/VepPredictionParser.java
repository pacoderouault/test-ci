package ngsdiaglim.modeles.parsers;

import htsjdk.samtools.util.StringUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class VepPredictionParser {

    private final Logger logger = LogManager.getLogger(VepPredictionParser.class);
    public static final String INDEL_SYMBOL_STR = "<indel>";
    private final Map<String, Integer> col2colidx = new HashMap<>();
    private final String pipe = "\\|";
    private final char ampRegex = '&';
    private final String tag;
    private final boolean valid;

    public VepPredictionParser(final VCFHeader header) {
        this(header, getDefaultTag());
    }

    public VepPredictionParser(final VCFHeader header, String tag) {
        this.tag = tag == null ? getDefaultTag() : tag;
        final VCFInfoHeaderLine info = header == null ? null : header.getInfoHeaderLine(tag);
        if( info == null || info.getDescription() == null) {
            logger.warn("NO INFO["+tag+"] found in header. This VCF was probably NOT annotated with VEP. But it's not a problem if this tool doesn't need to access VEP Annotations.");
            this.valid = false;
            return;
        }
        String description = info.getDescription();
        final String chunck = " Format:";
        int i=description.indexOf(chunck);
        if(i == -1) {
            this.valid = false;
            logger.warn("Cannot find "+chunck+ " in "+description);
            return;
        }
        description=description.substring(i+chunck.length()).replaceAll("[ \'\\.\\(\\)]+","").trim();
        final List<String> tokens = Arrays.asList(StringUtils.splitPreserveAllTokens(description, this.pipe));
        for(i = 0; i < tokens.size(); ++i) {
            final String token= tokens.get(i);

            if(StringUtil.isBlank(token)) continue;

            if(this.col2colidx.containsKey(token)) {
                logger.warn("Column  "+token+" defined twice in "+description);
                continue;
            }
            this.col2colidx.put(token, i);
        }
        this.valid=true;
    }

    public boolean isValid() {return valid;}

    public static String getDefaultTag() {
        return "CSQ";
    }

    public List<VepPrediction> getPredictions(final VariantContext ctx)
    {
        if(!isValid() || this.col2colidx.isEmpty()) return Collections.emptyList();
        final List<? extends Object> L = ctx.getAttributeAsList(this.tag);
        ArrayList<VepPrediction> preds = new ArrayList<>(L.size());
        for(final Object o2 : L)  _predictions(preds, o2, ctx);
        return preds;
    }

    public VepPrediction parseOnePrediction(final VariantContext ctx, final Object o)
    {
        if(o==null || !isValid()) return null;

        if(!(o instanceof String)) {
            return parseOnePrediction(ctx, o.toString());
        }

        final String s = ((String) o).trim();
        final String[] tokens = s.split(this.pipe);
        return new VepPrediction(tokens,s,ctx);
    }

    private void _predictions(final List<VepPrediction> preds, final Object o, final VariantContext ctx)
    {
        final VepPrediction pred = parseOnePrediction(ctx, o);
        if(pred!=null) preds.add(pred);
    }

    public class VepPrediction {

        private final String source;
        private final String[] tokens;
        private final List<Allele> alleles;

        public VepPrediction(final String[] tokens, final String source, final VariantContext ctx) {
            this.source=source;
            this.tokens=tokens;
            this.alleles = Collections.unmodifiableList(ctx.getAlleles());

            /* special case for ALT, can be '-' */
            Integer idx_allele = VepPredictionParser.this.col2colidx.get("Allele");
            if(	idx_allele != null && idx_allele < tokens.length && tokens[idx_allele].equals("-")) {
                if(ctx.getAlternateAlleles().size() == 1) {
                    this.tokens[idx_allele] = ctx.getAlternateAlleles().get(0).getDisplayString();
                } else {
                    this.tokens[idx_allele] = INDEL_SYMBOL_STR;
                }
            }
        }
        public String getByCol(final String col) {
            if(col == null || col.isEmpty()) return null;
            final Integer idx = VepPredictionParser.this.col2colidx.get(col);
            if(idx == null || idx >= tokens.length || tokens[idx].isEmpty()) {
                return null;
            }
            return this.tokens[idx];
        }

        /** alias of getByColl */
        public String get(final String col) {
            return this.getByCol(col);
        }

        public boolean hasAttribute(final String col) {
            String s = this.get(col);
            return s != null && !s.isEmpty();
        }

        /** return the 'Allele' String or null if not found */
        public String getAlleleStr() {
            return getByCol("Allele");
        }

        /** return the 'ALLELE_NUM' String or null if not found */
        public String getAlleleNum() {
            return getByCol("ALLELE_NUM");
        }

        public Allele getAllele() {
            String s = getAlleleNum();
            if(!StringUtil.isBlank(s)) {
                int allele_num;
                try {
                    allele_num = Integer.parseInt(s);
                } catch(NumberFormatException err) {
                    return null;
                }
                if(allele_num <= 0 /* yes <= */ || allele_num >= this.alleles.size()) {
                    return null;
                }
                return alleles.get(allele_num);
            }


            s = getAlleleStr();
            if(!StringUtil.isBlank(s)) {
                Allele a = Allele.create(s, false);
                if(s.equals("<indel>")) return a;

                if(!this.alleles.contains(a)) {
                    if(!this.alleles.isEmpty() && this.alleles.get(0).isReference() && !this.alleles.get(0).isSymbolic()) {
                        /* 'cannot find allele G in [T*, TG]' */
                        a = Allele.create( this.alleles.get(0).getDisplayString()+s, false);
                    }
					/*
					if(!this.alleles.contains(a))
						{
						//LOG.warn("cannot find allele "+s+" / "+a+" in "+this.alleles);
						}
						*/
                }
                return a;
            }

            return null;
        }


        public String getExon() {
            return getByCol("EXON");
        }


        /** return -1 negative strand, 1 position strand , else 0 */
        public int getStrand() {
            final String s = getByCol("STRAND");
            if(s == null) return 0;
            if(s.equals("1")) return 1;
            if(s.equals("-1")) return -1;
            return 0;
        }


        /**  getHGNC || getSymbol */
        public String getGeneName() {
            String s = getByCol("HGNC");
            if(StringUtils.isBlank(s)) s = getSymbol();
            return s;
        }

        public String getHGNC() {
            return getByCol("HGNC");
        }


        public String getHgncId() {
            return getByCol("HGNC_ID");
        }

        public String getSymbol() {
            return getByCol("SYMBOL");
        }

        public String getRefSeq() {
            return getByCol("RefSeq");
        }

        public String getFeature() {
            return getByCol("Feature");
        }
        public String getFeatureType() {
            return getByCol("Feature_type");
        }

        public String getGene() {
            return getByCol("Gene");
        }

        public String getENSP() {
            return getByCol("ENSP");
        }

        public String getSymbolSource() {
            return getByCol("SYMBOL_SOURCE");
        }

        public Map<String,String> getMap() {
            final Map<String, String> hash = new LinkedHashMap<>();
            for(final String c : col2colidx.keySet()) {
                final int idx = col2colidx.get(c);
                if(idx >= this.tokens.length) continue;
                hash.put(c, tokens[idx]);
            }
            return hash;
        }


        public String getEnsemblGene() {
            return getByCol("Gene");
        }


        public Double getSift() {
            String s = getByCol("SIFT");
            if(s==null) return null;
            try {
                return Double.valueOf(s);
            } catch(Exception err) {
                return null;
            }
        }

        public Double getPolyphen() {
            String s = getByCol("PolyPhen");
            if(s == null) return null;
            try {
                return Double.valueOf(s);
            } catch(Exception err) {
                return null;
            }
        }

        /** return the "Consequence" String or NULL if not found */
        public String getSOTermsString() {
            return getByCol("Consequence");
        }

        /** return the "Consequence" splitted, as an array of String, empty if consequence is not found */
        public List<String> getSOTermsStrings() {
            final String EFF = getSOTermsString();
            if(EFF==null || EFF.isEmpty()) return Collections.emptyList();
            return Arrays.asList(StringUtils.splitPreserveAllTokens(EFF, VepPredictionParser.this.ampRegex));
//            return VepPredictionParser.this.ampRegex.splitAsStringList(EFF);
        }

//        /** convert the list of getConsequences() to a list of SequenceOntology Terms */
//        public Set<SequenceOntologyTree.Term> getSOTerms()
//        {
//            final List<String> effects = getSOTermsStrings();
//            if(effects.isEmpty()) return Collections.emptySet();
//            final Set<SequenceOntologyTree.Term> set=new HashSet<>(effects.size());
//            for(final String label:effects) {
//                if(label.isEmpty()) continue;
//                final SequenceOntologyTree.Term t =VepPredictionParser.this.soTree.getTermByLabel(label);
//                if(t==null) {
//                    LOG.warning("Current Sequence Ontology Tree doesn't contain "+ label);
//                }
//                else
//                {
//                    set.add(t);
//                }
//            }
//            return set;
//        }

        public Integer getPositionInCDna() {
            final String s = getByCol("cDNA_position");
            if(s == null || s.trim().isEmpty()) return null;
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return null;
            }
        }

        public Integer getPositionInCDS() {
            final String s = getByCol("CDS_position");
            if(s == null || s.trim().isEmpty()) return null;
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return null;
            }
        }


        private boolean _isEmpty(String s) {
            return s==null || s.trim().isEmpty();
        }

        /** returns some the possible ways to name a gene */
        public Set<String> getGeneKeys() {
            final Set<String> keys= new HashSet<>();
            if(!_isEmpty(this.getFeature())) {
                keys.add("VEP_FEATURE_"+this.getFeature());
            }
            if(!_isEmpty(this.getGene())) {
                keys.add("VEP_GENE_"+this.getGene());
            }
            if(!_isEmpty(this.getSymbol())) {
                keys.add("VEP_SYMBOL_"+this.getSymbol());
            }
            if(!_isEmpty(this.getRefSeq()))
            {
                keys.add("VEP_REFSEQ_"+this.getRefSeq());
            }
            return keys;
        }

        /** return the prediction encoded in the original VariantContext */
        public String getOriginalAttributeAsString() {
            return this.source;
        }





        @Override
        public String toString() {
            return this.source;
        }
    }
}
