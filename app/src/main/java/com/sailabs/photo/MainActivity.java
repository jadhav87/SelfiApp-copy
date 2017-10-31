package com.sailabs.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.artiligence.photo.R;

public class MainActivity extends Activity {
    ImageButton cam_btn;
    static int TAKE_PIC = 1;
    Uri outPutfileUri;
    ImageView mImageView;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView1);
        cam_btn = (ImageButton) findViewById(R.id.cam_btn);

        cam_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(),
                        "Test.jpg");
                outPutfileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(intent, TAKE_PIC);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PIC && resultCode == RESULT_OK) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);

                new File(outPutfileUri.getPath()).delete();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap dBmp = null;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            dBmp = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.bg_landcsap, o);

            int wid = o.outWidth - 20;
            int hgt = o.outHeight - 150;

            Drawable drawable = getResources().getDrawable(R.drawable.bg_landcsap);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, wid, hgt, true);
            Bitmap newImage = overlay(drawableToBitmap(drawable), scaledBitmap);

            String imageName = "WHD" + Long.toString(System.currentTimeMillis()) + ".jpg";
            File storagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/WHD").getAbsolutePath());

            storagePath.mkdirs();

            File myImage = new File(storagePath, imageName);

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                newImage.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();

                MediaScannerConnection.scanFile(MainActivity.this, new String[]{myImage.getPath()}, new String[]{"image/jpeg"}, null);
            } catch (FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch (IOException e) {
                Log.d("In Saving File", e + "");
            }

            newImage = null;

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
            startActivity(intent);

        }
    }

    //----------------------------------------------------------------------------------------------------------//
    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        canvas.drawBitmap(bmp2, 10, 10, null);

        return bmOverlay;
    }

    public Bitmap drawableToBitmap(Drawable drawable) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap dBmp = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.bg_landcsap, o);

        int width = o.outWidth;

        width = width > 0 ? width : 1;
        int height = o.outHeight;
        height = height > 0 ? height : 1;

        Bitmap bitmap = null;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		if (MainActivity.this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		}else {
//			bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
//		}
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public String getOrientation() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getOrientation() == Surface.ROTATION_0) {
            // landscape oriented devices
            return "landscape";
        } else {
            // portrait oriented device
            return "portrait";
        }
    }

}
