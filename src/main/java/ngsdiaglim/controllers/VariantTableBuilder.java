package ngsdiaglim.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ngsdiaglim.App;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.comparators.SiftPredictionComparator;
import ngsdiaglim.comparators.SpliceAIComparators;
import ngsdiaglim.comparators.VariantPredictionComparator;
import ngsdiaglim.controllers.cells.variantsTableCells.*;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.controllers.ui.popupfilters.*;
import ngsdiaglim.controllers.ui.rowfactories.Theme1;
import ngsdiaglim.controllers.ui.rowfactories.Theme2;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.*;
import ngsdiaglim.modeles.analyse.ExternalVariation;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.users.UserVariantTableColumns;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.populations.GnomAD;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import ngsdiaglim.utils.ScrollBarUtil;
import ngsdiaglim.utils.TableViewUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class VariantTableBuilder {

    private final static Logger logger = LogManager.getLogger(VariantTableBuilder.class);

    private final TableView<Annotation> table;
    private final static NaturalSortComparator naturalSortComparator = new NaturalSortComparator();
    private final static VariantPredictionComparator variantPredictionComparator = new VariantPredictionComparator();
    private final static SpliceAIComparators spliceAIComparator = new SpliceAIComparators();

    private final HashMap<VariantsTableColumns, TableColumn<Annotation, ?>> columnsMap = new HashMap<>();
    private final HashMap<VariantsTableColumns, Boolean> defaultVisibleColumns = new HashMap<>();
    private final HashMap<VariantsTableColumns, Double> defaultColumnsSize = new HashMap<>();
    private final List<VariantsTableColumns> defaultColumnsOrder = new ArrayList<>();
    private final List<TableColumn<Annotation, ?>> columns = new ArrayList<>();
    private UserVariantTableColumns userVariantTableColumns;

    public VariantTableBuilder(TableView<Annotation> table) {
        this.table = table;
    }

    public void buildTable(boolean basicTable) throws SQLException {

        table.getStyleClass().add("variants-table");

        User loggedUser = App.get().getLoggedUser();

        if (!basicTable) {
            setRowFactory();
        }
        userVariantTableColumns = DAOController.getUserVariantTableColumnsDAO().getUsersVariantTableColumn(loggedUser.getId());

        initColumnsMap();
        setDefaultColumnsOrder();
        setDefaultVisibleColumns();
        setColumnsOrder();
        setDefaultColumnsSize();
        table.getColumns().setAll(columns);
        if (!basicTable) {
            Platform.runLater(() -> {
                table.getColumns().addListener((ListChangeListener<TableColumn<Annotation, ?>>) change -> {
                    try {
                        saveColumnsOrder();
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                });

                table.getVisibleLeafColumns().addListener((ListChangeListener<TableColumn<Annotation, ?>>) change -> {
                    try {
                        saveColumnsVisible();
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                });

                addColumnResizeListener();
            });
        }

        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableViewUtils.installCopyHandler(table);
        setContextMenu();

    }


    public void buildTable() throws SQLException {
        buildTable(false);
    }


    public TableColumn<Annotation, ?> getColumn(VariantsTableColumns variantsTableColumns) {
        return columnsMap.get(variantsTableColumns);
    }

    public List<VariantsTableColumns> getDefaultColumnsOrder() {return defaultColumnsOrder;}

    public List<TableColumn<Annotation, ?>> getColumns() {return columns;}

    private void initColumnsMap() {

        columnsMap.clear();

        TableColumn<Annotation, Integer> indexColumn = new TableColumn<>(VariantsTableColumns.INDEX.getName());
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory( data -> new ReadOnlyObjectWrapper(table.getItems().indexOf(data.getValue())+ 1));
        indexColumn.getStyleClass().add("index-column");
        columnsMap.put(VariantsTableColumns.INDEX, indexColumn);

        TableColumn<Annotation, Hotspot> hotspotColumn = new TableColumn<>(VariantsTableColumns.HOTSPOT.getName());
        hotspotColumn.setCellValueFactory(data -> data.getValue().getVariant().hotspotProperty());
        hotspotColumn.setCellFactory(data -> new HotspotTableCell());
        columnsMap.put(VariantsTableColumns.HOTSPOT, hotspotColumn);

        FilterTableColumn<Annotation, String> contigColumn = new FilterTableColumn<>(VariantsTableColumns.CONTIG.getName());
        contigColumn.setCellValueFactory(data -> {
            if (data.getValue().getGenome().equals(Genome.GRCh38)) {
                return data.getValue().getVariant().getGrch38PositionVariant().contigProperty();
            } else {
                return data.getValue().getVariant().getGrch37PositionVariant().contigProperty();
            }
        });
        contigColumn.setPopupFilter(new ContigPopupFilter(contigColumn));
        contigColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.CONTIG, contigColumn);

        FilterTableColumn<Annotation, Number> posColumn = new FilterTableColumn<>(VariantsTableColumns.POSITION.getName());
        posColumn.setCellValueFactory(data -> {
            if (data.getValue().getGenome().equals(Genome.GRCh38)) {
                return data.getValue().getVariant().getGrch38PositionVariant().startProperty();
            } else {
                return data.getValue().getVariant().getGrch37PositionVariant().startProperty();
            }
        });
//        posColumn.setPopupFilter(new PositionPopupFilter(posColumn));
        columnsMap.put(VariantsTableColumns.POSITION, posColumn);

        FilterTableColumn<Annotation, String> refColumn = new FilterTableColumn<>(VariantsTableColumns.REF.getName());
        refColumn.setCellValueFactory(data -> {
            if (data.getValue().getGenome().equals(Genome.GRCh38)) {
                return data.getValue().getVariant().getGrch38PositionVariant().refProperty();
            } else {
                return data.getValue().getVariant().getGrch37PositionVariant().refProperty();
            }
        });
//        refColumn.setPopupFilter(new RefPopUpFilter(refColumn));
        columnsMap.put(VariantsTableColumns.REF, refColumn);

        FilterTableColumn<Annotation, String> altColumn = new FilterTableColumn<>(VariantsTableColumns.ALT.getName());
        altColumn.setCellValueFactory(data -> {
            if (data.getValue().getGenome().equals(Genome.GRCh38)) {
                return data.getValue().getVariant().getGrch38PositionVariant().altProperty();
            } else {
                return data.getValue().getVariant().getGrch37PositionVariant().altProperty();
            }
        });
//        altColumn.setPopupFilter(new AltPopupFilter(altColumn));
        columnsMap.put(VariantsTableColumns.ALT, altColumn);

        FilterTableColumn<Annotation, ACMG> acmgColumn2 = new FilterTableColumn<>(VariantsTableColumns.ACMG.getName());
        acmgColumn2.setCellValueFactory(data -> data.getValue().getVariant().acmgProperty());
        acmgColumn2.setCellFactory(data -> new ACMGTableCell());
        acmgColumn2.setPopupFilter(new ACMGPopupFilter(acmgColumn2));
        columnsMap.put(VariantsTableColumns.ACMG, acmgColumn2);

        FilterTableColumn<Annotation, Number> occurrenceColumn = new FilterTableColumn<>(VariantsTableColumns.OCCURENCE.getName());
        occurrenceColumn.setCellValueFactory(data -> data.getValue().getVariant().occurrenceProperty());
        occurrenceColumn.setPopupFilter(new OccurencePopupFilter(occurrenceColumn));
        columnsMap.put(VariantsTableColumns.OCCURENCE, occurrenceColumn);

        FilterTableColumn<Annotation, Number> occurrenceRunColumn = new FilterTableColumn<>(VariantsTableColumns.OCCURENCE_IN_RUN.getName());
        occurrenceRunColumn.setCellValueFactory(data -> data.getValue().getVariant().occurrenceInRunProperty());
        occurrenceRunColumn.setPopupFilter(new RunOccurencePopupFilter(occurrenceRunColumn));
        columnsMap.put(VariantsTableColumns.OCCURENCE_IN_RUN, occurrenceRunColumn);

        FilterTableColumn<Annotation, Number> depthColumn = new FilterTableColumn<>(VariantsTableColumns.DEPTH.getName());
        depthColumn.setCellValueFactory(data -> data.getValue().depthProperty());
        depthColumn.setPopupFilter(new DepthPopupFilter(depthColumn));
        columnsMap.put(VariantsTableColumns.DEPTH, depthColumn);

        FilterTableColumn<Annotation, Number> vafColumn = new FilterTableColumn<>(VariantsTableColumns.VAF.getName());
        vafColumn.setCellValueFactory(data -> data.getValue().vafProperty());
        vafColumn.setPopupFilter(new VafPopupFilter(vafColumn));
        columnsMap.put(VariantsTableColumns.VAF, vafColumn);

        TableColumn<Annotation, String> alleleDepthColumn = new TableColumn<>(VariantsTableColumns.ALLELE_DEPTH.getName());
        alleleDepthColumn.setCellValueFactory(data -> data.getValue().allelesDepthProperty());
        columnsMap.put(VariantsTableColumns.ALLELE_DEPTH, alleleDepthColumn);

        TableColumn<Annotation, String> alleleStrandDepthColumn = new TableColumn<>(VariantsTableColumns.ALLELE_STRAND_DEPTH.getName());
        alleleStrandDepthColumn.setCellValueFactory(data -> data.getValue().allelesStrandDepthProperty());
        columnsMap.put(VariantsTableColumns.ALLELE_STRAND_DEPTH, alleleStrandDepthColumn);

        FilterTableColumn<Annotation, String> genesColumn = new FilterTableColumn<>(VariantsTableColumns.GENES.getName());
        genesColumn.setCellValueFactory(data -> data.getValue().geneNamesProperty());
        genesColumn.setPopupFilter(new GenesPopupFilter(genesColumn));
        genesColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.GENES, genesColumn);

        FilterTableColumn<Annotation, String> geneColumn = new FilterTableColumn<>(VariantsTableColumns.GENE.getName());
        geneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTranscriptConsequence().getGeneName()));
        geneColumn.setPopupFilter(new GenePopupFilter(geneColumn));
        geneColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.GENE, geneColumn);

        TableColumn<Annotation, String> proteinColumn = new TableColumn<>(VariantsTableColumns.PROTEIN.getName());
        proteinColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().proteinNameProperty());
        proteinColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.PROTEIN, proteinColumn);

        TableColumn<Annotation, Transcript> transcriptColumn = new TableColumn<>(VariantsTableColumns.TRANSCRIPT.getName());
        transcriptColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().transcriptProperty());
        transcriptColumn.setCellFactory(data -> new TranscriptTableCell());
        proteinColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.TRANSCRIPT, transcriptColumn);

        TableColumn<Annotation, String> exonColumn = new TableColumn<>(VariantsTableColumns.EXON.getName());
        exonColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().exonProperty());
        columnsMap.put(VariantsTableColumns.EXON, exonColumn);

        TableColumn<Annotation, String> intronColumn = new TableColumn<>(VariantsTableColumns.INTRON.getName());
        intronColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().intronProperty());
        columnsMap.put(VariantsTableColumns.INTRON, intronColumn);

        FilterTableColumn<Annotation, String> hgvscColumn = new FilterTableColumn<>(VariantsTableColumns.HGVSC.getName());
        hgvscColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().hgvscProperty());
        hgvscColumn.setPopupFilter(new HgvscPopupFilter(hgvscColumn));
        hgvscColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.HGVSC, hgvscColumn);

        FilterTableColumn<Annotation, String> hgvspColumn = new FilterTableColumn<>(VariantsTableColumns.HGVSP.getName());
        hgvspColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().hgvspProperty());
        hgvspColumn.setPopupFilter(new HgvspPopupFilter(hgvspColumn));
        hgvspColumn.setComparator(naturalSortComparator);
        columnsMap.put(VariantsTableColumns.HGVSP, hgvspColumn);

        FilterTableColumn<Annotation, EnsemblConsequence> consequenceColumn = new FilterTableColumn<>(VariantsTableColumns.CONSEQUENCE.getName());
        consequenceColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().consequenceProperty());
        consequenceColumn.setCellFactory(data -> new EnsemblConsequenceTableCell<>());
        consequenceColumn.setPopupFilter(new ConsequencePopupFilter(consequenceColumn));
        columnsMap.put(VariantsTableColumns.CONSEQUENCE, consequenceColumn);

        FilterTableColumn<Annotation, String> clinvarSignColumn = new FilterTableColumn<>(VariantsTableColumns.CLIVAR_SIGN.getName());
        clinvarSignColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().clinvarSignProperty());
        clinvarSignColumn.setPopupFilter(new ClinvarSignPopupFilter(clinvarSignColumn));
        columnsMap.put(VariantsTableColumns.CLIVAR_SIGN, clinvarSignColumn);

        FilterTableColumn<Annotation, VariantPrediction> polyphen2HvarColumn = new FilterTableColumn<>(VariantsTableColumns.POLYPHEN2_HVAR.getName());
        polyphen2HvarColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().polyphen2HvarPredProperty());
        polyphen2HvarColumn.setCellFactory(data -> new VariantPredictionTableCell());
        polyphen2HvarColumn.setComparator(variantPredictionComparator);
        polyphen2HvarColumn.setPopupFilter(new PolyphenHvarPopupFilter(polyphen2HvarColumn));
        columnsMap.put(VariantsTableColumns.POLYPHEN2_HVAR, polyphen2HvarColumn);

        FilterTableColumn<Annotation, VariantPrediction> polyphen2HdivColumn = new FilterTableColumn<>(VariantsTableColumns.POLYPHEN2_HDIV.getName());
        polyphen2HdivColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().polyphen2HdivPredProperty());
        polyphen2HdivColumn.setCellFactory(data -> new VariantPredictionTableCell());
        polyphen2HdivColumn.setComparator(variantPredictionComparator);
        polyphen2HdivColumn.setPopupFilter(new PolyphenHdivPopupFilter(polyphen2HdivColumn));
        columnsMap.put(VariantsTableColumns.POLYPHEN2_HDIV, polyphen2HdivColumn);

        FilterTableColumn<Annotation, VariantPrediction> siftColumn = new FilterTableColumn<>(VariantsTableColumns.SIFT.getName());
        siftColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().siftPredProperty());
        siftColumn.setCellFactory(data -> new VariantPredictionTableCell());
        siftColumn.setComparator(new SiftPredictionComparator());
        siftColumn.setPopupFilter(new SiftPopupFilter(siftColumn));
        columnsMap.put(VariantsTableColumns.SIFT, siftColumn);

        FilterTableColumn<Annotation, VariantPrediction> caddColumn = new FilterTableColumn<>(VariantsTableColumns.CADD.getName());
        caddColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().caddPhredPredProperty());
        caddColumn.setCellFactory(data -> new VariantPredictionTableCell());
        caddColumn.setComparator(variantPredictionComparator);
        caddColumn.setPopupFilter(new CaddPopupFilter(caddColumn));
        columnsMap.put(VariantsTableColumns.CADD, caddColumn);

        FilterTableColumn<Annotation, VariantPrediction> gerpColumn = new FilterTableColumn<>(VariantsTableColumns.GERP.getName());
        gerpColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().gerpPredProperty());
        gerpColumn.setCellFactory(data -> new VariantPredictionTableCell());
        gerpColumn.setComparator(variantPredictionComparator);
        gerpColumn.setPopupFilter(new GerpPopupFilter(gerpColumn));
        columnsMap.put(VariantsTableColumns.GERP, gerpColumn);

        FilterTableColumn<Annotation, VariantPrediction> revelColumn = new FilterTableColumn<>(VariantsTableColumns.REVEL.getName());
        revelColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().revelPredProperty());
        revelColumn.setCellFactory(data -> new VariantPredictionTableCell());
        revelColumn.setComparator(variantPredictionComparator);
        revelColumn.setPopupFilter(new RevelPopupFilter(revelColumn));
        columnsMap.put(VariantsTableColumns.REVEL, revelColumn);

        FilterTableColumn<Annotation, VariantPrediction> mvpColumn = new FilterTableColumn<>(VariantsTableColumns.MVP.getName());
        mvpColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().mvpPredProperty());
        mvpColumn.setCellFactory(data -> new VariantPredictionTableCell());
        mvpColumn.setComparator(variantPredictionComparator);
        mvpColumn.setPopupFilter(new MVPPopupFilter(mvpColumn));
        columnsMap.put(VariantsTableColumns.MVP, mvpColumn);

        TableColumn<Annotation, SpliceAIPredictions> spliceAIColumn = new TableColumn<>(VariantsTableColumns.SPLICE_AI.getName());
        spliceAIColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().spliceAIPredsProperty());
        spliceAIColumn.setCellFactory(data -> new SpliceAIPredsTableCell());
        spliceAIColumn.setComparator(spliceAIComparator);
        columnsMap.put(VariantsTableColumns.SPLICE_AI, spliceAIColumn);

        FilterTableColumn<Annotation, GnomadPopulationFreq> gnomadMaxColumn = new FilterTableColumn<>(VariantsTableColumns.GNOMAD_MAX.getName());
        gnomadMaxColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME)));
        gnomadMaxColumn.setCellFactory(data -> new PopulationFrequencyTableCell());
        gnomadMaxColumn.setPopupFilter(new GnomadPopupFilter(gnomadMaxColumn));
        columnsMap.put(VariantsTableColumns.GNOMAD_MAX, gnomadMaxColumn);

        TableColumn<Annotation, List<ExternalVariation>> externalVariationsColumn = new TableColumn<>(VariantsTableColumns.EXTERNAL_VARIATIONS.getName());
        externalVariationsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTranscriptConsequence().getExternalVariations()));
        externalVariationsColumn.setCellFactory(data -> new ExternalVariationsTableCell<>());
        columnsMap.put(VariantsTableColumns.EXTERNAL_VARIATIONS, externalVariationsColumn);
    }


    public void setColumnsHeaderEvent() {
        for (Node n : table.lookupAll("TableColumnHeader")) {
            if (n instanceof TableColumnHeader) {
                TableColumnHeader tch = (TableColumnHeader) n;
                tch.addEventFilter(MouseEvent.ANY, e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        TableColumnBase<?, ?> columnBase = tch.getTableColumn();
                        if (columnBase instanceof FilterTableColumn) {
                            FilterTableColumn<Annotation, ?> filterTableColumn = (FilterTableColumn<Annotation, ?>) columnBase;
                            filterTableColumn.showPopupFilter();
                            e.consume();
                        }
                    }
                });
            }
        }
    }

    private void setDefaultColumnsOrder() {
        defaultColumnsOrder.add(VariantsTableColumns.INDEX);
        defaultColumnsOrder.add(VariantsTableColumns.HOTSPOT);
        defaultColumnsOrder.add(VariantsTableColumns.CONTIG);
        defaultColumnsOrder.add(VariantsTableColumns.POSITION);
        defaultColumnsOrder.add(VariantsTableColumns.REF);
        defaultColumnsOrder.add(VariantsTableColumns.ALT);
        defaultColumnsOrder.add(VariantsTableColumns.GENES);
        defaultColumnsOrder.add(VariantsTableColumns.TRANSCRIPT);
        defaultColumnsOrder.add(VariantsTableColumns.GENE);
        defaultColumnsOrder.add(VariantsTableColumns.CONSEQUENCE);
        defaultColumnsOrder.add(VariantsTableColumns.HGVSC);
        defaultColumnsOrder.add(VariantsTableColumns.HGVSP);
        defaultColumnsOrder.add(VariantsTableColumns.ACMG);
        defaultColumnsOrder.add(VariantsTableColumns.OCCURENCE);
        defaultColumnsOrder.add(VariantsTableColumns.OCCURENCE_IN_RUN);
        defaultColumnsOrder.add(VariantsTableColumns.EXTERNAL_VARIATIONS);
        defaultColumnsOrder.add(VariantsTableColumns.DEPTH);
        defaultColumnsOrder.add(VariantsTableColumns.VAF);
        defaultColumnsOrder.add(VariantsTableColumns.ALLELE_DEPTH);
        defaultColumnsOrder.add(VariantsTableColumns.ALLELE_STRAND_DEPTH);
        defaultColumnsOrder.add(VariantsTableColumns.EXON);
        defaultColumnsOrder.add(VariantsTableColumns.INTRON);
        defaultColumnsOrder.add(VariantsTableColumns.PROTEIN);
        defaultColumnsOrder.add(VariantsTableColumns.CLIVAR_SIGN);
        defaultColumnsOrder.add(VariantsTableColumns.GNOMAD_MAX);
        defaultColumnsOrder.add(VariantsTableColumns.POLYPHEN2_HVAR);
        defaultColumnsOrder.add(VariantsTableColumns.POLYPHEN2_HDIV);
        defaultColumnsOrder.add(VariantsTableColumns.SIFT);
        defaultColumnsOrder.add(VariantsTableColumns.CADD);
        defaultColumnsOrder.add(VariantsTableColumns.GERP);
        defaultColumnsOrder.add(VariantsTableColumns.REVEL);
        defaultColumnsOrder.add(VariantsTableColumns.MVP);
        defaultColumnsOrder.add(VariantsTableColumns.SPLICE_AI);
    }


    private void setColumnsOrder() {
        columns.clear();
        if (userVariantTableColumns == null) {
            defaultColumnsOrder.forEach(c -> {
                if (columnsMap.containsKey(c)) {
                    columns.add(columnsMap.get(c));
                }
            });
        } else {
            userVariantTableColumns.getColumnsOrder().forEach(c -> {
                if (columnsMap.containsKey(c)) {
                    columns.add(columnsMap.get(c));
                }
            });
            defaultColumnsOrder.forEach(c -> {
                if (columnsMap.containsKey(c) && !columns.contains(columnsMap.get(c))) {
                    columns.add(columnsMap.get(c));
                }
            });
        }
    }

    private void setDefaultVisibleColumns() throws SQLException {
        defaultVisibleColumns.put(VariantsTableColumns.INDEX, true);
        defaultVisibleColumns.put(VariantsTableColumns.HOTSPOT, true);
        defaultVisibleColumns.put(VariantsTableColumns.CONTIG, true);
        defaultVisibleColumns.put(VariantsTableColumns.POSITION, true);
        defaultVisibleColumns.put(VariantsTableColumns.REF, true);
        defaultVisibleColumns.put(VariantsTableColumns.ALT, true);
        defaultVisibleColumns.put(VariantsTableColumns.ACMG, true);
        defaultVisibleColumns.put(VariantsTableColumns.OCCURENCE, true);
        defaultVisibleColumns.put(VariantsTableColumns.OCCURENCE_IN_RUN, true);
        defaultVisibleColumns.put(VariantsTableColumns.DEPTH, true);
        defaultVisibleColumns.put(VariantsTableColumns.VAF, true);
        defaultVisibleColumns.put(VariantsTableColumns.ALLELE_DEPTH, true);
        defaultVisibleColumns.put(VariantsTableColumns.ALLELE_STRAND_DEPTH, true);
        defaultVisibleColumns.put(VariantsTableColumns.GENES, true);
        defaultVisibleColumns.put(VariantsTableColumns.GENE, true);
        defaultVisibleColumns.put(VariantsTableColumns.PROTEIN, false);
        defaultVisibleColumns.put(VariantsTableColumns.TRANSCRIPT, true);
        defaultVisibleColumns.put(VariantsTableColumns.EXON, false);
        defaultVisibleColumns.put(VariantsTableColumns.INTRON, false);
        defaultVisibleColumns.put(VariantsTableColumns.HGVSC, true);
        defaultVisibleColumns.put(VariantsTableColumns.HGVSP, true);
        defaultVisibleColumns.put(VariantsTableColumns.CONSEQUENCE, true);
        defaultVisibleColumns.put(VariantsTableColumns.CLIVAR_SIGN, true);
        defaultVisibleColumns.put(VariantsTableColumns.POLYPHEN2_HDIV, true);
        defaultVisibleColumns.put(VariantsTableColumns.POLYPHEN2_HVAR, true);
        defaultVisibleColumns.put(VariantsTableColumns.SIFT, true);
        defaultVisibleColumns.put(VariantsTableColumns.CADD, true);
        defaultVisibleColumns.put(VariantsTableColumns.GERP, true);
        defaultVisibleColumns.put(VariantsTableColumns.REVEL, true);
        defaultVisibleColumns.put(VariantsTableColumns.MVP, true);
        defaultVisibleColumns.put(VariantsTableColumns.SPLICE_AI, true);
        defaultVisibleColumns.put(VariantsTableColumns.GNOMAD_MAX, true);
        defaultVisibleColumns.put(VariantsTableColumns.EXTERNAL_VARIATIONS, true);

        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns != null) {
            userVariantTableColumns.getColumnsVisibility().forEach((k, v) -> {
                if (defaultVisibleColumns.containsKey(k)) {
                    defaultVisibleColumns.put(k, v);
                }
            });
        }

        defaultVisibleColumns.forEach((k, v) -> {
            if (columnsMap.containsKey(k)) {
                columnsMap.get(k).setVisible(v);
            }
        });
    }

    private void setDefaultColumnsSize() throws SQLException {
        double v = 100;
        defaultColumnsSize.put(VariantsTableColumns.INDEX, 30d);
        defaultColumnsSize.put(VariantsTableColumns.HOTSPOT, v);
        defaultColumnsSize.put(VariantsTableColumns.CONTIG, v);
        defaultColumnsSize.put(VariantsTableColumns.POSITION, v);
        defaultColumnsSize.put(VariantsTableColumns.REF, v);
        defaultColumnsSize.put(VariantsTableColumns.ALT, v);
        defaultColumnsSize.put(VariantsTableColumns.ACMG, v);
        defaultColumnsSize.put(VariantsTableColumns.OCCURENCE, v);
        defaultColumnsSize.put(VariantsTableColumns.OCCURENCE_IN_RUN, v);
        defaultColumnsSize.put(VariantsTableColumns.DEPTH, v);
        defaultColumnsSize.put(VariantsTableColumns.VAF, v);
        defaultColumnsSize.put(VariantsTableColumns.ALLELE_DEPTH, v);
        defaultColumnsSize.put(VariantsTableColumns.ALLELE_STRAND_DEPTH, v);
        defaultColumnsSize.put(VariantsTableColumns.GENES, v);
        defaultColumnsSize.put(VariantsTableColumns.GENE, v);
        defaultColumnsSize.put(VariantsTableColumns.PROTEIN, v);
        defaultColumnsSize.put(VariantsTableColumns.TRANSCRIPT, v);
        defaultColumnsSize.put(VariantsTableColumns.EXON, v);
        defaultColumnsSize.put(VariantsTableColumns.INTRON, v);
        defaultColumnsSize.put(VariantsTableColumns.HGVSC, v);
        defaultColumnsSize.put(VariantsTableColumns.HGVSP, v);
        defaultColumnsSize.put(VariantsTableColumns.CONSEQUENCE, v);
        defaultColumnsSize.put(VariantsTableColumns.CLIVAR_SIGN, v);
        defaultColumnsSize.put(VariantsTableColumns.POLYPHEN2_HDIV, v);
        defaultColumnsSize.put(VariantsTableColumns.POLYPHEN2_HVAR, v);
        defaultColumnsSize.put(VariantsTableColumns.SIFT, v);
        defaultColumnsSize.put(VariantsTableColumns.CADD, v);
        defaultColumnsSize.put(VariantsTableColumns.GERP, v);
        defaultColumnsSize.put(VariantsTableColumns.REVEL, v);
        defaultColumnsSize.put(VariantsTableColumns.MVP, v);
        defaultColumnsSize.put(VariantsTableColumns.SPLICE_AI, v);
        defaultColumnsSize.put(VariantsTableColumns.GNOMAD_MAX, v);
        defaultColumnsSize.put(VariantsTableColumns.EXTERNAL_VARIATIONS, v);

        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns != null) {
            userVariantTableColumns.getColumnsSize().forEach((k, v2) -> {
                if (defaultColumnsSize.containsKey(k)) {
                    defaultColumnsSize.put(k, v2);
                }
            });
        }

        defaultColumnsSize.forEach((k, v2) -> {
            if (columnsMap.containsKey(k)) {
                columnsMap.get(k).setPrefWidth(v2);
            }
        });
    }

    private void addColumnResizeListener() {

        final ChangeListener<Number> listener = new ChangeListener<>() {
            final Timer timer = new Timer(); // uses a timer to call your resize method
            TimerTask task = null; // task to execute after defined delay
            final long delayTime = 500; // delay that has to pass in order to consider an operation done

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
                if (oldValue != null && oldValue.doubleValue() > 0) {
                    if (task != null) { // there was already a task scheduled from the previous operation ...
                        task.cancel(); // cancel it, we have a new size to consider
                    }
                    task = new TimerTask() // create new task that calls your resize operation
                    {
                        @Override
                        public void run() {
                            // here you can place your resize code
                            try {
                                saveColumnsSize();
                            } catch (SQLException e) {
                                logger.error(e);
                            }
                        }
                    };
                    // schedule new task
                    timer.schedule(task, delayTime);
                }
            }
        };


        table.getColumns().forEach(c -> c.widthProperty().addListener(listener));
    }


    private void saveColumnsOrder() throws SQLException {
        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns == null) {
            userVariantTableColumns = new UserVariantTableColumns();
            userVariantTableColumns.setColumnsOrderFromTable(table);
            DAOController.getUserVariantTableColumnsDAO().addUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        } else {
            userVariantTableColumns.setColumnsOrderFromTable(table);
            DAOController.getUserVariantTableColumnsDAO().updateUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        }
    }


    private void saveColumnsVisible() throws SQLException {
        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns == null) {
            userVariantTableColumns = new UserVariantTableColumns();
            userVariantTableColumns.setVisibleColumnsFromTable(table);
            DAOController.getUserVariantTableColumnsDAO().addUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        } else {
            userVariantTableColumns.setVisibleColumnsFromTable(table);
            DAOController.getUserVariantTableColumnsDAO().updateUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        }
    }


    private void saveColumnsSize() throws SQLException {
        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns == null) {
            userVariantTableColumns = new UserVariantTableColumns();
            userVariantTableColumns.setColumnsSizeFromTable(table);
            DAOController.getUserVariantTableColumnsDAO().addUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        } else {
            userVariantTableColumns.setColumnsSizeFromTable(table);
            DAOController.getUserVariantTableColumnsDAO().updateUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        }
    }


    public void setRowFactory() {
        // set table row factory
        String themeName = App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_TABLE_THEME);
        if (themeName != null) {
            try {
                VariantsTableTheme theme = VariantsTableTheme.valueOf(themeName);
                if (theme.equals(VariantsTableTheme.THEME1)) {
//                    System.out.println(table.getRowFactory().);
//                    if (!(table.getRowFactory() instanceof Theme1)) {
                        table.setRowFactory(row -> new Theme1());
//                        System.out.println("set theme1");
//                    } else {
//                        System.out.println("theme 1 deja installe");
//                    }
                } else {
//                    if (!(table.getRowFactory() instanceof Theme1)) {
                        table.setRowFactory(row -> new Theme2());
//                        System.out.println("set theme2");
//                    } else {
//                        System.out.println("theme 2 deja installe");
//                    }
                }
            } catch (Exception e) {
                logger.error(e);
                table.setRowFactory(row -> new Theme2());
            }
        } else {
            table.setRowFactory(row -> new Theme2());
        }
        table.refresh();
    }


    public void setContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem copyMi = new MenuItem(App.getBundle().getString("analysisview.table.contextmenu.copy"));
        copyMi.setOnAction(e -> TableViewUtils.copySelectionToClipboard(table));
        menu.getItems().add(copyMi);
        table.setContextMenu(menu);
    }

    public void clear() {
        table.setOnKeyPressed(null);
        if (table.getContextMenu() != null) {
            table.getContextMenu().getItems().forEach(e -> e.setOnAction(null));
        }
    }
}
