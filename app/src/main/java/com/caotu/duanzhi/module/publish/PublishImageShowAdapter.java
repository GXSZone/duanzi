package com.caotu.duanzhi.module.publish;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.RImageView;

import java.util.ArrayList;
import java.util.List;

public class PublishImageShowAdapter extends RecyclerView.Adapter {
    public static final int ADD_IMAGE_VIEW = 1;
    public static final int COMMON_IMAGE_VIEW = 2;
    private List<LocalMedia> imagUrls;
    private int itemWidth;
    private boolean isVideo;

    /**
     * 设置数据集
     *
     * @param imagUrls
     */
    public void setImagUrls(List<LocalMedia> imagUrls, boolean isvideo) {
        if (imagUrls == null) {
            this.imagUrls = new ArrayList<>();
        } else {
            this.imagUrls = imagUrls;
        }
        isVideo = isvideo;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            default:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publish_normal_layout, parent, false);
                viewHolder = new CommonViewHolder(convertView);
                break;
            case ADD_IMAGE_VIEW:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publish_add_layout, parent, false);
                viewHolder = new AddViewHolder(convertView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            default:
                CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
                String url = "";
                if (position < imagUrls.size()) {
                    LocalMedia localMedia = imagUrls.get(position);
                    //判断是否是视频
                    boolean isVideo = PictureMimeType.isVideo(localMedia.getPictureType());
                    if (isVideo) {
                        url = localMedia.getPath();
                    } else {
                        url = localMedia.getCompressPath();
                    }
                }
                // TODO: 2018/11/6 直接控制图片
                RequestOptions options = new RequestOptions();
                options.override(160, 160)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .placeholder(R.drawable.image_placeholder);
                Glide.with(MyApplication.getInstance())
                        .asBitmap()
                        .load(url)
                        .apply(options)
                        .into(commonViewHolder.normalView);

                commonViewHolder.VideoImageView.setVisibility(isVideo ? View.VISIBLE : View.GONE);
                commonViewHolder.rightImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        imagUrls.remove(position);
                        if (isVideo) {
                            imagUrls.clear();
                        } else {
                            imagUrls.remove(position);
                        }
                        notifyDataSetChanged();
                        if (onClickItemListener != null) {
                            onClickItemListener.onClickDelete(imagUrls);
                        }
                    }
                });
                commonViewHolder.normalLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isVideo) {
                            //预览视频
                            //人为控制一下 防止越界
                            startPreview(imagUrls, 0);
                        } else {
                            //预览图片
                            startPreview(imagUrls, position);
                        }
                    }
                });
                break;
            case ADD_IMAGE_VIEW:
                AddViewHolder addViewHolder = (AddViewHolder) holder;
                addViewHolder.addView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //继续选择图片
                        if (onClickItemListener != null) {
                            onClickItemListener.onClickAdd();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (isVideo) {
            return imagUrls != null ? imagUrls.size() : 0;
        }
        if (imagUrls != null) {
            int size = imagUrls.size();
            if (size == 0) {
                return 0;
            }
            if (size < 9) {
                return size + 1;
            } else {
                return 9;
            }

        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        int size = imagUrls.size();
        if (isVideo) {
            return COMMON_IMAGE_VIEW;
        }
        if (position < size) {
            return COMMON_IMAGE_VIEW;
        } else if (size < 9) {
            return ADD_IMAGE_VIEW;
        }
        return super.getItemViewType(position);
    }

    /**
     * 预览图片和视频
     *
     * @param previewImages
     * @param position
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        boolean isVideo = PictureMimeType.isVideo(previewImages.get(position).getPictureType());
        if (isVideo) {
            PictureSelector.create(runningActivity).externalPictureVideo(previewImages.get(position).getPath());
        } else {
            PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                    .themeStyle(R.style.picture_QQ_style).openExternalPreview(position, previewImages);
        }
        // TODO: 2018/11/6 这个是改过的UI页面 ,先用框架原生的用着
//        Activity context = MyApplication.getInstance().getRunningActivity();
//        Intent intent = new Intent(context, PictureVideoAndImageActivity.class);
//        intent.putExtra("local_media", (Serializable) previewImages);
//        intent.putExtra("position", position);
//        context.startActivity(intent);
    }


    OnClickItemListener onClickItemListener;

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener {
        void onClickDelete(List<LocalMedia> imagUrls);

        void onClickAdd();
    }


    class CommonViewHolder extends RecyclerView.ViewHolder {
        public RImageView normalView;
        public ImageView rightImageView;
        public ImageView VideoImageView;
        public RelativeLayout normalLayout;

        public CommonViewHolder(View itemView) {
            super(itemView);
            normalLayout = itemView.findViewById(R.id.item_publish_normal_rl);
            normalView = itemView.findViewById(R.id.item_publish_normal_giv);
            rightImageView = itemView.findViewById(R.id.item_publish_normal_delete_iv);
            VideoImageView = itemView.findViewById(R.id.item_publish_normal_play_iv);

        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout addView;
//        public RelativeLayout addLayout;

        public AddViewHolder(View itemView) {
            super(itemView);
//            addLayout = itemView.findViewById(R.id.item_publish_add_rl);
            addView = itemView.findViewById(R.id.item_publish_add_iv);
        }

    }
}


