package com.ecc.aipao98.controller;

import com.ecc.aipao98.dao.SignupDetailDao;
import com.ecc.aipao98.dao.SmsLogDao;
import com.ecc.aipao98.pojo.SignupDetail;
import com.ecc.aipao98.pojo.SmsLog;
import com.ecc.aipao98.until.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sunyc   验证码校验
 * @create 2022-08-19 10:30
 */
@RestController
@RequestMapping("/codeCheck")
@Slf4j
public class codeCheckCrontroller {
    @Autowired
    private SignupDetailDao signupDetailDao;//报名表
    @Autowired
    private SmsLogDao smsLogDao;//短信流水
    /**
    *@Description: 校验验证码是否正确
    *@date: 2022/8/19
    */
    @PostMapping("/VerifiCodeImageCheck")
    public R codeImageCheck(HttpServletRequest request, HttpServletResponse response){
        log.error("---------------------------------------------------------");
        log.error("----------------------校验验证码是否正确并发送短信-----------------------------------");

        //校验图片验证码
        Map<String, String> resultMap = CheckCodeUtil.checkCode(request);
        //判断code是否为空
        if("1".equals(resultMap.get("flag"))){
            return R.error().message("图片验证码已过期，请重新输入");
        }
        //判断验证码是否正确
        if("2".equals(resultMap.get("flag"))){
            return R.error().message("图片验证码输入错误，请重新输入");
        }
        //存放返回的数据
        Map<String,Object> map=new HashMap<>();
        map.put("request",resultMap);
        //日志
        log.error("---codeCheckCrontroller.codeImageCheck-验证吗 check 通过。。开始请求短信接口发送短信");
        //检查该手机号五分钟内是否有有效的验证码
        int fiveCount = smsLogDao.isFiveCount(resultMap.get("phone"));
        //每天的发送次数
        int sendCount = smsLogDao.sendCodeCount(resultMap.get("phone"));
        if(fiveCount>0){
            return R.error().message("---codeCheckCrontroller.codeImageCheck-五分钟内验证码有效。。。。。");
        }
        if(sendCount>5){
            return R.error().message("---codeCheckCrontroller.codeImageCheck-获取验证码已达每天的上限。。。");
        }
        //判断该手机号是否已报名
        int phoneCount = signupDetailDao.checkPhone(resultMap.get("phone"));
        if(phoneCount>0){
            //清除session域中的图片验证码
            request.getSession().removeAttribute(resultMap.get("sessionCode"));
            //清除session域中的来源省份id
            request.getSession().removeAttribute(resultMap.get("provinceId"));
            return R.error().message("该手机号："+resultMap.get("phone")+"--已报名");
        }
        //生成验证码
        int messageCode= (int) ((Math.random()*9+1)*1000);
        //request.getSession().setAttribute("phonecode:"+phone,messageCode);
        log.error("---codeCheckCrontroller.codeImageCheck-短信验证码:"+messageCode+"-------");
        HttpResult httpResult=new HttpResult();
        try {//不调用注释掉
            //httpResult=SendSmsSyc.sendSms(phone, String.valueOf(messageCode));
        } catch (Exception e) {
            //throw new RuntimeException("短信接口调用失败。。。。。。");
            return R.error().message("---codeCheckCrontroller.codeImageCheck-短信接口调用失败。。。。。。");
        }
        //保存发送短信流水
        SmsLog smsLog=new SmsLog();
        smsLog.setResultStatus("1");
        smsLog.setCode(Integer.valueOf(messageCode));
        smsLog.setResultMessage("发送成功");
        smsLog.setResultId(UUID.randomUUID().toString());
        smsLog.setIp(resultMap.get("ipAddress"));
        smsLog.setCreateTime(new Date());
        smsLog.setCellPhone(resultMap.get("phone"));
        int insert = smsLogDao.insert(smsLog);
        if(insert>0){
            map.put("smslog",smsLog);//返回的数据
            log.error("---codeCheckCrontroller.codeImageCheck---短信流水插入成功-----");
            return R.ok().data(map);
        }else {
            log.error("---codeCheckCrontroller.codeImageCheck---短信流水插入失败-----");
            return R.error().message("codeCheckCrontroller.codeImageCheck---短信流水插入失败");
        }
    }
    /**
    *@Description: 校验手机验证码是否正确
    *@date: 2022/8/19
    */
    @PostMapping("/phoneCodeCheck")
    public R phoneCodeCheck(HttpServletRequest request,HttpServletResponse response){
        log.error("---------------------------------------------------------");
        log.error("----------------------报名-----------------------------------");

        //校验图片验证码
        Map<String, String> resultMap = CheckCodeUtil.checkCode(request);

        log.error("--codeCheckCrontroller.phoneCodeCheck==resultmap:"+resultMap);
        //判断code是否为空
        if("1".equals(resultMap.get("flag"))){
            return R.error().message("图片验证码已过期，请重新输入");
        }
        //判断验证码是否正确
        if("2".equals(resultMap.get("flag"))){
            return R.error().message("图片验证码输入错误，请重新输入");
        }
        //判断该手机号是否已报名
        int phoneCount = signupDetailDao.checkPhone(resultMap.get("phone"));
        if(phoneCount>0){
            //清除session域中的图片验证码
            request.getSession().removeAttribute(resultMap.get("sessionCode"));
            //清除session域中的来源省份id
            request.getSession().removeAttribute(resultMap.get("provinceId"));
            return R.error().message("该手机号："+resultMap.get("phone")+"--已报名");
        }
        //判断手机验证码是否正确
        int phoneCodeCount = smsLogDao.checkPhoneFlag(resultMap.get("phone"), resultMap.get("phoneCode"));
        if(phoneCodeCount>0){
            //插入报名表
            SignupDetail signupDetail=new SignupDetail();
            signupDetail.setCellPhone(resultMap.get("phone"));
            signupDetail.setCreateDate(new Date());
            signupDetail.setProvinceId(resultMap.get("provinceId"));
            signupDetail.setSourceChannel("wechat");
            Map<String,Object> map=new HashMap<>();
            //把请求参数也返回
            map.put("request",resultMap);
            int insert = signupDetailDao.insert(signupDetail);
            map.put("signupDetail",signupDetail);
            if (insert>0){
                log.error("--codeCheckCrontroller.phoneCodeCheck==报名成功...");
            }
            return R.ok().data(map);
        }
        //清除session域中的图片验证码
        request.getSession().removeAttribute(resultMap.get("sessionCode"));
        //清除session域中的来源省份id
        request.getSession().removeAttribute(resultMap.get("provinceId"));

        return R.error().message("手机验证码不正确");
    }
}
