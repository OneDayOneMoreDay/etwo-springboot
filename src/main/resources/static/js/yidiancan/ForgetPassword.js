$(function() {
    //邮箱验证码的发送
    var ForgetPasswordCodeURL = http+'/shop/getForgetPasswordEmailCode';
    var ForgetUrl=http+'/shop/resetPassword';
    $('.ForgetPasswordCodeButton').click(function () {
        //获取邮箱地址
        var email = $('#email').val();
        // alert(email);
        if (!email) {
            alert('请输入邮箱！');
            return;
        }
        // 访问后台进行发验证
        $.ajax({
            url: ForgetPasswordCodeURL,
            async: false,
            cache: false,
            type: "GET",
            dataType: 'json',
            xhrFields: { withCredentials: true },
            crossDomain: true,
            data: {
                email:email,
            },
            success: function (data) {
                if (data.success) {
                    alert("发送成功，请注意查收！");
                } else {
                    alert('发送失败！' + data.msg());
                    // $('#captcha_img').click();
                }
            }
        });
    });
    $('.Forget').click(function () {
        var email = $('#email').val();
        // alert(email);
        if (!email) {
            alert('请输入邮箱！');
            return;
        }
        var  newPassword=$('#newPassword').val();
        var  newPassword2=$('#newPassword2').val();
        if(newPassword!=newPassword2){
            alert("两次密码输入不正确，请重新输入！");
            return;
        }
        var forgetPasswordEmailCode=$('#forgetPasswordEmailCode').val();
        if(!forgetPasswordEmailCode){
            alert("请输入邮箱验证码！");
            return;
        }
        // 访问后台进行发验证
        $.ajax({
            url: ForgetUrl,
            async: false,
            cache: false,
            type: "POST",
            dataType: 'json',
            xhrFields: { withCredentials: true },
            crossDomain: true,
            data: {
                email:email,
                newPassword:newPassword,
                forgetPasswordEmailCode:forgetPasswordEmailCode,

            },
            success: function (data) {
                if (data.success) {
                    alert("重置密码成功！");
                    window.location.reload();
                } else {
                    alert(data.msg());
                    // $('#captcha_img').click();
                }
            }
        });
    })
});