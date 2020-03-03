package com.ci6222.expiredlah;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Item_add_control extends Fragment {


    private static TextView textview;
    public static ImageView imageView;
    Calendar ca = Calendar.getInstance();
    int  mYear = ca.get(Calendar.YEAR);
    int  mMonth = ca.get(Calendar.MONTH);
    int  mDay = ca.get(Calendar.DAY_OF_MONTH);
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_add,
                container, false);

        textview = (TextView) view.findViewById(R.id.tv2);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mYear = year;
                                mMonth = month;
                                mDay = dayOfMonth;
                                String date = year+"-";
                                if(month+1<10){
                                    date = date+ "0"+ (month+1)+"-";
                                }
                                else{
                                    date = date + (month+1)+"-";
                                }
                                if (dayOfMonth<10){
                                    date = date + "0"+dayOfMonth;
                                }
                                else{
                                    date = date +dayOfMonth;
                                }

                                    textview.setText(date);


                            }
                        },
                        mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        imageView = view.findViewById(R.id.imageView2);
        Button PhotoBuuton = view.findViewById(R.id.button);
        return view;
    }

    public void changeTextProperties(int fontsize, String text)
    {
        textview.setTextSize(fontsize);
        textview.setText(text);
    }

}