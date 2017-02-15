package lzf.record.video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TextView start;
    private static final int CAMERA_RQ = 8099;
    private static final int CHECK_PERMISSION = 8001;
    private TextView file_path_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (TextView) findViewById(R.id.start);
        file_path_text = (TextView) findViewById(R.id.path);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startRecordVideo();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO}, CHECK_PERMISSION);
                }

            }
        });
    }



    public void startRecordVideo() {
        file_path_text.setText("");
        File saveDir = null;
        saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
        saveDir.mkdirs();
        MaterialCamera materialCamera = new MaterialCamera(MainActivity.this)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .defaultToFrontFacing(true)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.mcam_use_video);
        /*
        * code  返回code
        * showGuide 是否显示引导
        * */
        materialCamera.start(CAMERA_RQ, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_RQ:
                    try {
                        String filePath = intent.getStringExtra("videoUrl");
                        Log.e("lzf_video", filePath);
                        if (filePath != null && !filePath.equals("")) {
                            if (filePath.startsWith("file://")) {
                                filePath = intent.getStringExtra("videoUrl").substring(7, filePath.length());
                                file_path_text.setText("视频保存在：" + filePath);
                            }
                        }
                    } catch (Exception ex) {

                    }
                    break;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CHECK_PERMISSION
                && grantResults.length == 4
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            startRecordVideo();
        }
    }
}
