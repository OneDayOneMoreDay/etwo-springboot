$(document).ready(function() {
    $.ajax({
        url : http+"/type",
        xhrFields: { withCredentials: true },
        crossDomain: true,
        async : false,
        cache : false,
        type : "GET",
        dataType : 'json',
        success : function(data) {
            if(data.success){//如果请求成功，返回数据
                var typeList = data.typeList;
                // $('#dishtype').html('');
                var typeHtml = '';
                typeList.map(function (item) {
                    typeHtml +="<tr>\n" +
                        "        <td>"+item.typeId+"</td>\n" +
                        // "        <td>"+item.typeShopId+"</td>\n" +
                        "        <td>"+item.typeName+"</td>\n" +
                        "        <td>"+" <button class=\"deleteButton\"  style='    background-color:  #CCE8EB;\n" +
                        "    border-radius: 5px;\n" +
                        "    -webkit-border-radius: 5px;\n" +
                        "    -moz-border-border-radius: 5px;\n" +
                        "    border: none;\n" +
                        "    padding: 10px 25px 10px 25px;\n" +
                        "     color: #FFF;\n" +
                        "    text-shadow: 2px 2px 2px #949494;' type=\"button\" name=\"\" value=\"删除菜品类\" id='"+item.typeId+"'>删除该菜品类</button>"+"</td>\n" +
                        "        <td>"+" <button class='modifyButton' data-id='"+item.typeId+"' style='    background-color: #9DC45F;\n" +
                        "    border-radius: 5px;\n" +
                        "    -webkit-border-radius: 5px;\n" +
                        "    -moz-border-border-radius: 5px;\n" +
                        "    border: none;\n" +
                        "    padding: 10px 25px 10px 25px;\n" +
                        "     color: #FFF;\n" +
                        "    text-shadow: 1px 1px 1px #949494;' data-toggle=\"modal\" data-target=\"#myModal\">\n" +
                        "\t修改菜品类\n" +
                        "</button>"+"</td>\n" +
                        "    </tr>";

                });
                $('#dishtype').append(typeHtml);
            }
        },
    });
});