package com.ruoyi.framework.web.page;

/**
 * About ：返回报文
 * @Author : jyhuang on 2019/3/29.9:10
 */
public class Response {

    public static BaseResp failure(int code, String msg) {
        BaseResp result = new BaseResp();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static BaseResp failure(String msg) {
        BaseResp result = new BaseResp();
        result.setCode(Code.FAIL.getValue());
        result.setMsg(msg);
        return result;
    }

    public static BaseResp ok(String msg, Object data) {
        BaseResp result = new BaseResp();
        result.setCode(Code.SUCCESS.getValue());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static BaseResp ok(Object data) {
        BaseResp result = new BaseResp();
        result.setCode(Code.SUCCESS.getValue());
        result.setData(data);
        return result;
    }

    public static BaseResp ok(String msg) {
        BaseResp result = new BaseResp();
        result.setCode(Code.SUCCESS.getValue());
        result.setMsg(msg);
        return result;
    }

    public static void main(String [] args){
        System.out.println(Response.ok("123"));
    }
}
