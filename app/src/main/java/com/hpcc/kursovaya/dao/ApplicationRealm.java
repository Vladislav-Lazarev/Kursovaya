package com.hpcc.kursovaya.dao;

import android.annotation.SuppressLint;
import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@SuppressLint("Registered")
public class ApplicationRealm extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init( this );
        RealmConfiguration config = new RealmConfiguration.Builder().name( "DB_realm.realm" ).build();
        Realm.setDefaultConfiguration(config);
    }
}
