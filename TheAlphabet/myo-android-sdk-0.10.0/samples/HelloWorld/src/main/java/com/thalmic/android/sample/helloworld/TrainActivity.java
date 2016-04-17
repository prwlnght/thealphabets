package com.thalmic.android.sample.helloworld;


import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class TrainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Spinner dropdown = (Spinner) findViewById(R.id.alphabets);
        String[] items = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(TrainActivity.this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        ImageView image;
        image = (ImageView) findViewById(R.id.imageView);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);


        if (parent.getItemAtPosition(pos).toString() == "A") {
            image.setImageResource(R.drawable.ic_alphabeta);

        } else if (parent.getItemAtPosition(pos).toString() == "B") {
            image.setImageResource(R.drawable.ic_alphabetb);
        } else if (parent.getItemAtPosition(pos).toString() == "C") {
            image.setImageResource(R.drawable.ic_alphabetc);
        } else if (parent.getItemAtPosition(pos).toString() == "D") {
            image.setImageResource(R.drawable.ic_alphabetd);
        } else if (parent.getItemAtPosition(pos).toString() == "E") {
            image.setImageResource(R.drawable.ic_alphabete);
        } else if (parent.getItemAtPosition(pos).toString() == "F") {
            image.setImageResource(R.drawable.ic_alphabetf);
        } else if (parent.getItemAtPosition(pos).toString() == "G") {
            image.setImageResource(R.drawable.ic_alphabetg);
        } else if (parent.getItemAtPosition(pos).toString() == "H") {
            image.setImageResource(R.drawable.ic_alphabeth);
        } else if (parent.getItemAtPosition(pos).toString() == "I") {
            image.setImageResource(R.drawable.ic_alphabeti);
        } else if (parent.getItemAtPosition(pos).toString() == "J") {
            image.setImageResource(R.drawable.ic_alphabetj);
        } else if (parent.getItemAtPosition(pos).toString() == "K") {
            image.setImageResource(R.drawable.ic_alphabetk);
        } else if (parent.getItemAtPosition(pos).toString() == "L") {
            image.setImageResource(R.drawable.ic_alphabetl);
        } else if (parent.getItemAtPosition(pos).toString() == "M") {
            image.setImageResource(R.drawable.ic_alphabetm);
        } else if (parent.getItemAtPosition(pos).toString() == "N") {
            image.setImageResource(R.drawable.ic_alphabetn);
        } else if (parent.getItemAtPosition(pos).toString() == "O") {
            image.setImageResource(R.drawable.ic_alphabeto);
        } else if (parent.getItemAtPosition(pos).toString() == "P") {
            image.setImageResource(R.drawable.ic_alphabetp);
        } else if (parent.getItemAtPosition(pos).toString() == "Q") {
            image.setImageResource(R.drawable.ic_alphabetq);
        } else if (parent.getItemAtPosition(pos).toString() == "R") {
            image.setImageResource(R.drawable.ic_alphabetr);
        } else if (parent.getItemAtPosition(pos).toString() == "S") {
            image.setImageResource(R.drawable.ic_alphabets);
        } else if (parent.getItemAtPosition(pos).toString() == "T") {
            image.setImageResource(R.drawable.ic_alphabett);
        } else if (parent.getItemAtPosition(pos).toString() == "U") {
            image.setImageResource(R.drawable.ic_alphabetu);
        } else if (parent.getItemAtPosition(pos).toString() == "V") {
            image.setImageResource(R.drawable.ic_alphabetv);
        } else if (parent.getItemAtPosition(pos).toString() == "W") {
            image.setImageResource(R.drawable.ic_alphabetw);
        } else if (parent.getItemAtPosition(pos).toString() == "X") {
            image.setImageResource(R.drawable.ic_alphabetx);
        } else if (parent.getItemAtPosition(pos).toString() == "Y") {
            image.setImageResource(R.drawable.ic_alphabety);
        } else if (parent.getItemAtPosition(pos).toString() == "Z") {
            image.setImageResource(R.drawable.ic_alphabetz);
        } else if (parent.getItemAtPosition(pos).toString() == "0") {
            image.setImageResource(R.drawable.ic_number0);
        } else if (parent.getItemAtPosition(pos).toString() == "1") {
            image.setImageResource(R.drawable.ic_number1);
        } else if (parent.getItemAtPosition(pos).toString() == "2") {
            image.setImageResource(R.drawable.ic_number2);
        } else if (parent.getItemAtPosition(pos).toString() == "3") {
            image.setImageResource(R.drawable.ic_number3);
        } else if (parent.getItemAtPosition(pos).toString() == "4") {
            image.setImageResource(R.drawable.ic_number4);
        } else if (parent.getItemAtPosition(pos).toString() == "5") {
            image.setImageResource(R.drawable.ic_number5);
        } else if (parent.getItemAtPosition(pos).toString() == "6") {
            image.setImageResource(R.drawable.ic_number6);
        } else if (parent.getItemAtPosition(pos).toString() == "7") {
            image.setImageResource(R.drawable.ic_number7);
        } else if (parent.getItemAtPosition(pos).toString() == "8") {
            image.setImageResource(R.drawable.ic_number8);
        } else if (parent.getItemAtPosition(pos).toString() == "9") {
            image.setImageResource(R.drawable.ic_number9);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.nav_home == id) {
            Intent intent = new Intent(this, HelloWorldActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.nav_train == id) {
            Intent intent = new Intent(this, TrainActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.nav_test == id) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
