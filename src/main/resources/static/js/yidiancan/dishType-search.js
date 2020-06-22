$(function() {
    var dishUrl=http+'/dish/getDishByDishTypeId';
    $('.submit').click(function () {
        var  dishTypeId= $('#dishTypeId').val();
        if (!dishTypeId) {
            alert('请输入要搜索的菜品ID！例如：1000、1001、1002等');
            return;
        }
        // 访问后台进行发验证
        $.ajax({
            url: dishUrl,
            async: false,
            cache: false,
            type: "GET",
            dataType: 'json',
            xhrFields: { withCredentials: true },
            crossDomain: true,
            data: {
                dishTypeId:dishTypeId,
            },
            success: function (data) {
                if (data.success) {
                    // alert(1111);
                    var dishList = data.dishList;
                    // alert(data.dishList);
                    $('#dishtype').html('');//清空信息
                    var dishHtml1='';
                    var dishHtml = '';
                    dishList.map(function (item) {
                        dishHtml1="    <tr>\n" +
                            // "        <td>菜品ID</td>\n" +
                            "        <td>菜品分类ID</td>\n" +
                            "        <td>菜品类名字</td>\n" +
                            "        <td>菜品价格</td>\n" +
                            "        <td>菜品数量</td>\n" +
                            "    </tr>";
                        dishHtml +="<tr>\n" +
                            // "        <td>"+item.dishId+"</td>\n" +
                            "        <td>"+item.dishTypeId+"</td>\n" +
                            "        <td>"+item.dishName+"</td>\n" +
                            "        <td>"+item.dishPrice+"</td>\n" +
                            "        <td>"+item.dishNumber+"</td>\n" +
                            "    </tr>";

                    });
                    $('#dishtype').append(dishHtml1);
                    $('#dishtype').append(dishHtml);
                } else {
                    alert(data.msg());
                }
            }
        });
    });
});