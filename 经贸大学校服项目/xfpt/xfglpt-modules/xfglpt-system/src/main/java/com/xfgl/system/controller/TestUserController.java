package com.xfgl.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestUserController
{
    @GetMapping("/user/info")
    public Object info()
    {
        return "{\"username\":\"admin\",\"password\":\"admin123\"}";
    }
}
