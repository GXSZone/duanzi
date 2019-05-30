package weige.umenglib;

/**
 * 对用户信息的封装
 * https://developer.umeng.com/docs/66632/detail/66639#h2-u6388u6743u57FAu672Cu4FE1u606Fu89E3u6790u5982u4E0B15
 */
public class UserAuthInfo {


    private String uid = "";
    private String name = "";
    /**
     * //未验证 "0":男 "1":女, QQ 微信不返回 用户性别, 始终为 "0"
     */
    private String gender = "0";
    private String iconurl = "";

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = "女".equals(gender) ? "1" : "0";
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }
}
