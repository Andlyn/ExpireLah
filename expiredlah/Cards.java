package com.ci6222.expiredlah;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;


public class Cards extends Fragment {
    private View view;
        private LinearLayout noData;
        private ListView orderListView;
        public TextView Name;
        public TextView Date;
        public TextView Location;
        public ImageView IV;
        public Button del;
        public Button edit;
        public String Id;
        public String Iname;
        public String Ilocation;
        public String Iuri;
        public String Idate;
        public MainActivity MainA;
        public SQLiteDatabase DB;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)  {
            view = inflater.inflate(R.layout.fragment_cards, container, false);
            Name= (TextView) view.findViewById(R.id.product_name);
            Date= (TextView) view.findViewById(R.id.product_date);
            Location= (TextView) view.findViewById(R.id.product_location);
            IV = (ImageView) view.findViewById(R.id.imageView3);
            del = (Button)view.findViewById(R.id.del);
            del.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(MainActivity.isCurrent){
                        ContentValues values = new ContentValues();
                        values.put("location","removed");
                        DB.update("Product",values, "ID ='"+Id+"'", new String[]{});
                        //DB.delete("Product", "ID ='"+Id+"'", new String[]{});
                        MainA.getValue(true);
                    }
                    else{

                        DB.delete("Product", "ID ='"+Id+"'", new String[]{});
                        //DB.delete("Product", "ID ='"+Id+"'", new String[]{});
                        MainA.getValue(false);
                    }

                }

            });
            edit = (Button)view.findViewById(R.id.edit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        Intent intent=new Intent(MainActivity.MainA, AddActivity.class);
                        intent.putExtra("Initi",new String[]{Id,Iname,Idate,Ilocation,Iuri});
                        startActivity(intent);


                }
            });
            return view;
        }
        public void setContent(String name,String date, String location, Bitmap Uri,String ID,SQLiteDatabase db ,MainActivity ma ,String pic){
            Name.setText(name);

            //Bitmap p = new Bitmap()
            IV.setImageBitmap(Uri);
            Id = ID;
            Iname = name;
            Ilocation = location;
            Iuri = pic;
            Idate =date;
            DB = db;
            MainA = ma;
            Date.setText("Expired date is "+date);
            if(!location.equals("removed")){
                Location.setText("Stored in "+location);
            }
            else{
                Location.setText("removed");
            }
        }


}