package com.ecc.aipao98.until;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sunyc  图片验证码校验
 * @create 2022-08-19 13:09
 */
@Slf4j
public class CheckCodeUtil {

    public static Map<String, String> checkCode(HttpServletRequest request){
        log.error("---------------------------------------------------------");
        log.error("----------------------图片验证码校验-----------------------------------");


        Map<String,String> resultMap=new HashMap<>();
        //从request请求获取请求参数 手机号
        String phone = String.valueOf(request.getParameter("phone"));
        //从request请求获取请求参数 验证码
        String code = String.valueOf(request.getParameter("code"));
        String phoneCode = String.valueOf(request.getParameter("phoneCode"));
        //从request请求获取请求参数 status=1 check 验证码  status=0 check 验证码和发送短息
        String status = String.valueOf(request.getParameter("status"));
        //获取访问用户的ip地址
        String ipAddress = NetworkUtil.getIpAddress(request);
        //获取来源省份id
        String provinceId = (String) request.getSession().getAttribute("provinceId");

        resultMap.put("phone",phone);
        resultMap.put("code",code);
        resultMap.put("phoneCode",phoneCode);
        resultMap.put("ipAddress",ipAddress);
        resultMap.put("sessionCode","checkCode:" + ipAddress);
        resultMap.put("provinceId",provinceId);


        //获取存在session域中的coed码
        String sessionCode = String.valueOf(request.getSession().getAttribute("checkCode:" + ipAddress));
        //日志记录
        log.error("----CheckCodeUtil.checkCode-请求参数：phone="+phone+",code="+code+",sessionCode="+sessionCode);
        //判断code是否为空
        if(StringUtils.isBlank(code) || StringUtils.isBlank(sessionCode)){
             resultMap.put("flag","1");
            //return R.error().message("验证码已过期，请重新输入");
        }
        //判断验证码是否正确
        if(!code.equalsIgnoreCase(sessionCode)){
            resultMap.put("flag","2");
            //return R.error().message("验证码输入错误，请重新输入");
        }
        resultMap.put("flag","0");
        log.error("----CheckCodeUtil.checkCode-请求参数:"+resultMap);
        System.out.println("resultMap.phone="+resultMap.get("phone"));
        return resultMap;
    }
}
