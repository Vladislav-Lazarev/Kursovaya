package com.hpcc.kursovaya.dao.entity.setting;

import com.hpcc.kursovaya.dao.entity.schedule.call.CallScheduleDay;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Setting extends RealmObject {
    private RealmList<BackupDB> backupDBList;
    private CallScheduleDay callScheduleDay;

    {
        backupDBList = new RealmList<>();
        callScheduleDay = new CallScheduleDay();
    }
    public Setting() {

    }
    public Setting(@NotNull RealmList<BackupDB> backupDBList, @NotNull CallScheduleDay callScheduleDay) {
        setBackupDBList(backupDBList);
        setCallScheduleDay(callScheduleDay);
    }

    public RealmList<BackupDB> setBackupDBList() {
        return backupDBList;
    }
    public Setting setBackupDBList(@NotNull RealmList<BackupDB> backupDBList) {
        // TODO setBackupDB - проверка
        this.backupDBList = backupDBList;
        return this;
    }

    public CallScheduleDay getCallScheduleDay() {
        return callScheduleDay;
    }
    public Setting setCallScheduleDay(@NotNull CallScheduleDay callScheduleDay) {
        // TODO setCallScheduleDay - проверка
        this.callScheduleDay = callScheduleDay;
        return this;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "backupDB=" + backupDBList.toString() +
                ", callScheduleDay=" + callScheduleDay +
                '}';
    }
}
