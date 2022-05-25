package com.example.btnaddtab;

import java.io.Serializable;

public class info implements Serializable {
    String forumName, school, title, excerpt, like, comment, gender, post_avatarUrl, thumbnailUrl, forumAlias;
    int id;

    public String getComment() {
        return comment;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getForumAlias() {
        return forumAlias;
    }

    public String getForumName() {
        return forumName;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getSchool() {
        return school;
    }

    public String getPost_avatarUrl() {
        return post_avatarUrl;
    }

    public String getLike() {
        return like;
    }

    public int getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }
}
