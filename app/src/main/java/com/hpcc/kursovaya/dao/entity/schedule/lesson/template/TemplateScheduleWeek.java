package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateScheduleWeek extends RealmObject {
    private static final String TAG = TemplateScheduleWeek.class.getSimpleName();

    protected static RealmList<Integer> convert(List<TemplateAcademicHour> templateScheduleDayList) {
        RealmList<Integer> result = new RealmList<>();

        for (TemplateAcademicHour templateAcademicHour : templateScheduleDayList){
            result.add(templateAcademicHour.getId());
        }
        return result;
    }
    protected static List<List<TemplateAcademicHour>> convert(RealmList<Integer> idScheduleDayList) {
        List<List<TemplateAcademicHour>> result = new RealmList<>();
        List<TemplateAcademicHour> list = new RealmList<>();
        int i = 0;

        for (Integer id : idScheduleDayList){
            if (i++ == ConstantEntity.MAX_COUNT_LESSON * 2){
                result.add(list);
                list.clear();
                i = 0;
            }
            list.add(DBManager.read(TemplateAcademicHour.class, ConstantEntity.ID, id));
        }

        if (list.size() != ConstantEntity.MAX_COUNT_WEEK) {
            throw new RuntimeException("Exception convert = " + list.size());
        }
        return result;
    }

    @PrimaryKey
    private int id;
    private RealmList<Integer> idScheduleDayList;

    public TemplateScheduleWeek() {
        id = 0;
        idScheduleDayList = new RealmList<>();
    }
    public TemplateScheduleWeek(@NotNull List<TemplateAcademicHour> templateScheduleDayList) {
        setTemplateScheduleDayList(templateScheduleDayList);
    }

    private void setId(int id){
        if (id < ConstantEntity.ONE){
            throw new RuntimeException("Exception! setId()");
        }
        this.id = id;
    }
    public int getId() {
        return id;
    }

    /*public void clear() {
        idScheduleDayList.clear();
    }

    public boolean addAll(int index, @NonNull Collection<? extends RealmList<Integer>> c) {
        return idScheduleDayList.addAll(index, c);
    }

    @Nullable
    public RealmList<Integer> get(int location) {
        return idScheduleDayList.get(location);
    }

    public int size() {
        return idScheduleDayList.size();
    }

    public boolean isEmpty() {
        return idScheduleDayList.isEmpty();
    }*/

    @NotNull
    public List<List<TemplateAcademicHour>> getTemplateScheduleDayList() {
        return convert(idScheduleDayList);
    }
    public TemplateScheduleWeek setTemplateScheduleDayList(@NotNull List<TemplateAcademicHour> templateScheduleDayList) {
        if (templateScheduleDayList.size() > ConstantEntity.ZERO) {
            throw new RuntimeException("Exception! setTemplateScheduleDayList()");
        }
        this.idScheduleDayList = convert(templateScheduleDayList);
        return this;
    }

    @Override
    public String toString() {
        return "TemplateScheduleWeek{" +
                "id=" + id +
                ", idScheduleDayList=" + idScheduleDayList.toString() +
                '}';
    }
}
