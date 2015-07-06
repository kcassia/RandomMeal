package com.example.randommeal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by 박계현 on 2014-11-12.
 */
public class ListAdapter extends BaseAdapter
{
    private Context context;
    private String[] stores;

    public ListAdapter(Context context, String[] stores)
    {
        this.context = context;
        this.stores = stores;
    }

    @Override
    public int getCount() {
        return stores.length;
    }

    @Override
    public Object getItem(int position) {
        return stores[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ListItem layout = new ListItem(context.getApplicationContext());

        layout.setNames(stores[position]);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setCancelable(false);
                ab.setTitle("평점을 선택해주세요");

                LinearLayout innerLayout = new LinearLayout(context);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                final int[] styleString = {0, 1, 2, 3, 4, 5};
                final RadioButton[] styles = new RadioButton[6];
                final RadioGroup styleGroup = new RadioGroup(context);
                for(int i=0; i<styles.length; i++) {
                    styles[i] = new RadioButton(context);
                    styleGroup.addView(styles[i]);
                    styles[i].setText(String.valueOf(styleString[i]));
                }

                innerLayout.addView(styleGroup);
                ab.setView(innerLayout);

                ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Store store = null;
                        for(int j=0; j<styles.length; j++)
                            if(styleGroup.getCheckedRadioButtonId() == styles[j].getId())
                                store = new Store(layout.getNames(), (double)styleString[j]);

                        if(store!=null) {
                            SendThread sendThread = new SendThread(store);
                            sendThread.start();
                        }

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ab.show();

            }
        });
        return layout;
    }
}
