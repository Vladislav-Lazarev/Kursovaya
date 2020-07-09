package com.hpcc.kursovaya.dao.query;

import android.util.Log;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DBManager {
    private static  final String TAG = DBManager.class.getSimpleName();
    private static List result = new ArrayList<>();

    public static <T extends RealmObject> int write(@NotNull final T model){
        Realm realm = Realm.getDefaultInstance();
        result.clear();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(model);
                    result.add(ConstantApplication.ONE);
                    Log.v(TAG, "Success -> " + model.getClass().getSimpleName() + " was write: " + model);
                }
            });


        } catch (Throwable ex) {
            result.add(ConstantApplication.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantApplication.ZERO);
    }
    public static <T extends RealmObject> int writeAll(@NotNull final List<T> model) {
        Realm realm = Realm.getDefaultInstance();
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
            result.add(ConstantApplication.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantApplication.ZERO);
    }

    public static <T extends RealmObject, V> int delete(@NotNull final Class<T> clazz, @NotNull final String fieldName, @NotNull final V value){
        Realm realm = Realm.getDefaultInstance();
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
                        result.add(ConstantApplication.ONE);
                    }
                    assert deleteModel != null;
                    Log.v(TAG, "Success -> " + deleteModel.getClass().getSimpleName() + " was delete: " + deleteModel.toString());
                }
            });
        } catch (Throwable ex) {
            result.add(ConstantApplication.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantApplication.ZERO);
    }
    public static <T extends RealmObject> int deleteAll(@NotNull final Class<T> clazz){
        Realm realm = Realm.getDefaultInstance();
        return deleteAll(clazz, null, null);
    }

    public static <T extends RealmObject, V> int deleteAll(@NotNull final Class<T> clazz, final String fieldName, final V value){
        Realm realm = Realm.getDefaultInstance();
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
                    if (model != null || model.size() > ConstantApplication.ZERO) {
                        model.deleteAllFromRealm();
                        result.add(deleteModel.size());
                    }
                    Log.v(TAG, "Success -> " + deleteModel.getClass().getSimpleName() + " was deleteAll: " + deleteModel.toString());
                }

            });
        } catch (Throwable ex) {
            result.add(ConstantApplication.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantApplication.ZERO);
    }

    public static <T extends RealmObject, V> T read(@NotNull final Class<T> clazz, @NotNull String fieldName, @NotNull V value){
        Realm realm = Realm.getDefaultInstance();
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

            result.add(query.findFirst());
            if (!result.isEmpty()){
                Log.v(TAG, "Success -> " + result.get(ConstantApplication.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantApplication.ZERO).toString());
            } else {
                Log.v(TAG, "Success -> " + null + " was read: " + result.get(ConstantApplication.ZERO).toString());
            }
        } catch (Throwable ex) {
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
            return null;
        }

        return (T)result.get(ConstantApplication.ZERO);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz){
        return readAll(clazz, Collections.singletonList(null), Collections.singletonList(null), null, null);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz, final String nameSort){
        return readAll(clazz, nameSort, Sort.ASCENDING);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz, @NotNull final String nameSort, @NotNull Sort sort){
        return readAll(clazz, null, null, nameSort, sort);
    }

    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                     @NotNull final String fieldName, @NotNull Object value) {
        return readAll(clazz, fieldName, value, null);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                     @NotNull final String fieldName, @NotNull Object value,
                                                                     final String nameSort) {
        return readAll(clazz, fieldName, value, nameSort, Sort.ASCENDING);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                  final String fieldName, Object value,
                                                                  final String fieldSort, Sort sort){
        Realm realm = Realm.getDefaultInstance();
        result.clear();

        try {
            final RealmQuery<T> query = realm.where(clazz);
            if (fieldName != null) {
                if (value instanceof Integer) {
                    query.equalTo(fieldName, (Integer) value);
                } else if (value instanceof String) {
                    query.equalTo(fieldName, ((String) value));
                } else if (value instanceof Date) {
                    query.equalTo(fieldName, (Date) value);
                } else {
                    throw new Exception("Error type!");
                }
            }

            RealmResults<T> model = query.findAll();
            if (fieldSort != null){
                model = model.sort(fieldSort, sort);
            }

            result.add(model);
            if (!result.isEmpty()){
                Log.v(TAG, "Success -> " + result.get(ConstantApplication.ZERO).getClass().getSimpleName() + " was readAll: " + result.get(ConstantApplication.ZERO).toString());
            } else {
                Log.v(TAG, "Success -> " + null + " was readAll: " + result.get(ConstantApplication.ZERO).toString());
            }
        } catch (Throwable ex) {
            result.add(null);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (RealmResults<T>) result.get(ConstantApplication.ZERO);
    }

    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                     final List<String> fieldsName, final List<Object> values){
        return readAll(clazz, fieldsName, values, null);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                  final List<String> fieldsName, final List<Object> values,
                                                                  List<String> fieldsSort){
        List<Sort> sorts = new ArrayList<>();
        for (int i = 0; fieldsSort != null && i < fieldsSort.size(); i++) {
            sorts.add(Sort.ASCENDING);
        }

        return readAll(clazz, fieldsName, values, fieldsSort, sorts);
    }
    public static <T extends RealmObject> RealmResults<T> readAll(@NotNull final Class<T> clazz,
                                                                  List<String> fieldsName, List<Object> values,
                                                                  List<String> fieldsSort, List<Sort> sorts){
        Realm realm = Realm.getDefaultInstance();

        if (fieldsName.size() != values.size()){
            return null;
        }

        if (fieldsSort != null && sorts != null){
            if (fieldsSort.size() != sorts.size()) return null;
        }

        result.clear();

        try {
            final RealmQuery<T> query = realm.where(clazz);

            for (int i = 0; i < fieldsName.size(); i++) {
                String field = fieldsName.get(i);
                Object value = values.get(i);

                if (field != null) {
                    if (value instanceof Integer) {
                        query.equalTo(field, (Integer) value);
                    } else if (value instanceof String) {
                        query.equalTo(field, (String) value);
                    } else if (value instanceof Date) {
                        query.equalTo(field, (Date) value);
                    } else {
                        throw new Exception("Error type!");
                    }
                }

                if(i < (fieldsName.size() - 1)){
                    query.and();
                }
            }

            if (fieldsSort != null){
                query.sort((String[]) fieldsSort.toArray(), (Sort[]) sorts.toArray());
            }

            result.add(query.findAll());
            Log.v(TAG, "Success -> " + result.get(ConstantApplication.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantApplication.ZERO).toString());
        } catch (Throwable ex) {
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
            return null;
        }

        return (RealmResults<T>) result.get(ConstantApplication.ZERO);
    }

    public static <T extends RealmObject> int findMaxID(@NotNull Class<T> clazz){
        Realm realm = Realm.getDefaultInstance();

        Number maxID = realm.where(clazz).max(ConstantApplication.ID);

        if (maxID != null) {
            return maxID.intValue();
        }
        return ConstantApplication.ZERO;
    }

    public static <T extends RealmObject> T copyObjectFromRealm(T obj){
        Realm realm = Realm.getDefaultInstance();

        if(obj!=null) {
            return realm.copyFromRealm(obj);
        } else {
            return obj;
        }
    }
    public static <T extends RealmObject> List<T> copyObjectFromRealm(List<T> obj){
        Realm realm = Realm.getDefaultInstance();
        return realm.copyFromRealm(obj);
    }
}