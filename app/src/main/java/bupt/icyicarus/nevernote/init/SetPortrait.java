package bupt.icyicarus.nevernote.init;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

public class SetPortrait extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void toastOut(String arg) {
        Toast.makeText(this, arg, Toast.LENGTH_LONG).show();
    }
}
