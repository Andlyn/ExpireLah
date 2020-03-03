package com.ci6222.expiredlah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    static ImageView imageView;
    static EditText text_nmae ;
    static EditText text_date;
    static EditText text_location;
    static Bitmap mBitmap;
    static MainActivity MainA;
    public boolean isEdit = false;
    public String existID;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Item_add_control itemFragment =
                (Item_add_control)
                        getSupportFragmentManager().findFragmentById(R.id.fragment);
        imageView = Item_add_control.imageView;
        text_nmae = (EditText)findViewById(R.id.tv1);
        text_date = (EditText)findViewById(R.id.tv2);
        text_location = (EditText)findViewById(R.id.tv3);
        databaseHelper = new DatabaseHelper(this,"test_db",null,1);
        db = databaseHelper.getWritableDatabase();
        String ID[] = getIntent().getStringArrayExtra("Initi");
        if(ID!=null&&ID.length!=0){

            text_nmae.setText(ID[1]);
            text_date.setText(ID[2]);
            text_location.setText(ID[3]);
            isEdit =true;
            existID = ID[0];
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(Uri.fromFile(new File(ID[4]))));
            }
            catch (Exception e)
            {}
            imageView.setImageBitmap(bitmap);
            mBitmap =bitmap;

        }

    }

    public void OpenAlbum(View v)
    {
        imageView = Item_add_control.imageView;
        openAlbum();
    }

    public void openAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (100 == requestCode) {
            if (data != null) {
                try {
                   mBitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(data.getData()));
                    imageView.setImageBitmap(mBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if(0 == requestCode){

                try {
                    mBitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(imageUri));
                    imageView.setImageBitmap(mBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

    }
    private String mTempPhotoPath;
    private Uri imageUri;
    public final static int CAMERA_REQUEST_CODE = 0;
    public final static int GALLERY_REQUEST_CODE = 1;
    public void OpenCamera(View v){
        if (ContextCompat.checkSelfPermission(AddActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_REQUEST_CODE);

        }else {
            takePhoto();
        }

    }
    private void takePhoto(){
        // 跳转到系统的拍照界面
        imageView = Item_add_control.imageView;
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        /*imageUri = Uri.fromFile(new File(mTempPhotoPath));*/
        imageUri = FileProvider.getUriForFile(AddActivity.this,
                AddActivity.this.getApplicationContext().getPackageName() +".my.provider",
                new File(mTempPhotoPath));
        //intentToTakePhoto.setDataAndType(imageUri, "image/*");
        //下面这句指定调用相机拍照后的照片存储的路径
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, CAMERA_REQUEST_CODE);
    }
    public void Save(View v){


        //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd ");
        String name = text_nmae.getText().toString();
        String date = text_date.getText().toString();
        String location = text_location.getText().toString();
        ContentValues values = new ContentValues();
        UUID uuid=UUID.randomUUID();

        values.put("name",name);
        //Date datetime=simpleDateFormat.parse(date);
        values.put("EXPdate",date);
        values.put("location",location);

        String path = "";
        try
        {
            path = saveBitmap(mBitmap, uuid.toString());
        }
        catch (Exception e)
        {

        }
        values.put("picture",path);
        if(!isEdit){
            values.put("id", uuid.toString());
            db.insert("Product",null,values);
        }
        else{
            db.update("Product",values,"ID ='"+existID+"'",new String[]{});
        }
        this.finish();
        MainA.getValue(true);


        }
     String saveBitmap(Bitmap bm, String name)
             throws Exception
     {
        String tempFilePath = Environment.getExternalStorageDirectory() + "/"
                + getPackageName() + "/" + name + ".jpg" ;
        File tempFile = new File(tempFilePath);
        if (!tempFile.exists())
        {
            if (!tempFile.getParentFile().exists())
            {
                tempFile.getParentFile().mkdirs();
            }
        }

        tempFile.delete();
        tempFile.createNewFile();

        int quality = 100;
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);

        bos.flush();
        bos.close();

        bm.recycle();

        return tempFilePath;
    }

    }

