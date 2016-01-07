package bupt.icyicarus.nevernote.mediaView.photo;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import bupt.icyicarus.nevernote.init.SetPortrait;

public class PhotoViewer extends SetPortrait {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		iv = new ImageView(this);
		setContentView(iv);

		String path = getIntent().getStringExtra(EXTRA_PATH);
		if (path != null) {
			iv.setImageURI(Uri.fromFile(new File(path)));
		} else {
			finish();
		}
	}

	private ImageView iv;

	public static final String EXTRA_PATH = "path";

}
