package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * 感兴趣用户字段
 */
public class InterestUserBean {
    public List<UserBeanIn> userlist;

    public class UserBeanIn {
        public String usersource; //已关注好友名
        public String username;
        public String userheadphoto;
        public String userid;
        public String authname;
        public String guajianurl;
        public AuthBean auth;
        public String userlevel; //获赞
    }

}
