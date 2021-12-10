package ngsdiaglim.importer;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Role;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ImportUsers {

    public static void importUsers(File userFile) throws IOException, SQLException {
        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(userFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                String[] tks = line.split("\t");
                String name = tks[0];
                boolean is_admin = Boolean.parseBoolean(tks[1]);
                if (!DAOController.getUsersDAO().userExists(name)) {
                    Set<Role> roles = new HashSet<>();
                    if (is_admin) {
                        roles.add(DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name()));
                    } else {
                        roles.add(DAOController.getRolesDAO().getRole(DefaultRolesEnum.GUEST.name()));
                    }
                    DAOController.getUsersDAO().addUser(name, name, roles);
                }
            }
        }

        reader.close();
    }
}
