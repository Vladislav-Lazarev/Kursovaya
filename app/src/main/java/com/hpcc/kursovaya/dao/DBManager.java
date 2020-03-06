package com.hpcc.kursovaya.dao;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DBManager {
    public static <T extends RealmObject> void write(Realm realm, final T model){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmDB) {
               realmDB.copyToRealmOrUpdate(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("Success", model.getClass().getSimpleName() + " was write: " + model.toString());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e("Failed", error.getMessage());
            }
        });
    }
    public static <T extends RealmObject> void writeAll(Realm realm, final T model){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmDB) {
                realmDB.insertOrUpdate(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("Success", model.getClass().getSimpleName() + " was writeAll: " + model.toString());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e("Failed", error.getMessage());
            }
        });
    }

    public static <T extends RealmObject> void delete(Realm realm, Class<T> clazz, final String fieldName, final String value){
        final RealmResults<T> results = realm.where(clazz)
                .equalTo(fieldName, value)
                .findAllAsync();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteFirstFromRealm();
            }
        });
    }
    public static <T extends RealmObject> void deleteAll(Realm realm, Class<T> clazz){
        final RealmResults<T> results = realm.where(clazz).findAllAsync();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    public static <T extends RealmObject> T read(Realm realm, @NotNull final Class<T> clazz,@NotNull final String fieldName, final String value){
        final T[] first = (T[])new RealmObject[]{null};
        final RealmQuery<T> where = realm.where(clazz);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realmDB) {
                first[0] = realmDB.where(clazz).equalTo(fieldName, value).findFirst();
            }
        });

        T first_el = where.equalTo(fieldName, value).findFirst();

        return first[0];
    }
    public static <T extends RealmObject> RealmResults<T> readAll(Realm realm, final Class<T> clazz){
        final RealmResults<T>[] all = new RealmResults[]{null};

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmDB) {
                all[0] = realmDB.where(clazz).findAllAsync();
            }
        });

        RealmResults<T> results = realm.where(clazz).findAll();

        return all[0];
    }

    public static <T extends RealmObject> int findMaxID(Realm realm, Class<T> clazz){
        final Number maxID = realm.where(clazz).max(ConstantEntity.ID);
        if(maxID == null){
            return 0;
        }else{
            return maxID.intValue();
        }
    }
}