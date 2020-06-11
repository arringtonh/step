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
    private String id;

    public Comment(String name, String content, String email, String id) {
        this.name = name;
        this.content = content;
        this.date = new Date();
        this.email = email;
        this.id = id;
    }

    public Comment(String name, String content, Date date, String email, String id) {
        this.name = name;
        this.content = content;
        this.date = date;
        this.email = email;
        this.id = id;
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
        return obj;
    }

    public boolean isSameId(String id) {
        return (this.id == id);
    }
}