package ngsdiaglim.importer;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.VariantPathogenicity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImportVariants {

    public static void importVariants(File dir) throws IOException, SQLException {
        for (File f : Files.list(dir.toPath()).map(Path::toFile).collect(Collectors.toList())) {

            String hash = null;
            String contig = null;
            Integer start = null;
            Integer end = null;
            String ref = null;
            String alt = null;
            Integer acmg = null;
            Boolean acmg_conf = null;
            Boolean fp = null;
            List<String[]> history = new ArrayList<>();
            List<String[]> comments = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] tks = line.split("\t");
                    switch (tks[0]) {
                        case "hash":
                            hash = tks[1];
                            break;
                        case "contig":
                            contig = tks[1];
                            break;
                        case "start":
                            start = Integer.parseInt(tks[1]);
                            break;
                        case "end":
                            end = Integer.parseInt(tks[1]);
                            break;
                        case "ref":
                            ref = tks[1];
                            break;
                        case "alt":
                            alt = tks[1];
                            break;
                        case "acmg":
                            acmg = Integer.valueOf(tks[1]);
                            break;
                        case "acmg_conf":
                            acmg_conf = Boolean.parseBoolean(tks[1]);
                            break;
                        case "fp":
                            fp = Boolean.parseBoolean(tks[1]);
                            break;
                        case "history":
                            history.add(tks);
                            break;
                        case "comment":
                            comments.add(tks);
                            break;
                    }
                }
            }
            reader.close();

            if (contig == null || contig.isBlank() || start == null || end == null || ref == null || ref.isBlank() || alt == null || alt.isBlank() || acmg == null || acmg_conf == null || fp == null) {
                throw new NullPointerException("Variant is null : " + f);
            }
            Variant v = DAOController.getVariantsDAO().getVariant(contig, start, ref, alt);
            if (v == null) {
                System.out.println("Variant isn't found : " + f);
            } else {

                ACMG variant_acmg = ACMG.getFromPathogenicityValue(acmg);
                for (String[] tks : history) {
                    String userName = tks[1];
                    String userNameConfirm = tks[2].equals("null") ? tks[1] : tks[2];
                    Integer oldPatho = Integer.parseInt(tks[3]);
                    Integer newPatho = Integer.parseInt(tks[4]);
                    LocalDateTime date = LocalDateTime.parse(tks[5]);
                    LocalDateTime dateConf = tks[6].equals("null") ? LocalDateTime.parse(tks[5]) : LocalDateTime.parse(tks[6]);
                    String comment = tks[7].replace("&NL&", "\n");
                    ACMG newACMG = ACMG.getFromPathogenicityValue(newPatho);
                    User user = DAOController.getUsersDAO().getUser(userName);
                    User userConf = DAOController.getUsersDAO().getUser(userNameConfirm);

                    VariantPathogenicity vp = new VariantPathogenicity(
                            v.getId(),
                            newACMG,
                            user.getId(),
                            userName,
                            date,
                            userConf.getId(),
                            userNameConfirm,
                            dateConf,
                            comment
                    );

                    DAOController.getVariantPathogenicityDAO().addVariantPathogenicity(vp);
                }

                for (String[] tks : comments) {
                    String userName = tks[1];
                    LocalDate date = LocalDate.parse(tks[2]);
                    String comment = tks[3].replace("&NL&", "\n");
                    User user = DAOController.getUsersDAO().getUser(userName);
                    DAOController.getVariantCommentaryDAO().addVariantCommentary(v.getId(), user, comment, date.atStartOfDay());
                }
                v.setAcmg(variant_acmg);
                v.setPathogenicityConfirmed(true);
                DAOController.getVariantsDAO().updateVariant(v);
            }
        }
    }
}
