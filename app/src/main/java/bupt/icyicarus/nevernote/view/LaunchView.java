package bupt.icyicarus.nevernote.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.Initialization;

public class LaunchView extends Initialization {

    private ScaleAnimation scaleAnimation;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(LaunchView.this, MergeView.class));
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_launch);

        ImageView ivLaunch = (ImageView) findViewById(R.id.imageViewLaunchBackground);
        WindowManager windowManager = this.getWindowManager();
        scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, windowManager.getDefaultDisplay().getWidth() / 2, windowManager.getDefaultDisplay().getHeight() / 2);
        scaleAnimation.setDuration(2500);
        scaleAnimation.setFillAfter(true);
        ivLaunch.setAnimation(scaleAnimation);
        if (ActivityCompat.checkSelfPermission(LaunchView.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            AndPermission.with(LaunchView.this).requestCode(3).permission(Manifest.permission.RECORD_AUDIO).send();
        } else {
            scaleAnimation.startNow();
            handler.postDelayed(runnable, 2000);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(LaunchView.this, requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode) {
            }

            @Override
            public void onFailed(int requestCode) {
                Toast.makeText(LaunchView.this, "Audio note will not be possible until you allow NeverNote to access your microphone", Toast.LENGTH_SHORT).show();
            }
        });
        scaleAnimation.startNow();
        handler.postDelayed(runnable, 2000);
    }
}
