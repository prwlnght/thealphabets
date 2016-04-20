package com.cse535.thealphabets;


import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.ArrayList;
import java.util.List;

public class ReadMyo extends AbstractDeviceListener {

    private final Hub hub;

    private boolean recording = false;
    private List<Vector3> accelerometerData = new ArrayList<Vector3>();
    private List<Quaternion> orienationData = new ArrayList<Quaternion>();
    private List<Vector3> gyroscopeData = new ArrayList<Vector3>();


    public ReadMyo(Hub hub) {
        this.hub = hub;
    }

    public void start() {
        hub.addListener(this);
        recording = true;
    }

    public void stop() {
        hub.removeListener(this);
        recording = false;
    }

    public boolean isRecording() {
        return recording;
    }

    public void reset() {
        accelerometerData.clear();
        orienationData.clear();
        gyroscopeData.clear();
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
        accelerometerData.add(accel);
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        orienationData.add(rotation);
    }

    @Override
    public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
        gyroscopeData.add(gyro);
    }

    public List<Vector3> getAccelerometerData() {
        return accelerometerData;
    }

    public List<Quaternion> getOrienationData() {
        return orienationData;
    }

    public List<Vector3> getGyroscopeData() {
        return gyroscopeData;
    }

}
