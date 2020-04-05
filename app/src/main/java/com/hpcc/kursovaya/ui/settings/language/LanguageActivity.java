package com.hpcc.kursovaya.ui.settings.language;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.settings.SettingsFragment;

public class LanguageActivity extends AppCompatActivity {
    private final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_choose_language);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        Resources res = getResources();
        final String[] LANGUAGE = {res.getString(R.string.english),res.getString(R.string.russian),res.getString(R.string.ukranian)};
        ListView languagesLSV = findViewById(R.id.languagesLSV);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listview_item_settings, LANGUAGE);

        final Context context = this;
        languagesLSV.setAdapter(adapter);
        languagesLSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        LocaleManager.setNewLocale(context, "en");
                        break;
                    case 1:
                        LocaleManager.setNewLocale(context, "ru");
                        break;
                    case 2:
                        LocaleManager.setNewLocale(context, "uk-rUA");
                        break;
                    default:
                }
                recreate();
                String selectedItem = LANGUAGE[position];
                Log.i(TAG, selectedItem);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

}
