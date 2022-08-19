package com.ecc.aipao98.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sunyc
 * @create 2022-08-19 13:42
 */
@RestController
@Slf4j
public class indexController {

    @GetMapping("/index/{provinceId}")
    public ModelAndView index(@PathVariable String provinceId,
                              ModelAndView modelAndView,
                              HttpServletRequest request){
        log.error("---------------------------------------------------------");
        log.error("----------------------首页-----------------------------------");

        log.error("indexController.index--来源省份id="+provinceId);
      //将来源的provinceId 保存在session域中
        request.getSession().setAttribute("provinceId",provinceId);

        modelAndView.setViewName("index");
        return modelAndView;




    }
}
