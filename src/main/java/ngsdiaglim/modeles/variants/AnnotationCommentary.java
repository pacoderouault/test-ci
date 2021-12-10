package ngsdiaglim.modeles.variants;

import java.time.LocalDateTime;

public class AnnotationCommentary {
    private long id;
    private long variant_id;
    private long analysis_id;
    private long userID;
    private String username;
    private String comment;
    private LocalDateTime datetime;

    public AnnotationCommentary(long id, long variant_id, long analysis_id, long userID, String username, String comment, LocalDateTime datetime) {
        this.id = id;
        this.variant_id = variant_id;
        this.analysis_id = analysis_id;
        this.userID = userID;
        this.username = username;
        this.comment = comment;
        this.datetime = datetime;
    }

    public long getId() {return id;}

    public long getVariantId() {return variant_id;}

    public long getAnalysisId() {return analysis_id;}

    public long getUserID() {return userID;}

    public String getUsername() {return username;}

    public String getComment() {return comment;}

    public LocalDateTime getDatetime() {return datetime;}
}