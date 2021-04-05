package com.ruoyi.framework.web.page;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * About: user服务通用返回报文
 * Other:
 * Created: jyhuang on 2016/6/27 11:14.
 * Editored:
 */
@Getter
@Setter
public class BaseResp<T> implements Serializable {
    private static final long serializableUID = 1L;
    /**
     * 返回应答码-默认为失败
     * */
    private int code = Code.FAIL.getValue();
    //返回应答信息
    private String msg;
    //返回数据域
    private T data;

    public BaseResp code(final int code) {
        this.code = code;
        return this;
    }

    public BaseResp msg(final String msg) {
        this.msg = msg;
        return this;
    }

    public BaseResp data(final T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "BaseRsp [" +
                "code=" + code +
                ", msg=" + msg +
                ", data=" + JSON.toJSONString(data) + "]";
    }

}
