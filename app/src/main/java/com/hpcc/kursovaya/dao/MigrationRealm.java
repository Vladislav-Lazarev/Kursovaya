package com.hpcc.kursovaya.dao;

import java.util.Locale;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrationRealm implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion < newVersion){
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Migration missing v%d to v%d", oldVersion, newVersion));
        }

        if (oldVersion == 0) {
            RealmObjectSchema obj = schema.get("Subject");
            ++oldVersion;
        }
    }
}
