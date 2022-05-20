package ngsdiaglim.database.dao;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.variants.GenomicVariant;
import ngsdiaglim.modeles.variants.Variant;

import java.sql.*;

public class VariantsDAO extends DAO {

    public long addVariant(String contig_grch37, int start_grch37, int end_grch37, String ref_grch37, String alt_grch37,
                           String contig_grch38, int start_grch38, int end_grch38, String ref_grch38, String alt_grch38) throws SQLException {
        final String sql = "INSERT INTO variants (contig_grch37, start_grch37, end_grch37, ref_grch37, alt_grch37, " +
                "contig_grch38, start_grch38, end_grch38, ref_grch38, alt_grch38) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, contig_grch37);
            stm.setInt(++i, start_grch37);
            stm.setInt(++i, end_grch37);
            stm.setString(++i, ref_grch37);
            stm.setString(++i, alt_grch37);
            stm.setString(++i, contig_grch38);
            stm.setInt(++i, start_grch38);
            stm.setInt(++i, end_grch38);
            stm.setString(++i, ref_grch38);
            stm.setString(++i, alt_grch38);
            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                else {
                    return -1;
                }
            }
        }
    }

    /**
     * Get variant from this posittion and genotype
     * @param contig Contig of the Variant
     * @param start Position of the variant
     * @param ref Reference allele
     * @param alt Alternative Allele
     * @return The Variant matches with the params
     */
    public Variant getVariant(Genome genome, String contig, int start, String ref, String alt) throws SQLException {
        String sql = "SELECT id, contig_grch37, start_grch37, end_grch37, ref_grch37, alt_grch37," +
                " contig_grch38, start_grch38, end_grch38, ref_grch38, alt_grch38," +
                " pathogenicity_value, pathogenicity_confirmed, false_positive FROM variants WHERE ";

        if (genome.equals(Genome.GRCh38)) {
            sql += "contig_grch38=? AND start_grch38=? AND ref_grch38=? AND alt_grch38=?;";
        } else {
            sql += "contig_grch37=? AND start_grch37=? AND ref_grch37=? AND alt_grch37=?;";
        }

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, contig);
            stm.setInt(2, start);
            stm.setString(3, ref);
            stm.setString(4, alt);
            ResultSet rs = stm.executeQuery();
            Variant variant = null;
            if (rs.next()) {
                GenomicVariant grch37Variant = new GenomicVariant(
                        rs.getString("contig_grch37"),
                        rs.getInt("start_grch37"),
                        rs.getInt("end_grch37"),
                        rs.getString("ref_grch37"),
                        rs.getString("alt_grch37")
                );
                GenomicVariant grch38Variant = new GenomicVariant(
                        rs.getString("contig_grch38"),
                        rs.getInt("start_grch38"),
                        rs.getInt("end_grch38"),
                        rs.getString("ref_grch38"),
                        rs.getString("alt_grch38")
                );
                variant = new Variant(
                        rs.getInt("id"),
                        grch37Variant,
                        grch38Variant
                );
                variant.setAcmg(ACMG.getFromPathogenicityValue(rs.getInt("pathogenicity_value")));
                variant.setPathogenicityConfirmed(rs.getBoolean("pathogenicity_confirmed"));
                variant.setFalsePositive(rs.getBoolean("false_positive"));
                variant.setOccurrence(DAOController.getVariantAnalysisDAO().countOccurrence(variant.getId()));
            }
            return variant;
        }
    }

    /**
     * Get variant from his id
     */
    public Variant getVariant(Long id) throws SQLException {
        final String sql = "SELECT contig_grch37, start_grch37, end_grch37, ref_grch37, alt_grch37," +
                " contig_grch38, start_grch38, end_grch38, ref_grch38, alt_grch38," +
                " pathogenicity_value, pathogenicity_confirmed, false_positive FROM variants WHERE id=?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            Variant variant = null;
            if (rs.next()) {
                GenomicVariant grch37Variant = new GenomicVariant(
                        rs.getString("contig_grch37"),
                        rs.getInt("start_grch37"),
                        rs.getInt("end_grch37"),
                        rs.getString("ref_grch37"),
                        rs.getString("alt_grch37")
                );
                GenomicVariant grch38Variant = new GenomicVariant(
                        rs.getString("contig_grch38"),
                        rs.getInt("start_grch38"),
                        rs.getInt("end_grch38"),
                        rs.getString("ref_grch38"),
                        rs.getString("alt_grch38")
                );
                variant = new Variant(
                        id,
                        grch37Variant,
                        grch38Variant
                );
                variant.setAcmg(ACMG.getFromPathogenicityValue(rs.getInt("pathogenicity_value")));
                variant.setPathogenicityConfirmed(rs.getBoolean("pathogenicity_confirmed"));
                variant.setFalsePositive(rs.getBoolean("false_positive"));
                variant.setOccurrence(DAOController.getVariantAnalysisDAO().countOccurrence(variant.getId()));
            }
            return variant;
        }
    }

    /**
     * Update the variant
     */
    public void updateVariant(Variant variant) throws SQLException {
        final String sql = "UPDATE variants SET pathogenicity_value=?, pathogenicity_confirmed=?, false_positive=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setInt(++i, variant.getAcmg().getPathogenicityValue());
            stm.setBoolean(++i, variant.isPathogenicityConfirmed());
            stm.setBoolean(++i, variant.isFalsePositive());
            stm.setLong(++i, variant.getId());
            stm.executeUpdate();
        }
    }




}
