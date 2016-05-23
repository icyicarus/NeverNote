package bupt.icyicarus.nevernote.mediaView.video;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.font.FontManager;
import bupt.icyicarus.nevernote.init.Initialization;

public class VideoViewer extends Initialization {

    public static final String EXTRA_PATH = "path";
    private Button btnVideoStart, btnVideoPause, btnVideoStop;
    private SurfaceView sfVideo;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mpVideo;
    private String path;
    private SeekBar sbVideo;
    private Handler updateSeekBarHandler = new Handler();

    private Runnable updateSeekBarThread = new Runnable() {
        @Override
        public void run() {
            sbVideo.setProgress(mpVideo.getCurrentPosition());
            updateSeekBarHandler.postDelayed(updateSeekBarThread, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_video_viewer);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.containerVideoViewer), iconFont);

        sfVideo = (SurfaceView) findViewById(R.id.sfVideo);
        btnVideoStart = (Button) findViewById(R.id.btnVideoStart);
        btnVideoPause = (Button) findViewById(R.id.btnVideoPause);
        btnVideoStop = (Button) findViewById(R.id.btnVideoStop);

        btnVideoPause.setEnabled(false);
        btnVideoPause.setTextColor(Color.LTGRAY);
        btnVideoStop.setEnabled(false);
        btnVideoStop.setTextColor(Color.LTGRAY);

        btnVideoStart.setTextColor(Color.RED);

        mpVideo = new MediaPlayer();
        path = getIntent().getStringExtra(EXTRA_PATH);

        surfaceHolder = sfVideo.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mpVideo.setDataSource(path);
                    mpVideo.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mpVideo.setDisplay(surfaceHolder);
                    mpVideo.prepare();
                    mpVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.pause();
                            mp.seekTo(0);
                            sbVideo.setProgress(0);
                            updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
                            btnVideoPause.setEnabled(false);
                            btnVideoStop.setEnabled(false);
                        }
                    });


                    sbVideo = (SeekBar) findViewById(R.id.sbVideo);
                    sbVideo.setMax(mpVideo.getDuration());
                    sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                mpVideo.seekTo(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        btnVideoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpVideo.isPlaying()) {
                    mpVideo.pause();
                    mpVideo.seekTo(0);
                    mpVideo.start();
                } else {
                    mpVideo.start();
                }
                btnVideoPause.setEnabled(true);
                btnVideoPause.setTextColor(Color.RED);
                btnVideoStop.setEnabled(true);
                btnVideoStop.setTextColor(Color.RED);
                updateSeekBarHandler.post(updateSeekBarThread);
            }
        });

        btnVideoPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpVideo.pause();
                btnVideoPause.setEnabled(false);
                btnVideoPause.setTextColor(Color.LTGRAY);
            }
        });

        btnVideoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpVideo.pause();
                mpVideo.seekTo(0);
                sbVideo.setProgress(0);
                updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
                btnVideoPause.setEnabled(false);
                btnVideoPause.setTextColor(Color.LTGRAY);
                btnVideoStop.setEnabled(false);
                btnVideoStop.setTextColor(Color.LTGRAY);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mpVideo.stop();
        mpVideo.release();
        updateSeekBarHandler.removeCallbacks(updateSeekBarThread);
        super.onDestroy();
    }

}
