package com.hpcc.kursovaya.dao.entity.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.AlarmClassReceiver;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAnotherEvent;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class AnotherEvent extends RealmObject implements EntityI<AnotherEvent>, Parcelable, Cloneable{
    private static final String TAG = AcademicHour.class.getSimpleName();
    @PrimaryKey
    private int id;// Индентификатор
    private int idTemplateAnotherHour;// Шаблон AnotherHour
    private Date date;// Дата проведения
    private String note;// Заметка
    private int repeatForNextWeek;//значение повтора для оператора выбора
    private int notificationBefore;//значение оповещения для оператора выбора

    public AnotherEvent() {
        id = 0;
        idTemplateAnotherHour = 0;
        notificationBefore = 0;
        date = new Date();
        note = "";
    }
    public AnotherEvent(int id, @NotNull TemplateAnotherEvent templateAnotherEvent, @NotNull Date date, @NotNull String note, int notificationBefore, int repeatForNextWeek) {
        this();
        setId(id);
        setTemplateAnotherEvent(templateAnotherEvent);
        setDate(date);
        setNote(note);
        setNotificationBefore(notificationBefore);
        setRepeatForNextWeek(repeatForNextWeek);
    }

    public int getRepeatForNextWeek() {
        return repeatForNextWeek;
    }

    public AnotherEvent setRepeatForNextWeek(int repeatForNextWeek) {
        this.repeatForNextWeek = repeatForNextWeek;
        return this;
    }

    public static RealmResults<AnotherEvent> anotherEventsFromPeriod(Date from, Date to){
        Realm realm = Realm.getDefaultInstance();
        List<RealmResults<AnotherEvent>> list = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                list.add(realm.where(AnotherEvent.class).between("date", from, to).findAll());
            }
        });
        Log.d(TAG,  "anotherEventListFromPeriod" + list.get(ConstantApplication.ZERO).toString());
        return (list.get(ConstantApplication.ZERO));
    }

    public int getNotificationBefore() {
        return notificationBefore;
    }

    public AnotherEvent setNotificationBefore(int notificationBefore) {
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
    public TemplateAnotherEvent getTemplateAnotherEvent() {
        return DBManager.copyObjectFromRealm(DBManager.read(TemplateAnotherEvent.class, ConstantApplication.ID, idTemplateAnotherHour));
    }
    public AnotherEvent setTemplateAnotherEvent(@NotNull TemplateAnotherEvent templateAnotherEvent) {
        // TODO setTemplateAcademicHour - проверка
        this.idTemplateAnotherHour = templateAnotherEvent.getId();
        return this;
    }

    @NotNull
    public Date getDate() {
        return date;
    }
    public AnotherEvent setDate(@NotNull Date date) {
        // TODO setDateTime - проверка
        this.date = date;
        return this;
    }

    @NotNull
    public String getNote() {
        return note;
    }
    public AnotherEvent setNote(@NotNull String note) {
        // TODO setNote - проверка
        this.note = note;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        AnotherEvent that = (AnotherEvent) o;
        return Objects.equals(idTemplateAnotherHour, that.idTemplateAnotherHour) &&
                Objects.equals(date, that.date) &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTemplateAnotherHour, date, note);
    }

    @Override
    public String toString() {
        return "AcademicHour{" +
                "id=" + id +
                ", idTemplateAcademicHour=" + idTemplateAnotherHour +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
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
        RealmResults<TemplateAnotherEvent> existingEntities =
                DBManager.readAll(TemplateAnotherEvent.class);
        for (TemplateAnotherEvent entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        setTemplateAnotherEvent(getTemplateAnotherEvent());
        setDate(getDate());
        setNote(getNote());

        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception {
        try {
            setTemplateAnotherEvent(getTemplateAnotherEvent());
            setDate(getDate());
            setNote(getNote());
        } catch(RuntimeException ex) {
            throw new Exception("Entity = ", ex);
        }
    }
    @Override
    public AnotherEvent createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }

        return this;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected AnotherEvent(Parcel in) {
        id = in.readInt();
        idTemplateAnotherHour = in.readInt();
        date = (Date) in.readSerializable();
        note = in.readString();
        repeatForNextWeek = in.readInt();
        notificationBefore = in.readInt();
    }
    public static final Creator<AnotherEvent> CREATOR = new Creator<AnotherEvent>() {
        @Override
        public AnotherEvent createFromParcel(Parcel in) {
            return new AnotherEvent(in);
        }

        @Override
        public AnotherEvent[] newArray(int size) {
            return new AnotherEvent[size];
        }
    };

    public static void setNotifaction(Context context, AcademicHour academicHour){
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

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idTemplateAnotherHour);
        dest.writeSerializable(date);
        dest.writeString(note);
        dest.writeInt(repeatForNextWeek);
        dest.writeInt(notificationBefore);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public AnotherEvent clone() throws CloneNotSupportedException {
        return (AnotherEvent) super.clone();
    }
}
