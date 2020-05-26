package com.redis.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面跳转
 *
 * @author agstar
 */
@Controller
public class ViewController {

    @RequestMapping(value = {"", "/", "index", "index.html"})
    public String index() {
        return "index";
    }


}
