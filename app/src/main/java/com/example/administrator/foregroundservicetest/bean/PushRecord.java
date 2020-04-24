package com.example.administrator.foregroundservicetest.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/17</p>
 * <p>@for : </p>
 * <p></p>
 */
@Entity
public class PushRecord {
    @Id
    private Long id;

    private String title;

    private String time;

    public PushRecord() {
    }

    public PushRecord(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
