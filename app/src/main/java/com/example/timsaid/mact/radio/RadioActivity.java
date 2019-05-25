package com.example.timsaid.mact.radio;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.timsaid.mact.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class RadioActivity extends Activity implements OnClickListener{
   private Activity activity;
    private Button Startrecording,stoptrecording,Playtape;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String voicePath ;
    private long time;
    private long currentTime;
    private String uploadFileName;
    public void RadioClick(){
        setContentView(R.layout.activity_radio);
        player = new MediaPlayer();
        Startrecording = (Button) findViewById(R.id.Startrecording);
        stoptrecording = (Button) findViewById(R.id.stoptrecording);
        Playtape = (Button) findViewById(R.id.Playtape);
        //点击事件
        Playtape.setOnClickListener(this);
        Startrecording.setOnClickListener(this);
        stoptrecording.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Startrecording://开始录音
                fasong();
                break;
            case R.id.stoptrecording://结束录音
                try {
//                    jieshu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.Playtape://播放录音
                play();
                break;
            default:
                break;
        }
    }

    /**
     * 发送语音
     */
    public void fasong(){
        if(recorder != null){
            //不等于空的时候让他变闲置
            recorder.reset();
        }else{
            recorder = new MediaRecorder();
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //输出格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        //设置音频编码器
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //缓存目录
        String str = FileUtil.getSDCardPath()+"mact/voice/";
        //检查该目录是否存在  否则创建
        FileUtil.checkDir(str);
        //设置文件名
        currentTime=System.currentTimeMillis();
        voicePath = str+ currentTime+".wav";
        uploadFileName=currentTime+".wav";
        //设置录音的输出路径
        recorder.setOutputFile(voicePath);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        time = System.currentTimeMillis();
    }

    /**
     * 暂停录音
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void zanting(){
        recorder.pause();
    }

    /**
     * 暂停后开始录音
     */
    public void resume(){
        recorder.start();
    }



    /**
     * 结束语音
     */
    public void jieshu(String msg){
        recorder.stop();
        long shijian =System.currentTimeMillis() - time;
        if(shijian<1000){//判断，如果录音时间小于一秒，则删除文件提示，过短
            File file = new File(voicePath);
            if(file.exists()){//判断文件是否存在，如果存在删除文件
                file.delete();//删除文件
                Toast.makeText(RadioActivity.this, "录音时间过短",Toast.LENGTH_SHORT).show();
            }
        }

//        UploadRadioFile uploadRadioFile = new UploadRadioFile();
//        uploadRadioFile.uploadFile(voicePath,"http://192.168.1.5:8080/jeecg-boot/sys/common/upload");
        //this.runUpFile();
        if(recorder != null){
            recorder.release();
            recorder = null;
            System.gc();
        }

        this.uploadMultiFile(msg);
    }





    /**
     * 文件上传
     *
     */

    public void uploadMultiFile(String msg) {
        /*final String url = "http://172.17.76.118:8080/jeecg-boot/sys/common/upload";*/
        final String url = "http://192.168.1.106:8080/mact_web/a/mact/mobile/upload?id="+msg;
        File file = new File("/"+voicePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",uploadFileName , fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                //设置超时
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "uploadMultiFile() e=" + e);
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "uploadMultiFile() response=" + response.body().string());
            }
        });
    }




    /**
     * 播放录音
     */
    public void play(){
        if(player != null){
            player.reset();
            try {
                //设置语言的来源
                player.setDataSource(voicePath);
                //初始化
                player.prepare();
                //开始播放
                player.start();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
