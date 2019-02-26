package com.caotu.duanzhi.module.other;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

import java.util.HashMap;
import java.util.Random;

public class TestActivity extends AppCompatActivity {
    int size = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
    }


    public void read(View view) {
        long time1 = System.currentTimeMillis();
        //初始化从sp读取历史记录
        MyApplication.getInstance().setMap(MySpUtils.getHashMapData());
        long time2 = System.currentTimeMillis();
        long time = time2 - time1;
        ToastUtil.showShort("读文件耗时:" + time + " 毫秒" + "  换成秒:" + (time / 1000));
    }

    public void save(View view) {
        long time1 = System.currentTimeMillis();
        MySpUtils.putHashMapData(MyApplication.getInstance().getMap());
        long time2 = System.currentTimeMillis();
        long time = time2 - time1;
        ToastUtil.showShort("存文件操作耗时:" + time + " 毫秒" + "  换成秒:" + (time / 1000));
    }

    public void readString(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Long> map = new HashMap<>(size);
                for (int i = 0; i < size; i++) {
                    map.put(getItemID(), System.currentTimeMillis());
                }
                MyApplication.getInstance().setMap(map);
                ToastUtil.showShort("假数据设置完毕");
            }
        }).start();
    }

    public void clear(View view) {
        MySpUtils.deleteKey(MySpUtils.SP_LOOK_HISTORY);
        ToastUtil.showShort("清除数据成功");
    }

    public void change(View view) {
        size += 10000;
        ToastUtil.showShort(size + "");
    }


    /**
     * 生成随机数当作getItemID
     * n ： 需要的长度
     *
     * @return
     */
    private static String getItemID() {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String str = random.nextInt(2) % 2 == 0 ? "num" : "char";
            if ("char".equalsIgnoreCase(str)) { // 产生字母
                int nextInt = random.nextInt(2) % 2 == 0 ? 65 : 97;
                // System.out.println(nextInt + "!!!!"); 1,0,1,1,1,0,0
                val += (char) (nextInt + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(str)) { // 产生数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    boolean change = false;
    private VideoEditor mEditor;

    public void changeImage(View view) {
        long time1 = System.currentTimeMillis();
        String video1 = PathConfig.LOCALFILE + "191317154973water.mp4";
        String video2 = PathConfig.LOCALFILE + "1921414513767water.mp4";
        if (mEditor == null) {
            mEditor = new VideoEditor();
            mEditor.setOnProgessListener(new onVideoEditorProgressListener() {

                @Override
                public void onProgress(VideoEditor v, int percent) {
//                    Log.i("jindu", "onProgress: " + percent);
                }
            });
        }

        String videoSrc = mEditor.executeConcatMP4(new String[]{video1, video2});
        long time2 = System.currentTimeMillis();
        Log.i("sudu", "time: " + (time2 - time1));
        ToastUtil.showShort(videoSrc);
//        ImageView viewById = findViewById(R.id.change_imageview);
//        TextView text = findViewById(R.id.textview_color);
//        if (change) {
//            viewById.setColorFilter(DevicesUtils.getColor(R.color.transparent));
//            setDrawableColor(text, Color.parseColor("#C7C7C7"));
//        } else {
//            viewById.setColorFilter(Color.parseColor("#6D5444"));
//            setDrawableColor(text, DevicesUtils.getColor(R.color.color_bottom_selector));
//        }
//
//        change = !change;
    }

    /**
     * 图片上色
     */
    public void setDrawableColor(TextView textView, int color) {
        Drawable[] drawables = textView.getCompoundDrawables();
        for (int i = 0, size = drawables.length; i < size; i++) {
            if (null != drawables[i]) {
                drawables[i].setColorFilter(new PorterDuffColorFilter(color,
                        PorterDuff.Mode.SRC_IN));
            }
        }
    }
}
