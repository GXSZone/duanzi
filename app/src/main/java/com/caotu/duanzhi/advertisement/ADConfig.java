package com.caotu.duanzhi.advertisement;

/**
 * 广告埋点字段
 */
public interface ADConfig {

    String splash_show = "AZ_SP_show";
    String splash_click = "AZ_SP_click";
    String splash_skip = "AZ_SP_skip";

    String item_show = "AZ_XXL_show";
    String item_click = "AZ_XXL_click";
    String tab_video_show = "sysp_show";
    String tab_video_click = "sysp_click";
    String tab_pic_show = "sytp_show";
    String tab_pic_click = "sytp_click";
    String tab_text_show = "sydz_show";
    String tab_text_click = "sydz_click";

    String comment_show = "AZ_PL_show";
    String comment_click = "AZ_PL_click";

    String detail_header_show = "AZ_NRXQ_show";
    String detail_header_click = "AZ_NRXQ_click";

    String banner_show = "AZ_BANNER_show";
    String banner_click = "AZ_BANNER_click";
}
