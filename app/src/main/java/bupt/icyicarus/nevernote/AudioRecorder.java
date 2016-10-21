package bupt.icyicarus.nevernote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import bupt.icyicarus.nevernote.font.FontManager;
import bupt.icyicarus.nevernote.init.Initialization;

public class AudioRecorder extends Initialization {

    public static final String EXTRA_PATH = "path";
    private Button btnRecordStart, btnRecordStop;
    private MediaRecorder myRecorder;
    private File savedFile = null;
    private int result = RESULT_CANCELED;
    private View.OnClickListener btnClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonStartRecording:
                    if (ContextCompat.checkSelfPermission(AudioRecorder.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AudioRecorder.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
                    } else {
                        recordAudio();
                    }
                    break;
                case R.id.buttonStopRecording:
                    if (savedFile.exists() && savedFile != null) {
                        myRecorder.stop();
                        myRecorder.release();
                        new AlertDialog.Builder(AudioRecorder.this).setTitle("Save?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result = RESULT_OK;
                                setResult(RESULT_OK);
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!savedFile.delete())
                                    Toast.makeText(AudioRecorder.this, "Delete File Error", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                    btnRecordStart.setText(R.string.start_record);
                    btnRecordStart.setEnabled(true);
                    btnRecordStop.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_audio_recorder);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.containerAudioRecorder), iconFont);

        btnRecordStart = (Button) findViewById(R.id.buttonStartRecording);
        btnRecordStop = (Button) findViewById(R.id.buttonStopRecording);
        btnRecordStart.setOnClickListener(btnClickHandler);
        btnRecordStop.setOnClickListener(btnClickHandler);
    }

    @Override
    protected void onDestroy() {
        if (result != RESULT_OK && savedFile != null) {
            if (!savedFile.delete())
                Toast.makeText(AudioRecorder.this, "Delete File Error", Toast.LENGTH_SHORT).show();
        }
        if (myRecorder != null) {
            myRecorder.stop();
            myRecorder = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordAudio();
                } else {
                    Toast.makeText(AudioRecorder.this, "Permission Denied, Please Check", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void recordAudio() {
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        String path = getIntent().getStringExtra(EXTRA_PATH);
        try {
            savedFile = new File(path);
            myRecorder.setOutputFile(path);
            if (!savedFile.createNewFile())
                throw new Exception("Create New File Error!");
            myRecorder.prepare();
            myRecorder.start();
            btnRecordStart.setText(R.string.recording);
            btnRecordStart.setEnabled(false);
            btnRecordStop.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}