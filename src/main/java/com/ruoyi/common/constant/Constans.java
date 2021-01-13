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
        STYLE_THREE("3"),
        STYLE_FOUR("4");

        private final String value;

        reportCss(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum AsynDownStatus{
        PROCESSED_SUCC("1"),
        PROCESSED_FAIL("2"),
        PROCESSED_INIT("4"),
        PROCESSING("3");

        private final String value;

        AsynDownStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Unit{
        UNIT_YUAN("1"), //元
        UNIT_WANYUAN("2");  //万元

        private final String value;

        Unit(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
