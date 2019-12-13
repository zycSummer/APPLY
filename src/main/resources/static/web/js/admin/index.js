var userId = sessionStorage.getItem('userId');
var roleId = sessionStorage.getItem('roleId');

layui.use(['form','element','layer','upload','table','jquery'],function(){
    var form = layui.form,
        table = layui.table,
        upload = layui.upload,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        element = layui.element;
    $ = layui.jquery;

    //点击 申报
    $(".declare").on('click', function () {
        window.open('/admin/business');
    });

    //点击 复审
    $(".review").on('click', function () {
        //校验企业是否有复审权限
        $.get('/company/checkCompanyReview',{'userId':userId}, function (res) {
            if(res.success == true){
                window.open('/admin/review');
            }else{
                return layer.msg('您没有复审权限!');
            }
        });
    });
});
