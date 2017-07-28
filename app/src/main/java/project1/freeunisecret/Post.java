package project1.freeunisecret;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tornike on 27-Jul-17.
 */

public class Post {
    private int id;
    private String text;
    private long createTime;
    private int numComments;
    private int numHearts;
    private ArrayList<Comment> comments;

    public Post(String text, long createTime, int numComments, int numHearts, ArrayList<Comment> comments, int id) {
        this.text = text;
        this.createTime = createTime;
        this.numComments = numComments;
        this.numHearts = numHearts;
        this.comments = comments;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateTime() {
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
