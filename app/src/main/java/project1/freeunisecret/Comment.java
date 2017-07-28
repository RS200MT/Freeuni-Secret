package project1.freeunisecret;

import java.util.Comparator;

/**
 * Created by Tornike on 27-Jul-17.
 */

public class Comment {
    private String commentText;
    private long writeTime;
    private int numHearts;

    public Comment(String commentText, long writeTime, int numHearts){
        this.commentText = commentText;
        this.writeTime = writeTime;
        this.numHearts = numHearts;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }

    public int getNumHearts() {
        return numHearts;
    }

    public void setNumHearts(int numHearts) {
        this.numHearts = numHearts;
    }
}
