//获取url？号后面的参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
var page = getQueryString('page');
$("#loginMain,#register,#forgetPwd").addClass('layui-hide');
if(!page){
    $("#loginMain").removeClass('layui-hide');
}
if(page == 'regin'){
    $("#register").removeClass('layui-hide');
}
if(page == 'forgetPwdBtn'){
    $("#forgetPwd").removeClass('layui-hide');
}

layui.use(['form', 'layer', 'jquery','upload'], function () {
    var form = layui.form,
        upload  = layui.upload ,
        layer = parent.layer === undefined ? layui.layer : top.layer;
    $ = layui.jquery;

    //判断是否在ifarme
    function judgeInIframe() {
        if (window.frames.length != parent.frames.length) {
            parent.window.location.replace(window.location.href)
        }
    }
    judgeInIframe();

    var height = $(window).height()-$(".login_face").height()-40;
    $("#register .reginForm").height(height);

    // 系统提示 获取
    $.get('/districtCommittee/getSysPoints',function(res){
        if (res.success == false) {
            return layer.msg(res.msg);
        }
        $("#systemPrompt").html(res.data['points']);
    });

    // 用户类型 获取
    $.get('/user/getUserRoles', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        var str = '';
        if(res.data.length>0){
            $.each(res.data,function(i,v){
                str += '<option value="'+v.roleId+'">'+v.roleName+'</option>';
            });
        }
        $("#userType").html(str);
        form.render('select','login');
    });

    // 企业类型 获取
    $.get('/user/getSysSiteType', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        var str = '';
        if(res.data.length>0){
            $.each(res.data,function(i,v){
                str += '<option value="'+v.typeId+'">'+v.typeName+'</option>';
            });
        }
        $("#businessType").html(str);
        form.render('select','regin');
    });

    // 所属行业 获取
    $.get('/user/getSysIndustyType', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        var str = '';
        if(res.data.length>0){
            $.each(res.data,function(i,v){
                str += '<option value="'+v.typeId+'">'+v.typeName+'</option>';
            });
        }
        $("#industryInvolved").html(str);
        form.render('select','regin');
    });

    // 所属镇（街道） 获取
    $.get('/user/getSysStreet', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        var str = '';
        if(res.data.length>0){
            $.each(res.data,function(i,v){
                str += '<option value="'+v.streetId+'">'+v.streetName+'</option>';
            });
        }
        $("#townStreet").html(str);
        form.render('select','regin');

        form.on('select(townStreet)', function(data){
            getPark($("#townStreet").val());
        });

        getPark($("#townStreet").val());

    });

    //所属镇工业园  获取
    function getPark(val){
        $.get('/user/getSysIndustrialPark', function (res) {
            if (!res.success) {
                return layer.msg(res.msg);
            }
            var str = '<option value=""></option>';
            if(res.data.length>0){
                $.each(res.data,function(i,v){
                    if(val == v.streetId){
                        str += '<option value="'+v.parkId+'">'+v.parkName+'</option>';
                    }
                });
            }
            $("#industrialPark").html(str);
            form.render('select','regin');
        });
    }

    // 验证
    form.verify({
        username: function(value, item){ //value：表单的值、item：表单的DOM对象
            if(!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)){
                return '用户名不能有特殊字符';
            }
            if(/(^\_)|(\__)|(\_+$)/.test(value)){
                return '用户名首尾不能出现下划线\'_\'';
            }
            if(/^\d+\d+\d$/.test(value)){
                return '用户名不能全为数字';
            }
        }
        //我们既支持上述函数式的方式，也支持下述数组的形式
        //数组的两个值分别代表：[正则匹配、匹配不符时的提示文字]
        ,pass: [
            /^[\S]{6,12}$/
            ,'密码必须6到12位，且不能出现空格'
        ]
    });

    //登录按钮点击事件
    form.on("submit(login)", function (data) {
        var formData = data.field;
        $.ajax({
            type: 'POST',
            url: '/user/login',
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(formData),
            success: function (res) {console.log(res);
                if (res.success != true) {
                    return layer.msg(res.msg);
                }
                sessionStorage.setItem('userId',data.field.userId);
                sessionStorage.setItem('roleId',data.field.roleId);
                sessionStorage.setItem('userName',res.data.userName);
                window.location.href = res.data.url;
            },
            error: function (res) {
                layui.layer.msg(res.msg);
            }
        });

        return false;
    });

    // 登录页面 用户注册
    $("#reginBtn").on('click',function(){
        $("#register").removeClass('layui-hide');
        $("#loginMain,#forgetPwd").addClass('layui-hide');
        window.location.href = window.location.pathname+'?page=regin';
    });

    // 登录页面 忘记密码
    $("#forgetPwdBtn").on('click',function(){
        $("#forgetPwd").removeClass('layui-hide');
        $("#loginMain,#register").addClass('layui-hide');
        window.location.href = window.location.pathname+'?page=forgetPwdBtn';
    });

    //用户注册页面 上传
    var file = [];
    upload.render({
        elem: '.upload'
        ,url: '/file/moreFileUpload'
        ,accept: 'images' //允许上传的文件类型
        ,acceptMime: 'image/*'//只显示图片文件
        ,size: 1024*5
        ,exts:'jpg|png|gif|bmp|jpeg'
        // ,auto:false//需要设置 bindAction 参数来指向一个其它按钮提交上传
        // ,bindAction:'.reSubmit'//指向一个按钮触发上传
        ,field:'file'//设定文件域的字段名
        ,multiple:true
        ,choose:function(obj){
            //将每次选择的文件追加到文件队列
            var files = obj.pushFile();
            //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
            obj.preview(function(index, file, result){
                console.log(index); //得到文件索引
                console.log(file); //得到文件对象
                console.log(result); //得到文件base64编码，比如图片
                $('#demo1').append('<img src="'+ result +'" alt="'+ file.name +'" class="layui-upload-img">');
                $('#demo2').append('<img src="'+ result +'" alt="'+ file.name +'" class="hide" style="display:none">');
                //这里还可以做一些 append 文件列表 DOM 的操作

                //删除
                // str.find('.demo-delete').on('click', function(){
                //     delete files[index]; //删除对应的文件
                //     str.remove();
                //     uploadInst.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                // });
            });
        }
        ,done: function(res, index, upload){
            if(res.success == true){ //上传成功
                $("#businessLicense").val(res.data.path);
                file.push(index);
            }else{
                return layer.msg('上传失败');
            }
        }
        ,error: function(index, upload){
            //请求异常回调
        }
    });

    //点击 缩略图放大
    $(document).on("click", "#demo1 .layui-upload-img", function () {
        var idx = $(this).index()+1;
        var obj = $('#demo2 img:nth-child('+idx+')');
        layer.open({
            type: 1,
            title: false,
            closeBtn: 0,
            area: '700px',
            skin: 'layui-layer-nobg', //没有背景色
            shadeClose: true,
            content: obj
        });
        return false;
    });

    //用户注册页面 返回
    $(".reBack").on('click',function(){
        $("#loginMain").removeClass('layui-hide');
        $("#forgetPwd,#register").addClass('layui-hide');
        window.location.href = window.location.pathname;
    });

    //用户注册页面 提交
    form.on('submit(reSubmit)', function(data){
        var pwd = $("#rePassword").val();
        var confirmPwd = $("#reConfirmPwd").val();
        if(pwd != confirmPwd){
            layer.msg('确认新密码和新密码不一致，请重新输入。');
            return false;
        }
        if(file.length == 0){
            layer.msg('请上传营业执照');
            return false;
        }

        $.ajax({
            url: "/user/insertSite",
            type: "POST",
            data: JSON.stringify(data.field),
            contentType:"application/json",
            dataType: "json",
            error : function(request) {
                parent.layer.alert("网络超时");
            },
            success: function (res) {
                if(res.msg == 'success'){
                    layer.confirm('提交成功！是否要重新登录？', {
                        icon: 3,
                        title: ''
                    }, function (index) {
                        window.location.href = window.location.pathname;
                    });
                }
            }
        });
        return false; //阻止表单跳转
    });

    // 忘记密码页面 下一步
    $(".nextStep").on('click',function(){
        if(!$("#foUserID").val()) return layer.msg('用户ID不能为空!');

        $.post('/user/getMailByUserId', {'userId':$("#foUserID").val()}, function (res) {
            if(res.msg == 'success'){
                $(".process li").removeClass('layui-this');
                $(".process li:nth-child(2)").addClass('layui-this');

                $(".nextStep,.foUserID").addClass('layui-hide');
                $(".foEmail,.secondStep").removeClass('layui-hide');

                $("#foEmail").val(res.data.mail);
            }else{
                $("#foEmail").val('');
                return layer.msg('此用户ID不存在!');
            }
        });
        return false;
    });

    // 忘记密码页面 下下一步
    $(".secondStep").on('click',function(){
        clearInterval(timer);
        if(!$("#foVerify").val()) return layer.msg('验证码不能为空!');

        $.ajax({
            url: "/mail/checkVerify",
            type: "POST",
            data: JSON.stringify({'inputString':$("#foVerify").val()}),
            contentType: "application/json",
            dataType: "json",
            success: function (res) {
                if(res.msg == true){
                    $(".process li").removeClass('layui-this');
                    $(".process li:nth-child(3)").addClass('layui-this');

                    $(".secondStep,.foUserID,.foEmail").addClass('layui-hide');
                    $(".foNewPwd,.foConfirm").removeClass('layui-hide');
                }else{
                    return layer.msg('“验证码”输入错误!');
                }
            },
            error : function(request) {
                layer.msg(request);
            }
        });
    });

    var maxtime = 120,timer; //2分钟
    function CountDown() {
        if (maxtime >= 0) {
            $("#foGetCode").text(maxtime);
            --maxtime;
        } else {
            clearInterval(timer);
            $("#foGetCode").removeClass('layui-btn-disabled').attr('disabled',false).text('获取验证码');
        }
    }

    // 忘记密码页面 获取验证码
    $("#foGetCode").on('click',function() {
        if(!$("#foEmail").val()) return layer.msg('联系人邮箱不能为空!');

        if(!$(this).attr('disabled')){
            //用户点击获取验证码按钮（点完之后2分钟内不可再此点击，按钮上显示倒计时）
            $("#foGetCode").addClass('layui-btn-disabled').attr('disabled',true);
            maxtime = 120;
            timer = setInterval(CountDown, 1000);

            $.post('/getCheckCode',{'email':$("#foEmail").val()}, function (res) {
                if(res.msg == 'success'){
                    return layer.msg('发送成功！');
                }else{
                    clearInterval(timer);
                    $("#foGetCode").removeClass('layui-btn-disabled').attr('disabled',false).text('获取验证码');
                    return layer.msg('发送失败！');
                }
            });
        }
    });

    // 忘记密码页面 确定
    form.on('submit(foConfirm)', function(data){
        var pwd = $("#foNewPwd").val();
        var confirmPwd = $("#foConfirmPwd").val();
        var foUserID = $("#foUserID").val();
        if(pwd == confirmPwd){
            $.ajax({
                url: "/user/updateUser",
                type: "POST",
                data: JSON.stringify({"userPwd":confirmPwd,"userId":foUserID}),
                contentType: "application/json",
                dataType: "json",
                success: function (res) {
                    if(res.success == false){
                        layer.msg(res.msg);
                    }
                    if(res.success == true){
                        layer.confirm('修改成功！是否要重新登录？', {
                            icon: 3,
                            title: ''
                        }, function (index) {
                            window.location.href = window.location.pathname;
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

        return false; //阻止表单跳转
    });

    $("#foUserID,#foVerify").bind("keydown",function(e){
        // 兼容FF和IE和Opera
        var theEvent = e || window.event;
        var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
        if (code == 13) {
            return false;
        }
    });

    // $(document).not('input').bind("keydown",function(e){
    //     // 兼容FF和IE和Opera
    //     var theEvent = e || window.event;
    //     var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    //     if (code == 13) {
    //         if(!$("#register").hasClass('layui-hide')) {
    //             $(".reSubmit").click();
    //         }
    //     }
    // });

    $("#imgCode").click(function () {
        nextPic();
    });

    function nextPic() {
        $("#verify").val('');
        $("#imgCode>img").attr('src', "/verify/verificationCode?id=" + Math.random().toString());
    }

});