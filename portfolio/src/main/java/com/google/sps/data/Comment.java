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

    public Comment(String name, String content) {
        this.name = name;
        this.content = content;
        this.date = new Date();
    }

    public Comment(String name, String content, Date date) {
        this.name = name;
        this.content = content;
        this.date = date;
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

    public JsonObject getJsonObject() {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("content", content);
        obj.addProperty("date", date.toString());
        return obj;
    }
}