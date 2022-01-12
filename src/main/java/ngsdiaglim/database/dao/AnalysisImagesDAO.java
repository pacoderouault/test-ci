package ngsdiaglim.database.dao;

public class AnalysisImagesDAO extends DAO {
//    /**
//     * Add an image to the ddb
//     * @param imgFile The File of the image to insert
//     * @param analysisId Analysis linked to the image
//     * @return The id of the image inserted
//     * @throws SQLException
//     * @throws FileNotFoundException
//     */
//    public long addAnalysisImage(File imgFile, long analysisId) throws SQLException, FileNotFoundException {
//        final String sql = "INSERT INTO analysisImages(analysis_id, image) VALUES(?, ?);";
//
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stm.setLong(1, analysisId);
//            InputStream inputImage = new FileInputStream(imgFile);
//            stm.setBinaryStream(2, inputImage, (int)(imgFile.length()));
//            stm.executeUpdate();
//            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    return generatedKeys.getLong(1);
//                }
//                else {
//                    return -1;
//                }
//            }
//        }
//    }
//
//    /**
//     * Add an image to the ddb
//     * @param img The Image to insert
//     * @param analysisId Analysis linked to the image
//     * @return The id of the image inserted
//     * @throws SQLException
//     * @throws FileNotFoundException
//     */
//    public long addAnalysisImage(Image img, long analysisId) throws SQLException, IOException {
//        final String sql = "INSERT INTO analysisImages(analysis_id, image) VALUES(?, ?);";
//        try ( Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stm.setLong(1, analysisId);
//
//            BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            ImageIO.write(bImage, "png", outputStream);
//            byte[] res  = outputStream.toByteArray();
//            InputStream inputStream = new ByteArrayInputStream(res);
//
//            stm.setBinaryStream(2, inputStream);
//            stm.executeUpdate();
//            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    return generatedKeys.getLong(1);
//                }
//                else {
//                    return -1;
//                }
//            }
//        }
//    }
//
//
//    /**
//     *
//     * @return All the Image attached to the Analysis
//     * @throws SQLException
//     * @throws IOException
//     */
//    public List<AdditionalImage> getAdditionalImages(long analysisId) throws SQLException, IOException {
//        List<AdditionalImage> images = new ArrayList<>();
//        final String sql = "SELECT id, image FROM analysisImages WHERE analysis_id=?;";
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
//            stm.setLong(1, analysisId);
//            ResultSet rs = stm.executeQuery();
//            while(rs.next()) {
//                Blob blob = rs.getBlob(2);
//                InputStream in = blob.getBinaryStream();
//                images.add(new AdditionalImage(rs.getLong("id"), analysisId, new Image(in)));
//            }
//            return images;
//        }
//    }
//
//
//    /**
//     * Delete an image from the ddb
//     * @param ai
//     * @throws SQLException
//     */
//    public void deleteAdditionalImage(AdditionalImage ai) throws SQLException {
//        final String sql = "DELETE FROM analysisImages WHERE id=?;";
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
//            stm.setLong(1, ai.getId());
//            stm.executeUpdate();
//        }
//    }
}
