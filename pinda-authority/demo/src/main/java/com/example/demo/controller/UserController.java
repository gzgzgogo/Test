package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    //简单数据类型校验
    @RequestMapping("/delete")
    public String delete(@Validated @NotBlank(message = "id不能为空") String id){
        System.out.println("delete..." + id);
        return "OK";
    }

    //对象属性校验
    @RequestMapping("/save")
    public String save(@Validated User user){
        System.out.println("save..." + user);
        return "OK";
    }
    @GetMapping("/get")
    public String get(String text){
        return "处理之后的文本内容为：" + text;
    }

}
