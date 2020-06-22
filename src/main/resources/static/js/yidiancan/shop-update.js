$(function() {
    var shopupdateURL= http+'/shop/update';
    var shopId;
    $('.box1').on('click','.modifyButton3',function () {
        shopId=$(this).data('id');
        // alert(shopId);
    })
    $('.shopUpdateButton').click(function() {
        // 获取页面已有的一个form表单
        var form = document.getElementById("myForm2");
        // 用表单来初始化
        var formData = new FormData(form);
        //获取表格信息并传值
        var dishName=formData.get("shopName");
        var dishTypeId=formData.get("shopAddress");
        var dishPrice=formData.get("shopNotice");
        var dishImg = $('#shop_Img')[0].files[0];
        formData.append("shopId",shopId);
        formData.append('shopImg', dishImg);



        // 访问后台进行发验证
        $.ajax({
            url : shopupdateURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            contentType : false,
            processData : false,
            async : false,
            type : "POST",
            data : formData,
            success : function(data) {
                if (data.success) {
                    alert("修改店铺信息成功！");
                    alert(data.msg);
                    window.location.reload();
                } else {
                    alert('修改店铺信息失败！');
                }
            }
        });
    });
});