package com.example.randommeal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MainActivity extends Activity {

    private String selectedRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DBManager dbManager =  DBManager.getInstance(getApplicationContext());
        TextView startText = (TextView) findViewById(R.id.startText);

        SQLiteDatabase db = dbManager.getReadableDatabase();
        String sql = "select * from MEAL";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext())
        {
            Log.d("_id", String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
            Log.d("name", String.valueOf(cursor.getString(cursor.getColumnIndex("name"))));
            Log.d("region", String.valueOf(cursor.getString(cursor.getColumnIndex("region"))));
            Log.d("style", String.valueOf(cursor.getString(cursor.getColumnIndex("style"))));
        }
        db.close();

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        startText.startAnimation(anim);

        ViewGroup activity = (ViewGroup) findViewById(R.id.mainActivity);
        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setCancelable(false);
                ad.setTitle("구역");
                SQLiteDatabase db = dbManager.getReadableDatabase();
                String sql = "select region from MEAL where not region is null";
                Cursor cursor = db.rawQuery(sql, null);
                ArrayList<String> regions = new ArrayList<String>();
                while(cursor.moveToNext()) {
                    regions.add(cursor.getString(cursor.getColumnIndex("region")));
                }
                HashSet hashSet = new HashSet(regions);
                regions.clear();
                regions = new ArrayList<String>(hashSet);
                db.close();

                final String[] regionString = regions.toArray(new String[regions.size()]);

                if(!regions.isEmpty())
                    selectedRegion = regionString[0];
                ad.setSingleChoiceItems(regionString, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                selectedRegion = regionString[whichButton];
                            }
                        }).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                                intent.putExtra("selectedRegion", selectedRegion);
                                if(selectedRegion == null)
                                    Toast.makeText(MainActivity.this, "구역이 없습니다.", Toast.LENGTH_SHORT).show();
                                else
                                {
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                Button addButton = new Button(MainActivity.this);
                Button deleteButton = new Button(MainActivity.this);

                LinearLayout layout = new LinearLayout(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                addButton.setLayoutParams(param);
                deleteButton.setLayoutParams(param);

                layout.setLayoutParams(params);
                layout.addView(deleteButton);
                layout.addView(addButton);

                addButton.setText("추가");
                deleteButton.setText("삭제");

                ad.setView(layout);
                final AlertDialog dialog = ad.create();

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                            ad.setCancelable(false);
                            ad.setTitle("구역 추가");
                            final EditText text = new EditText(MainActivity.this);

                            ad.setView(text);
                            ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialog.dismiss();
                                    SQLiteDatabase db = dbManager.getReadableDatabase();
                                    String sql = "select * from MEAL where region = ?";
                                    Cursor cursor = db.rawQuery(sql, new String[]{text.getText().toString()});

                                    if(cursor.moveToNext())
                                    {
                                        Toast.makeText(getApplicationContext(), "이미 해당 구역이 존재합니다.", Toast.LENGTH_SHORT).show();
                                        db.close();
                                    }
                                    else
                                    {
                                        db.close();
                                        db = dbManager.getWritableDatabase();
                                        db.execSQL("insert into MEAL(region) values('" + text.getText().toString() + "');");
                                        db.close();
                                        Toast.makeText(getApplicationContext(), "구역 추가 완료", Toast.LENGTH_SHORT).show();
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
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedRegion == null)
                            Toast.makeText(getApplicationContext(), "삭제할 구역이 없습니다,", Toast.LENGTH_SHORT).show();
                        else {
                            AlertDialog.Builder confirm = new AlertDialog.Builder(MainActivity.this);
                            confirm.setMessage(selectedRegion + " 삭제하시겠습니까?");
                            confirm.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    SQLiteDatabase db = dbManager.getWritableDatabase();
                                    db.delete("MEAL", "region=?", new String[]{selectedRegion});
                                    db.close();
                                    Toast.makeText(getApplicationContext(), "구역 삭제 완료", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            confirm.show();
                        }
                    }
                });

                dialog.show();
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
}
