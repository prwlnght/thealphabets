package com.cse535.thealphabets;


import android.widget.TextView;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
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

    private TextView mTextView;

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

    @Override
    public void onPose (Myo myo, long timestamp, Pose pose){
        switch (pose) {
            case UNKNOWN:
                mTextView.setText(R.string.thealphabets);
                break;
            case REST:
            case DOUBLE_TAP:
                int restTextId = R.string.thealphabets;
                switch (myo.getArm()) {
                    case LEFT:
                        restTextId = R.string.arm_left;
                        break;
                    case RIGHT:
                        restTextId = R.string.arm_right;
                        break;
                }
                mTextView.setText(restTextId);
                break;
            case FIST:
                mTextView.setText(R.string.pose_fist);
                break;
            case WAVE_IN:
               mTextView.setText(R.string.pose_wavein);
                break;
            case WAVE_OUT:
                mTextView.setText(R.string.pose_waveout);
                break;
            case FINGERS_SPREAD:
                mTextView.setText(R.string.pose_fingersspread);
                break;
        }
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
