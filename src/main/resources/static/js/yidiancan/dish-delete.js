$(function() {
    var dishdeleteURL= http+'/dish/delete';
    $('.dishButton').click(function() {
        var dishId=$(this).attr("id");
        // var dishId=$('#dishId').val();
        // if(!dishId) {
        //     alert('请输入菜品id！');
        //     return;
        // }
        // 访问后台进行发验证
        $.ajax({
            url : dishdeleteURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "GET",
            dataType : 'json',
            data : {
                dishId:dishId,
            },
            success : function(data) {
                if (data.success) {
                    alert("删除菜品成功！");
                    window.location.reload();
                } else {
                    alert('删除菜品失败！' + data.errMsg);
                }
            }
        });
    });
});