package com.hpcc.kursovaya.ui.groups;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TouchDelegate;
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
import com.hpcc.kursovaya.dao.entity.Group;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class GroupListAdapter extends ArrayAdapter<Group> {
    private static final String TAG = "GroupListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView speciality;
        TextView course;
        TextView name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        Group group = new Group()
                .setName(getItem(position).getName())
                .setSpecialty(getItem(position).getSpecialty())
                .setCourse(getItem(position).getCourse());

        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            holder = new ViewHolder();

            holder.speciality = (TextView) convertView.findViewById(R.id.speciality_label);
            holder.course = (TextView) convertView.findViewById(R.id.course_label);
            holder.name = (TextView) convertView.findViewById(R.id.groupName_label);

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
                    popupMenu.getMenuInflater().inflate(R.menu.popup_group_listview,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.edit_group:
                                    Intent intent = new Intent(mContext, EditGroupActivity.class);
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
                    rect.top -= 2300;    // increase top hit area
                    rect.left -= 2300;   // increase left hit area
                    rect.bottom += 2300; // increase bottom hit area
                    rect.right += 2300;  // increase right hit area
                    parent.setTouchDelegate( new TouchDelegate( rect , button));
                }
            });

            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim: R.anim.load_up_anim);
        result.startAnimation(animation);

        lastPosition = position;

        holder.name.setText(group.getName());
        holder.speciality.setText(group.getSpecialty().getName());
        holder.course.setText(String.valueOf(group.getCourse()));

        return convertView;
    }


    public GroupListAdapter(@NonNull Context context, int resource, @NonNull List<Group> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }
}
