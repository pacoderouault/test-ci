package ngsdiaglim.importer;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.Prescriber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class ImportPrescibers {

    public static void importPrescribers(File file) throws IOException, SQLException {
        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                String[] tks = line.split("\t");
                String status = tks[0];
                String firstname = tks[1];
                String lastname = tks[2];
                String address = tks[3].replace("&NL&", "\n");
                Prescriber p = new Prescriber(status, firstname, lastname, address);
                DAOController.getPrescriberDAO().addPrescriber(p);
            }
        }

        reader.close();
    }

}
