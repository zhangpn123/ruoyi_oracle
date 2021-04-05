package com.ruoyi.framework.web.page;

/**
 * 响应码
 * @author jyhuang
 */
public enum Code {
	//操作成功
	SUCCESS(0),
	//操作失败
	FAIL(201),
	//用户未登录
	NO_LOGIN(202),
	//用户无权限
	NO_PERMISSION(203),
	//服务器异常
	EXCEPTION(301),
	//用户已禁用
	ACCOUNT_DISABLE(205),
	//用户不存在
	ACCOUNT_NOTEXIST(206),
	//license异常
	LICENSE_ERROR(207),
	//验证码错误
	CAPTCHA_ERROR(208),
	//机型不兼容
	TAS_MOD_NOTMATCH(900),
	//面向机构不存在
	TAS_FACEAGE_NOTEXIST(901),
	//应用已是最新版本
	TAS_HAS_NEW_VERSION(902),
	//任务不存在
	TAS_NO_TASK(902),
	//终端时间戳非法
	TAS_TIMESTAMP_ERROR(903),
	//TOKEN异常
	TOKEN_ERROR(401);
	//1000-1100为KDP 专用
	private final int value;
	Code(int value) {
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
}
