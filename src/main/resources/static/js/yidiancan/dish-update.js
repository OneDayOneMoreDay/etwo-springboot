$(function() {
    var dishupdateURL= http+'/dish/update';
    var dishId;
    $('#dishAll').on('click','.modifyButton2',function () {
        dishId=$(this).data('id');
        alert(dishId);
    })
    $('.dishUpdateButton').click(function() {
        // 获取页面已有的一个form表单
        var form = document.getElementById("myForm");
        // 表单初始化
        var formData = new FormData(form);
        var dishName=formData.get("dishName");
        var dishTypeId=formData.get("dishTypeId");
        var dishPrice=formData.get("dishPrice");
        var dishNumber=formData.get("dishNumber");
        var dishImg = $('#dish_Img')[0].files[0];
        // var dishImg=formData.get("dishImg");
        // formData.append("typeName",typeName1)
        // formData.append("file", dishImg);
        // formData.append("dishName",1);
        formData.append("dishId",dishId);
        formData.append('dishImg', dishImg);
        // formData.append("dishImg", file);
        // formData.append("dishTypeId",2);
        // formData.append("dishPrice",3);
        // formData.append("dishNumber",4);


        // 访问后台进行发验证
        $.ajax({
            url : dishupdateURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            contentType : false,
            processData : false,
            async : false,
            type : "POST",
            data : formData,
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