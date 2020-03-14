package com.hpcc.kursovaya.dao.entity.query;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DBManager {
    private static  final String TAG = "DBManager";
    private static Realm realmDefault = Realm.getDefaultInstance();

    public static <T extends RealmObject> void write(final T model){
        realmDefault.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was write: " + model);

                Log.println(Log.INFO, TAG,"Success -> " + model.getClass().getSimpleName() + " was write: " + model);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG, "Failed -> " + error.getMessage(), error);

                Log.println(Log.ERROR, TAG, "Failed -> " + error.getMessage() + "\n" + error);
            }
        });
    }
    public static <T extends RealmObject> void writeAll(final T model){
        realmDefault.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(model);
            }
        }, new Realm.Transaction.OnSuccess() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was writeAll: " + model);

                Log.println(Log.INFO, TAG,"Success -> " + model.getClass().getSimpleName() + " was writeAll: " + model);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG, "Failed -> " + error.getMessage(), error);

                Log.println(Log.ERROR, TAG, "Failed -> " + error.getMessage() + "\n" + error);
            }
        });
    }

    public static <T extends RealmObject> int delete(final Class<T> clazz, final String fieldName, final String value){
        final List<T> model = new ArrayList<>();
        final List<Integer> sizeDelete = new ArrayList<>();

        realmDefault.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                T firstModel = realm.where(clazz)
                        .equalTo(fieldName, value)
                        .findFirst();

                if (firstModel == null){
                    model.add(null);
                    sizeDelete.add(ConstantEntity.ZERO);
                } else {
                    firstModel.deleteFromRealm();
                    sizeDelete.add(ConstantEntity.ONE);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was delete: " + model.get(ConstantEntity.ZERO));

                Log.println(Log.INFO, TAG,"Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was delete: " + model.get(ConstantEntity.ZERO));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Failed -> " + error.getMessage(), error);

                Log.println(Log.ERROR, TAG, "Failed -> " + error.getMessage() + "\n" + error);
            }
        });

        return sizeDelete.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> int deleteAll(final Class<T> clazz){
        final List<RealmResults<T>> model = new ArrayList<>();

        realmDefault.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<T> allModel = realm.where(clazz).findAll();
                model.add(allModel);
                allModel.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was deleteAll: " + model.get(ConstantEntity.ZERO));

                Log.println(Log.INFO, TAG,"Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was deleteAll: " + model.get(ConstantEntity.ZERO));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Failed -> " + error.getMessage(), error);

                Log.println(Log.ERROR, TAG, "Failed -> " + error.getMessage() + "\n" + error);
            }
        });

        return model.get(ConstantEntity.ZERO).size();
    }

    public static <T extends RealmObject> T read(final Class<T> clazz, final String fieldName, final String value){
        final List<T> model = new ArrayList<>();

        realmDefault.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.add(realm.where(clazz)
                        .equalTo(fieldName, value)
                        .findFirst());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + model.get(ConstantEntity.ZERO));

                Log.println(Log.INFO, TAG,"Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + model.get(ConstantEntity.ZERO));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Failed -> " + error.getMessage(), error);

                Log.println(Log.ERROR, TAG, "Failed -> " + error.getMessage() + "\n" + error);
            }
        });

        return model.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(final Class<T> clazz){
        final List<RealmResults<T>> model = new ArrayList<>();

        realmDefault.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.add(realm.where(clazz).findAll());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was readAll: " + model.get(ConstantEntity.ZERO));

                Log.println(Log.INFO, TAG,"Success -> " + model.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was readAll: " + model.get(ConstantEntity.ZERO));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Failed -> " + error.getMessage(), error);

                Log.println(Log.ERROR, TAG, "Failed -> " + error.getMessage() + "\n" + error);
            }
        });

        return model.get(ConstantEntity.ZERO);
    }

    public static <T extends RealmObject> int findMaxID(Class<T> clazz){
        /*final Number maxID = realmDefault.where(clazz).max(ConstantEntity.ID);
        if(maxID != null){
            return maxID.intValue();
        }*/
        return ConstantEntity.ZERO;
    }

    public static <T extends RealmObject> int size(final Class<T> clazz){
        final List<Integer> size = new ArrayList<>();

        realmDefault.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                size.add(realm.where(clazz).findAll().size());
            }
        });

        return size.get(ConstantEntity.ZERO);
    }
}