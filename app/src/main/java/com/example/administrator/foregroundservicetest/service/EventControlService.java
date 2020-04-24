package com.example.administrator.foregroundservicetest.service;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/24</p>
 * <p>@for : </p>
 * <p></p>
 */
public class EventControlService {

    EnumServiceAction action;

    Object o;

    private EventControlService() {
    }

    public EventControlService(EnumServiceAction action) {
        this.action = action;
    }

    public EnumServiceAction getAction() {
        return action;
    }

    public void setAction(EnumServiceAction action) {
        this.action = action;
    }

    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }
}
