package com.haoyun.customaudiorecorder;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Spinner mSpAudioSources;
    private int mAudioSource;
    private Button mBtnRecord;
    private MediaRecorder mMediaRecorder;
    private boolean mRecording = false;
    String[] mAudioSourceStrings = null;
    private int mAudioChannels = 1;
    private int mSamplingRate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSpAudioSources = (Spinner) findViewById(R.id.spAudioSources);
        mAudioSourceStrings = getResources().getStringArray(R.array.audio_sources);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mAudioSourceStrings);
        mSpAudioSources.setAdapter(adapter);
        mSpAudioSources.setOnItemSelectedListener(this);
        mSpAudioSources.setSelection(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        mBtnRecord = (Button) findViewById(R.id.btnRecord);

        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick:");
                if (!mRecording)
                    startRecord();
                else
                    stopRecord();
                mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                    @Override
                    public void onInfo(MediaRecorder mr, int what, int extra) {
                        Log.v(TAG, "onInfo: what = " + what + ", extra = " + extra);
                    }
                });
                mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                    @Override
                    public void onError(MediaRecorder mr, int what, int extra) {
                        Log.v(TAG, "onError: what = " + what + ", extra = " + extra);
                    }
                });
            }
        });

        Switch switchChannels = (Switch) findViewById(R.id.swChannels);
        switchChannels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAudioChannels = 2;
                } else {
                    mAudioChannels = 1;
                }
                Log.v(TAG, "mAudioChannels = " + mAudioChannels);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mAudioSource = position;
        Log.v(TAG, "Audio Source Pos = " + position);
        Log.v(TAG, "mAudioSource = " + mAudioSource);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void startRecord() {
        Log.v(TAG, "startRecord:");
        mBtnRecord.setText("Stop");
        mMediaRecorder = new MediaRecorder();
        Log.v(TAG, "onClick: setAudioSource = " + mAudioSource);
        mMediaRecorder.setAudioSource(mAudioSource);
        mMediaRecorder.setAudioChannels(mAudioChannels);
        mMediaRecorder.setAudioSamplingRate(mSamplingRate);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), mAudioSourceStrings[mAudioSource] + ".m4a");
        Log.v(TAG, "File Path = " + file);
        mMediaRecorder.setOutputFile(String.valueOf(file));
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();
        mRecording = true;
    }

    private void stopRecord() {
        Log.v(TAG, "stopRecord:");
        mBtnRecord.setText("Record");
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mRecording = false;
    }
}
