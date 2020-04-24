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
public class ServiceRunRecord {
    @Id
    private Long id;

    private String name;

    private String time;

    public ServiceRunRecord() {
    }

    public ServiceRunRecord(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
