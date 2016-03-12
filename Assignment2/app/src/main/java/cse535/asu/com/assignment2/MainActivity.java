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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.database.Cursor;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    //Global variables
    private final String DATABASE_NAME = "Patient_Info_Database";
    private String DATABASE_LOCATION, SDCARD_LOCATION;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private String name, gender_string, age;
    private Integer id;
    private String tableName;
    private boolean tableCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Things for initializing graphs
        SDCARD_LOCATION = Environment.getExternalStorageDirectory().getPath();
        DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";
        System.out.println(DATABASE_LOCATION);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Rate");
        graph.setTitle("Health Monitoring");
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        //register the sensors
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener();

        final Button runButton = (Button)findViewById(R.id.runbutton);
        runButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                EditText patientName = (EditText) findViewById(R.id.editText);
                name = patientName.getText().toString();
                EditText patientID = (EditText) findViewById(R.id.EditText01);
                id = Integer.parseInt(patientID.getText().toString());
                EditText patientAge = (EditText) findViewById(R.id.EditText02);
                age = patientAge.getText().toString();

                RadioButton male = (RadioButton) findViewById(R.id.male);
                RadioButton female = (RadioButton) findViewById(R.id.female);
                RadioButton other = (RadioButton) findViewById(R.id.other);

                RadioGroup gender = (RadioGroup) findViewById(R.id.radioGroup);
                Integer genderId = gender.getCheckedRadioButtonId();

                if (genderId == male.getId())
                {
                    gender_string = "male";
                }
                else if (genderId == female.getId())
                {
                    gender_string = "female";
                }
                else
                {
                    gender_string = "other";
                }

                tableName = name
                        + "_" + id
                        + "_" + age
                        + "_" + gender_string;

                final SQLiteDatabase db1 = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
                final String readQuery = "SELECT xValues, yValues, zValues FROM " + tableName +
                        " LIMIT 10 OFFSET (SELECT COUNT(*) FROM " + tableName + " )-10";



                Runnable runnable1 = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Cursor c1 = db1.rawQuery(readQuery, null);
                        LineGraphSeries<DataPoint> mSeriesX = new LineGraphSeries<DataPoint>();
                        LineGraphSeries<DataPoint> mSeriesY = new LineGraphSeries<DataPoint>();
                        LineGraphSeries<DataPoint> mSeriesZ = new LineGraphSeries<DataPoint>();
                        double graph2LastXValue = 0d;

                        if (c1 !=null)
                        {
                            if (c1.moveToFirst())
                            {
                                do
                                {
                                    Double x = c1.getDouble(c1.getColumnIndex("xValues"));
                                    mSeriesX.appendData(new DataPoint(graph2LastXValue, x), true, 101);
                                    Double y = c1.getDouble(c1.getColumnIndex("yValues"));
                                    mSeriesY.appendData(new DataPoint(graph2LastXValue, y), true, 101);
                                    Double z = c1.getDouble(c1.getColumnIndex("zValues"));
                                    mSeriesZ.appendData(new DataPoint(graph2LastXValue, z), true, 101);
                                    graph2LastXValue += 1d;
                                }
                                while (c1.moveToNext());

                            }

                            GraphView graph = (GraphView) findViewById(R.id.graph);
                            graph.setTitle("Health Data for" + name);
                            graph.setBackgroundColor(Color.argb(60, 255, 0, 255));
                            graph.setTitleColor(Color.MAGENTA);
                            graph.getViewport().setScalable(true);
                            graph.getViewport().setScrollable(true);
                            graph.getViewport().setXAxisBoundsManual(true);
                            graph.getViewport().setMinX(0);
                            graph.getViewport().setMaxX(120);

                            graph.addSeries(mSeriesX);
                            graph.addSeries(mSeriesY);
                            graph.addSeries(mSeriesZ);
                            mSeriesX.setColor(Color.MAGENTA);
                            mSeriesY.setColor(Color.GREEN);
                            mSeriesZ.setColor(Color.BLUE);
                            mSeriesX.setBackgroundColor(Color.GRAY);
                            mSeriesX.setThickness(2);
                            mSeriesY.setThickness(2);
                            mSeriesZ.setThickness(2);
                            mSeriesX.setDrawBackground(true);
                            mSeriesX.setDrawBackground(true);
                            mSeriesX.setDataPointsRadius(10);

                            c1.close();
                        }

                    }
                };

                Handler handlerGraph = new Handler();
                handlerGraph.post(runnable1);
            }

        });

        final Button clearButton = (Button) findViewById(R.id.clearbutton);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopAcclService();
                //Put code to stop reading accelerometer values and end write db connection
            }
        });

        final Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override

            public void onClick(View v)
            {

                Toast.makeText(MainActivity.this, "waiting for db update", Toast.LENGTH_LONG).show();
                System.out.println("Waiting for DB Update");

                //createDatabase if not exist
                SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                //SQLiteDatabase.openDatabase("/sdcard/Assigment2DB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

                System.out.println("Creating DB");

                //TODO put some ty catch magic here.
                EditText patientName = (EditText) findViewById(R.id.editText);
                name = patientName.getText().toString();
                EditText patientID = (EditText) findViewById(R.id.EditText01);
                id = Integer.parseInt(patientID.getText().toString());
                EditText patientAge = (EditText) findViewById(R.id.EditText02);
                age = patientAge.getText().toString();

                RadioButton male = (RadioButton) findViewById(R.id.male);
                RadioButton female = (RadioButton) findViewById(R.id.female);
                RadioButton other = (RadioButton) findViewById(R.id.other);

                RadioGroup gender = (RadioGroup) findViewById(R.id.radioGroup);
                Integer genderId = gender.getCheckedRadioButtonId();

                if (genderId == male.getId())
                {
                    gender_string = "male";
                }
                else if (genderId == female.getId())
                {
                            gender_string = "female";
                }
                else
                {
                    gender_string = "other";
                }

                tableName = name
                        + "_" + id
                        + "_" + age
                        + "_" + gender_string;

                createTable();
                startAcclService();
                runButton.setEnabled(false);
                clearButton.setEnabled(false);
                stopButton.setEnabled(false);

                Toast.makeText(MainActivity.this, "database creation is done", Toast.LENGTH_LONG).show();
                System.out.println("Database creation is done");

                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        runButton.setEnabled(true);
                        clearButton.setEnabled(true);
                        stopButton.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Good morning, where is my bacon", Toast.LENGTH_LONG).show();
                        System.out.println("Good Morning");

                    }
                };

                Handler handler = new Handler();
                handler.postDelayed(runnable, 10000);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void startAcclService()
    {

        //put all this in a method in a thread
        mSensorManager.registerListener(MainActivity.this, mAccelerometer, 1000000);
        //This get the accls values and puts in in the database
        double[] x = new double[100];
        double[] y = new double[100];
        double[] z = new double[100];

    }

    private void stopAcclService()
    {
        mSensorManager.unregisterListener(MainActivity.this);
    }



    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Toast.makeText(this, event.values.toString(), Toast.LENGTH_SHORT  );
        writeToTable(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    private void writeToTable(float x, float y, float z)
    {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);

        Log.i("writeTable", "in WriteTable");

        db.beginTransaction();
        try
        {
            db.execSQL("insert into " + tableName + "(xValues, yValues, zValues) values (" + (double) x + ", " + (double) y + ", " + (double) z + ");");
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            Log.i("Database", e.getMessage());
        }
        finally
        {
            db.endTransaction();
        }
        db.close();

    }

    public void createTable()
    {
        //create the database

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        Log.i("createTable", "in CreateTable");
        String CREATE_TABLE_SQL = "create table if not exists " + tableName + " ("
                        + "timeStamp integer PRIMARY KEY autoincrement, "
                        + "xValues double, "
                        + "yValues double, "
                        + "zValues double ); ";

        db.beginTransaction();
        try
        {
            db.execSQL(CREATE_TABLE_SQL);
            //db.execSQL(INSERT_DUMMY_VALUES_SQL);
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            Log.i("Database", e.getMessage());
        }
        finally
        {
            db.endTransaction();
            tableCreated = true;
            db.close();
        }
    }
}
