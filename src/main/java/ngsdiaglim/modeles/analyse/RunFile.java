package ngsdiaglim.modeles.analyse;

import java.io.File;

public class RunFile {

    private long id;
    private final File file;
    private final Run run;

    public RunFile(File file, Run run) {
        this.file = file;
        this.run = run;
    }

    public RunFile(long id, File file, Run run) {
        this(file, run);
        this.id = id;
    }

    public long getId() {return id;}

    public File getFile() {return file;}

    public Run getRun() {return run;}

    public boolean exists() {
        return file.exists();
    }
}
