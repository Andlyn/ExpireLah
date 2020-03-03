package com.ci6222.expiredlah;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    LinearLayout LL;
    public static Boolean isCurrent = true;
    public  static  MainActivity MainA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LL = findViewById(R.id.cardlinear);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        AddActivity.MainA = MainActivity.this;
        MainA = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        databaseHelper = new DatabaseHelper(this,"test_db",null,1);
        db = databaseHelper.getWritableDatabase();
        //this.onget

    }

    @Override
    protected void onStart(){
        super.onStart();
        getValue(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.show_his)
        {
            isCurrent = false;
            getValue(false);
        }
        else if (id == R.id.remove_all)
        {
            removeAll();
        }
        else if (id == R.id.show_now)
        {
            isCurrent =true;
            getValue(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public  void getValue(Boolean iscurrent)
    {
        Cursor rs;
        if(iscurrent){
            rs=db.query("Product",new String[]{"name","EXPdate","location","picture","id"},"location != 'removed'",null,null,null,"EXPdate");
        }
        else{
            rs=db.query("Product",new String[]{"name","EXPdate","location","picture","id"},"location == 'removed'",null,null,null,"EXPdate");

        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments =fragmentManager.getFragments();
        for(int i = 0;i<fragments.size();i++){
            fragmentManager.beginTransaction().remove(fragments.get(i)).commitAllowingStateLoss();
        }
        while(rs.moveToNext()){
            String Name = rs.getString(rs.getColumnIndex("name"));
            String Date = rs.getString(rs.getColumnIndex("EXPdate"));
            String Location = rs.getString(rs.getColumnIndex("location"));
            String pic = rs.getString(rs.getColumnIndex("picture"));
            String ID = rs.getString(rs.getColumnIndex("id"));
            Cards card = new Cards();


            FragmentTransaction transition = fragmentManager.beginTransaction();
            transition.add(R.id.cardlinear, card);
            transition.show(card);
            transition.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
            Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(Uri.fromFile(new File(pic))));
                    }
                    catch (Exception e)
                    {}
            //card.setContent(Name,"Expired Date is "+Date,"Stored in "+Location, bitmap,ID, db,this, pic);

            card.setContent(Name,Date,Location, bitmap,ID, db,this, pic);
        }
        //text_location.setText(textview_data);
    }
    public  void removeAll()
    {

        Calendar ca = Calendar.getInstance();
        int  mYear = ca.get(Calendar.YEAR);
        int  mMonth = ca.get(Calendar.MONTH);
        int  mDay = ca.get(Calendar.DAY_OF_MONTH);
        String date = ca.get(Calendar.YEAR)+"-";
        if((ca.get(Calendar.MONTH)+1)<10){
            date = date+ "0"+ (ca.get(Calendar.MONTH)+1)+"-";
        }
        else{
            date = date+ (ca.get(Calendar.MONTH)+1)+"-";
        }
        if (ca.get(Calendar.DATE)<10){
            date = date+"0"+ca.get(Calendar.DATE);
        }
        else{
            date = date+ca.get(Calendar.DATE);
        }

        //Log.i("dddd",date);
        Cursor rs=db.query("Product",new String[]{"name","EXPdate","location","picture","id"}," location != 'removed' and EXPdate < '" + date.toString()+"'",null,null,null,"EXPdate");

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments =fragmentManager.getFragments();
        for(int i = 0;i<fragments.size();i++){
            fragmentManager.beginTransaction().remove(fragments.get(i)).commitAllowingStateLoss();
        }


        while(rs.moveToNext()){
            String Name = rs.getString(rs.getColumnIndex("name"));
            String Date = rs.getString(rs.getColumnIndex("EXPdate"));
            String Location = rs.getString(rs.getColumnIndex("location"));
            String pic = rs.getString(rs.getColumnIndex("picture"));
            String ID = rs.getString(rs.getColumnIndex("id"));
            ContentValues values = new ContentValues();
            values.put("location","removed");
            db.update("Product",values, "ID ='"+ID+"'", new String[]{});

        }
        getValue(true);
        //text_location.setText(textview_data);
    }

}
