package project1.freeunisecret;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Tornike on 27-Jul-17.
 */

public class Comment extends RealmObject {
    private String commentText;
    private long writeTime;
    private String postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Comment(String commentText, long writeTime, String postId){
        this.commentText = commentText;
        this.writeTime = writeTime;
        this.postId = postId;

    }

    public  Comment(){

    }

    public String getWriteDate(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(writeTime));
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


}
