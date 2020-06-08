/**
 * Class representing a comment.
 */
package com.google.sps.data;
import java.util.Date;


public class Comment {
    private String name; // name of the commenter
    private String content; // content of the comment
    private Date date; // date the comment was left
    private String email;

    public Comment(String name, String content, String email) {
        this.name = name;
        this.content = content;
        this.date = new Date();
        this.email = email;
    }

    public Comment(String name, String content, Date date, String email) {
        this.name = name;
        this.content = content;
        this.date = date;
        this.email = email;
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
}