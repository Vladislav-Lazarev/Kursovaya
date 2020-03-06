package com.hpcc.kursovaya.dao;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init( this );
        RealmConfiguration config = new RealmConfiguration.Builder().name( "DB_realm.realm" ).build();
        Realm.setDefaultConfiguration(config);

    }
}
