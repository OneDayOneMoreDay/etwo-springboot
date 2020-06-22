$(function() {
    //定义全局变量进行与原密码的验证
    var afterPassword;
    //获取管理员信息
    $.ajax({
        url : http+"/shop/getShopInfo",
        xhrFields: { withCredentials: true },
        crossDomain: true,
        async : false,
        cache : false,
        type : "GET",
        dataType : 'json',
        success : function(data) {
            if(data.success){//如果请求成功，返回数据
                var getShopInfo = data.shop;
                afterPassword=getShopInfo.shopPassword;
            }
        }
    });
    var passwordUpdateURL= http+'/shop/updatePassword';
    $('.passwordButton').click(function() {
        //获取输入的密码是否与原密码相同
        var afterPassword1=$('.afterPassword1').val();
        alert(afterPassword1);
        if(afterPassword1!=afterPassword){
            alert("原密码输入不正确！");
            return;
        }

        //获取要修改的密码
        var password=$('#password').val();
        // alert(typeName);
        if(!password){
            alert('请输入修改密码！');
            return;}
        //获取确认要修改的密码
        var confirmPassword=$('#confirmPassword').val();
        if(!confirmPassword) {
            alert('请确认要修改的密码！');
            return;
        }
        if(password!=confirmPassword){
            alert("修改密码不一致，请重新输入！")
        }

        // 访问后台进行发验证
        $.ajax({
            url : passwordUpdateURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "POST",
            dataType : 'json',
            data : {
                password:password,
                confirmPassword:confirmPassword,
            },
            success : function(data) {
                if (data.success) {
                    alert("修改密码成功！");
                    window.location.reload();
                } else {
                    alert('修改密码失败！' + data.msg());
                }
            }
        });
    });
});