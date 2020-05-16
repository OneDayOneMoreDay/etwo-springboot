package com.jxnu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @date 2020/4/17 14:38
 *
 * 该controller是用于转发直接访问/WEB-INF/html下的html页面的
 */
@Controller
public class HtmlController {

    @RequestMapping(path = "/html",params = "action")
    public String html(String action){
        Logger logger = LoggerFactory.getLogger(HtmlController.class);
        logger.info("action = " + action);

        switch (action){
            case "login":
               return "login";
        }
        return "login";
    }
}
