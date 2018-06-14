package com.example.linksus.mycompress;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.linksus.mycompress.ffmeg.CompressListener;
import com.example.linksus.mycompress.ffmeg.Compressor;
import com.example.linksus.mycompress.ffmeg.InitListener;
import java.io.File;


public class MainActivity extends AppCompatActivity {

    private TextView etCommand;
    private Compressor mCompressor;
    private final String TAG = "TTT";
    private String currentInputVideoPath = "/mnt/sdcard/videokit/in.mp4";
    private String currentOutputVideoPath = "/mnt/sdcard/videokit/out.mp4";
    String cmd  = "-y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 480x640 -aspect 9:16 " + currentOutputVideoPath;
    private String height;
    private String width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCommand = findViewById(R.id.etCommand);
        etCommand.setText(cmd);
        findViewById(R.id.btnRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    File file = new File(currentOutputVideoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    MediaMetadataRetriever retr = new MediaMetadataRetriever();
                    retr.setDataSource(currentInputVideoPath);
                    try {
                        // 视频高度
                        height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                        // 视频宽度
                        width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    float h = Float.parseFloat(height);
                    float w = Float.parseFloat(width);
                    String cmd  = "-y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s "
                            +"480x640"
                            +" -aspect "+(h)/(w)+
                            " " +
                            currentOutputVideoPath;
                    execCommand(cmd);
            }
        });
        mCompressor = new Compressor(this);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                Log.v(TAG, "load library succeed");
            }

            @Override
            public void onLoadFail(String reason) {
                Log.i(TAG, "load library fail:" + reason);
            }
        });


    }
    private void execCommand(String cmd) {
        File mFile = new File(currentOutputVideoPath);
        if (mFile.exists()) {
            mFile.delete();
        }
        mCompressor.execCommand(cmd, new CompressListener() {
            @Override
            public void onExecSuccess(String message) {
                Log.i(TAG, "success " + message);



            }

            @Override
            public void onExecFail(String reason) {
                Log.i(TAG, "fail " + reason);

            }

            @Override
            public void onExecProgress(String message) {
                Log.e(TAG, "progress " + message);


            }
        });
    }


}
