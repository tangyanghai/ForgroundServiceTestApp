package com.example.administrator.foregroundservicetest.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/17</p>
 * <p>@for : </p>
 * <p></p>
 */
@Table(name = "push_record")
public class PushRecord {
    @Column(
            name = "ID",
            isId = true,
            autoGen = true
    )
    private int id;
    @Column(name = "TITLE")
    private String title;

    @Column(name = "TIME")
    private String time;

    public PushRecord() {
    }

    public PushRecord(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
