package com.caotu.duanzhi.module.publish;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            default:
                CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
                String url = "";
                LocalMedia localMedia;
                if (isVideo) {
                    localMedia = imagUrls.get(position);
                } else {
                    if (imagUrls.size() < 9) {
                        localMedia = imagUrls.get(position - 1);
                    } else {
                        localMedia = imagUrls.get(position);
                    }
                }
                //判断是否是视频
                boolean isVideo = PictureMimeType.isVideo(localMedia.getPictureType());
                if (isVideo) {
                    url = localMedia.getPath();
                } else {
                    if (TextUtils.isEmpty(localMedia.getCompressPath())) {
                        url = localMedia.getPath();
                    } else {
                        url = localMedia.getCompressPath();
                    }
                }

                // TODO: 2018/11/6 直接控制图片
                RequestOptions options = new RequestOptions();
                options.override(160, 160)
                        .centerCrop()
                        .placeholder(R.drawable.image_placeholder);
                Glide.with(MyApplication.getInstance())
                        .load(url)
                        .apply(options)
                        .into(commonViewHolder.normalView);

                commonViewHolder.VideoImageView.setVisibility(isVideo ? View.VISIBLE : View.GONE);
                commonViewHolder.rightImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isVideo) {
                            imagUrls.clear();
                        } else {
                            if (imagUrls.size() < 9) {
                                imagUrls.remove(position - 1);
                            } else {
                                imagUrls.remove(position);
                            }

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
                            if (imagUrls.size() < 9) {
                                startPreview(imagUrls, position - 1);
                            } else {
                                startPreview(imagUrls, position);
                            }
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
        if (position == 0 && size < 9) {
            return ADD_IMAGE_VIEW;
        } else if (position < size) {
            return COMMON_IMAGE_VIEW;
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
            if (DevicesUtils.isOppo()) {
                PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                        .themeStyle(R.style.picture_default_style).openExternalPreview(position, previewImages);
            } else {
                PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                        .themeStyle(R.style.picture_QQ_style).openExternalPreview(position, previewImages);
            }
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
        public View addView;

        public AddViewHolder(View itemView) {
            super(itemView);
            addView = itemView.findViewById(R.id.item_publish_add_iv);
        }

    }
}


