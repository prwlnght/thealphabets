package cse535.asu.com.assignment2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //Global variables
    private final String DATABASE_NAME = "Patient_Info_Database";
    private String DATABASE_LOCATION, SDCARD_LOCATION;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View view) {
        //      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //           .setAction("Action", null).show();
        // }
        //});

        //Things for initializing graphs
        SDCARD_LOCATION = Environment.getExternalStorageDirectory().getPath();
        DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Rate");
        graph.setTitle("Health Monitoring");
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        final String[][] str = {{"Alice","83","1"},{"Bob","7","0"},{"Mallory", "42","2"},{"Carol", "43", "1"},{"Dave", "91", "0"},{"Eve", "66", "1"},{"Walter", "77", "0"},{"Peggy", "65","2"},{"Trent", "21","0"},{"Peggy", "71","2"}};

        //register the sensors
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener()

        Button startButton = (Button)findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                EditText editText = (EditText) findViewById(R.id.editText);
                String name = editText.getText().toString();
                EditText editText01 = (EditText) findViewById(R.id.EditText01);
                Integer id = Integer.parseInt(editText01.getText().toString());
                EditText editText02= (EditText) findViewById(R.id.EditText02);
                String age = editText02.getText().toString();

                RadioButton male = (RadioButton) findViewById(R.id.male);
                RadioButton female = (RadioButton) findViewById(R.id.female);
                RadioButton other = (RadioButton) findViewById(R.id.other);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("ERROR!!!");
                alertDialog.setMessage("Please Enter Valid Data");

                RadioGroup gender = (RadioGroup) findViewById(R.id.radioGroup);
                int genderId = gender.getCheckedRadioButtonId();
                Integer gen;

                if(genderId==male.getId()){
                    gen=0;
                }
                else if(genderId==female.getId()){
                    gen=1;
                }
                else{
                    gen=2;
                }
                String input_name = name;
                String input_age = age;
                int input_gender = gen;

                if(id<=0 || id>10||str[id-1][0].equals(input_name)==false || str[id-1][1].equals(input_age)==false || Integer.parseInt(str[id-1][2])!=input_gender){
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    GraphView graph = (GraphView) findViewById(R.id.graph);

                    graph.removeAllSeries();
                    alertDialog.show();

                }
                else {

                        GraphView graph = (GraphView) findViewById(R.id.graph);
                    graph.setTitle("Health Data for" + name);
                    graph.setBackgroundColor(Color.argb(60, 255, 0, 255 ));
                    graph.setTitleColor(Color.MAGENTA);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(120);


                        //GridLabelRenderer(graph);





                        double data;
                        System.out.println("name: " + name + " age: " + age + " id: " + id + " gender: " + genderId);

                        double graph2LastXValue = 0d;

                        Random generator = new Random(id);
                        LineGraphSeries<DataPoint> mSeries2 = new LineGraphSeries<DataPoint>();

                        for (int i = 0; i < 100; i++) {
                            data = generator.nextDouble() * 0.5;
                            mSeries2.appendData(new DataPoint(graph2LastXValue, data), true, 101);
                            graph2LastXValue += 1d;

                        }
                    graph.addSeries(mSeries2);
                    mSeries2.setColor(Color.MAGENTA);
                    mSeries2.setBackgroundColor(Color.GRAY);
                    mSeries2.setThickness(2);
                    mSeries2.setDrawBackground(true);
                    mSeries2.setDrawBackground(true);
                    mSeries2.setDataPointsRadius(10);




                }
            }

        });

        Button stopButton = (Button)findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                GraphView graph = (GraphView) findViewById(R.id.graph);

                graph.removeAllSeries();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //This get the accls values and puts in in the database
    public void startAcclService(View view){

        //put all this in a method in a thread
        mSensorManager.registerListener(this, mAccelerometer, 1000000);
        x = y = z = new double[100];
        //graphing related init
        /*graphView = new LineGraphView(this, "GraphViewDemo");
        gvdx = new GraphViewData[10];
        gvdy = new GraphViewData[10];
        gvdz = new GraphViewData[10];*/
       /* batchSize = 10;
        updatedAcclValues = new double[3];
        tableCreated = false;*/
        //uploadComplete = false;

        //get database location
        SDCARD_LOCATION = Environment.getExternalStorageDirectory().getPath();
        DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";


        /*get table name*/
        //creating a database
        //createDatabase();

        //compose the table name
        tableName = "A" + patientNameEdit.getText().toString()
                + "_" + patientIdEdit.getText().toString()
                + "_" + ageEdit.getText().toString()
                + "_" + radioSexButton.getText().toString();
        SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //SQLiteDatabase.openDatabase("/sdcard/Assigment2DB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        createTable();
        //readyToWrite = true;
        //enabling Run Button

        //disable StartBtn -- after implementing a reset functionality
        startAcclBtn.setEnabled(false);

        Toast.makeText(this, "waiting for db update", Toast.LENGTH_LONG).show();

    }


    //This function is the one that 'starts the graph' **This might not be needed. If not needed for
    //final implementation, move the logic to the onclick listener.
    public void initializeGraph(){



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

    private double getRandom() {
        double mLastRandom = 2;
        Random mRand = new Random();
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void writeToTable(double[] xArray, double[] yArray, double[] zArray) {
        //
        //databaseLocked = true;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
        Log.i("writeTable", "in WriteTable");

        for (int counter = 0; counter < xArray.length; counter++){
            db.beginTransaction();
            try{//make table name global
                db.execSQL("insert into " + tableName + "(xValues, yValues, xValues) values ("+ (double)xArray[counter] + ", " + (double)yArray[counter] + ", " + (double)zArray[counter] + ");");
                //Log.i("DatabaseWriter", "Writing" + xObject[counter] + ", " + yObjects[counter] + ", " + zObjects[counter] + ");" );
                //db.execSQL(INSERT_DUMMY_VALUES_SQL);
                db.setTransactionSuccessful();
                //databasePopulatedFlag = true;
            }catch(SQLiteException e){
                Log.i("Database", e.getMessage());
            }finally{
                db.endTransaction();
            }
        }
        db.close();

    }

    public void createTable(){
        //create the database

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        Log.i("createTable", "in CreateTable");
        String CREATE_TABLE_SQL = "create table if not exists " + tableName + " ("
                + "timeStamp integer PRIMARY KEY autoincrement, "
                + "xValues double, "
                + "yValues double, "
                + "zValues double ); ";

        //String INSERT_DUMMY_VALUES_SQL = "insert into " + tableName + "(xValues, yValues, xValues) values (12, 13, 14);";

        db.beginTransaction();
        try{
            db.execSQL(CREATE_TABLE_SQL);
            //db.execSQL(INSERT_DUMMY_VALUES_SQL);
            db.setTransactionSuccessful();
        }catch(SQLiteException e){
            Log.i("Database", e.getMessage());
        }finally{
            db.endTransaction();
            tableCreated = true;
            db.close();
        }
    }
}
