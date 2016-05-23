package bupt.icyicarus.nevernote.mediaView.photo;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

import bupt.icyicarus.nevernote.init.Initialization;

public class PhotoViewer extends Initialization {

	public static final String EXTRA_PATH = "path";
	private ImageView iv;

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

}
