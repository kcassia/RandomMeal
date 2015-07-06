package com.example.randommeal;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ListItem extends LinearLayout {

    private TextView nameText;

    public ListItem(Context context) {
        super(context);

       init(context);

    }

    public ListItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_item, this, true);

        nameText = (TextView)findViewById(R.id.nameText);
    }

    public void setNames(String data){nameText.setText(data);}
    public String getNames(){return nameText.getText().toString();}
}
