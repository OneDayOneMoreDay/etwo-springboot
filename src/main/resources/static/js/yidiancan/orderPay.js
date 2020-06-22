$(function() {
    var orderPayURL= http+'/order/pay';
    $('.orderPayButton').click(function() {
        var orderId=$(this).attr("id");
        // 访问后台进行发验证
        $.ajax({
            url : orderPayURL,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "GET",
            dataType : 'json',
            data : {
                orderId:orderId,
            },
            success : function(data) {
                if (data.success) {
                    alert("结账成功！");
                    window.location.reload();
                } else {
                    alert('结账失败！' + data.errMsg);
                }
            }
        });
    });
});