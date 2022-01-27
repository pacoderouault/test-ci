package ngsdiaglim.modeles.ciq;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.CIQRecordState;

import java.time.LocalDateTime;

public class CIQRecordHistory {

    private final long id;
    private final long ciqRecordId;
    private final long userId;
    private final SimpleFloatProperty mean = new SimpleFloatProperty();
    private final SimpleFloatProperty sd = new SimpleFloatProperty();
    private final SimpleObjectProperty<CIQRecordState> oldState = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<CIQRecordState> newState = new SimpleObjectProperty<>();
    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty comment = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();

    public CIQRecordHistory(long id, long ciqRecordId, CIQRecordState oldState, CIQRecordState newState, long userId, String username, float mean, float sd, LocalDateTime dateTime, String comment) {
        this.id = id;
        this.ciqRecordId = ciqRecordId;
        this.oldState.set(oldState);
        this.newState.set(newState);
        this.userId = userId;
        this.username.set(username);
        this.mean.set(mean);
        this.sd.set(sd);
        this.comment.set(comment);
        this.dateTime.set(dateTime);
    }

    public long getId() {return id;}

    public long getCiqRecordId() {return ciqRecordId;}

    public CIQRecordState getOldState() {
        return oldState.get();
    }

    public SimpleObjectProperty<CIQRecordState> oldStateProperty() {
        return oldState;
    }

    public CIQRecordState getNewState() {
        return newState.get();
    }

    public SimpleObjectProperty<CIQRecordState> newStateProperty() {
        return newState;
    }

    public long getUserId() {return userId;}

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public float getMean() {
        return mean.get();
    }

    public SimpleFloatProperty meanProperty() {
        return mean;
    }

    public float getSd() {
        return sd.get();
    }

    public SimpleFloatProperty sdProperty() {
        return sd;
    }

    public String getComment() {
        return comment.get();
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }
}
