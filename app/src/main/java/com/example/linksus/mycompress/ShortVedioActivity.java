package com.example.linksus.mycompress;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_LOW_MEMORY;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_NO_VIDEO_TRACK;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH;

public class ShortVedioActivity extends AppCompatActivity {


    private String currentInputVideoPath = "/mnt/sdcard/videokit/in.mp4";
    private String currentOutputVideoPath = "/mnt/sdcard/videokit/out.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_vedio);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressVideoResouce(ShortVedioActivity.this,currentInputVideoPath);
            }
        });
    }

    /**
     * 压缩视频
     *
     * @param mContext
     * @param filepath
     */
    public void compressVideoResouce(Context mContext, String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            Log.d("ttt","请先选择转发文件");
            return;
        }
        //PLShortVideoTranscoder初始化，三个参数，第一个context，第二个要压缩文件的路径，第三个视频压缩后输出的路径
        final PLShortVideoTranscoder mShortVideoTranscoder = new PLShortVideoTranscoder(mContext, currentInputVideoPath, currentOutputVideoPath);
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(filepath);
        String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
        String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
        int transcodingBitrateLevel = 1;//我这里选择的2500*1000压缩，这里可以自己选择合适的压缩比例
        mShortVideoTranscoder.transcode(Integer.parseInt(width), Integer.parseInt(height), getEncodingBitrateLevel(transcodingBitrateLevel), false, new PLVideoSaveListener() {
            @Override
            public void onSaveVideoSuccess(String s) {
                Log.d("ttt","压缩成功");
            }

            @Override
            public void onSaveVideoFailed(final int errorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (errorCode) {
                            case ERROR_NO_VIDEO_TRACK:
                                Log.d("ttt","改文件没有视频信息");
                                break;
                            case ERROR_SRC_DST_SAME_FILE_PATH:
                                Log.d("ttt","源文件路径和目标路径不能相同");
                                break;
                            case ERROR_LOW_MEMORY:
                                Log.d("ttt","手机内存不足，无法对该视频进行时光倒流！");
                                break;
                            default:
                                Log.d("ttt","transcode failed: " + errorCode);
                        }
                    }
                });
            }

            @Override
            public void onSaveVideoCanceled() {
                Log.e("ttt","onSaveVideoCanceled");
            }

            @Override
            public void onProgressUpdate(float percentage) {
                Log.e("ttt","onProgressUpdate==========" + percentage);
            }
        });
    }

    /**
     * 设置压缩质量
     *
     * @param position
     * @return
     */
    private int getEncodingBitrateLevel(int position) {
        return ENCODING_BITRATE_LEVEL_ARRAY[position];
    }

    /**
     * 选的越高文件质量越大，质量越好
     */
    public static final int[] ENCODING_BITRATE_LEVEL_ARRAY = {
            500 * 1000,
            800 * 1000,
            1000 * 1000,
            1200 * 1000,
            1600 * 1000,
            2000 * 1000,
            2500 * 1000,
            4000 * 1000,
            8000 * 1000,
    };
}
