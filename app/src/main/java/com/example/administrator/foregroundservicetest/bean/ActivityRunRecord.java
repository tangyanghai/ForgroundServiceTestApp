package com.example.administrator.foregroundservicetest.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/17</p>
 * <p>@for : </p>
 * <p></p>
 */
@Table(name = "act_record")
public class ActivityRunRecord {
    @Column(
            name = "ID",
            isId = true,
            autoGen = true
    )
    private int id;

    @Column(
            name = "ACT_NAME"
    )
    private String name;

    @Column(
            name = "TIME"
    )
    private String time;

    public ActivityRunRecord() {
    }

    public ActivityRunRecord(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
