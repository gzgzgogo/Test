package com.itheima.pinda.authority.biz.service.auth.impl;

import com.itheima.pinda.authority.biz.service.auth.ValidateCodeService;
import com.itheima.pinda.common.constant.CacheKey;
import com.itheima.pinda.exception.BizException;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class ValidateCodeServiceImpl implements ValidateCodeService{
    @Autowired
    private CacheChannel channel;
    @Override
    public void create(String key, HttpServletResponse response) throws IOException {
        Captcha captcha = new ArithmeticCaptcha(115, 42);
        if(StringUtils.isBlank(key)){
            throw BizException.validFail("验证码key不能为空!");
        }
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
        captcha.setCharType(2);
        channel.set(CacheKey.CAPTCHA,key,StringUtils.lowerCase(captcha.text()));
        captcha.out(response.getOutputStream());
    }

    @Override
    public boolean check(String key, String value) {
        if(StringUtils.isBlank(key)){
            throw BizException.validFail("验证码key不能为空!");
        }
        CacheObject cacheObject = channel.get(CacheKey.CAPTCHA,key);
        if(cacheObject.getValue() == null){
            throw BizException.validFail("验证码已过期!");
        }
        if(!StringUtils.equals(value,String.valueOf(cacheObject.getValue()))){
            throw BizException.validFail("验证码错误!");
        }
        channel.evict(CacheKey.CAPTCHA,key);
        return true;
    }

}
