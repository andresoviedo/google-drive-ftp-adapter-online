package org.andresoviedo.gdfao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
public class IndexController {

    private static Logger logger = Logger.getLogger(IndexController.class.getName());

    @RequestMapping({"","/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping({"/help"})
    public String help() {
        return "help";
    }

    @RequestMapping({"/privacy-policy"})
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @RequestMapping({"/terms-and-conditions"})
    public String termsAndConditions() {
        return "terms-and-conditions";
    }
}