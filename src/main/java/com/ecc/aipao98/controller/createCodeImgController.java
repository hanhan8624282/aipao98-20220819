package com.ecc.aipao98.controller;

import com.ecc.aipao98.until.ImageUtil;
import com.ecc.aipao98.until.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author sunyc 图片验证码生成以及获取
 * @create 2022-08-19 10:21
 */
@RestController
@RequestMapping("/code")
@Slf4j
public class createCodeImgController {
    /**
     * 去除 l、o、I、O、0、1
     */
    private static final char[] CHAR_ARRAY = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    /**生成登录图片验证码
     * @param session
     * @return
     */
    @GetMapping("/getVerifyImage")
    public void getVerifyImage(HttpServletRequest request, HttpServletResponse res, HttpSession session) throws IOException {
        log.error("---------------------------------------------------------");
        log.error("----------------------图片验证码加载-----------------------------------");

        String checkCode = RandomStringUtils.random(4, CHAR_ARRAY);
        //获取访问用户的ip地址
        String ipAddress = NetworkUtil.getIpAddress(request);
        log.error("----getVerifyImage-- 获取用户的ip地址："+ipAddress);
        log.error("----getVerifyImage-- 开始生产验证码checkCode="+checkCode);
        //TODO 直接返回byte数据
        ByteArrayOutputStream os = (ByteArrayOutputStream) ImageUtil.generator(102, 25, checkCode);
        byte[] bytes = os.toByteArray();
        try {
            os.close();
        } catch (IOException e) {
        }
        session.setAttribute("checkCode:"+ipAddress, checkCode);//将验证码值存入HttpSession中
        log.error("----getVerifyImage--存入session域中：=checkCode:"+ipAddress);
        res.setContentType("image/jpg");
        res.setContentLength(bytes.length);
        res.getOutputStream().write(bytes);
    }
    //获取图片验证码
    @GetMapping("/getCheckCode")//该方法为验证码值的获取方法
    public Object getCheckCode(HttpSession session,HttpServletRequest request) {
        //获取访问用户的ip地址
        String ipAddress = NetworkUtil.getIpAddress(request);
        log.error("获取验证码=="+session.getAttribute("checkCode:"+ipAddress));
        return session.getAttribute("checkCode");
    }
}
