$(function() {
    var addtypeNameURL= http+'/addType';
    $('.addButton').click(function() {
        //获取菜品类名信息
        var typeName=$('#typeName').val();
        // alert(typeName);
        // if(!typeName){
        //     alert('请输入菜品类！');
        //     return;
        // }
        // 访问后台进行发验证
        $.ajax({
            url : addtypeNameURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "GET",
            dataType : 'json',
            data : {
                typeName:typeName,
            },
            success : function(data) {
                if (data.success) {
                    alert("添加菜品分类成功！");
                    window.location.reload();
                } else {
                    alert('添加菜品类失败！' + data.errMsg);
                }
            }
        });
    });
});