package com.esm.convertbitmapintouri;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    ImageView img_pick_show;
    TextView img_url;

    ActivityResultLauncher<Intent> activityResultLauncher;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_pick_show = findViewById(R.id.imageView);
        img_url = findViewById(R.id.textView);

        img_pick_show.setOnClickListener(v -> {
            Intent takePickIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(takePickIntent);
        });


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Bundle extras = result.getData().getExtras();

                    Uri imageUri;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    // For Not losing Image Quality
                    WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,
                            imageBitmap.getHeight(), imageBitmap.getWidth(), false).copy(
                            Bitmap.Config.RGB_565, true));

                    Bitmap bm = result1.get();
                    imageUri = saveImage(bm, MainActivity.this);

                    img_pick_show.setImageURI(imageUri);
                    img_url.setText("" + imageUri);

                });

    }

    Uri saveImage(Bitmap bitmap, Context context) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;


        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "captured_image.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(),
                    "com.esm.convertbitmapintouri" + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return uri;

    }
}