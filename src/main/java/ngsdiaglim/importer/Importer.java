package ngsdiaglim.importer;

import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.importer.anapath.CreateDefaultParamsAnapath;
import ngsdiaglim.importer.anapath.ImportsRunsAnapath;
import ngsdiaglim.importer.bgm.CreateDefaultParamsBGM;
import ngsdiaglim.importer.bgm.ImportsRunsBGM;
import ngsdiaglim.importer.hemato.CreateDefaultParamsHemato;
import ngsdiaglim.importer.hemato.ImportsRunsHemato;

import java.io.File;

public class Importer {

    public static void importBGM() {
        try {

            App.get().setLoggedUser(DAOController.getUsersDAO().getUser("admin"));

            File localRunDir = new File("/mnt/Data/CHU_services/biochimie_genetique_moleculaire/CMT/Runs_MiSeq/propres/allRuns");
            File dir = new File("/mnt/Data/tmp/BGMEXPORT/");
            File runDir = new File(dir, "runs");

            File prescribersFile = new File(dir, "prescripteurs.tsv");
            ImportPrescibers.importPrescribers(prescribersFile);

            CreateDefaultParamsBGM.createDefaultMiseq();
            CreateDefaultParamsBGM.createDefaultProton();

            ImportUsers.importUsers(new File(dir, "users.txt"));

            ImportsRunsBGM runImporter = new ImportsRunsBGM(runDir, localRunDir);
//            runImporter.importRuns();

            File variantsDir = new File(dir, "variants");
            ImportVariants.importVariants(variantsDir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void importHemato() {
        try {

            App.get().setLoggedUser(DAOController.getUsersDAO().getUser("admin"));

            File dir = new File("/mnt/Data/tmp/HEMATOEXPORT/");
            File runDir = new File(dir, "runs");

            CreateDefaultParamsHemato.createDefaultLympho();
            CreateDefaultParamsHemato.createDefaultMyelo();
            CreateDefaultParamsHemato.createDefaultTP53ATM();
            CreateDefaultParamsHemato.createDefaultMicropanel();

            ImportUsers.importUsers(new File(dir, "users.txt"));

            ImportsRunsHemato runImporter = new ImportsRunsHemato(runDir);
            runImporter.importRuns();

            File variantsDir = new File(dir, "variants");
            ImportVariants.importVariants(variantsDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void importAnapath() {
        try {

            App.get().setLoggedUser(DAOController.getUsersDAO().getUser("admin"));

            File dir = new File("/mnt/Data/tmp/ANAPATHEXPORT/");
            File runDir = new File(dir, "runs");

            CreateDefaultParamsAnapath.createDefaultTumSol();
            CreateDefaultParamsAnapath.createDefaultNF2();
            CreateDefaultParamsAnapath.createDefaultAgilent();
            CreateDefaultParamsAnapath.createDefaultAgilentcfDNA();

            ImportUsers.importUsers(new File(dir, "users.txt"));

            ImportsRunsAnapath runImporter = new ImportsRunsAnapath(runDir);
            runImporter.importRuns();

            File variantsDir = new File(dir, "variants");
            ImportVariants.importVariants(variantsDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
