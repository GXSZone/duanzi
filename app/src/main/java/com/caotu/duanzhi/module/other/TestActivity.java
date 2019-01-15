package com.caotu.duanzhi.module.other;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;

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
}
