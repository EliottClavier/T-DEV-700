package com.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {

    //@RequestMapping("/")
    /*public String home(){return "Hello World!";}*/

    @RestController
    @RequestMapping("/admin/auth/login")
    public static class IndexController {

        @GetMapping(value = "/")
        public ModelAndView getHome(){
            ModelAndView mv = new ModelAndView();
            mv.setViewName("index");
            return mv;
        }
    }
}