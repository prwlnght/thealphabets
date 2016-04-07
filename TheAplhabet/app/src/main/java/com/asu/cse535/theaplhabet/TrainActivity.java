package com.asu.cse535.theaplhabet;


import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.view.View;

public class TrainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Spinner dropdown = (Spinner)findViewById(R.id.alphabets);
        String[] items = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(TrainActivity.this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        try {
            drawer.setDrawerListener(toggle);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Exception caught", Toast.LENGTH_SHORT).show();
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(TrainActivity.this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id)
    {
        ImageView image;
        image = (ImageView)findViewById(R.id.imageView);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);


        if (parent.getItemAtPosition(pos).toString() == "A")
        {
            image.setImageResource(R.drawable.ic_alphabeta);

        }
        else if (parent.getItemAtPosition(pos).toString() == "B")
        {
            image.setImageResource(R.drawable.ic_alphabetb);
        }
        else if (parent.getItemAtPosition(pos).toString() == "C")
        {
            image.setImageResource(R.drawable.ic_alphabetc);
        }
        else if (parent.getItemAtPosition(pos).toString() == "D")
        {
            image.setImageResource(R.drawable.ic_alphabetd);
        }
        else if (parent.getItemAtPosition(pos).toString() == "E")
        {
            image.setImageResource(R.drawable.ic_alphabete);
        }
        else if (parent.getItemAtPosition(pos).toString() == "F")
        {
            image.setImageResource(R.drawable.ic_alphabetf);
        }
        else if (parent.getItemAtPosition(pos).toString() == "G")
        {
            image.setImageResource(R.drawable.ic_alphabetg);
        }
        else if (parent.getItemAtPosition(pos).toString() == "H")
        {
            image.setImageResource(R.drawable.ic_alphabeth);
        }
        else if (parent.getItemAtPosition(pos).toString() == "I")
        {
            image.setImageResource(R.drawable.ic_alphabeti);
        }
        else if (parent.getItemAtPosition(pos).toString() == "J")
        {
            image.setImageResource(R.drawable.ic_alphabetj);
        }
        else if (parent.getItemAtPosition(pos).toString() == "K")
        {
            image.setImageResource(R.drawable.ic_alphabetk);
        }
        else if (parent.getItemAtPosition(pos).toString() == "L")
        {
            image.setImageResource(R.drawable.ic_alphabetl);
        }
        else if (parent.getItemAtPosition(pos).toString() == "M")
        {
            image.setImageResource(R.drawable.ic_alphabetm);
        }
        else if (parent.getItemAtPosition(pos).toString() == "N")
        {
            image.setImageResource(R.drawable.ic_alphabetn);
        }
        else if (parent.getItemAtPosition(pos).toString() == "O")
        {
            image.setImageResource(R.drawable.ic_alphabeto);
        }
        else if (parent.getItemAtPosition(pos).toString() == "P")
        {
            image.setImageResource(R.drawable.ic_alphabetp);
        }
        else if (parent.getItemAtPosition(pos).toString() == "Q")
        {
            image.setImageResource(R.drawable.ic_alphabetq);
        }
        else if (parent.getItemAtPosition(pos).toString() == "R")
        {
            image.setImageResource(R.drawable.ic_alphabetr);
        }
        else if (parent.getItemAtPosition(pos).toString() == "S")
        {
            image.setImageResource(R.drawable.ic_alphabets);
        }
        else if (parent.getItemAtPosition(pos).toString() == "T")
        {
            image.setImageResource(R.drawable.ic_alphabett);
        }
        else if (parent.getItemAtPosition(pos).toString() == "U")
        {
            image.setImageResource(R.drawable.ic_alphabetu);
        }
        else if (parent.getItemAtPosition(pos).toString() == "V")
        {
            image.setImageResource(R.drawable.ic_alphabetv);
        }
        else if (parent.getItemAtPosition(pos).toString() == "W")
        {
            image.setImageResource(R.drawable.ic_alphabetw);
        }
        else if (parent.getItemAtPosition(pos).toString() == "X")
        {
            image.setImageResource(R.drawable.ic_alphabetx);
        }
        else if (parent.getItemAtPosition(pos).toString() == "Y")
        {
            image.setImageResource(R.drawable.ic_alphabety);
        }
        else if (parent.getItemAtPosition(pos).toString() == "Z")
        {
            image.setImageResource(R.drawable.ic_alphabetz);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent intent = new Intent(TrainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_train) {
            Intent intent = new Intent(TrainActivity.this, TrainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_test) {
            Intent intent = new Intent(TrainActivity.this, TestActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
