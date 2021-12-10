package ngsdiaglim;

import ngsdiaglim.database.DAOController;
import org.junit.jupiter.api.BeforeAll;

import java.sql.SQLException;


public abstract class BaseSetup {

    private static boolean started = false;

    @BeforeAll
    public static void beforeAllMethod() throws SQLException {
        if (!started) {
            System.out.println("@BeforeAll static method invoked once.");
            System.out.println("Create database");
            DAOController.getDatabaseCreatorDAO().createTables();
            started = true;
        }
        System.out.println("@BeforeAll static method invoked for every class.");
    }
}

//public class ModuleFeature1Class extends BaseSetup {
//
//    @Test
//    public void testMethod() {
//        System.out.println("ModuleFeature1Class: in testMethod().");
//    }
//}
//
//public class ModuleFeature2Class extends BaseSetup {
//
//    @Test
//    public void testMethod() {
//        System.out.println("ModuleFeature2Class: in testMethod().");
//    }
//}