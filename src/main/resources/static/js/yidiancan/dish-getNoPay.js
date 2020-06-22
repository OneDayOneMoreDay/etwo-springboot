$(document).ready(function() {
    $.ajax({
        url : http+"/order/getNoPay",
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
                $('.getNoPay').html('');//清空信息
                var orderHtml1='';
                var orderHtml2 = '';
                orderList.map(function (item) {
                    orderHtml1="<tr>\n" +
                        "            <td>订单编号</td>\n" +
                        "            <td>下单时间</td>\n" +
                        "            <td>结账时间</td>\n" +
                        "            <td>桌号</td>\n" +
                        "            <td>一共消费（/元）</td>\n" +
                        "            <td>操作</td>\n" +
                        "        </tr>";
                    orderHtml2+="<tr>\n" +
                        "            <td>"+item.orderId+"</td>\n" +
                        "            <td>"+item.orderDateBuy+"</td>\n" +
                        "            <td>"+item.orderDatePay+"</td>\n" +
                        "            <td>"+item.deskId+"</td>\n" +
                        "            <td>"+item.orderTotalPrice+"</td>\n" +
                        "            <td>"+" <button class=\"orderPayButton\"  style='    background-color:  #CCE8EB;\n" +
                        "    border-radius: 5px;\n" +
                        "    -webkit-border-radius: 5px;\n" +
                        "    -moz-border-border-radius: 5px;\n" +
                        "    border: none;\n" +
                        "    padding: 10px 25px 10px 25px;\n" +
                        "     color: #FFF;\n" +
                        "    text-shadow: 2px 2px 2px #949494;' type=\"button\" name=\"\" value=\"结账\" id='"+item.orderId+"'>结账</button>"+"</td>\n" +
                        "</td>\n" +
                        "        </tr>";

                });
                $('.getNoPay').append(orderHtml1);
                $('.getNoPay').append(orderHtml2);

            }


        },
        error : function(data){
            alert(data.message);
        }
    });
});