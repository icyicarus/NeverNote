package bupt.icyicarus.nevernote.mediaView.audio;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.font.FontManager;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class AudioViewer extends SetPortrait {

    public static final String EXTRA_PATH = "path";
    private MediaPlayer mpAudio;
    private Button btnAudioStart, btnAudioPause, btnAudioStop;
    private SeekBar sbAudio;
    private Handler updateSeekBarHandler = new Handler();

    private Runnable updateSeekBarThread = new Runnable() {
        @Override
        public void run() {
            sbAudio.setProgress(mpAudio.getCurrentPosition());
            updateSeekBarHandler.postDelayed(updateSeekBarThread, 100);
        }
    };
    private OnClickListener btnClickHandler = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAudioStart:
                    if (mpAudio.isPlaying()) {
                        mpAudio.pause();
                        mpAudio.seekTo(0);
                        mpAudio.start();
                    } else {
                        mpAudio.start();
                    }
                    btnAudioPause.setEnabled(true);
                    btnAudioPause.setTextColor(Color.BLACK);
                    btnAudioStop.setEnabled(true);
                    btnAudioStop.setTextColor(Color.BLACK);
                    updateSeekBarHandler.post(updateSeekBarThread);
                    break;
                case R.id.btnAudioPause:
                    mpAudio.pause();
                    btnAudioPause.setEnabled(false);
                    btnAudioPause.setTextColor(Color.LTGRAY);
                    break;
                case R.id.btnAudioStop:
                    mpAudio.pause();
                    mpAudio.seekTo(0);
                    sbAudio.setProgress(0);
                    updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
                    btnAudioPause.setEnabled(false);
                    btnAudioPause.setTextColor(Color.LTGRAY);
                    btnAudioStop.setEnabled(false);
                    btnAudioStop.setTextColor(Color.LTGRAY);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_audio_viewer);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.containerAudioViewer), iconFont);

        String path = getIntent().getStringExtra(EXTRA_PATH);

        System.out.println(path);
        mpAudio = new MediaPlayer();
        try {
            mpAudio.setDataSource(this, Uri.parse(path));
            mpAudio.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mpAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
                mp.seekTo(0);
                sbAudio.setProgress(0);
                updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
                btnAudioPause.setEnabled(false);
                btnAudioStop.setEnabled(false);
            }
        });

        btnAudioStart = (Button) findViewById(R.id.btnAudioStart);
        btnAudioPause = (Button) findViewById(R.id.btnAudioPause);
        btnAudioStop = (Button) findViewById(R.id.btnAudioStop);

        btnAudioPause.setEnabled(false);
        btnAudioPause.setTextColor(Color.LTGRAY);
        btnAudioStop.setEnabled(false);
        btnAudioStop.setTextColor(Color.LTGRAY);

        btnAudioStart.setTextColor(Color.BLACK);

        btnAudioStart.setOnClickListener(btnClickHandler);
        btnAudioPause.setOnClickListener(btnClickHandler);
        btnAudioStop.setOnClickListener(btnClickHandler);

        sbAudio = (SeekBar) findViewById(R.id.sbAudio);
        sbAudio.setMax(mpAudio.getDuration());
        sbAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mpAudio.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mpAudio.stop();
        mpAudio.release();
        updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
        super.onDestroy();
    }
}
