package com.sunfusheng.widget;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author sunfusheng on 2018/6/19.
 */
public class ImageData implements Serializable, Parcelable {
    public String url;
    public String text;

    public int realWidth;
    public int realHeight;

    public int startX;
    public int startY;
    public int width;
    public int height;

    public ImageData(String url) {
        this.url = url;
    }

    public ImageData from(ImageData imageData, LayoutHelper layoutHelper, int position) {
        if (imageData != null && layoutHelper != null) {
            Point coordinate = layoutHelper.getCoordinate(position);
            if (coordinate != null) {
                imageData.startX = coordinate.x;
                imageData.startY = coordinate.y;
            }

            Point size = layoutHelper.getSize(position);
            if (size != null) {
                imageData.width = size.x;
                imageData.height = size.y;
            }
        }
        return imageData;
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "url='" + url + '\'' +
                ", text='" + text + '\'' +
                ", realWidth=" + realWidth +
                ", realHeight=" + realHeight +
                ", startX=" + startX +
                ", startY=" + startY +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.text);
        dest.writeInt(this.realWidth);
        dest.writeInt(this.realHeight);
        dest.writeInt(this.startX);
        dest.writeInt(this.startY);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected ImageData(Parcel in) {
        this.url = in.readString();
        this.text = in.readString();
        this.realWidth = in.readInt();
        this.realHeight = in.readInt();
        this.startX = in.readInt();
        this.startY = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };
}
