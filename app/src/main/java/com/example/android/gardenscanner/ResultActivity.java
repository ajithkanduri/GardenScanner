package com.example.android.gardenscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    String project_name;
    String site_no;
    String millis;
    String dirpath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        project_name = getIntent().getStringExtra("projectName");
        site_no = getIntent().getStringExtra("siteNo");
        millis = getIntent().getStringExtra("millis");
        String frame_condition = getIntent().getStringExtra("frame_condition");
        String drip_damage = getIntent().getStringExtra("drip_damage");
        String drippers_damaged = getIntent().getStringExtra("drippers");
        String drinage_condition = getIntent().getStringExtra("drinage");
        Log.d("result",frame_condition);
        HashMap<String,String> infected = loadMap("infected");
        String pest_infected = infected.get("0");
        String disease_infected = infected.get("1");
        String nutrient_deficient = infected.get("2");
        String water_insufficient = infected.get("3");
        String temp_wilting = infected.get("4");
        String permanent_wilting = infected.get("5");
        TextView pest = findViewById(R.id.pest);
        TextView disease = findViewById(R.id.disease);
        TextView nutrient = findViewById(R.id.nutrient);
        TextView water = findViewById(R.id.water);
        TextView temp = findViewById(R.id.temp);
        TextView perm = findViewById(R.id.perm);
        TextView fab = findViewById(R.id.fab);
        TextView medium = findViewById(R.id.medium);
        TextView inlet = findViewById(R.id.water_inlet);
        TextView drippers = findViewById(R.id.drippers);
        TextView drip_pipes = findViewById(R.id.drip_pipes);
        TextView drinage = findViewById(R.id.drinage);
        TextView pots_damaged = findViewById(R.id.pots_damaged);
        fab.setText(frame_condition);
        drinage.setText(drinage_condition);
        drippers.setText(drippers_damaged);
        drip_pipes.setText(drip_damage);
        pots_damaged.setText(infected.get("6")+" Missing and "+infected.get("7")+" Broken");
        if(infected.get("8").equals("0")&&infected.get("9").equals("0"))
        {
            medium.setText("GOOD");
        }
        else
        {
            //medium.setText(infected.get("8")+" Average "+infected.get("9")+" Poor ");
            medium.setText(Integer.parseInt(infected.get("8"))+Integer.parseInt(infected.get("9"))+" Damaged");
        }
        if(infected.get("10").equals("0")&&infected.get("11").equals("0"))
        {
            inlet.setText("GOOD");
        }
        else
        {
            //inlet.setText(infected.get("10")+" Average "+infected.get("11")+" Poor ");
            inlet.setText(Integer.parseInt(infected.get("10"))+Integer.parseInt(infected.get("11"))+" Damaged");
        }
        pest.setText(pest_infected);
        disease.setText(disease_infected);
        nutrient.setText(nutrient_deficient);
        water.setText(water_insufficient);
        temp.setText(temp_wilting);
        perm.setText(permanent_wilting);
        Button button = findViewById(R.id.generate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutToImage();
                try {
                    imageToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void layoutToImage() {
        // get view group using reference
        RelativeLayout relativeLayout =  findViewById(R.id.print);
        // convert view group to bitmap
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        Bitmap bm = relativeLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/NewPDF.pdf")); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }
    private  HashMap<String,String> loadMap(String key){
        HashMap<String,String> outputMap = new HashMap<String,String>();
        Log.d("Test",project_name+site_no+millis+".txt");
        SharedPreferences pSharedPref = getSharedPreferences(project_name+site_no+millis+".txt", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
}
