package ngsdiaglim.modeles.analyse;

import java.time.LocalDateTime;

public class AnalysisCommentary {

    private final long id;
    private final long analysis_id;
    private final long userID;
    private final String username;
    private final String comment;
    private final LocalDateTime datetime;

    public AnalysisCommentary(long id, long analysis_id, long userID, String username, String comment, LocalDateTime datetime) {
        this.id = id;
        this.analysis_id = analysis_id;
        this.userID = userID;
        this.username = username;
        this.comment = comment;
        this.datetime = datetime;
    }

    public long getId() {return id;}

    public long getVariantId() {return analysis_id;}

    public long getUserID() {return userID;}

    public String getUsername() {return username;}

    public String getComment() {return comment;}

    public LocalDateTime getDatetime() {return datetime;}
}
