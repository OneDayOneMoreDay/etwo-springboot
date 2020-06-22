$(document).ready(function() {
    $.ajax({
        url : http+"/typeAndDish",
        xhrFields: { withCredentials: true },
        crossDomain: true,
        traditional: true,
        async : false,
        cache : false,
        type : "GET",
        dataType : 'json',
        success : function(data) {
            if(data.success){//如果请求成功，返回数据
                var typeList = data.typeList;
                for (let i in typeList) {
                    let outer="    <div>\n" +
                        "        <table >\n" +
                        "            <tr style='background-color: #CCE8EB;'>\n" +
                        "                <td>菜品类ID</td>\n" +
                        // "                <td>菜品分类ID</td>\n" +
                        "                <td>菜品类名字</td>\n" +
                        "            </tr>\n" +
                        "        </table>\n" +
                        "    </div>"+"    <div>\n" +
                        "        <table>\n" +
                        "            <tr>\n" +
                        "                <td>"+typeList[i].typeId+"</td>\n" +
                        // "                <td>"+typeList[i].typeShopId+"</td>\n" +
                        "                <td>"+typeList[i].typeName+"</td>\n" +
                        "            </tr>\n" +
                        "        </table>\n" +
                        "    </div>";
                    // let outer ="<div>"+"  <tr>\n" +
                    //     "        <td>"+typeList[i].typeId+"</td>\n" +
                    //     "        <td>"+typeList[i].typeShopId+"</td>\n" +
                    //     "        <td>"+typeList[i].typeName+"</td>\n" +
                    //     "    </tr>"+"</div>";
                    $("#dishAll").append(outer);
                    let code = typeList[i].dishes;
                    console.log(code.length);
                    for(let j = 0;j<code.length;j++){
                        let core="    <div>\n" +
                            "        <table>\n" +
                            "            <tr style='background-color: #F5FAFA ;'>\n" +
                            "                <td>菜品ID</td>\n" +
                            // "                <td>菜品分类ID</td>\n" +
                            "                <td>菜品类名字</td>\n" +
                            "                <td>菜品价格(/元)</td>\n" +
                            "                <td>菜品数量</td>\n" +
                            "                <td>删除操作</td>\n" +
                            "                <td>修改操作</td>\n" +
                            "            </tr>\n" +
                            "        </table>\n" +
                            "    </div>"+"    <div>\n" +
                            "        <table>\n" +
                            "            <tr>\n" +
                            "        <td>"+code[j].dishId+"</td>\n" +
                            // "        <td>"+code[j].dishTypeId+"</td>\n" +
                            "        <td>"+code[j].dishName+"</td>\n" +
                            "        <td>"+code[j].dishPrice+"</td>\n" +
                            "        <td>"+code[j].dishNumber+"</td>\n" +
                            "        <td>"+" <button class=\"dishButton\"  style='    background-color:  #CCE8EB;\n" +
                            "    border-radius: 5px;\n" +
                            "    -webkit-border-radius: 5px;\n" +
                            "    -moz-border-border-radius: 5px;\n" +
                            "    border: none;\n" +
                            "    padding: 10px 25px 10px 25px;\n" +
                            "     color: #FFF;\n" +
                            "    text-shadow: 2px 2px 2px #949494;' type=\"button\" name=\"\" value=\"删除菜品\" id='"+code[j].dishId+"'>删除该菜品</button>"+"</td>\n" +
                            "        <td>"+" <button class='modifyButton2' data-id='"+code[j].dishId+"' style=' background-color: #9DC45F;\n" +
                            "    border-radius: 5px;\n" +
                            "    -webkit-border-radius: 5px;\n" +
                            "    -moz-border-border-radius: 5px;\n" +
                            "    border: none;\n" +
                            "    padding: 10px 25px 10px 25px;\n" +
                            "     color: #FFF;\n" +
                            "    text-shadow: 1px 1px 1px #949494;' data-toggle=\"modal\" data-target=\"#myModal2\">\n" +
                            "\t修改菜品\n" +
                            "</button>"+"</td>\n" +
                            "            </tr>\n" +
                            "        </table>\n" +
                            "    </div>";
                        // let core = '<div>'++'</div>'
                        // let core = "<tr>\n" +
                        //     "        <td>"+code[j].dishId+"</td>\n" +
                        //     "        <td>"+code[j].dishTypeId+"</td>\n" +
                        //     "        <td>"+code[j].dishName+"</td>\n" +
                        //     "        <td>"+code[j].dishPrice+"</td>\n" +
                        //     "        <td>"+code[j].dishNumber+"</td>\n" +
                        //     "    </tr>";
                        $("#dishAll").append(core);
                    }
                }
            }
        },
        error : function(data){
            alert(data.message);
        }
    });
});