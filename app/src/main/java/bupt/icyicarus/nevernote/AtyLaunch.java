package bupt.icyicarus.nevernote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import bupt.icyicarus.nevernote.init.SetPortrait;

public class AtyLaunch extends SetPortrait {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (launchView) {
                    case "overview":
                        startActivity(new Intent(AtyLaunch.this, OverView.class));
                        break;
                    case "calender":
                        startActivity(new Intent(AtyLaunch.this, CalenderView.class));
                        break;
                    default:
                        break;
                }
            }
        }, 2000);
    }
}
