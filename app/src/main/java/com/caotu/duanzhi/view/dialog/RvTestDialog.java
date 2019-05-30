package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

public class RvTestDialog extends BaseDialogFragment {

    private RecyclerView rv;

    /**
     * 如果想要点击外部消失的话 重写此方法
     *
     * @param savedInstanceState
     * @return
     */

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //设置点击外部可消失
        dialog.setCanceledOnTouchOutside(false);
        //设置使软键盘弹出的时候dialog不会被顶起
        Window win = dialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        return dialog;
    }

    @Override
    protected void initView(View inflate) {
        rv = inflate.findViewById(R.id.rv_content);

        inflate.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (DevicesUtils.getScreenHeight() * 0.65)));
        bingDate();

    }

    private void bingDate() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            arrayList.add("test" + i);
        }

        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(android.R.id.text1, item);
            }

        };
        adapter.setNewData(arrayList);
        rv.setAdapter(adapter);

    }

    @Override
    public int getLayout() {
        return R.layout.layout_just_rv;
    }
}
