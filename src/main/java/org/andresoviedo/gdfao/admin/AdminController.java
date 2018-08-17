package org.andresoviedo.gdfao.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static Logger logger = Logger.getLogger(AdminController.class.getName());

    @RequestMapping({"", "/"})
    public String index(Authentication authentication) {
        return "admin";
    }
}