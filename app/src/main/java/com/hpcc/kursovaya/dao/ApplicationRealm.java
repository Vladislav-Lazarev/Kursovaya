package com.hpcc.kursovaya.dao;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.io.File;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationRealm extends Application {
    private Locale locale = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleManager.setLocale(this);

        Realm.init(this);
        File directory = new File(
                getExternalFilesDir(null).getAbsolutePath() +
                ConstantApplication.DIR_DELIMITER +
                ConstantApplication.DIR_DB
        );

        RealmConfiguration config = new RealmConfiguration.Builder()
                .directory(directory)
                .name(ConstantApplication.DB_NAME)
                .deleteRealmIfMigrationNeeded()
                //.schemaVersion(1)
                //.migration(new MigrationRealm())
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
