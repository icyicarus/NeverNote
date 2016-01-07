package bupt.icyicarus.nevernote.mediaView.video;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import bupt.icyicarus.nevernote.init.SetPortrait;

public class VideoViewer extends SetPortrait {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vv = new VideoView(this);
        vv.setMediaController(new MediaController(this));
        setContentView(vv);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {
            vv.setVideoPath(path);
        } else {
            finish();
        }
    }

    private VideoView vv;

    public static final String EXTRA_PATH = "path";

}
