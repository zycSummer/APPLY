var userName = sessionStorage.getItem('userName');
$("#userName").text(userName);
layui.use(['form','element','layer','jquery'],function(){
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        element = layui.element;
    $ = layui.jquery;
    // var ctx = $("meta[name='ctx']").attr("content");
    //上次登录时间【此处应该从接口获取，实际使用中请自行更换】
    // $.getJSON(ctx + "admin/user/lastLoginTime", function (data) {
    //     var lastTime = data["lastLoginTime"].split("日");
    //     $(".loginTime").html(lastTime[0]+"日</br>"+lastTime[1]);
    // });
    //icon动画
    // $(".panel a").hover(function(){
    //     $(this).find(".layui-anim").addClass("layui-anim-scaleSpring");
    // },function(){
    //     $(this).find(".layui-anim").removeClass("layui-anim-scaleSpring");
    // })

    //用户数量
    // $.get(ctx + "admin/user/users/count",function(data){
    //     $(".userAll span").text(data.count);
    // })

    // 修改密码
    $('#userName').click(function(){
        layer.open({
            type: 1,
            title: '修改密码',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '300px'],
            content: $('#changePwd'),
            btn: ['确认','取消'],
            success: function(){
                $('#changePwd').removeClass('layui-hide');
            },
            yes: function(index,layero){
                var oldPwd = $("#oldPwd").val();
                var newPwd = $("#newPwd").val();
                var confirmPwd = $("#confirmPwd").val();

                if(!oldPwd) return layer.msg('请输入当前密码');
                if(!newPwd) return layer.msg('请输入新密码');

                if(newPwd == confirmPwd){
                    $.ajax({
                        url: "/user/updateUserByPwd",
                        type: "get",
                        data: {"oldPwd":oldPwd,"newPwd":newPwd},
                        success: function (res) {
                            if(res.success == false){
                                layer.msg(res.msg);
                            }
                            if(res.success == true){
                                layer.confirm('修改成功！是否要重新登录？', {
                                    icon: 3,
                                    title: ''
                                }, function (index) {
                                    layer.close(index);
                                    window.location.href = '/admin/login';
                                });
                            }
                        },
                        error : function(request) {
                            layer.msg(request);
                        }
                    });
                }else{
                    layer.msg('确认新密码和新密码不一致，请重新输入。');
                }
            },
            end: function (index) {
                $("#changePwd").addClass('layui-hide');
                $("#changePwd .layui-input").val('');
            }
        });
    });

    //退出
    $('#signOut').click(function(){
        layer.confirm('确定退出吗？', {
            title:'提示',
            btn: ['确定','取消']
        }, function(){
            window.location.href='/admin/login';
        });
        return false;
    });

});
