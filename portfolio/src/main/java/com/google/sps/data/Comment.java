/**
 * Class representing a comment.
 */
package com.google.sps.data;
import java.util.Date;


public class Comment {
    private String name; // name of the commenter
    private String content; // content of the comment
    private final Date date = new Date(); // date the comment was left

    public Comment(String name, String content) {
        this.name = name;
        this.content = content;
    }
}