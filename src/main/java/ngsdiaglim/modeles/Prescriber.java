package ngsdiaglim.modeles;

import javafx.beans.property.SimpleStringProperty;

import java.util.StringJoiner;

public class Prescriber {

    private long id;
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final SimpleStringProperty firstName = new SimpleStringProperty();
    private final SimpleStringProperty lastName = new SimpleStringProperty();
    private final SimpleStringProperty address = new SimpleStringProperty();

    public Prescriber(long id, String status, String firstName, String lastName, String address) {
        this(status, firstName, lastName, address);
        this.id = id;
    }

    public Prescriber(String status, String firstName, String lastName, String address) {
        this.status.set(status);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.address.set(address);
    }

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" ");
        return sj.add(getStatus()).add(getFirstName()).add(getLastName()).toString();
    }

    public PrescriberState getState() {
        if (getLastName() == null || getLastName().isEmpty()) {
            return new PrescriberState(PrescriberState.State.ERROR, "Le nom est vide");
        }
        else if (getAddress() == null || getAddress().isEmpty()) {
            return new PrescriberState(PrescriberState.State.WARNING, "L'adresse est vide");
        }
        else if (getFirstName() == null || getFirstName().isEmpty()) {
            return new PrescriberState(PrescriberState.State.WARNING, "Le prénom est vide");
        }
        else if (getStatus() == null || getStatus().isEmpty()) {
            return new PrescriberState(PrescriberState.State.WARNING, "Le status est vide");
        }
        else {
            return new PrescriberState(PrescriberState.State.NORMAL, "");
        }
    }

    public static class PrescriberState {

        private PrescriberState.State state;
        private String message;

        public PrescriberState(PrescriberState.State state, String message) {
            this.state = state;
            this.message = message;
        }

        public PrescriberState.State getState() { return state; }

        public void setState(PrescriberState.State state) {
            this.state = state;
        }

        public String getMessage() { return message; }

        public void setMessage(String message) {
            this.message = message;
        }

        public enum State {
            NORMAL,
            WARNING,
            ERROR;
        }
    }
}
