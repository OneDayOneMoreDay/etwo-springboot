$(function() {
    var updateURL= http+'/updateType';
    var typeId;
    $('#dishtype').on('click','.modifyButton',function () {
        // alert(111)
        typeId=$(this).data('id');
        // alert(typeId)
    })
    $('.updateButton').click(function() {


        //获取菜品类信息
        var typeName=$('#typeName1').val();
        // alert(typeName);
        if(!typeName){
            alert('请输入修改后的菜品类名！');
            return;}
        // //获取菜品类id


        // 访问后台进行发验证
        $.ajax({
            url : updateURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "GET",
            dataType : 'json',
            data : {
                typeName:typeName,
                typeId:typeId,
            },
            success : function(data) {
                if (data.success) {
                    alert("修改菜品分类成功！");
                    window.location.reload();
                } else {
                    alert('修改菜品类失败！' + data.msg());
                }
            }
        });
    });
});