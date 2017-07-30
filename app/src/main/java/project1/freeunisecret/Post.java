package project1.freeunisecret;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tornike on 27-Jul-17.
 */

public class Post {
    private String id;
    private String postAuthorId;
    private String text;
    private long createTime;
    private int numComments;
    private int numHearts;
    private ArrayList<Comment> comments;
    private String imageUrl;

    public String getPostAuthorId() {
        return postAuthorId;
    }
    public Post(){

    }

    public void setPostAuthorId(String postAuthorId) {
        this.postAuthorId = postAuthorId;
    }

    public Post(String text, long createTime, int numComments, int numHearts, ArrayList<Comment> comments,  String id, String postAuthorId, String imageUrl) {
        this.text = text;
        this.createTime = createTime;
        this.numComments = numComments;
        this.numHearts = numHearts;
        this.comments = comments;
        this.id = id;
        this.postAuthorId = postAuthorId;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCreateTime() {
       return this.createTime;
    }

    public String getDate(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(createTime));
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public int getNumHearts() {
        return numHearts;
    }

    public void setNumHearts(int numHearts) {
        this.numHearts = numHearts;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
