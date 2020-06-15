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
    private long timestamp; // date the comment was left
    private String userId;
    private long commentId;

    public Comment(String name, String content, String userId) {
        this.name = name;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.userId = userId;
    }

    public Comment(String name, String content, long timestamp, String userId, long commentId) {
        this.name = name;
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JsonObject getJsonObject() {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("content", content);
        obj.addProperty("timestamp", timestamp);
        obj.addProperty("commentId", commentId);
        return obj;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSameUserId(String userId) {
        return this.userId.equals(userId);
    }

    public long getCommentId() {
        return this.commentId;
    }
}