package com.jxnu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/25 20:24
 */
public class VerifyCodeUtil {
    /**
     * 检查验证码是否和预期相符
     * @param req
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest req, String verifyCode) {
        //从session中获取KaptCha生成的验证码
        String verifyCodeExpected = (String) req.getSession().getAttribute("vrifyCode");
        req.getSession().removeAttribute("vrifyCode");
        Logger logger = LoggerFactory.getLogger(VerifyCodeUtil.class);
        logger.info("正确的图片验证码 = "+verifyCodeExpected);

        if (!verifyCode.equalsIgnoreCase(verifyCodeExpected)) {
            return false;
        }
        return true;
    }
}
