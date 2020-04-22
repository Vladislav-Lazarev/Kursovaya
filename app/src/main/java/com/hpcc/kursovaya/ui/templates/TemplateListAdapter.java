package com.hpcc.kursovaya.ui.templates;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateScheduleWeek;

import java.util.List;

import io.realm.Sort;

public class TemplateListAdapter extends ArrayAdapter<TemplateScheduleWeek> {
    private static final String TAG = TemplateListAdapter.class.getSimpleName();

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<TemplateScheduleWeek> scheduleWeekList;

    static class ViewHolder {
        TextView name;
    }

    public TemplateListAdapter (@NonNull Context context, int resource, @NonNull List<TemplateScheduleWeek> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        scheduleWeekList = objects;
    }

    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        TemplateScheduleWeek scheduleWeek = new TemplateScheduleWeek(
                getItem(position).getName(),
                getItem(position).getTemplateAcademicHourList());

        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.template_text);
            /*//setting onclick action on button
            final Button button = (Button) convertView.findViewById(R.id.btn_lsvOptions);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    PopupMenu popupMenu = new PopupMenu(mContext,button);
                    //enabling icons in menu
                    try {
                        Field[] fields = popupMenu.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if ("mPopup".equals(field.getName())) {
                                field.setAccessible(true);
                                Object menuPopupHelper = field.get(popupMenu);
                                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                setForceIcons.invoke(menuPopupHelper, true);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popupMenu.getMenuInflater().inflate(R.menu.popup_templates_listview,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.edit_template:
                                    Intent intent = new Intent(mContext, EditTemplateActivity.class);
                                    mContext.startActivity(intent);
                                    return true;
                                case R.id.edit_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle(mContext.getString(R.string.delete_alert_header))
                                            .setMessage(R.string.delete_alert_msg)
                                            .setPositiveButton(R.string.delete_positive,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    })
                                            .setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    final AlertDialog alert = builder.create();
                                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface arg0) {
                                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.sideBar));
                                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.sideBar));
                                        }
                                    });
                                    alert.show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
            //increasing hit area
            final View parentEl = (View) button.getParent();  // button: the view you want to enlarge hit area
            parent.post( new Runnable() {
                public void run() {
                    final Rect rect = new Rect();
                    button.getHitRect(rect);
                    rect.left += 2300;   // increase left hit area
                    rect.right += 2300;  // increase right hit area
                    parent.setTouchDelegate( new TouchDelegate( rect , button));
                }
            });
*/
            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim:R.anim.load_up_anim);
        result.startAnimation(animation);

        lastPosition = position;

        holder.name.setText(scheduleWeek.getName());

        return convertView;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Vlad Code
    public void write(TemplateScheduleWeek object) {
        if (object.existsEntity()) {
            Log.d(TAG, "And\\Edit Entity = " + object);
            Toast.makeText(mContext, R.string.toast_exists_entity, Toast.LENGTH_SHORT).show();
        } else {
            try {
                DBManager.write(object.createEntity());
                Toast.makeText(mContext, R.string.toast_add_edit_entity, Toast.LENGTH_SHORT).show();
            } catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
        }
        notifyDataSetChanged();
    }

    public void update(String nameSort) {
        update(nameSort, Sort.ASCENDING);
    }
    public void update(String nameSort, Sort sort) {
        // Могу сортировтаь по Названию и по кол-во Курсо(не в приоритете)
        scheduleWeekList.clear();
        scheduleWeekList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(TemplateScheduleWeek.class, nameSort, sort)));
        notifyDataSetChanged();
    }

    public void delete(TemplateScheduleWeek object) {
        for (TemplateAcademicHour templateAcademicHour : object.getTemplateAcademicHourList()){
            DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
        }
        DBManager.delete(TemplateScheduleWeek.class, ConstantApplication.ID, object.getId());
        notifyDataSetChanged();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}