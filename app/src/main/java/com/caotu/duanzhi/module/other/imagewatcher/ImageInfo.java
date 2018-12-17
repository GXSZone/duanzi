package com.caotu.duanzhi.module.other.imagewatcher;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片信息
 */
public class ImageInfo implements Parcelable {

  private String thumbnailUrl;// 缩略图，质量很差
  private String originUrl;// 原图或者高清图

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public String getOriginUrl() {
    return originUrl;
  }

  public void setOriginUrl(String originUrl) {
    this.originUrl = originUrl;
  }

  @Override public String toString() {
    return "ImageInfo{"
        + "thumbnailUrl='"
        + thumbnailUrl
        + '\''
        + ", originUrl='"
        + originUrl
        + '\''
        + '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.thumbnailUrl);
    dest.writeString(this.originUrl);
  }

  public ImageInfo() {
  }

  protected ImageInfo(Parcel in) {
    this.thumbnailUrl = in.readString();
    this.originUrl = in.readString();
  }

  public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
    @Override
    public ImageInfo createFromParcel(Parcel source) {
      return new ImageInfo(source);
    }

    @Override
    public ImageInfo[] newArray(int size) {
      return new ImageInfo[size];
    }
  };
}