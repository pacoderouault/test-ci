package ngsdiaglim.modeles.igv;

import ngsdiaglim.App;
import ngsdiaglim.enumerations.OS;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.utils.PlatformUtils;

import java.io.IOException;
import java.util.Optional;

public class IGVConfig {

    private final OS os;

    public IGVConfig() {
        os = PlatformUtils.getOS();
    }

    public void launchIGV() throws IOException {
        if (os != null) {
            if (os.equals(OS.WINDOWS)) {
                launchIgvWindows();
            } else if (os.equals(OS.LINUX)) {
                launchIgvLinux();
            }
        }
    }

    public boolean igvIsRunning() {
        if (os != null) {
//            System.out.println(os);
            if (os.equals(OS.WINDOWS)) {
                return igvIsRunningOnWindows();
            } else if (os.equals(OS.LINUX)) {
                return igvIsRunningOnLinux();
            }
        }
        return false;
    }

    private boolean igvIsRunningOnWindows() {
        ProcessHandle.allProcesses().forEach(p -> {
            if (p.info().commandLine().isPresent()) {
                System.out.println(p);
                System.out.println(p.info());
                System.out.println(p.info().commandLine());
            }
        });
        Optional<ProcessHandle> process = ProcessHandle.allProcesses()
                .filter(p -> p.info().commandLine().isPresent() && p.info().commandLine().get().endsWith("IGV_Xen.exe")).findAny();
        return process.isPresent();
    }

    private boolean igvIsRunningOnLinux() {
        Optional<ProcessHandle> process = ProcessHandle.allProcesses()
                .filter(p -> p.info().commandLine().isPresent() && p.info().commandLine().get().endsWith("igv.sh")).findAny();
        return process.isPresent();
    }

    private void launchIgvWindows() throws IOException {
        String igvpath = App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.IGV_PATH);
        Process p = Runtime.getRuntime().exec("start \"\" " + igvpath);
        try {
            System.out.println(p.waitFor());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void launchIgvLinux() throws IOException {
        String igvpath = App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.IGV_PATH);
        System.out.println(igvpath);
        Process p = Runtime.getRuntime().exec(igvpath);
        try {
            System.out.println(p.waitFor());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
