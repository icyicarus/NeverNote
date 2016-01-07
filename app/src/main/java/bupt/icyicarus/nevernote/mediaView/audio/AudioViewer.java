package bupt.icyicarus.nevernote.mediaView.audio;

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
import bupt.icyicarus.nevernote.init.SetPortrait;

public class AudioViewer extends SetPortrait {

    private MediaPlayer mp;
    private Button btnPlayStart, btnPlayPause, btnPlayStop;
    private SeekBar sbPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_audio_viewer);
        String path = getIntent().getStringExtra(EXTRA_PATH);

        toastOut(path);

        System.out.println(path);
        mp = new MediaPlayer();
        try {
            mp.setDataSource(this, Uri.parse(path));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
                mp.seekTo(0);
                sbPlay.setProgress(0);
                updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
                btnPlayPause.setEnabled(false);
                btnPlayStop.setEnabled(false);
            }
        });

        btnPlayStart = (Button) findViewById(R.id.btnPlayStart);
        btnPlayPause = (Button) findViewById(R.id.btnPlayPause);
        btnPlayStop = (Button) findViewById(R.id.btnPlayStop);

        btnPlayPause.setEnabled(false);
        btnPlayStop.setEnabled(false);

        btnPlayStart.setOnClickListener(btnClickHandler);
        btnPlayPause.setOnClickListener(btnClickHandler);
        btnPlayStop.setOnClickListener(btnClickHandler);

        sbPlay = (SeekBar) findViewById(R.id.sbPlay);
        sbPlay.setMax(mp.getDuration());
        sbPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
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

    private Handler updateSeekBarHandler = new Handler();

    private Runnable updateSeekBarThread = new Runnable() {
        @Override
        public void run() {
            sbPlay.setProgress(mp.getCurrentPosition());
            updateSeekBarHandler.postDelayed(updateSeekBarThread, 100);
        }
    };

    public static final String EXTRA_PATH = "path";

    private OnClickListener btnClickHandler = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPlayStart:
                    if (mp.isPlaying()) {
                        mp.pause();
                        mp.seekTo(0);
                        mp.start();
                    } else {
                        mp.start();
                    }
                    btnPlayPause.setEnabled(true);
                    btnPlayStop.setEnabled(true);
                    updateSeekBarHandler.post(updateSeekBarThread);
                    break;
                case R.id.btnPlayPause:
                    if (mp.isPlaying()) {
                        mp.pause();
                    } else {
                        mp.start();
                    }
                    break;
                case R.id.btnPlayStop:
                    mp.pause();
                    mp.seekTo(0);
                    sbPlay.setProgress(0);
                    updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
                    btnPlayPause.setEnabled(false);
                    btnPlayStop.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        mp.stop();
        mp.release();
        updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
        super.onDestroy();
    }
}
