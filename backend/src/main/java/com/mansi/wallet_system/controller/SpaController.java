package com.mansi.wallet_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/login",
            "/register",
            "/dashboard",
            "/wallet",
            "/transfer",
            "/deposit",
            "/history",
            "/admin"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}
