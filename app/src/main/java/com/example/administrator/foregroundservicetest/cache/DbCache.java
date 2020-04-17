package com.example.administrator.foregroundservicetest.cache;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/16</p>
 * <p>@for : 数据库数据缓存</p>
 * <p></p>
 */
public class DbCache implements ICache{
    private static final DbCache ourInstance = new DbCache();
    private DbManager db;

    public static DbCache getInstance() {
        return ourInstance;
    }

    private DbCache() {
        init();
    }

    public void init() {
        //本地数据的初始化
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("push_app_db") //设置数据库名
                .setDbVersion(1) //设置数据库版本,每次启动应用时将会检查该版本号,
                //发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                .setAllowTransaction(true)//设置是否开启事务,默认为false关闭事务
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {

                    }
                })//设置数据库创建时的Listener
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //balabala...
                    }
                });//设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
        //.setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下
        try {
            db = x.getDb(daoConfig);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized  <T> void add(T... list) {
        try {
            db.saveBindingId(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized <T> List<T> find(Class<T> clz) {
        List<T> list = null;
        try {
            Selector<T> selector = db.selector(clz);
            if (selector == null) {
                return new ArrayList<>();
            }
            list = selector
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (list == null) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

}
