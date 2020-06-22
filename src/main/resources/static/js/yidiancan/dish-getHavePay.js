$(document).ready(function() {
    $.ajax({
        url : http+"/order/getHavePay",
        xhrFields: { withCredentials: true },
        crossDomain: true,
        traditional: true,
        async : false,
        cache : false,
        type : "GET",
        dataType : 'json',
        success : function(data) {
            if(data.success){//如果请求成功，返回数据
                var orderList = data.orderList;
                $('.getHavePay').html('');//清空信息
                var orderHtml1='';
                var orderHtml2 = '';
                orderList.map(function (item) {
                    orderHtml1="<tr>\n" +
                        "            <td>订单编号</td>\n" +
                        "            <td>下单时间</td>\n" +
                        "            <td>结账时间</td>\n" +
                        "            <td>桌号</td>\n" +
                        "            <td>一共消费（/元）</td>\n" +
                        "        </tr>";
                    orderHtml2+="<tr>\n" +
                        "            <td>"+item.orderId+"</td>\n" +
                        "            <td>"+item.orderDateBuy+"</td>\n" +
                        "            <td>"+item.orderDatePay+"</td>\n" +
                        "            <td>"+item.deskId+"</td>\n" +
                        "            <td>"+item.orderTotalPrice+"</td>\n" +
                        "        </tr>";

                });
                $('.getHavePay').append(orderHtml1);
                $('.getHavePay').append(orderHtml2);

            }


        },
        error : function(data){
            alert(data.message);
        }
    });
});