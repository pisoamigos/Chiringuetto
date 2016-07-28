package com.example.chiringuetto.chiringuetto;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Retratado extends AppCompatActivity {
    private MediaPlayer mp;
private Button galeria;
    private Button camara;
  private  Button guardar;
   private Bitmap aux;
    private Bitmap bmp;
     private ImageView fondo;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retratado);
        fondo = (ImageView) findViewById(R.id.imageView5);
        camara= (Button) findViewById(R.id.button2);
        Button btnShare = ((Button)findViewById(R.id.button4));
        galeria = (Button) findViewById(R.id.button);
        guardar = (Button) findViewById(R.id.button3);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);

            }
        });
        btnShare.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        SharePic();
                    }});
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaStore.Images.Media.insertImage(getContentResolver(), aux, "Chiringuetto", "Retra");
                Toast.makeText(Retratado.this, "Guardado en la galería", Toast.LENGTH_SHORT).show();
                mp= MediaPlayer.create(Retratado.this, R.raw.hasquedadoretra);
                mp.start();

            }
        });
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i=  new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, CAMERA);


            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = intent.getData();
                    combinaImagenes(selectedImage);
break;

                }
            case CAMERA:
                if(resultCode == RESULT_OK) {
                    Bundle ext = intent.getExtras();
                    bmp = (Bitmap) ext.get("data");
                    combinaImagenes2(bmp);
                    break;
                }
        }
    }

    public void combinaImagenes(Uri uri){
        Drawable res;
        Bitmap b=null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            res = Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
            res = getResources().getDrawable(R.mipmap.definitivo
            );
        }

            if (res.getIntrinsicHeight() > 1580 || res.getIntrinsicWidth() > 1020) {
                b = Utiles.cropBitmap(Utiles.drawableToBitmap(res), 1880, 1320);
            } else {
                b = Utiles.drawableToBitmap(res);
            }

        Drawable res2= new BitmapDrawable(getResources(), b);
        Resources r = getResources();
        Drawable[] layers = new Drawable[2];
        layers[0] = res2;
        //layers[0]= resize(layers[0]);
        layers[1] = r.getDrawable(R.mipmap.definitivo);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        int width = layerDrawable.getIntrinsicWidth();
        int height = layerDrawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        layerDrawable.draw(canvas);
        fondo.setImageDrawable(layerDrawable);
        fondo.buildDrawingCache();
        aux = fondo.getDrawingCache();
    }
    public void combinaImagenes2(Bitmap bit) {
        Drawable res = new BitmapDrawable(getResources(), bit);
        Bitmap bb=null;
        if (res.getIntrinsicHeight() > 1580 || res.getIntrinsicWidth() > 1020) {
            bb = Utiles.cropBitmap(Utiles.drawableToBitmap(res), 1880, 1320);
         } else {
        bb = Utiles.drawableToBitmap(res);
    }
            Drawable res2 = new BitmapDrawable(getResources(), bb);
            Resources r = getResources();
            Drawable[] layers = new Drawable[2];
            layers[0] = res2;
            //layers[0]= resize(layers[0]);
            layers[1] = r.getDrawable(R.mipmap.definitivo);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            int width = layerDrawable.getIntrinsicWidth();
            int height = layerDrawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            layerDrawable.draw(canvas);
            fondo.setImageDrawable(layerDrawable);
            fondo.buildDrawingCache();
            aux = fondo.getDrawingCache();
        }


    private void SharePic(){
           //Se carga la imagen que se quiere compartir
           //Se guarda la imagen en la SDCARD
           ByteArrayOutputStream bytes = new ByteArrayOutputStream();
           aux.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
           File f = new File( Environment.getExternalStorageDirectory() + File.separator + "tmp" + File.separator + "retratado.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }
        //compartir imagen
        Intent share = new Intent(Intent.ACTION_SEND);
           share.setType("image/jpeg");
           share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
           share.putExtra(Intent.EXTRA_TEXT, "Mi imagen");
           startActivity(Intent.createChooser(share, "Compartir imagen"));
    }


}
