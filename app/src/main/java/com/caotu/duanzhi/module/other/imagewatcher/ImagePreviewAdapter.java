package com.caotu.duanzhi.module.other.imagewatcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.imagewatcher.photoview.PhotoView;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author SherlockHolmes
 */
public class ImagePreviewAdapter extends PagerAdapter {

    private static final String TAG = "ImagePreview";
    private List<ImageInfo> imageInfo;
    private HashMap<String, SubsamplingScaleImageViewDragClose> imageHashMap = new HashMap<>();
    private HashMap<String, PhotoView> imageGifHashMap = new HashMap<>();
    private String finalLoadUrl = "";

    public ImagePreviewAdapter(@NonNull List<ImageInfo> imageInfo) {
        super();
        this.imageInfo = imageInfo;
    }

    public void closePage() {
        try {
            if (imageHashMap != null && imageHashMap.size() > 0) {
                for (Object o : imageHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((SubsamplingScaleImageViewDragClose) entry.getValue()).destroyDrawingCache();
                        ((SubsamplingScaleImageViewDragClose) entry.getValue()).recycle();
                    }
                }
                imageHashMap.clear();
                imageHashMap = null;
            }
            if (imageGifHashMap != null && imageGifHashMap.size() > 0) {
                for (Object o : imageGifHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((PhotoView) entry.getValue()).destroyDrawingCache();
                        ((PhotoView) entry.getValue()).setImageBitmap(null);
                    }
                }
                imageGifHashMap.clear();
                imageGifHashMap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return imageInfo.size();
    }

    public static final int MODE_SCALE_TO_MEDIUM_TO_MAX_TO_MIN = 1001;// 三级放大
    public static final int MODE_SCALE_TO_MAX_TO_MIN = 1002;// 二级放大，最大与最小
    public static final int MODE_SCALE_TO_MEDIUM_TO_MIN = 1003;// 二级放大，中等与最小

    private int index = 0;// 默认显示第几个
    private float minScale = 1.0f;// 最小缩放倍数
    private float mediumScale = 3.0f;// 中等缩放倍数
    private float maxScale = 5.0f;// 最大缩放倍数
    private int zoomTransitionDuration = 200;// 动画持续时间 单位毫秒 ms

    // 加载失败时的占位图
    private int errorPlaceHolder = R.mipmap.shenlue_logo;

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (activity == null) {
            return container;
        }
        View convertView = View.inflate(activity, R.layout.image_watcher_item_photoview, null);
        final ProgressBar progressBar = convertView.findViewById(R.id.progress_view);
        final FingerDragHelper fingerDragHelper = convertView.findViewById(R.id.fingerDragHelper);
        final SubsamplingScaleImageViewDragClose imageView = convertView.findViewById(R.id.photo_view);
        final PhotoView imageGif = convertView.findViewById(R.id.gif_view);

        final ImageInfo info = this.imageInfo.get(position);
        final String originPathUrl = info.getOriginUrl();

        imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageViewDragClose.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomDuration(zoomTransitionDuration);
        imageView.setMinScale(minScale);
        imageView.setMaxScale(maxScale);
        imageView.setDoubleTapZoomScale(mediumScale);

        imageGif.setZoomTransitionDuration(zoomTransitionDuration);
        imageGif.setMinimumScale(minScale);
        imageGif.setMaximumScale(maxScale);
        imageGif.setScaleType(ImageView.ScaleType.FIT_CENTER);


        fingerDragHelper.setOnAlphaChangeListener(new FingerDragHelper.onAlphaChangedListener() {
            @Override
            public void onTranslationYChanged(MotionEvent event, float translationY) {
                float yAbs = Math.abs(translationY);
                float percent = yAbs / DevicesUtils.getScreenHeight();
                float number = 1.0F - percent;

                if (activity instanceof PictureWatcherActivity) {
                    ((PictureWatcherActivity) activity).setAlpha(number);
                }

                if (imageGif.getVisibility() == View.VISIBLE) {
                    imageGif.setScaleY(number);
                    imageGif.setScaleX(number);
                }
                if (imageView.getVisibility() == View.VISIBLE) {
                    imageView.setScaleY(number);
                    imageView.setScaleX(number);
                }
            }
        });


        imageGifHashMap.remove(originPathUrl);
        imageGifHashMap.put(originPathUrl, imageGif);

        imageHashMap.remove(originPathUrl);
        imageHashMap.put(originPathUrl, imageView);

        finalLoadUrl = originPathUrl;

        finalLoadUrl = finalLoadUrl.trim();
        final String url = finalLoadUrl;

        // 显示加载圈圈
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(activity).downloadOnly().load(url).addListener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target,
                                        boolean isFirstResource) {

                Glide.with(activity).downloadOnly().load(url).addListener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target,
                                                boolean isFirstResource) {

                        Glide.with(activity).downloadOnly().load(url).addListener(new RequestListener<File>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target,
                                                        boolean isFirstResource) {
                                loadFailed(imageView, imageGif, progressBar, e);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                loadSuccess(resource, imageView, imageGif, progressBar);
                                return true;
                            }
                        }).into(new FileTarget() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        loadSuccess(resource, imageView, imageGif, progressBar);
                        return true;
                    }
                }).into(new FileTarget() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
                return true;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource,
                                           boolean isFirstResource) {
                loadSuccess(resource, imageView, imageGif, progressBar);
                return true;
            }
        }).into(new FileTarget() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }
        });

        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            GlideUtils.clearMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (imageHashMap != null && imageHashMap.get(imageInfo.get(position).getOriginUrl()) != null) {
                imageHashMap.get(imageInfo.get(position).getOriginUrl()).destroyDrawingCache();
                imageHashMap.get(imageInfo.get(position).getOriginUrl()).recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (imageGifHashMap != null && imageGifHashMap.get(imageInfo.get(position).getOriginUrl()) != null) {
                imageGifHashMap.get(imageInfo.get(position).getOriginUrl()).destroyDrawingCache();
                imageGifHashMap.get(imageInfo.get(position).getOriginUrl()).setImageBitmap(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, final Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private void loadFailed(SubsamplingScaleImageViewDragClose imageView, ImageView imageGif, ProgressBar progressBar, GlideException e) {
        progressBar.setVisibility(View.GONE);
        imageGif.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        imageView.setZoomEnabled(false);
        imageView.setImage(ImageSource.resource(errorPlaceHolder));

        String errorMsg = "加载失败";
        if (e != null) {
            errorMsg = errorMsg.concat(":\n").concat(e.getMessage());
        }
        if (errorMsg.length() > 200) {
            errorMsg = errorMsg.substring(0, 199);
        }
        ToastUtil.showShort(errorMsg);
    }

    private void loadSuccess(File resource, SubsamplingScaleImageViewDragClose imageView, ImageView imageGif, ProgressBar progressBar) {
        String imagePath = resource.getAbsolutePath();
        boolean isCacheIsGif = GlideUtils.isGifImageWithMime(imagePath);
        if (isCacheIsGif) {
            loadGifImageSpec(imagePath, imageView, imageGif, progressBar);
        } else {
            loadImageSpec(imagePath, imageView, imageGif, progressBar);
        }
    }

    private void setImageSpec(final String imagePath, final SubsamplingScaleImageViewDragClose imageView) {
        boolean isLongImage = GlideUtils.isLongImage( imagePath);
        if (isLongImage) {
            imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_START);
            imageView.setMinScale(GlideUtils.getLongImageMinScale( imagePath));
            imageView.setMaxScale(GlideUtils.getLongImageMaxScale( imagePath));
            imageView.setDoubleTapZoomScale(GlideUtils.getLongImageMaxScale(imagePath));
        } else {
            boolean isWideImage = GlideUtils.isWideImage(imagePath);
            boolean isSmallImage = GlideUtils.isSmallImage(imagePath);
            if (isWideImage) {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(minScale);
                imageView.setMaxScale(maxScale);
                imageView.setDoubleTapZoomScale(GlideUtils.getWideImageDoubleScale( imagePath));
            } else if (isSmallImage) {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CUSTOM);
                imageView.setMinScale(GlideUtils.getSmallImageMinScale(imagePath));
                imageView.setMaxScale(GlideUtils.getSmallImageMaxScale(imagePath));
                imageView.setDoubleTapZoomScale(GlideUtils.getSmallImageMaxScale( imagePath));
            } else {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(minScale);
                imageView.setMaxScale(maxScale);
                imageView.setDoubleTapZoomScale(mediumScale);
            }
        }
    }

    private void loadImageSpec(final String imagePath, final SubsamplingScaleImageViewDragClose imageView, final ImageView imageGif, final ProgressBar progressBar) {

        imageGif.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        setImageSpec(imagePath, imageView);

        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        imageView.setImage(ImageSource.uri(Uri.fromFile(new File(imagePath))));

        imageView.setOnImageEventListener(new SubsamplingScaleImageViewDragClose.OnImageEventListener() {
            @Override
            public void onReady() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onImageLoaded() {

            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }

            @Override
            public void onPreviewReleased() {

            }
        });
    }

    private void loadGifImageSpec(final String imagePath, final SubsamplingScaleImageViewDragClose imageView, final ImageView imageGif, final ProgressBar progressBar) {

        imageGif.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);

        Glide.with(MyApplication.getInstance())
                .asGif()
                .load(imagePath)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(errorPlaceHolder))
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target,
                                                boolean isFirstResource) {
                        imageGif.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImage(ImageSource.resource(errorPlaceHolder));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageGif);
    }
}