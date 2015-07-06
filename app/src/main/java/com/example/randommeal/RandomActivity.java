package com.example.randommeal;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class RandomActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        final boolean[] selectedStyles = getIntent().getBooleanArrayExtra("selectedStyles");
        final String[] styles = {"한식", "일식", "중식", "양식", "기타"};

        final DBManager dbManager = DBManager.getInstance(getApplicationContext());
        final String selectedRegion = getIntent().getStringExtra("selectedRegion");

        final ViewGroup activity = (ViewGroup)findViewById(R.id.randomActivity);
        final ImageView image = (ImageView)findViewById(R.id.image);
        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setVisibility(View.INVISIBLE);

                SQLiteDatabase db = dbManager.getReadableDatabase();
                String sql = "select name from MEAL where not name is null and region = ?";

                boolean isFirst = true;
                boolean isChecked = false;
                for(int i=0; i<selectedStyles.length; i++) {
                    if (selectedStyles[i]) {
                        if(isFirst)
                        {
                            sql += " and style = '" + styles[i] + "'";
                            isFirst = false;
                        }
                        else
                            sql += " or style = '" + styles[i] + "'";
                        isChecked = true;
                    }
                }

                TextView text = new TextView(getApplicationContext());

                if(isChecked) {
                    Cursor cursor = db.rawQuery(sql, new String[]{selectedRegion});
                    ArrayList<String> stores = new ArrayList<String>();
                    while (cursor.moveToNext())
                        stores.add(cursor.getString(cursor.getColumnIndex("name")));
                    HashSet hashSet = new HashSet(stores);
                    stores.clear();
                    stores = new ArrayList<String>(hashSet);

                    if (stores.isEmpty())
                        text.setText("None");
                    else
                        text.setText(stores.get(new Random(System.currentTimeMillis()).nextInt(stores.size())));
                }
                else
                    text.setText("None");
                db.close();
                text.setTextSize(50);
                text.setTextColor(Color.BLACK);
                text.setBackgroundResource(R.drawable.meal);
                text.setGravity(Gravity.CENTER);
                text.setTextColor(Color.RED);
                text.setTypeface(Typeface.DEFAULT_BOLD);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                text.setLayoutParams(params);


                LinearLayout layout = new LinearLayout(RandomActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(text);
                layout.setGravity(Gravity.CENTER);

                setContentView(layout);
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
