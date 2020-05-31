package com.jxnu.utils;



import java.util.Random;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/24 19:18
 * 发送邮箱验证码
 */
public class EmailCodeUtil {

    /**
     * 随机获取一个4位数字的邮箱验证码
     * @return
     */
    public static String getCode(){
        String code="";
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            code += random.nextInt(10)+"";
        }
        return code;
    }

    /**
     * 发送邮箱验证码
     *
     * 要使用这个方法需要导入
     * <dependency>
     *     <groupId>org.apache.commons</groupId>
     *     <artifactId>commons-email</artifactId>
     *     <version>1.5</version>
     * </dependency>依赖
     * @param emailAddress 目标邮箱号
     * @param code 验证码
     * @return
     */
    /**public static boolean sendEmail(String emailAddress,String code){
        try {
            HtmlEmail email = new HtmlEmail();
            //需要修改，126邮箱为smtp.126.com,163邮箱为smtp.163.com，QQ为smtp.qq.com
            email.setHostName("smtp.163.com");
            email.setCharset("UTF-8");

            email.addTo(emailAddress);// 收件地址
            //此处填邮箱地址和用户名,用户名可以任意填写
            email.setFrom("dk476879110@163.com", "易点餐官方");
//            email.setFrom("476879110@qq.com");
            //此处填写邮箱地址和客户端授权码
            email.setAuthentication("dk476879110@qq.com", "AXZGRAIJDHPBHFFL");
            email.setSubject("易点餐注册验证码");//此处填写邮件名，邮件名可任意填写
            //此处填写邮件内容
            email.setMsg("【易点餐】易点餐注册验证码：" + code + ",一分钟内有效。若非本人操作，请忽略");
            email.send();

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }**/

}
