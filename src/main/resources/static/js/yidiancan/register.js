$(function() {
    //邮箱验证码的发送
    var verityCodeURL = http+'/shop/getEmailCode';
    // 注册的controller url
    var regUrl = http+'/shop/reg';
    $('.button').click(function () {
        //获取邮箱地址
        var email = $('#email').val();
        // alert(email);
        if (!email) {
            alert('请输入邮箱！');
            return;
        }
        // 访问后台进行发验证
        $.ajax({
            url: verityCodeURL,
            async: false,
            cache: false,
            type: "get",
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
});