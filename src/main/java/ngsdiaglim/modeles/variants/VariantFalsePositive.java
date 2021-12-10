package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class VariantFalsePositive {
    private long id;
    private final long variantId;
    private final SimpleBooleanProperty falsePositive = new SimpleBooleanProperty();
    private final long userId;
    private final SimpleStringProperty userName = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private long verifiedUserId;
    private final SimpleStringProperty verifiedUsername = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> verifiedDateTime = new SimpleObjectProperty<>();
    private final SimpleStringProperty commentary = new SimpleStringProperty();


    public VariantFalsePositive(long id, long variantId, boolean falsePositive, long userId, String userName, LocalDateTime dateTime, long verifiedUserId, String verifiedUsername, LocalDateTime verifiedDateTime, String commentary) {
        this(variantId, falsePositive, userId, userName, dateTime, verifiedUserId, verifiedUsername, verifiedDateTime, commentary);
        this.id = id;
    }

    public VariantFalsePositive(long variantId, boolean falsePositive, long userId, String userName, LocalDateTime dateTime, long verifiedUserId, String verifiedUsername, LocalDateTime verifiedDateTime, String commentary) {
        this.variantId = variantId;
        this.falsePositive.set(falsePositive);
        this.userId = userId;
        this.userName.set(userName);
        this.dateTime.set(dateTime);
        this.verifiedUserId = verifiedUserId;
        this.verifiedUsername.set(verifiedUsername);
        this.verifiedDateTime.set(verifiedDateTime);
        this.commentary.set(commentary);
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public long getVariantId() {return variantId;}

    public boolean isFalsePositive() {
        return falsePositive.get();
    }

    public SimpleBooleanProperty falsePositiveProperty() {
        return falsePositive;
    }

    public void setFalsePositive(boolean falsePositive) {
        this.falsePositive.set(falsePositive);
    }

    public long getUserId() {return userId;}

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    public long getVerifiedUserId() {return verifiedUserId;}

    public void setVerifiedUserId(long verifiedUserId) {
        this.verifiedUserId = verifiedUserId;
    }

    public String getVerifiedUsername() {
        return verifiedUsername.get();
    }

    public SimpleStringProperty verifiedUsernameProperty() {
        return verifiedUsername;
    }

    public void setVerifiedUsername(String verifiedUsername) {
        this.verifiedUsername.set(verifiedUsername);
    }

    public LocalDateTime getVerifiedDateTime() {
        return verifiedDateTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> verifiedDateTimeProperty() {
        return verifiedDateTime;
    }

    public void setVerifiedDateTime(LocalDateTime verifiedDateTime) {
        this.verifiedDateTime.set(verifiedDateTime);
    }

    public String getCommentary() {
        return commentary.get();
    }

    public SimpleStringProperty commentaryProperty() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary.set(commentary);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariantFalsePositive that = (VariantFalsePositive) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
