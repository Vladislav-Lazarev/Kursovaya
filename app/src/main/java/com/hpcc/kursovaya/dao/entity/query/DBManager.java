package com.hpcc.kursovaya.dao.entity.query;

import android.util.Log;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DBManager {
    private static  final String TAG = "DBManager";
    private static Realm realm = Realm.getDefaultInstance();
    private static List result = new ArrayList<>();

    public static <T extends RealmObject> int write(@NotNull final T model){
        result.clear();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(model);

                    result.add(ConstantEntity.ONE);
                    Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was write: " + model);
                }
            });
        } catch (Throwable ex) {
                result.add(ConstantEntity.ZERO);
                Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> int writeAll(@NotNull final List<T> model) {
        result.clear();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(model);

                    result.add(model.size());
                    Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was write: " + model);
                }
            });
        } catch (Throwable ex) {
            result.add(ConstantEntity.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantEntity.ZERO);
    }

    public static <T extends RealmObject> int delete(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final String value){
        result.clear();
        final T model;

        try {
            model = realm.where(clazz)
                    .equalTo(fieldName, value)
                    .findFirst();

            Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was delete: " + model.toString());

            result.add(ConstantEntity.ONE);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    model.deleteFromRealm();
                }
            });
        } catch (Throwable ex) {
            result.add(ConstantEntity.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> int deleteAll(@NotNull final Class<T> clazz){
        result.clear();
        final RealmResults<T> model;

        try {
            model = realm.where(clazz)
                    .findAll();

            Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was deleteAll: " + model.toString());

            result.add(model.size());

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    model.deleteAllFromRealm();
                }
            });
        } catch (Throwable ex) {
            result.add(ConstantEntity.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantEntity.ZERO);
    }

    public static <T extends RealmObject> T read(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final String value){
        result.clear();

        try {
            result.add(realm.where(clazz)
                    .equalTo(fieldName, value)
                    .findFirst());

            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.set(ConstantEntity.ZERO, null);

            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (T)result.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz){
        result.clear();

        try {
            result.add(realm.where(clazz)
                    .findAll());

            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.set(ConstantEntity.ZERO, null);

            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (RealmResults<T>) result.get(ConstantEntity.ZERO);
    }

    public static <T extends RealmObject> int findMaxID(@NotNull Class<T> clazz){
        Number maxID = realm.where(clazz).max(ConstantEntity.ID);

        if (maxID != null) {
            return maxID.intValue();
        }
        return ConstantEntity.ZERO;
    }
}