package com.hpcc.kursovaya.dao.entity.query;

import android.util.Log;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.my_type.Order;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DBManager {
    private static  final String TAG = DBManager.class.getSimpleName();
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

    public static <T extends RealmObject> int delete(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final Object value){
        result.clear();
        final T model;

        try {
            RealmQuery<T> query = realm.where(clazz);

            Class<?> classObj = value.getClass();

            if (String.class.equals(classObj)) {
                query.equalTo(fieldName, (String) value);
            } else if (Integer.class.equals(classObj)) {
                query.equalTo(fieldName, (Integer) value);
            } else if (Date.class.equals(classObj)) {
                query.equalTo(fieldName, (Date) value);
            }

            model = query.findFirst();

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

    public static <T extends RealmObject> T read(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final Object value){
        result.clear();

        try {
            RealmQuery<T> query = realm.where(clazz);

            Class<?> aClass = value.getClass();
            if (Integer.class.equals(aClass)) {
                query.equalTo(fieldName, (Integer) value);
            } else if (Date.class.equals(aClass)) {
                query.equalTo(fieldName, (Date) value);
            } else if (String.class.equals(aClass)) {
                query.equalTo(fieldName, (String) value);
            }

            result.add(query.findFirst());

            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.set(ConstantEntity.ZERO, null);

            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (T)result.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> RealmList<T> readAll(@NotNull final Class<T> clazz, @NotNull final String fieldNameSort){
        result.clear();

        try {
            result.add((realm.where(clazz)
                    .findAll().sort(fieldNameSort, Sort.ASCENDING)));

            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.set(ConstantEntity.ZERO, null);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        RealmList<T> realmList = new RealmList<>();
        realmList.addAll((Collection<? extends T>) result.get(ConstantEntity.ZERO));
        return realmList;
    }

    public static <T extends RealmObject> RealmList<T> readAll(@NotNull final Class<T> clazz, @NotNull final String fieldNameSort, Order order){
        result.clear();

        try {
            if(order == Order.ASC){
                result.add((realm.where(clazz)
                        .findAll().sort(fieldNameSort, Sort.ASCENDING)));
            }else{
                result.add((realm.where(clazz)
                        .findAll().sort(fieldNameSort, Sort.DESCENDING)));
            }

            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was readAll: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.set(ConstantEntity.ZERO, null);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        RealmList<T> realmList = new RealmList<>();
        realmList.addAll((Collection<? extends T>) result.get(ConstantEntity.ZERO));
        return realmList;
    }

    public static <T extends RealmObject> int findMaxID(@NotNull Class<T> clazz){
        Number maxID = realm.where(clazz).max(ConstantEntity.ID);

        if (maxID != null) {
            return maxID.intValue();
        }
        return ConstantEntity.ZERO;
    }

    public static <T extends RealmObject> T copyObjectFromRealm(T obj){
        T newObj = realm.copyFromRealm(obj);
        return newObj;
    }
    public static <T extends RealmObject> ArrayList<T> copyObjectFromRealm(List<T> obj){
        List<T> newObj = realm.copyFromRealm(obj);
        return new ArrayList<>(newObj);
    }
}