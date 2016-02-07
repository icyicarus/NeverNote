package bupt.icyicarus.nevernote.mediaView.audio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.font.FontManager;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class AudioRecorder extends SetPortrait {

    public static final String EXTRA_PATH = "path";
    private Button btnRecordStart, btnRecordStop;
    private MediaRecorder myRecorder;
    private File savedFile = null;
    private int result = RESULT_CANCELED;
    private View.OnClickListener btnClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRecordStart:
                    myRecorder = new MediaRecorder();
                    myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    myRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    String path = getIntent().getStringExtra(EXTRA_PATH);
                    toastOut(path);
                    try {
                        savedFile = new File(path);
                        myRecorder.setOutputFile(savedFile.getAbsolutePath());
                        savedFile.createNewFile();
                        myRecorder.prepare();
                        myRecorder.start();
                        btnRecordStart.setText(R.string.recording_t);
                        btnRecordStart.setEnabled(false);
                        btnRecordStop.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btnRecordStop:
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
                                savedFile.delete();
                            }
                        }).show();
                    }
                    btnRecordStart.setText(R.string.startRecord_t);
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
        setContentView(R.layout.aty_audio_recorder);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.containerAudioRecorder), iconFont);

        btnRecordStart = (Button) findViewById(R.id.btnRecordStart);
        btnRecordStop = (Button) findViewById(R.id.btnRecordStop);
        btnRecordStart.setOnClickListener(btnClickHandler);
        btnRecordStop.setOnClickListener(btnClickHandler);
    }

    @Override
    protected void onDestroy() {
        if (result != RESULT_OK && savedFile != null) {
            savedFile.delete();
        }
        super.onDestroy();
    }
}
