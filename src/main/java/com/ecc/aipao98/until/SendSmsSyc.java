package com.ecc.aipao98.until;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 诚立业单条短信测试 模板
 * @author zym
 * @date 2016-6-24 上午11:25:12
 */
@Slf4j
public class SendSmsSyc {

    public static HttpResult  sendSms(String phone,String messageCode) throws Exception {


        //String url = "https://www.sms-cly.cn/v7/msg/submit.json";
        String url = "http://sms.test.cly.cn:9001/v7/msg/submit.json";
        String userName = "yijinka";
        String password = "u8at0p";
        String content = "【诚立业】您的验证码是:"+messageCode;//发送手机的验证码
        String mobile = phone; //发送短信的手机号

        String seqid = UUID.randomUUID().toString();
        MsgSubmit msgSubmit = new MsgSubmit();
        msgSubmit.setUserName(userName);
        msgSubmit.setSign(MD5Utils.MD5Encode(userName + password + mobile + content));
        msgSubmit.setMobile(mobile);
        msgSubmit.setContent(content);
        msgSubmit.setSeqid(seqid);

        Gson gson = new Gson();
        StringBuilder jsonSb = new StringBuilder(gson.toJson(msgSubmit));

        HttpResult httpResult = HttpUtils.post(url, jsonSb, Charsets.UTF8);

        System.out.println(jsonSb.toString());
        System.out.println(httpResult.getContent().toString());
        log.error("----SendSmsSyc.sendSms--短信接口调用......");
        return httpResult;
    }
}
