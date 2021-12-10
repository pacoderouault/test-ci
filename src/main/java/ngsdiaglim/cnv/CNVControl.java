package ngsdiaglim.cnv;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.Gender;

import java.io.File;

public class CNVControl {

    private final long id;
    private final CNVControlGroup group;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final File depthFile;
    private final SimpleObjectProperty<Gender> gender = new SimpleObjectProperty<>();

    public CNVControl(long id, CNVControlGroup group, String name, File depthFile, Gender gender) {
        this.id = id;
        this.group = group;
        this.name.set(name);
        this.depthFile = depthFile;
        this.gender.set(gender);
    }

    public long getId() {return id;}

    public CNVControlGroup getGroup() {return group;}

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public File getDepthFile() {return depthFile;}

    public Gender getGender() {
        return gender.get();
    }

    public SimpleObjectProperty<Gender> genderProperty() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender.set(gender);
    }
}
