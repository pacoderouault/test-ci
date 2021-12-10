package ngsdiaglim.utils;

import javafx.application.Platform;
import ngsdiaglim.enumerations.OS;

import java.util.concurrent.CountDownLatch;

public class PlatformUtils {

    public static void runAndWait(Runnable action) {
        if (action == null)
            throw new NullPointerException("action");

        // run synchronously on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        // queue on JavaFX thread and wait for completion
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                doneLatch.countDown();
            }
        });

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            // ignore exception
        }
    }


    public static OS getOS() {
        String operSys = System.getProperty("os.name").toLowerCase();
        if (operSys.contains("win")) {
            return OS.WINDOWS;
        } else if (operSys.contains("nix") || operSys.contains("nux")
                || operSys.contains("aix")) {
            return OS.LINUX;
        } else if (operSys.contains("mac")) {
            return OS.MAC;
        } else if (operSys.contains("sunos")) {
            return OS.SOLARIS;
        }
        return null;
    }

}
