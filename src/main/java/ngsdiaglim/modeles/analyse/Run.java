package ngsdiaglim.modeles.analyse;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class Run {

    private static final Logger logger = LogManager.getLogger(Run.class);
    private long id;
    private final String path;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDate> creationDate = new SimpleObjectProperty<>();
    private ObservableList<Analysis> analyses;
    private final ObservableList<RunFile> runFiles = FXCollections.observableArrayList();
    private final String creationUser;

    public Run(long id, String path, String name, LocalDate date, LocalDate creationDate, String creationUser) {
        this.id = id;
        this.path = path;
        this.name.set(name);
        this.date.set(date);
        this.creationDate.set(creationDate);
        this.creationUser = creationUser;
//        Random rd = new Random(); // creating Random object
//        for (int i = 0; i < 12; i++) {
//            Analysis a = new Analysis();
//            a.setDone(rd.nextBoolean());
//            analyses.add(a);
//        }
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPath() {return path;}

    public LocalDate getDate() {
        return date.get();
    }

    public SimpleObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public LocalDate getCreationDate() {
        return creationDate.get();
    }

    public SimpleObjectProperty<LocalDate> creationDateProperty() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate.set(creationDate);
    }

    public ObservableList<Analysis> getAnalyses() throws SQLException {
        if (analyses == null) {
            analyses = DAOController.getAnalysisDAO().getAnalysis(this);
        }
        return analyses;
    }

    public void loadAnalysesFromDB()  throws SQLException {
        analyses = DAOController.getAnalysisDAO().getAnalysis(this);
    }

    public boolean hasAnalysis(String analysisName) {
        try {
            return getAnalyses().stream().anyMatch(p -> p.getName().equals(analysisName));
        } catch (SQLException e) {
            logger.error("Error when check if run has analysis", e);
        }
        return false;
    }

    public String getCreationUser() {return creationUser;}

    public ObservableList<RunFile> getRunFiles() {return runFiles;}

    public void addRunFiles(RunFile... files) {
        runFiles.addAll(files);
    }

    public void setRunFiles(List<RunFile> files) {
        runFiles.setAll(files);
    }

    public void loadRunFiles() {
        try {
            setRunFiles(DAOController.getRunFilesDAO().getRunFiles(this));
        } catch (SQLException e) {
            logger.error(e);
            Platform.runLater(() -> Message.error(e.getMessage(), e));
        }
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Run run = (Run) o;

        return id == run.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
