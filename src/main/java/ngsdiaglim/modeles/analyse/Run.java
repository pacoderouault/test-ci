package ngsdiaglim.modeles.analyse;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Random;

public class Run {

    private long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDate> creationDate = new SimpleObjectProperty<>();
    private final ObservableList<Analysis> analyses = FXCollections.observableArrayList();
    private final String creationUser;

    public Run(long id, String name, LocalDate date, LocalDate creationDate, String creationUser) {
        this.id = id;
        this.name.set(name);
        this.date.set(date);
        this.creationDate.set(creationDate);
        this.creationUser = creationUser;
        Random rd = new Random(); // creating Random object
        for (int i = 0; i < 12; i++) {
            Analysis a = new Analysis();
            a.setDone(rd.nextBoolean());
            analyses.add(a);
        }
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

    public ObservableList<Analysis> getAnalyses() {

        return analyses;
    }

    public String getCreationUser() {return creationUser;}

}
