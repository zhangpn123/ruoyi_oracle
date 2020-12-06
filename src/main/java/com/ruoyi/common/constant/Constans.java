package com.ruoyi.common.constant;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2020-12-06 13:43
 **/
public class  Constans {

    public enum reportCss{

        STYLE_ONE("1"),
        STYLE_TWO("2"),
        STYLE_THREE("3");

        private final String value;

        reportCss(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
