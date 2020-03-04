package com.hpcc.kursovaya.dao;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DBManager {
    public static <T extends RealmObject> void add(Realm realm, final T model){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmDB) {
               realmDB.copyToRealm(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("Success", model.getClass().getTypeName().toString() + " was added: " + model.toString());
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
    public static <T extends RealmObject> RealmResults<T> findAll(Realm realm, Class<T> clazz){
        return realm.where(clazz).findAllAsync();
    }
    public static <T extends RealmObject> T findByField(Realm realm, Class<T> clazz, final String fieldName, final String value){
        return realm.where(clazz).equalTo(fieldName, value).findFirstAsync();
    }
    public static <T extends RealmObject> int findMaxID(Realm realm, Class<T> clazz){
        final Number maxID = realm.where(clazz).max(ConstantEntity.ID);
        if(maxID == null){
            return 0;
        }else{
            return maxID.intValue();
        }
    }
    //
}