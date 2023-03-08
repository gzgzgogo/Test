package com.itheima.pinda.authority.test;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.base.Captcha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class EasyCaptcha {
    public static void main(String[] args) throws FileNotFoundException {
        //中文验证
        Captcha captcha = new ChineseCaptcha();
        System.out.println(captcha.text());
        captcha.out(new FileOutputStream(new File("d:\\test.png")));

        //算术验证
        Captcha mathCaptcha = new ArithmeticCaptcha();
        System.out.println(mathCaptcha.text());
        mathCaptcha.out(new FileOutputStream(new File("d:\\math.jpg")));
    }
}
