$(document).ready(function(){
    var loginOutUrl = http+'/shop/loginOut';
    var afterPassword;
    //获取管理员信息
    $.ajax({
        url : http+"/shop/getShopInfo",
        xhrFields: { withCredentials: true },
        crossDomain: true,
        async : false,
        cache : false,
        type : "GET",
        dataType : 'json',
        success : function(data) {
            if(data.success){//如果请求成功，返回数据
                var getShopInfo = data.shop;
                afterPassword=getShopInfo.shopPassword;
                // alert(afterPassword);
                // $('#shopName').html('');
                var shopNameHtml = '';
                shopNameHtml ="<img src='"+http+""+getShopInfo.shopImgPath+"' class='layui-nav-img' id='shopImgPath'/>"+"<span id=\"shopName\" style=\"color: #0c0c0c;\">"+getShopInfo.shopName+"</span>";
                // $('#shopName').append(shopNameHtml);
                $('#shopName').html(shopNameHtml);
                var touxiangHtml='';
                touxiangHtml="<img id=\"img1\" src='"+http+""+getShopInfo.shopImgPath+"' title=\"\" alt=\"\" style='width: 150px;height: 150px'/>\n" +
                    "                <p>你好！欢迎"+getShopInfo.shopName+"店主您!</p>";
                $('.side-touxiang').html(touxiangHtml);


            }
        }
        // error:function () {
        //     alert(2222)
        // }
    });
    $('#loginOut').click(function () {
        // 访问后台进行退出登录
        $.ajax({
            url : loginOutUrl,
            xhrFields: { withCredentials: true },
            crossDomain: true,
            async : false,
            cache : false,
            type : "GET",
            dataType : 'json',
            data : {
            },
            success : function(data) {
                if (data.success) {
                    alert("退出成功！");
                    //成功跳转到店铺管理页面
                    window.location.href = 'login.html';
                } else {
                    alert(data.msg);
                }
            }
        });
    });
    // 刷新iframe
    $("#refresh").click(function(){
        var iconRefresh= $("div.layui-show iframe");
        iconRefresh.attr('src', iconRefresh.attr('src'));
    })
    // 全屏
    $("#full").click(function () {
        //进入全屏
        if (document.webkitFullscreenElement) {
            document.webkitCancelFullScreen();
            $("#full").children("i").removeClass("icon-suoxiao1");
            $("#full").children("i").addClass("icon-quanping");
        }else{//退出全屏
            document.documentElement.webkitRequestFullScreen();
            $("#full").children("i").removeClass("icon-quanping");
            $("#full").children("i").addClass("icon-suoxiao1");
        }
    });
    //退出登录
    $("#exit").on("click",function(){
        sessionStorage.clear()   //清除所有session值
        window.location.reload()
    });

});
//引用layui框架的js
layui.use(['element', 'layer'], function(){
    var element = layui.element;
    // 监听导航菜单的点击

    element.on('nav(side-menu)', function (elem) {
        // layer.msg(elem.text());
        // console.log(elem.text());
        // str.replace(/^\s*|\s*$/g, "");去除字符串两端的空格
        // console.log(elem);
        var navItemText = elem.text().replace(/^\s*|\s*$/g, "");
        var tabTitleHTML = elem.html()+'<i class="layui-icon layui-icon-close"></i>';
        var layId = elem.parent().attr("lay-id");
        var tabBodyHtml = '<div class="layui-tab-item layui-show"><iframe src="'+layId+'.html" frameborder="0" scrolling="yes" width="100%" height="100%" ></iframe></div >';
        if(typeof (layId)!="undefined" && $("#tabs li[lay-id="+navItemText+"]").length>0){
            // 右边主体切换到lay-id=navItemText的tab选项卡
            element.tabChange('tab', elem.text().replace(/^\s*|\s*$/g, "")); //切换到 lay-id="yyy" 的这一项
        }else if(typeof (layId) != "undefined"){
            // 右边主体添加一个tab选项卡
            element.tabAdd('tab', {
                title: tabTitleHTML
                , content: tabBodyHtml
                , id: navItemText //'选项卡标题的lay-id属性值'
            });
            // 右边主体切换到刚刚增加tab的选项卡
            element.tabChange('tab', elem.text().replace(/^\s*|\s*$/g, "")); //切换到 lay-id="yyy" 的这一项
            // 为右边主体tab选项卡标题的那个×绑定删除tab选项卡事件
            $(".layui-icon-close").click(function () {
                var layId = $(this).parent().attr("lay-id");
                element.tabDelete('tab', layId);
            })
        }
    });

});