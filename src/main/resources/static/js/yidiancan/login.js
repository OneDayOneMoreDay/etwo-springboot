//点击图片刷新图片验证码
function getPic(){
    $("#codePic").attr("src",http+"/Kaptcha?code="+Math.random());
};
function Login() {
    //获得输入框的邮箱账号
    var emailElement = document.getElementById("email").value;
    //获得输入框的密码
    var pwdElement = document.getElementById("password").value;
}
$(function() {
    // 登录验证的 url
    var loginUrl = http+'/shop/login';
    $('#button').click(function() {
        // 获取输入的邮箱
        var email= $('#email').val();
        // alert(userName)
        // 获取输入的密码
        var password = $('#password').val();
        // alert(password);
        // 获取图片的验证码
        var code = $('#code').val();
        // 获取选项，记住密码，不记住密码
        var remember=$('#remember').val();
        // alert(national);
        // 访问后台进行登录验证
        $.ajax({
            url : loginUrl,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "post",
            dataType : 'json',
            data : {
                email:email,
                password : password,
                code:code,
                remember:remember,
            },
            success : function(data) {
                if (data.success) {
                    alert("登录成功！");
                    //成功跳转到店铺管理页面
                    window.location.href = 'shop-management.html';
                } else {
                    // alert("登录失败！");
                    alert(data.msg);
                    $('#captcha_img').click();
                }
            }
        });
    });
});