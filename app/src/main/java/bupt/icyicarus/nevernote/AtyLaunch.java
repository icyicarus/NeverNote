package bupt.icyicarus.nevernote;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import bupt.icyicarus.nevernote.init.SetPortrait;
import bupt.icyicarus.nevernote.views.DebugView;

public class AtyLaunch extends SetPortrait {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.aty_launch);

        ImageView ivLaunch = (ImageView) findViewById(R.id.ivLaunch);
        WindowManager windowManager = this.getWindowManager();
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, windowManager.getDefaultDisplay().getWidth() / 2, windowManager.getDefaultDisplay().getHeight() / 2);
        scaleAnimation.setDuration(2500);
        scaleAnimation.setFillAfter(true);
        ivLaunch.setAnimation(scaleAnimation);
        scaleAnimation.startNow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                switch (launchView) {
//                    case "Overview":
//                        startActivity(new Intent(AtyLaunch.this, OverView.class));
//                        break;
//                    case "Calender":
//                        startActivity(new Intent(AtyLaunch.this, CalenderView.class));
//                        break;
//                    default:
//                        break;
//                }
                startActivity(new Intent(AtyLaunch.this, DebugView.class));
                finish();
            }
        }, 2000);
    }
}
