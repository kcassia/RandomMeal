package com.example.randommeal;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;


public class ListActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        boolean[] selectedStyles = getIntent().getBooleanArrayExtra("selectedStyles");
        String[] styles = {"한식", "일식", "중식", "양식", "기타"};

        DBManager dbManager = DBManager.getInstance(getApplicationContext());
        String selectedRegion = getIntent().getStringExtra("selectedRegion");


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
        if(isChecked) {
            Cursor cursor = db.rawQuery(sql, new String[]{selectedRegion});
            ArrayList<String> stores = new ArrayList<String>();
            while (cursor.moveToNext())
                stores.add(cursor.getString(cursor.getColumnIndex("name")));
            HashSet hashSet = new HashSet(stores);
            stores.clear();
            stores = new ArrayList<String>(hashSet);

            String[] storeString = stores.toArray(new String[stores.size()]);

            ListView list = (ListView) findViewById(R.id.list);

            ListAdapter adapter = new ListAdapter(this, storeString);
            list.setAdapter(adapter);
        }

        db.close();
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
