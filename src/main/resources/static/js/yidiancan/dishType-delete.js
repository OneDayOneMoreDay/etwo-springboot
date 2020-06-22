$(function() {
    var deleteURL= http+'/deleteType';
    $('.deleteButton').click(function() {
        var typeId=$(this).attr("id");
        // var typeId=$('#typeId').val();
        // // alert(typeName);
        // if(!typeId) {
        //     alert('请输入菜品类id！');
        //     return;
        // }
        // 访问后台进行发验证
        $.ajax({
            url : deleteURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "GET",
            dataType : 'json',
            data : {
                typeId:typeId,
            },
            success : function(data) {
                if (data.success) {
                    alert("删除菜品分类成功！");
                    window.location.reload();
                    // 自动链接到前端展示系统首页
                    //  window.location.href = '/login.html';
                } else {
                    alert('删除菜品类失败！' + data.errMsg);
                    // $('#captcha_img').click();
                }
            }
        });
    });
});