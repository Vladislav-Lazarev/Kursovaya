package com.hpcc.kursovaya.ui.subjects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Subject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class SubjectListAdapter extends ArrayAdapter<Subject> {
    private static final String TAG = "SubjectListAdapter";

    private int mResource;
    private Context mContext;
    private int lastPosition =-1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Subject subject = new Subject(
                1,
                getItem(position).getName(),
                getItem(position).getSpecialityList(),
                getItem(position).getCountHours(),
                getItem(position).getCourseList(),
                getItem(position).getColor());


        final View result;
        ViewHolder holder;


        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            holder = new ViewHolder();

            holder.speciality = (TextView) convertView.findViewById(R.id.speciality_label);
            holder.course = (TextView) convertView.findViewById(R.id.course_label);
            holder.name = (TextView) convertView.findViewById(R.id.subjectName_label);

            //setting onclick action on button
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
                    popupMenu.getMenuInflater().inflate(R.menu.popup_subject_listview,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.edit_subject:
                                    Intent intent = new Intent(mContext, EditSubjectActivity.class);
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

            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

       Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim:R.anim.load_up_anim);
       result.startAnimation(animation);

        holder.name.setText(subject.getName());
        holder.speciality.setText(subject.getSpecialityList().first().getName());
        holder.course.setText("Pidor"); // (Integer.toString(subject.getCourseList().get(1).getNumber()));
        return convertView;
    }
    //



    static class ViewHolder{
        TextView speciality;
        TextView course;
        TextView name;
    }


    public SubjectListAdapter(@NonNull Context context, int resource, @NonNull List<Subject> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }
    //
}
