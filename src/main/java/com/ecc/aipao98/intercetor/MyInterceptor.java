package com.ecc.aipao98.intercetor;

import com.ecc.aipao98.until.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class MyInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if (handler instanceof HandlerMethod) {//判断是否为controller的请求（可过滤静态连接）
            response.setHeader("Cache-Control", "no-store");//禁止缓存

            String path = request.getServletPath();
            if(StringUtils.contains(path, "/index2") || StringUtils.contains(path, "/index2") || StringUtils.contains(path, "/prizeError")) {
                return true;
            }

            //只可以微信访问
           String source = NetworkUtil.getSource(request);
            if(StringUtils.isBlank(source) || !StringUtils.equals(source, "wechat")) {//只有微信、app可以参与
                response.sendRedirect(request.getContextPath() + "/index");// 返回错误页面
               return false;
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub
    }
}
