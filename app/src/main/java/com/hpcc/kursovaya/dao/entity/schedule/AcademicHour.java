package com.hpcc.kursovaya.dao.entity.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.AlarmClassReceiver;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class AcademicHour extends RealmObject implements EntityI<AcademicHour>, Parcelable, Cloneable {
    private static final String TAG = AcademicHour.class.getSimpleName();
    @PrimaryKey
    private int id;// Индентификатор
    private int idTemplateAcademicHour;// Шаблон полупары
    private Date date;// Дата проведения
    private String note;// Заметка
    private int repeatForNextWeek;//значение повтора для оператора выбора
    private int notificationBefore;//значение оповещения для оператора выбора
    private boolean isCompleted;// Проведенная или не проведенная полупара
    private boolean isCanceled;// Отмененная или не проведенная полупара
    private int numberAcademicHour;
    private int numberPair;

    public AcademicHour() {
        id = 0;
        idTemplateAcademicHour = 0;
        notificationBefore = 0;
        date = new Date();
        note = "";
        isCompleted = false;
        isCanceled = false;

        numberAcademicHour = 123;
        numberPair = 7;

    }
    public AcademicHour(int id, @NotNull TemplateAcademicHour templateAcademicHour, @NotNull Date date, @NotNull String note,int notificationBefore,int repeatForNextWeek, boolean isCompleted, boolean isCanceled, int numberAcademicHour, int numberPair) {
        this();
        setId(id);
        setTemplateAcademicHour(templateAcademicHour);
        setDate(date);
        setNote(note);
        setNotificationBefore(notificationBefore);
        setRepeatForNextWeek(repeatForNextWeek);
        setCompleted(isCompleted);
        setCanceled(isCanceled);

        setNumberAcademicHour(numberAcademicHour);
        setNumberPair(numberPair);
    }

    public int getRepeatForNextWeek() {
        return repeatForNextWeek;
    }

    public AcademicHour setRepeatForNextWeek(int repeatForNextWeek) {
        this.repeatForNextWeek = repeatForNextWeek;
        return this;
    }

    public int getNotificationBefore() {
        return notificationBefore;
    }

    public AcademicHour setNotificationBefore(int notificationBefore) {
        this.notificationBefore = notificationBefore;
        return this;
    }
    private void setId(int id){
        try{
            if (id < ConstantApplication.ONE){
                throw new Exception("Exception! setId()");
            }
            this.id = id;
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @NotNull
    public TemplateAcademicHour getTemplateAcademicHour() {
        return DBManager.copyObjectFromRealm(DBManager.read(TemplateAcademicHour.class, ConstantApplication.ID, idTemplateAcademicHour));
    }
    public AcademicHour setTemplateAcademicHour(@NotNull TemplateAcademicHour templateAcademicHour) {
        // TODO setTemplateAcademicHour - проверка
        this.idTemplateAcademicHour = templateAcademicHour.getId();
        return this;
    }

    @NotNull
    public Date getDate() {
        return date;
    }
    public AcademicHour setDate(@NotNull Date date) {
        // TODO setDateTime - проверка
        this.date = date;
        return this;
    }

    @NotNull
    public String getNote() {
        return note;
    }
    public AcademicHour setNote(@NotNull String note) {
        // TODO setNote - проверка
        this.note = note;
        return this;
    }

    @NotNull
    public int getNumberAcademicHour() {
        return numberAcademicHour;
    }
    public AcademicHour setNumberAcademicHour(int numberAcademicHour) {
        this.numberAcademicHour = numberAcademicHour;
        return this;
    }

    @NotNull
    public int getNumberPair() {
        return numberPair;
    }
    public AcademicHour setNumberPair(int numberPair) {
        this.numberPair = numberPair;
        return this;
    }

    @NotNull
    public boolean hasCompleted() {
        return isCompleted;
    }

    public AcademicHour setCompleted(@NotNull boolean completed) {
        // TODO setNote - проверка
        isCompleted = completed;
        return this;
    }
    @NotNull
    public boolean hasCanceled() {
        return isCanceled;
    }
    public AcademicHour setCanceled(@NotNull boolean canceled) {
        // TODO setNote - проверка
        isCanceled = canceled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        AcademicHour that = (AcademicHour) o;
        return isCompleted == that.isCompleted &&
                isCanceled == that.isCanceled &&
                Objects.equals(idTemplateAcademicHour, that.idTemplateAcademicHour) &&
                Objects.equals(date, that.date) &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTemplateAcademicHour, date, note, isCompleted, isCanceled);
    }

    @Override
    public String toString() {
        return "AcademicHour{" +
                "id=" + id +
                ", idTemplateAcademicHour=" + idTemplateAcademicHour +
                ", numberPair=" + numberPair +
                ", numberAcademicHour=" + numberAcademicHour +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", isCompleted=" + isCompleted +
                ", isCanceled=" + isCanceled +
                '}';
    }
    public static RealmResults<AcademicHour> academicHourListFromPeriod(Date from, Date to){
        Realm realm = Realm.getDefaultInstance();
        List<RealmResults<AcademicHour>> list = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                list.add(realm.where(AcademicHour.class).between("date", from, to).findAll());
            }
        });
        Log.d(TAG,  "academicHourListFromPeriod" + list.get(ConstantApplication.ZERO).toString());
        return (list.get(ConstantApplication.ZERO));
    }

    public static void setNotifaction(Context context,AcademicHour academicHour){
        DateTime dateOfNotification = new DateTime(academicHour.getDate());
        DateTime dateOfClass = new DateTime(academicHour.getDate());
        int numberOfLesson = academicHour.getTemplateAcademicHour().getNumberHalfPairButton()/2;
        int numberOfHalf = ((academicHour.getTemplateAcademicHour().getNumberHalfPairButton()+1) % 2 == 0)? 1 : 0;
        int hourOfDay = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][0];
        int minuteOfHour = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][1];
        switch(academicHour.getNotificationBefore()) {
            case 0:
                break;
            case 1:
                dateOfNotification =  dateOfNotification.withHourOfDay(hourOfDay/*<-- place for hour local variable*/-1).withMinuteOfHour(minuteOfHour/*minute of class*/);
                break;
            case 2:
                dateOfNotification =  dateOfNotification.withHourOfDay(hourOfDay/*<-- place for hour local variable*/-2).withMinuteOfHour(minuteOfHour);
                break;
            case 3:
                dateOfNotification =  dateOfNotification.withHourOfDay(hourOfDay/*<-- place for hour local variable*/-3).withMinuteOfHour(minuteOfHour);
                break;
            case 4:
                dateOfNotification =  dateOfNotification.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).plusDays(1);
                break;
            case 5:
                dateOfNotification = dateOfNotification.plusDays(2);
                break;
        }
        if(academicHour.getNotificationBefore()>0) {
            DateTime now = DateTime.now();
            Seconds difference = Seconds.secondsBetween(now,dateOfNotification);
            Log.d(TAG,dateOfNotification.toString());
            Log.d(TAG, difference.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND,difference.getSeconds());
            Intent _intent = new Intent(context, AlarmClassReceiver.class);
            _intent.putExtra("groupName",academicHour.getTemplateAcademicHour().getGroup().getName());
            _intent.putExtra("yearOfNot",Integer.toString(dateOfClass.getYear()));
            _intent.putExtra("monthOfYearNot",Integer.toString(dateOfClass.getMonthOfYear()));
            _intent.putExtra("dayOfMonthNot",Integer.toString(dateOfClass.getDayOfMonth()));
            _intent.putExtra("description",academicHour.getNote());
            _intent.putExtra("hourOfDay",hourOfDay);
            _intent.putExtra("minuteOfHour",minuteOfHour);
            String strFlag = dateOfClass.getMonthOfYear()+""+ dateOfClass.getDayOfMonth() + hourOfDay + minuteOfHour;
            final int id = Integer.valueOf(strFlag);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, _intent, 0);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void refreshAllNumberPair(AcademicHour beforeChangeAcademicHour){
        if (beforeChangeAcademicHour != null){
            dateSorting(beforeChangeAcademicHour.getTemplateAcademicHour().getGroup(),
                    beforeChangeAcademicHour.getTemplateAcademicHour().getSubject());
        }

        final List<AcademicHour> academicHourList = dateSorting(getTemplateAcademicHour().getGroup(),
                getTemplateAcademicHour().getSubject());
        Log.d("academicHourList", academicHourList.toString());

        for (int i = 0, numberAcademicHour = 1, numberPair = 1; i < academicHourList.size(); i++, numberAcademicHour++, numberPair++) {
            if (hasCanceled()){
                numberAcademicHour--;
                numberPair--;
                setNumberPair(ConstantApplication.ZERO);
                setNumberAcademicHour(ConstantApplication.ZERO);
                continue;
            }

            Pair<Integer, Integer> dayAndPair_1hour =
                    academicHourList.get(i).getTemplateAcademicHour().getDayAndPairButton();
            Pair<Integer, Integer> dayAndPair_2hour;

            academicHourList.get(i).setNumberAcademicHour(numberAcademicHour);

            switch (dayAndPair_1hour.second){
                case 0:
                case 2:
                case 4:
                case 6:
                case 8:
                    academicHourList.get(i).setNumberPair(numberPair);
                    if (i == academicHourList.size() - ConstantApplication.ONE) break;

                    final int iNext = ConstantApplication.ONE;
                    dayAndPair_2hour = academicHourList.get(i + iNext).getTemplateAcademicHour().getDayAndPairButton();
                    if (dayAndPair_1hour.first.equals(dayAndPair_2hour.first) &&
                            dayAndPair_1hour.second.equals(dayAndPair_2hour.second - ConstantApplication.secondCellShift(dayAndPair_1hour.second))){
                        academicHourList.get(++i).setNumberAcademicHour(++numberAcademicHour);
                    } else {
                        break;
                    }
                case 1:
                case 3:
                case 5:
                case 7:
                case 9:
                    academicHourList.get(i).setNumberPair(numberPair);
                    break;
                default:
                    Log.d("dayAndPair", "error");
            }


        }

        DBManager.writeAll(academicHourList);

        final AcademicHour currentAcademicHour = DBManager.read(AcademicHour.class, ConstantApplication.ID, id);
        // Если сущ удалена
        if (currentAcademicHour!= null){
            setNumberAcademicHour(currentAcademicHour.getNumberAcademicHour());
            setNumberPair(currentAcademicHour.getNumberPair());
        }
    }
    private List<AcademicHour> dateSorting(@NotNull Group group, @NotNull Subject subject){
        List<String> fieldsNameGroupAndSubject = Arrays.asList("idGroup", "idSubject");
        List<Object> valuesGroupAndSubject = Arrays.asList(group.getId(), subject.getId());
        List<String> fieldsSort = Arrays.asList("numberDayOfWeek");
        List<Sort> sorts = Arrays.asList(Sort.ASCENDING);
        /*List<String> fieldsSort = Arrays.asList("numberDayOfWeek", "numberHalfPair");
        List<Sort> sorts = Arrays.asList(Sort.ASCENDING, Sort.ASCENDING);*/

        final List<TemplateAcademicHour> templateAcademicHourList = DBManager.readAll(TemplateAcademicHour.class,
                fieldsNameGroupAndSubject, valuesGroupAndSubject,
                fieldsSort, sorts);

        Realm realm = Realm.getDefaultInstance();
        final List<RealmResults<AcademicHour>> academicHourRealmResults = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<AcademicHour> query = realm.where(AcademicHour.class);
                if (!templateAcademicHourList.isEmpty()){
                    query.beginGroup();
                    for (int i = 0; i < templateAcademicHourList.size(); i++) {
                        String field = "idTemplateAcademicHour";
                        Integer value = templateAcademicHourList.get(i).getId();

                        query.equalTo(field, value);

                        if(i < (templateAcademicHourList.size() - 1)){
                            query.or();
                        }
                    }
                    query.endGroup();
                    query.and().sort("date", Sort.ASCENDING);
                }
                academicHourRealmResults.add(query.findAll());
            }
        });

        List<AcademicHour> academicHourList = DBManager.copyObjectFromRealm(academicHourRealmResults.get(0));
        return academicHourList;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean existsEntity() {
        // TODO Пока коряво
        RealmResults<TemplateAcademicHour> existingEntities =
                DBManager.readAll(TemplateAcademicHour.class);
        for (TemplateAcademicHour entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        setTemplateAcademicHour(getTemplateAcademicHour());
        setDate(getDate());
        setNote(getNote());
        setCompleted(hasCompleted());
        setCanceled(hasCanceled());

        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception {
        try {
            setTemplateAcademicHour(getTemplateAcademicHour());
            setDate(getDate());
            setNote(getNote());
            setCompleted(hasCompleted());
            setCanceled(hasCanceled());
            setNumberPair(getNumberPair());
        } catch(RuntimeException ex) {
            throw new Exception("Entity = ", ex);
        }
    }
    @Override
    public AcademicHour createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }

        return this;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected AcademicHour(Parcel in) {
        id = in.readInt();
        idTemplateAcademicHour = in.readInt();
        date = (Date) in.readSerializable();
        note = in.readString();
        repeatForNextWeek = in.readInt();
        notificationBefore = in.readInt();
        isCompleted = (Boolean) in.readValue(null);
        isCanceled = (Boolean) in.readValue(null);
        numberAcademicHour = in.readInt();
        numberPair = in.readInt();
    }
    public static final Creator<AcademicHour> CREATOR = new Creator<AcademicHour>() {
        @Override
        public AcademicHour createFromParcel(Parcel in) {
            return new AcademicHour(in);
        }

        @Override
        public AcademicHour[] newArray(int size) {
            return new AcademicHour[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idTemplateAcademicHour);
        dest.writeSerializable(date);
        dest.writeString(note);
        dest.writeInt(repeatForNextWeek);
        dest.writeInt(notificationBefore);
        dest.writeValue(isCompleted);
        dest.writeValue(isCanceled);
        dest.writeInt(numberAcademicHour);
        dest.writeInt(numberPair);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public AcademicHour clone() throws CloneNotSupportedException {
        return (AcademicHour) super.clone();
    }

    public AcademicHour createEntityWithoutChecking() throws Exception {
        int maxID = DBManager.findMaxID(this.getClass());
        setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        return this;
    }

}
