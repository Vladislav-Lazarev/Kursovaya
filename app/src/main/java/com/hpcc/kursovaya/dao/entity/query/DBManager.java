package com.hpcc.kursovaya.dao.entity.query;

import android.util.Log;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
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

    public static <T extends RealmObject, V> int delete(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final V value){
        result.clear();

        try {
            final RealmQuery<T> query = realm.where(clazz);
            if (value instanceof Integer) {
                query.equalTo(fieldName, (Integer) value);
            } else if (value instanceof String) {
                query.equalTo(fieldName, (String) value);
            } else if (value instanceof Date) {
                query.equalTo(fieldName, (Date) value);
            } else {
                throw new Exception("Error type!");
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final T model = query.findFirst();
                    final T deleteModel = copyObjectFromRealm(model);
                    if (model != null) {
                        model.deleteFromRealm();
                        result.add(ConstantEntity.ONE);
                    }
                    Log.v(TAG, "Success -> " + deleteModel.getClass().getSimpleName() + " was delete: " + deleteModel.toString());
                }
            });
        } catch (Throwable ex) {
            result.add(ConstantEntity.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> int deleteAll(@NotNull final Class<T> clazz){
        return deleteAll(clazz, null, null);
    }
    public static <T extends RealmObject, V> int deleteAll(@NotNull final Class<T> clazz, final String fieldName, final V value){
        result.clear();

        try {
            final RealmQuery<T> query = realm.where(clazz);

            if (fieldName != null) {
                if (value instanceof Integer) {
                    query.equalTo(fieldName, (Integer) value);
                } else if (value instanceof String) {
                    query.equalTo(fieldName, (String) value);
                } else if (value instanceof Date) {
                    query.equalTo(fieldName, (Date) value);
                } else {
                    throw new Exception("Error type!");
                }
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmResults<T> model = query.findAll();
                    final List<T> deleteModel = copyObjectFromRealm(model);
                    if (model != null || model.size() > ConstantEntity.ZERO) {
                        model.deleteAllFromRealm();
                        result.add(deleteModel.size());
                    }
                    Log.v(TAG, "Success -> " + deleteModel.getClass().getSimpleName() + " was deleteAll: " + deleteModel.toString());
                }

            });
        } catch (Throwable ex) {
            result.add(ConstantEntity.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantEntity.ZERO);
    }

    public static <T extends RealmObject, V> T read(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final V value){
        result.clear();

        try {
            final RealmQuery<T> query = realm.where(clazz);

            if (fieldName != null) {
                if (value instanceof Integer) {
                    query.equalTo(fieldName, (Integer) value);
                } else if (value instanceof String) {
                    query.equalTo(fieldName, (String) value);
                } else if (value instanceof Date) {
                    query.equalTo(fieldName, (Date) value);
                } else {
                    throw new Exception("Error type!");
                }
            }

            result.add(query.findFirst());
            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.add(null);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (T)result.get(ConstantEntity.ZERO);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz){
        return readAll(clazz, null, null, null, null);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz, final String nameSort){
        return readAll(clazz, nameSort, Sort.ASCENDING);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz, @NotNull final String nameSort, @NotNull Sort sort){
        return readAll(clazz, null, null, nameSort, sort);
    }

    public static <T extends RealmObject, V> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                     @NotNull final String fieldName, @NotNull V value) {
        return readAll(clazz, fieldName, value, null);
    }
    public static <T extends RealmObject, V> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                     @NotNull final String fieldName, @NotNull V value,
                                                                     final String nameSort) {
        return readAll(clazz, fieldName, value, nameSort, Sort.ASCENDING);
    }
    public static <T extends RealmObject, V> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                  final String fieldName, V value,
                                                                  final String nameSort, Sort sort){
        result.clear();

        try {
            final RealmQuery<T> query = realm.where(clazz);
            if (fieldName != null) {
                if (value instanceof Integer) {
                    query.equalTo(fieldName, (Integer) value);
                } else if (value instanceof String) {
                    query.equalTo(fieldName, (String) value);
                } else if (value instanceof Date) {
                    query.equalTo(fieldName, (Date) value);
                } else {
                    throw new Exception("Error type!");
                }
            }

            RealmResults<T> model = query.findAll();
            if (nameSort != null){
                model = model.sort(nameSort, sort);
            }

            result.add(model);
            Log.v(TAG, "Success -> " + result.get(ConstantEntity.ZERO).getClass().getSimpleName() + " was readAll: " + result.get(ConstantEntity.ZERO).toString());
        } catch (Throwable ex) {
            result.add(null);
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

    public static <T extends RealmObject> T copyObjectFromRealm(T obj){
        return realm.copyFromRealm(obj);
    }
    public static <T extends RealmObject> List<T> copyObjectFromRealm(List<T> obj){
        return realm.copyFromRealm(obj);
    }
}