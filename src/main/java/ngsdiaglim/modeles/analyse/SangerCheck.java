package ngsdiaglim.modeles.analyse;

import ngsdiaglim.enumerations.SangerState;

import java.time.LocalDateTime;

public class SangerCheck {

    private final long id;
    private final long variantId;
    private final long analysisId;
    private final SangerState state;
    private final LocalDateTime dateTime;
    private final long userId;
    private final String userName;
    private final String comment;

    public SangerCheck(long id, long variantId, long analysisId, SangerState state, LocalDateTime dateTime, long userId, String userName, String comment) {
        this.id = id;
        this.variantId = variantId;
        this.analysisId = analysisId;
        this.state = state;
        this.dateTime = dateTime;
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
    }

    public long getId() {return id;}

    public long getVariantId() {return variantId;}

    public long getAnalysisId() {return analysisId;}

    public SangerState getState() {return state;}

    public long getUserId() {return userId;}

    public String getUserName() {return userName;}

    public String getComment() {return comment;}

    public LocalDateTime getDateTime() {return dateTime;}
}
