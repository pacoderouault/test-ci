package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.ACMG;

import java.time.LocalDateTime;

public class VariantPathogenicity {

    private long id;
    private final long variantId;
    private final SimpleObjectProperty<ACMG> acmg = new SimpleObjectProperty<>();
    private final long userId;
    private final SimpleStringProperty userName = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private long verifiedUserId;
    private final SimpleStringProperty verifiedUsername = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> verifiedDateTime = new SimpleObjectProperty<>();
    private final SimpleStringProperty commentary = new SimpleStringProperty();

    public VariantPathogenicity(long id, long variantId, ACMG acmg, long userId, String userName, LocalDateTime dateTime, long verifiedUserId, String verifiedUsername, LocalDateTime verifiedDateTime, String commentary) {
        this(variantId, acmg, userId, userName, dateTime, verifiedUserId, verifiedUsername, verifiedDateTime, commentary);
        this.id = id;
    }

    public VariantPathogenicity(long variantId, ACMG acmg, long userId, String userName, LocalDateTime dateTime, long verifiedUserId, String verifiedUsername, LocalDateTime verifiedDateTime, String commentary) {
        this.variantId = variantId;
        this.acmg.set(acmg);
        this.userId = userId;
        this.userName.set(userName);
        this.dateTime.set(dateTime);
        this.verifiedUserId = verifiedUserId;
        this.verifiedUsername.set(verifiedUsername);
        this.verifiedDateTime.set(verifiedDateTime);
        this.commentary.set(commentary);
    }

    public long getId() {return id;}

    public long getVariantId() {return variantId;}

    public ACMG getAcmg() {
        return acmg.get();
    }

    public SimpleObjectProperty<ACMG> acmgProperty() {
        return acmg;
    }

    public long getUserId() {return userId;}

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public long getVerifiedUserId() {return verifiedUserId;}

    public String getVerifiedUsername() {
        return verifiedUsername.get();
    }

    public SimpleStringProperty verifiedUsernameProperty() {
        return verifiedUsername;
    }

    public LocalDateTime getVerifiedDateTime() {
        return verifiedDateTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> verifiedDateTimeProperty() {
        return verifiedDateTime;
    }

    public String getCommentary() {
        return commentary.get();
    }

    public SimpleStringProperty commentaryProperty() {
        return commentary;
    }

    public void setVerifiedUserId(long verifiedUserId) {
        this.verifiedUserId = verifiedUserId;
    }

    public void setVerifiedUsername(String verifiedUsername) {
        this.verifiedUsername.set(verifiedUsername);
    }

    public void setVerifiedDateTime(LocalDateTime verifiedDateTime) {
        this.verifiedDateTime.set(verifiedDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariantPathogenicity that = (VariantPathogenicity) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
