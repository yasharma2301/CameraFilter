package com.example.camerafilter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity2 extends AppCompatActivity {
    Button capture,save;
    ImageView image;

    public static final int CAMERA_PERMISSION_CODE = 102;
    public static final int CAMERA_OPEN_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        capture = findViewById(R.id.capture);
        save = findViewById(R.id.save);
        image = findViewById(R.id.image);

        // Toast
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               askForCameraPermission();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Activity2.this,"Capture button clicked",Toast.LENGTH_SHORT).show();
            }
        });

        // take permission from user

    }

    void askForCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            // if camera permission is not already taken
            // take-permission
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }else{
            openCamera();
        }
    }

    void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_OPEN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            // code goes here
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = convertToGreyScale(bitmap);
            image.setImageBitmap(bitmap);
        }
    }

//    Bitmap convertToGreyScale(Bitmap originalBitmap){
//        int width,height;
//        height = originalBitmap.getHeight();
//        width = originalBitmap.getWidth();
//
//        Bitmap newGreyScaledBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
//
//        Canvas c = new Canvas(newGreyScaledBitmap);
//        Paint paint = new Paint();
//
//        ColorMatrix
//
//    }

// huji
    // own filters

    public Bitmap convertToGreyScale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bmpGrayscale);

        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();

        cm.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);

        paint.setColorFilter(filter);

        c.drawBitmap(bmpOriginal, 0, 0, paint);

        return bmpGrayscale;
    }
}

// take picture -> image view
//
// image -> phone will send this image in some temporary directory
// extract the location

// conversion of image in b/w