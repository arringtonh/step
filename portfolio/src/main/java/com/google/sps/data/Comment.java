/**
 * Class representing a comment.
 */
package com.google.sps.data;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Comment {
    private String name; // name of the commenter
    private String content; // content of the comment
    private Date date; // date the comment was left
    private String email;
    private String userId;
    private long commentId;

    public Comment(String name, String content, String email, String userId) {
        this.name = name;
        this.content = content;
        this.date = new Date();
        this.email = email;
        this.userId = userId;
    }

    public Comment(String name, String content, Date date, String email, String userId, long commentId) {
        this.name = name;
        this.content = content;
        this.date = date;
        this.email = email;
        this.userId = userId;
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public JsonObject getJsonObject() {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("content", content);
        obj.addProperty("date", date.toString());
        obj.addProperty("email", email);
        obj.addProperty("commentId", commentId);
        return obj;
    }

    public boolean isSameUserId(String userId) {
        return (this.userId == userId);
    }

    public long getCommentId() {
        return this.commentId;
    }
}