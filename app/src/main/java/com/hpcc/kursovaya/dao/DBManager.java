package com.hpcc.kursovaya.dao;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DBManager {
    private static Realm realm = Realm.getDefaultInstance();

    public static <T extends RealmObject> void write(final T model){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmDB) {
               realmDB.copyToRealmOrUpdate(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("Success", model.getClass().getTypeName().toString() + " was write: " + model.toString());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e("Failed", error.getMessage());
            }
        });
    }
    public static <T extends RealmObject> void writeAll(final T model){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmDB) {
                realmDB.insertOrUpdate(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("Success", model.getClass().getTypeName().toString() + " was writeAll: " + model.toString());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e("Failed", error.getMessage());
            }
        });
    }

    public static <T extends RealmObject> void delete(Class<T> clazz, final String fieldName, final String value){
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
    public static <T extends RealmObject> void deleteAll(Class<T> clazz){
        final RealmResults<T> results = realm.where(clazz).findAllAsync();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    public static <T extends RealmObject> T read(Class<T> clazz, final String fieldName, final String value){
        T obj = realm.where(clazz).equalTo(fieldName, value).findFirstAsync();

        return realm.where(clazz).equalTo(fieldName, value).findFirstAsync();
    }
    public static <T extends RealmObject> RealmResults<T> readAll(Class<T> clazz){
        return realm.where(clazz).findAllAsync();
    }

    public static <T extends RealmObject> int findMaxID(Class<T> clazz){
        final Number maxID = realm.where(clazz).max(ConstantEntity.ID);
        if(maxID == null){
            return 0;
        }else{
            return maxID.intValue();
        }
    }
}