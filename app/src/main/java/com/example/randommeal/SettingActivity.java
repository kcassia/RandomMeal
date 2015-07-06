package com.example.randommeal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class SettingActivity extends Activity {

    private boolean[] selectedStyles = new boolean[5];
    private String selectedRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        selectedRegion = getIntent().getStringExtra("selectedRegion");

        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        for(int i=0; i<5; i++)
            selectedStyles[i] = prefs.getBoolean(selectedRegion + String.valueOf(i), false);

        final DBManager dbManager =  DBManager.getInstance(getApplicationContext());
        Button addStore = (Button) findViewById(R.id.addStore);
        Button deleteStore = (Button) findViewById(R.id.deleteStore);
        Button startButton = (Button) findViewById(R.id.startButton);
        Button showButton = (Button) findViewById(R.id.showStore);
        Button kindButton = (Button) findViewById(R.id.kindButton);

        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingActivity.this);
                ad.setCancelable(false);
                ad.setTitle("음식점 추가");
                final EditText text = new EditText(SettingActivity.this);

                LinearLayout layout = new LinearLayout(SettingActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(text);


                final String[] styleString = {"한식", "일식", "중식", "양식"};
                final RadioButton[] styles = new RadioButton[4];
                final RadioGroup styleGroup = new RadioGroup(SettingActivity.this);
                for(int i=0; i<styles.length; i++) {
                    styles[i] = new RadioButton(SettingActivity.this);
                    styleGroup.addView(styles[i]);
                    styles[i].setText(styleString[i]);
                }

                layout.addView(styleGroup);
                TextView tv = new TextView(getApplicationContext());
                tv.setText(" ※ 미선택시 기타로 선택됩니다.");
                tv.setTextColor(Color.RED);
                tv.setTextSize(15);
                layout.addView(tv);


                ad.setView(layout);
                ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbManager.getReadableDatabase();

                        String sql = "select name from MEAL where region = ? and name = ?";
                        Cursor cursor = db.rawQuery(sql, new String[]{selectedRegion, text.getText().toString()});

                        if(cursor.moveToNext())
                        {
                            Toast.makeText(getApplicationContext(), "이미 해당 음식점이 존재합니다.", Toast.LENGTH_SHORT).show();
                            db.close();
                        }
                        else
                        {
                            db.close();
                            db = dbManager.getWritableDatabase();
                            String style = "기타";
                            for(int j=0; j<styles.length; j++)
                                if(styleGroup.getCheckedRadioButtonId() == styles[j].getId())
                                    style = styleString[j];

                            db.execSQL("insert into MEAL(name, region, style) values('" + text.getText().toString() + "', '" +  selectedRegion + "', '" + style +"');");
                            db.close();
                            Toast.makeText(getApplicationContext(), "음식점 추가 완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ad.show();
            }
        });
        deleteStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingActivity.this);
                ad.setCancelable(false);
                ad.setTitle("음식점 삭제");
                final EditText text = new EditText(SettingActivity.this);
                ad.setView(text);
                ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbManager.getReadableDatabase();
                        String sql = "select * from MEAL where region = ? and name = ?";
                        Cursor cursor = db.rawQuery(sql, new String[]{selectedRegion, text.getText().toString()});
                        if(cursor.moveToNext())
                            Toast.makeText(getApplicationContext(), "삭제 완료.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "삭제할 음식점이 없습니다.", Toast.LENGTH_SHORT).show();
                        db.close();

                        db = dbManager.getWritableDatabase();
                        db.delete("MEAL", "region=? and name=?", new String[]{selectedRegion, text.getText().toString()});
                        db.close();

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ad.show();
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingActivity.this, ListActivity.class);
                intent.putExtra("selectedRegion", selectedRegion);
                intent.putExtra("selectedStyles", selectedStyles);
                startActivity(intent);
            }
        });

        kindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(boolean z : selectedStyles)
                    Log.d("시작", String.valueOf(z));

                final String[] styles = {"한식", "일식", "중식", "양식", "기타"};
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingActivity.this);
                ad.setTitle("종류");
                ad.setMultiChoiceItems(styles, selectedStyles,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                selectedStyles[which] = isChecked;
                            }
                        });
                ad.setPositiveButton("설정",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                ad.show();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, RandomActivity.class);
                intent.putExtra("selectedRegion", selectedRegion);
                intent.putExtra("selectedStyles", selectedStyles);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for(int i=0; i<5; i++)
            editor.putBoolean(selectedRegion+ String.valueOf(i), selectedStyles[i]);
        editor.apply();

    }
}
