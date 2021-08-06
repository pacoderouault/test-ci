package ngsdiaglim.modeles.analyse;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Analysis {

    private long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleBooleanProperty done = new SimpleBooleanProperty(false);

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

    public boolean isDone() {
        return done.get();
    }

    public SimpleBooleanProperty doneProperty() {
        return done;
    }

    public void setDone(boolean done) {
        this.done.set(done);
    }
}
