package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.TabixGetter;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.SearchVariantResult;
import ngsdiaglim.modeles.variants.Variant;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VariantAnalysisDAO extends DAO {

    public void insertVariantAnalysis(long variant_id, long analysis_id) throws SQLException {
        final String sql = "INSERT INTO variantAnalyses (variant_id, analysis_id) VALUES(?, ?);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, variant_id);
            stm.setLong(++i, analysis_id);
            stm.executeUpdate();
        }
    }


    public void removeVariantAnalysis(long analysis_id) throws SQLException {
        final String sql = "DELETE FROM variantAnalyses WHERE analysis_id=?;";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, analysis_id);
            stm.executeUpdate();
        }
    }


    public int countOccurrence(long variant_id) throws SQLException {
        final String sql = "SELECT COUNT(DISTINCT analysis_id) AS count FROM variantAnalyses WHERE variant_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, variant_id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0;
            }
        }
    }


    /**
     *
     * @return the number of times the variant has been seen.
     */
    public List<Long> getAnalysesWithVariantInRun(long variant_id, Object[] analysisIds) throws SQLException {
        List<Long> analysesId = new ArrayList<>();
        final String sql = "SELECT analysis_id FROM variantAnalyses WHERE variant_id=? AND ARRAY_CONTAINS(?, analysis_id);";
        try (Connection connection = getConnection(); connection; PreparedStatement stm = connection.prepareStatement(sql)) {
            Array array = connection.createArrayOf("Int", analysisIds);
            stm.setLong(1, variant_id);
            stm.setArray(2, array);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                analysesId.add(rs.getLong("analysis_id"));
            }
            return analysesId;
        }
    }


    /**
     *
     * @return the number of times the variant has been seen.
     */
    public int countRunOccurrence(long variant_id, Object[] analysisIds) throws SQLException {
        Connection connection = getConnection();
        final String sql = "SELECT COUNT(DISTINCT analysis_id) AS count FROM variantAnalyses WHERE variant_id=? AND ARRAY_CONTAINS(?, analysis_id);";

        try (connection; PreparedStatement stm = connection.prepareStatement(sql)) {
            Array array = connection.createArrayOf("Int", analysisIds);
            stm.setLong(1, variant_id);
            stm.setArray(2, array);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }


    public ObservableList<SearchVariantResult> getVariants(Annotation annotation) throws Exception {
        ObservableList<SearchVariantResult> results = FXCollections.observableArrayList();
        final String sql = "SELECT A.id AS aId, A.name AS aName, A.vcf_path as aVcf_path," +
                " A.creation_datetime AS aCreation_datetime, A.sample_name AS aSample_name," +
                " R.id as rId, R.name AS rName, P.genome AS pGenome" +
                " FROM variantAnalyses AS VA JOIN Analysis AS A JOIN RUNS AS R JOIN AnalysisParameters AS P" +
                " WHERE VA.variant_id=? AND VA.analysis_id = A.id AND A.run_id=R.id AND A.AnalysisParameters_id=P.id ORDER BY A.creation_datetime DESC LIMIT 100;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, annotation.getVariant().getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Genome genome = Genome.valueOf(rs.getString("pGenome"));
                SearchVariantResult r = new SearchVariantResult(
                        rs.getLong("aID"),
                        rs.getLong("rId"),
                        rs.getString("aName"),
                        rs.getString("rName"),
                        rs.getString("aSample_name"),
                        rs.getTimestamp("aCreation_datetime").toLocalDateTime(),
                        new File(rs.getString("aVcf_path"))
                );
                TabixGetter tabixGetter = new TabixGetter(null, r.getVcfFile());
                List<Annotation> annotations = tabixGetter.getVariant(genome, annotation);
                if (!annotations.isEmpty()) {
                    r.setDepth(annotations.get(0).getDepth());
                    r.setVaf(annotations.get(0).getVaf());
                }
                results.add(r);
            }
        }
        return results;
    }


    public ObservableList<SearchVariantResult> getVariants(Annotation annotation, List<Long> analyses_id) throws Exception {

        Object[] analyses = analyses_id.toArray(new Object[0]);
//        final String sql = "SELECT COUNT(DISTINCT analysis_id) AS count FROM variantAnalyses WHERE variant_id=? AND ARRAY_CONTAINS(?, analysis_id);";

        ObservableList<SearchVariantResult> results = FXCollections.observableArrayList();
        final String sql = "SELECT A.id AS aId, A.name AS aName, A.vcf_path as aVcf_path," +
                " A.creation_datetime AS aCreation_datetime, A.sample_name AS aSample_name," +
                " R.id as rId, R.name AS rName, P.genome AS pGenome" +
                " FROM variantAnalyses AS VA JOIN Analysis AS A JOIN RUNS AS R JOIN AnalysisParameters AS P" +
                " WHERE ARRAY_CONTAINS(?, analysis_id) AND VA.variant_id=? AND VA.analysis_id = A.id AND A.run_id=R.id AND A.AnalysisParameters_id=P.id ORDER BY A.creation_datetime DESC LIMIT 100;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setArray(++i, connection.createArrayOf("Int", analyses));
            stm.setLong(++i, annotation.getVariant().getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Genome genome = Genome.valueOf(rs.getString("pGenome"));
                SearchVariantResult r = new SearchVariantResult(
                        rs.getLong("aID"),
                        rs.getLong("rId"),
                        rs.getString("aName"),
                        rs.getString("rName"),
                        rs.getString("aSample_name"),
                        rs.getTimestamp("aCreation_datetime").toLocalDateTime(),
                        new File(rs.getString("aVcf_path"))
                );
                TabixGetter tabixGetter = new TabixGetter(null, r.getVcfFile());
                List<Annotation> annotations = tabixGetter.getVariant(genome, annotation);
                if (!annotations.isEmpty()) {
                    r.setDepth(annotations.get(0).getDepth());
                    r.setVaf(annotations.get(0).getVaf());
                }
                results.add(r);
            }
        }
        return results;
    }

    public boolean hasVariant(long analysis_id, long variant_id) throws SQLException {
        final String sql = "SELECT variant_id AS p FROM variantAnalyses AS v WHERE v.analysis_id=? AND v.variant_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, analysis_id);
            stm.setLong(++i, variant_id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }
}
