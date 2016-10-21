package bupt.icyicarus.nevernote.mediaList;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;

public class MediaListCellData {

    public int type = 0;
    public int id = -1;
    public String path = null;
    public int iconID = R.drawable.logo;

    public MediaListCellData(String path) {
        this.path = path;

        if (path.endsWith(".jpg")) {
            iconID = R.drawable.img_photo;
            type = PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO;
        } else if (path.endsWith(".mp4")) {
            iconID = R.drawable.img_video;
            type = PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO;
        } else if (path.endsWith(".wav")) {
            iconID = R.drawable.img_audio;
            type = PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO;
        }
    }

    public MediaListCellData(String path, int id) {
        this(path);
        this.id = id;
    }
}
