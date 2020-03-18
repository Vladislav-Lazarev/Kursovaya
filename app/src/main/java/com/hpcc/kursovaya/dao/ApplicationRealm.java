package com.hpcc.kursovaya.dao;

import android.app.Application;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationRealm extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        File directory = new File(getExternalFilesDir(null).getAbsolutePath());

        RealmConfiguration config = new RealmConfiguration.Builder()
                //.directory(directory)
                .name("DB.realm").build();
        Realm.setDefaultConfiguration(config);
    }
}
