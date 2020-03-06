package com.hpcc.kursovaya.dao.entity.setting;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hpcc.kursovaya.dao.entity.schedule.call.CallScheduleDay;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Setting {
    private BackupDB backupDB;
    private CallScheduleDay callScheduleDay;

    {
        backupDB = new BackupDB();
        callScheduleDay = new CallScheduleDay();
    }
    public Setting() {

    }
    public Setting(BackupDB backupDB, CallScheduleDay callScheduleDay) {
        setBackupDB(backupDB);
        setCallScheduleDay(callScheduleDay);
    }

    public BackupDB getBackupDB() {
        return backupDB;
    }
    public Setting setBackupDB(BackupDB backupDB) {
        this.backupDB = backupDB;
        return this;
    }

    public CallScheduleDay getCallScheduleDay() {
        return callScheduleDay;
    }
    public Setting setCallScheduleDay(CallScheduleDay callScheduleDay) {
        this.callScheduleDay = callScheduleDay;
        return this;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "backupDB=" + backupDB +
                ", callScheduleDay=" + callScheduleDay +
                '}';
    }
}
