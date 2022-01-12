package ngsdiaglim.database.dao;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.variants.Variant;

import java.sql.*;

public class VariantsDAO extends DAO {

    public long addVariant(String contig, int start, int end, String ref, String alt) throws SQLException {
        final String sql = "INSERT INTO variants (contig, start, end_, ref, alt) VALUES (?, ?, ?, ?, ?);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, contig);
            stm.setInt(2, start);
            stm.setInt(3, end);
            stm.setString(4, ref);
            stm.setString(5, alt);
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
    public Variant getVariant(String contig, int start, String ref, String alt) throws SQLException {
        final String sql = "SELECT id, contig, start, end_, ref, alt, pathogenicity_value, pathogenicity_confirmed, false_positive" +
                " FROM variants WHERE contig=? AND start=? AND ref=? AND alt=?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, contig);
            stm.setInt(2, start);
            stm.setString(3, ref);
            stm.setString(4, alt);
            ResultSet rs = stm.executeQuery();
            Variant variant = null;
            if (rs.next()) {
                variant = new Variant(
                        rs.getInt("id"),
                        rs.getString("contig"),
                        rs.getInt("start"),
                        rs.getInt("end_"),
                        rs.getString("ref"),
                        rs.getString("alt")
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
        final String sql = "SELECT id, contig, start, end_, ref, alt, pathogenicity_value, pathogenicity_confirmed, false_positive" +
                " FROM variants WHERE id=?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            Variant variant = null;
            if (rs.next()) {
                variant = new Variant(
                        rs.getInt("id"),
                        rs.getString("contig"),
                        rs.getInt("start"),
                        rs.getInt("end_"),
                        rs.getString("ref"),
                        rs.getString("alt")
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
