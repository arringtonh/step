/**
 * Class representing a comment.
 */
package com.google.sps.data;
import java.util.Date;


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
}