package org.andresoviedo.gdfao.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
public class IndexController {

    private static Logger logger = Logger.getLogger(IndexController.class.getName());

    @RequestMapping({"","/", "/index"})
    public String index(Authentication authentication) {
        return "index";
    }
}