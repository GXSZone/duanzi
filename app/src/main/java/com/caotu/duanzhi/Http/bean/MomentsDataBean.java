package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhushijun on 2018/6/26
 */

public class MomentsDataBean implements Parcelable {
    public static final int TYPE_SELF = 0;
    public static final int TYPE_OTHER = 2;
    public static final int TYPE_THEME = 1;


    public static final int DATA_TYPE_video_00 = 0;
    public static final int DATA_TYPE_video_8 = 1;
    public static final int DATA_TYPE_image = 2;

    private int type;

    private String momentsId;
    private String momentsType;
    private String content;
    private List<String> imgs;

    private int with;
    private int height;

    private String theme;
    private String themeId;

    private String dimension;

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    private String userId;
    private String name;
    private String avatar;
    private String avatarHanger;//挂件的url

    public String getAvatarHanger() {
        return avatarHanger;
    }

    public void setAvatarHanger(String avatarHanger) {
        this.avatarHanger = avatarHanger;
    }

    private int liskes;
    private int unliskes;
    private int comments;
    private int shares;
    private int playCount;//播放次数

    private boolean isLike;
    private boolean unLike;
    private boolean isFocus;
    private BestMapBean bestMaps;

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    private String createtime;

    public boolean equals(Object obj) {
        if (obj instanceof MomentsDataBean) {
            MomentsDataBean bean = (MomentsDataBean) obj;
            //System.out.println("equal" + bean.momentsId);
            return (momentsId.equals(bean.momentsId));
        }
        return super.equals(obj);
    }

    public int hashCode() {
        MomentsDataBean bean = (MomentsDataBean) this;
        //System.out.println("Hash" + bean.momentsId);
        return momentsId.hashCode();

    }

    public static class BestMapBean implements Parcelable {
        /**
         * commentgood	赞数
         * commentid	评论id
         * commenttext	评论内容
         * userheadphoto	用户头像
         * userid	用户id
         * username	用户昵称
         */

        public int commentgood;
        public String commentid;
        public String commenttext;
        public String userheadphoto;
        public String userid;
        public String username;
        public boolean isGood;

        public BestMapBean() {
        }

        public BestMapBean(Parcel in) {
            this.commentgood = in.readInt();
            this.commentid = in.readString();
            this.commenttext = in.readString();
            this.userheadphoto = in.readString();
            this.userid = in.readString();
            this.username = in.readString();
            this.isGood = in.readInt() != 0;

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(commentgood);
            dest.writeString(commentid);
            dest.writeString(commenttext);
            dest.writeString(userheadphoto);
            dest.writeString(userid);
            dest.writeString(username);
            dest.writeInt(isGood ? 1 : 0);
        }


        @Override
        public String toString() {
            return "BestMapBean{" +
                    "commentgood=" + commentgood +
                    ", commentid='" + commentid + '\'' +
                    ", commenttext='" + commenttext + '\'' +
                    ", userheadphoto='" + userheadphoto + '\'' +
                    ", userid=" + userid +
                    ", username=" + username +
                    ", isGood=" + isGood +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<BestMapBean> CREATOR = new Creator<BestMapBean>() {

            @Override
            public BestMapBean createFromParcel(Parcel source) {
                return new BestMapBean(source);
            }

            @Override
            public BestMapBean[] newArray(int size) {
                return new BestMapBean[size];
            }

        };
    }

    public MomentsDataBean() {
    }

    public MomentsDataBean(String theme, String name, String avatar, String avatarHanger, List<String> imgs, int liskes, int unliskes, int comments, int shares, int playCount, String content, boolean isLike, boolean unLike, boolean isFocus, BestMapBean bestMaps) {
        this.theme = theme;
        this.name = name;
        this.avatar = avatar;
        this.avatarHanger = avatarHanger;
        this.imgs = imgs;
        this.liskes = liskes;
        this.unliskes = unliskes;
        this.comments = comments;
        this.shares = shares;
        this.playCount = playCount;
        this.content = content;
        this.isLike = isLike;
        this.unLike = unLike;
        this.isFocus = isFocus;
        this.bestMaps = bestMaps;
    }


    public BestMapBean getBestMaps() {
        return bestMaps;
    }

    public void setBestMaps(BestMapBean bestMaps) {
        this.bestMaps = bestMaps;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public int getLiskes() {
        return liskes;
    }

    public void setLiskes(int liskes) {
        this.liskes = liskes;
    }

    public void setUnliskes(int unliskes) {
        this.unliskes = unliskes;
    }

    public int getUnliskes() {
        return unliskes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean unLike() {
        return unLike;
    }

    public void setUnLike(boolean unLike) {
        this.unLike = unLike;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public void setMomentsId(String momentsId) {
        this.momentsId = momentsId;
    }

    public String getMomentsType() {
        return momentsType;
    }

    public void setMomentsType(String momentsType) {
        this.momentsType = momentsType;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getWith() {
        return with;
    }

    public void setWith(int with) {
        this.with = with;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "MomentsDataBean{" +
                "type=" + type +
                ", momentsId='" + momentsId + '\'' +
                ", momentsType='" + momentsType + '\'' +
                ", content='" + content + '\'' +
                ", imgs=" + imgs +
                ", with=" + with +
                ", height=" + height +
                ", theme='" + theme + '\'' +
                ", themeId='" + themeId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", avatarHanger='" + avatarHanger + '\'' +
                ", liskes=" + liskes +
                ", unliskes=" + unliskes +
                ", comments=" + comments +
                ", shares=" + shares +
                ", playCount=" + playCount +
                ", isLike=" + isLike +
                ", unLike=" + unLike +
                ", isFocus=" + isFocus +
                ", bestMaps=" + bestMaps +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.momentsId);
        dest.writeString(this.momentsType);
        dest.writeString(this.content);
        dest.writeStringList(this.imgs);
        dest.writeInt(this.with);
        dest.writeInt(this.height);
        dest.writeString(this.theme);
        dest.writeString(this.themeId);
        dest.writeString(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarHanger);
        dest.writeInt(this.liskes);
        dest.writeInt(this.unliskes);
        dest.writeInt(this.comments);
        dest.writeInt(this.shares);
        dest.writeInt(this.playCount);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeByte(this.unLike ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFocus ? (byte) 1 : (byte) 0);
        dest.writeValue(this.bestMaps);
    }

    protected MomentsDataBean(Parcel in) {
        this.type = in.readInt();
        this.momentsId = in.readString();
        this.momentsType = in.readString();
        this.content = in.readString();
        this.imgs = in.createStringArrayList();
        this.with = in.readInt();
        this.height = in.readInt();
        this.theme = in.readString();
        this.themeId = in.readString();
        this.userId = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.avatarHanger = in.readString();
        this.liskes = in.readInt();
        this.unliskes = in.readInt();
        this.comments = in.readInt();
        this.shares = in.readInt();
        this.playCount = in.readInt();
        this.isLike = in.readByte() != 0;
        this.unLike = in.readByte() != 0;
        this.isFocus = in.readByte() != 0;
        this.bestMaps = (BestMapBean) in.readValue(BestMapBean.class.getClassLoader());
    }

    public static final Creator<MomentsDataBean> CREATOR = new Creator<MomentsDataBean>() {
        @Override
        public MomentsDataBean createFromParcel(Parcel source) {
            return new MomentsDataBean(source);
        }

        @Override
        public MomentsDataBean[] newArray(int size) {
            return new MomentsDataBean[size];
        }
    };
}
