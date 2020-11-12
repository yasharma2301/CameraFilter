package com.example.camerafilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity2 extends AppCompatActivity {

    // constant variables from codes
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 110;
    public static final int READ_EXTERNAL_STORAGE_CODE = 105;
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 106;

    // declaring variables
    Button capture, save;
    ImageView image;
    Bitmap globalBitmap;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        // connecting variables to XML
        save = findViewById(R.id.save);
        capture = findViewById(R.id.capture);
        image = findViewById(R.id.image);

        // checking for read storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }

        // checking for write storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
        }

        // on click listener on capture button
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission();
            }
        });

        // on click listener on save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.getDrawable() != null) {
                    saveImageToDirectory();
                } else {
                    Toast.makeText(Activity2.this, "First Click an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // save method to save the click bitmap in directory
    private void saveImageToDirectory() {

        // get the bitmap that has been clicked
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        // log the value to see if its really created {not necessary}
        Log.d("MAP", "saveImageToGallery: " + bitmap.toString());

        // create an outputStream
        FileOutputStream outputStream = null;

        // get the directory to store file
        File file = Environment.getExternalStorageDirectory();

        // create a new file
        File dir = new File(file.getAbsolutePath() + "/cam_filter_images");

        // boolean to check if dir exists or not
        boolean isDirectoryCreated= dir.exists();

        // if does not exist create directory
        if (!isDirectoryCreated) {
            isDirectoryCreated= dir.mkdirs();
        }

        // now as the directory is created save our image bitmap
        if(isDirectoryCreated) {
            // unique file name
            String filename = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, filename);

            // try catch as it can generate exceptions
            try {
                // set the out put stream
                // streams in general work as a tube to pass data, from one side data chunks is passed and on other they are collected to form original data/file
                outputStream = new FileOutputStream(outFile);
                // compress our bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                // catching data chunks
                outputStream.flush();
                // streams can create memory leaks that's why you should use .close() method
                outputStream.close();
                // This image is saved in directory, to save in gallery you have to use a broadcast method
                // This may not work on some APIs, for that read stackoverflow :)
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outFile));
                sendBroadcast(intent);
                // finally make a toast message that it has been saved
                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            // if there was some error in saving prompt from here
            Toast.makeText(this, "Directory could't be formed!", Toast.LENGTH_SHORT).show();
        }
    }


    private void askForPermission() {
        // ask for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if all permissions are correct, open camera
                openCamera();
            } else {
                // if camera permissions are not granted, prompt
                Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // take your image into a bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            // apply grey filter
            bitmap = toGrayscale(bitmap);
            globalBitmap = bitmap;
            // set in image view
            image.setImageBitmap(bitmap);
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        // as B/W image is needed decrease saturation levels
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    private File createImageFile() throws IOException {
        // Create a unique image file name using timeStamps
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera in the device
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("IOException", "dispatchTakePictureIntent: " + ex.toString());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                // putExtra method as now we are not using data property to get a low quality image
                // instead we are using photoURI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}

