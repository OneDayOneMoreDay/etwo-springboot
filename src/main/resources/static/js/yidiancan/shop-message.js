$(document).ready(function() {
    //获取管理员信息
    $.ajax({
        url: http + "/shop/getShopInfo",
        xhrFields: {withCredentials: true},
        crossDomain: true,
        async: false,
        cache: false,
        type: "GET",
        dataType: 'json',
        success: function (data) {
            if (data.success) {//如果请求成功，返回数据
                $('.box1').html('');
                // alert(111)
                var getShopInfo2 = data.shop;
                var shopHtml='';
                shopHtml="<div class= \"box1\">\n" +
                    "    <div class=\"bg1\">\n" +
                    "        <img src='"+http+""+getShopInfo2.shopImgPath+"' style=\"margin-left:150px;width: 200px;height: 200px;\">" +
                    "        <div class=\"img2\"></div>\n" +
                    "        <div class=\"bg2\">\n" +
                    "            <div class=\"img3\"></div>\n" +
                    "            <p class=\"wenzi\" >店铺ID：<span id=\"shopId\">"+getShopInfo2.shopId+"</span></p>\n" +
                    "        </div>\n" +
                    "        <div class=\"bg3\">\n" +
                    "            <div class=\"img3\"></div>\n" +
                    "            <p class=\"wenzi\">店铺名字：<span id=\"shopName\">"+getShopInfo2.shopName+"</span></p>\n" +
                    "        </div>\n" +
                    "        <div class=\"bg3\">\n" +
                    "            <div class=\"img3\"></div>\n" +
                    "            <p class=\"wenzi\">店铺地址：<span id=\"shopAddress\">"+getShopInfo2.shopAddress+"</span></p>\n" +
                    "        </div>\n" +
                    "        <div class=\"bg3\">\n" +
                    "            <div class=\"img3\"></div>\n" +
                    "            <p class=\"wenzi\">店铺邮箱：<span id=\"shopEmail\">"+getShopInfo2.shopEmail+"</span></p>\n" +
                    "        </div>\n" +
                    "        <div class=\"bg4\">\n" +
                    "            <div class=\"img3\"></div>\n" +
                    "            <p class=\"wenzi\">店铺公告：<span id=\"shopNotice\">"+getShopInfo2.shopNotice+"</span></p>\n" +
                    "        </div>\n" +
                    "        <div class=\"bg5\">\n" +
                    "            <div class=\"img3\"></div>\n" +
                    "            <p class=\"wenzi\">"+" <button class='modifyButton3' data-id='"+getShopInfo2.shopId+"' style=' background-color: #C7C7C7;\n" +
                    "    border-radius: 5px;\n" +
                    "    -webkit-border-radius: 5px;\n" +
                    "    -moz-border-border-radius: 5px;\n" +
                    "    border: none;\n" +
                    "    padding: 10px 25px 10px 25px;\n" +
                    "     color: black;\n" +
                    "    text-shadow: 1px 1px 1px black;' data-toggle=\"modal\" data-target=\"#myModal3\">\n" +
                    "\t修改信息\n" +
                    "</button>"+"</span></p>\n" +
                    "        </div>"
                    "    </div>\n" +
                    "</div>";
                $('.box1').html(shopHtml);
            }
        },
    });
});