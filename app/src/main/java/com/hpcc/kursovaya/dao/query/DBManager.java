package com.hpcc.kursovaya.dao.query;

import android.util.Log;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DBManager {
    private static  final String TAG = DBManager.class.getSimpleName();
    private static Realm realm = Realm.getDefaultInstance();
    private static List result = new ArrayList<>();

    public static Realm getRealm() {
        return realm;
    }

    public static <T extends RealmObject> int write(@NotNull final T model){
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

        int returnValue = (int)result.get(ConstantApplication.ZERO);

        if(model instanceof AcademicHour && !((AcademicHour) model).hasCanceled() && !((AcademicHour) model).hasCompleted()){
            Realm realm = Realm.getDefaultInstance();
            List<RealmResults<AcademicHour>> resultPeriod = new ArrayList<>();
            DateTime begin = DateTime.now();
            DateTime end = DateTime.now();
            if(begin.getMonthOfYear()< DateTimeConstants.SEPTEMBER){
                begin = begin.minusYears(1).withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1);
                end = end.withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1);
            } else {
                begin = begin.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1);
                end = end.plusYears(1).withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1);
            }
            Date finalBegin = begin.toDate();
            Date finalEnd = end.toDate();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    resultPeriod.add(realm.where(AcademicHour.class).between("date", finalBegin, finalEnd).findAll());
                }
            });
            Log.d(TAG,  "academicHourListFromPeriod" + resultPeriod.get(ConstantApplication.ZERO).toString());
            List<AcademicHour> academicHours = DBManager.copyObjectFromRealm(resultPeriod.get(ConstantApplication.ZERO));
            Subject subject = ((AcademicHour) model).getTemplateAcademicHour().getSubject();
            Group group = ((AcademicHour) model).getTemplateAcademicHour().getGroup();

            ArrayList<AcademicHour> completedList = new ArrayList<>();
            ArrayList<AcademicHour> canceledList = new ArrayList<>();
            ArrayList<AcademicHour> restList = new ArrayList<>();
            for(AcademicHour academicHour: academicHours){
                Group listGroup = academicHour.getTemplateAcademicHour().getGroup();
                Subject listSubject = academicHour.getTemplateAcademicHour().getSubject();
                if(group.equals(listGroup) && subject.equals(listSubject)){
                    if(academicHour.hasCompleted()){
                        completedList.add(academicHour);
                    } else if(academicHour.hasCanceled()){
                        canceledList.add(academicHour);
                    } else {
                        restList.add(academicHour);
                    }
                }
            }
            Collections.sort(restList, (o1, o2) -> {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDate().compareTo(o2.getDate());
            });
            int totalHours = subject.getSpecialityCountHourMap().get(group.getSpecialty())-canceledList.size();
            if (totalHours < completedList.size()+restList.size()){
                DateTime current = new DateTime(((AcademicHour) model).getDate());
                boolean hasOld = false;
                for(AcademicHour academicHour: restList){
                    DateTime hourDate = new DateTime(academicHour.getDate());
                    if(hourDate.isBefore(current)){
                        TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                        DBManager.delete(TemplateAcademicHour.class,ConstantApplication.ID,templateAcademicHour.getId());
                        DBManager.delete(AcademicHour.class,ConstantApplication.ID,academicHour.getId());
                        hasOld = true;
                        break;
                    }
                }
                if(!hasOld){
                    TemplateAcademicHour templateAcademicHour = restList.get(restList.size()-1).getTemplateAcademicHour();
                    DBManager.delete(TemplateAcademicHour.class,ConstantApplication.ID,templateAcademicHour.getId());
                    DBManager.delete(AcademicHour.class, ConstantApplication.ID,restList.get(restList.size()-1).getId());
                }
            }
        }


        return returnValue;
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
            result.add(ConstantApplication.ZERO);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (int)result.get(ConstantApplication.ZERO);
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
            Log.v(TAG, "Success -> " + result.get(ConstantApplication.ZERO).getClass().getSimpleName() + " was read: " + result.get(ConstantApplication.ZERO).toString());
        } catch (Throwable ex) {
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
            return null;
        }

        return (T)result.get(ConstantApplication.ZERO);
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
            Log.v(TAG, "Success -> " + result.get(ConstantApplication.ZERO).getClass().getSimpleName() + " was readAll: " + result.get(ConstantApplication.ZERO).toString());
        } catch (Throwable ex) {
            result.add(null);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (RealmResults<T>) result.get(ConstantApplication.ZERO);
    }

    public static <T extends RealmObject> int findMaxID(@NotNull Class<T> clazz){
        Number maxID = realm.where(clazz).max(ConstantApplication.ID);

        if (maxID != null) {
            return maxID.intValue();
        }
        return ConstantApplication.ZERO;
    }

    public static <T extends RealmObject> T copyObjectFromRealm(T obj){
        if(obj!=null) {
            return realm.copyFromRealm(obj);
        } else {
            return obj;
        }
    }
    public static <T extends RealmObject> List<T> copyObjectFromRealm(List<T> obj){
        return realm.copyFromRealm(obj);
    }

    public static  <T extends RealmObject> RealmResults<T> search(@NotNull Class<T> clazz,
                              @NotNull final String fieldName, @NotNull String value, @NotNull Case casing,
                              @NotNull final String nameSort, Sort sort){
        result.clear();

        try {
            RealmResults<T> model = realm.where(clazz)
                    .like(fieldName, value, casing)
                    .sort(nameSort, sort).findAll();
            result.add(model);
            Log.v(TAG, "Success -> " + result.get(ConstantApplication.ZERO).getClass().getSimpleName() + " was search: " + result.get(ConstantApplication.ZERO).toString());
        } catch (Throwable ex) {
            result.add(null);
            Log.e(TAG, "Failed -> " + ex.getMessage(), ex);
        }

        return (RealmResults<T>) result.get(ConstantApplication.ZERO);
    }
}