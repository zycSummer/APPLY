var userId = sessionStorage.getItem('userId');
var roleId = sessionStorage.getItem('roleId');

var tableData1 = [];
var file=[];//--附件类型

layui.use(['form','element','layer','upload','table','jquery'],function(){
    var form = layui.form,
        table = layui.table,
        upload = layui.upload,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        element = layui.element;
    $ = layui.jquery;

    getTable1Data();

    //复审 保存
    $('#save').on('click', function () {
        //不需要所有的数据都输入才能保存，但要检查输入的值是否满足 “指标.sheet”中分值范围列的要求，不满足的不让保存。
        if(!$(this).attr('disabled')) {
            save();
        }
    });

    //复审 上传
    upload.render({
        elem: '#uploadMemo'
        ,url: '/file/moreFileUpload'
        ,accept: 'file'
        ,exts:'zip|rar|doc|xls|xlsx|docx|png|pdf|jpg|jpeg|gif|bmp'
        ,field:'file'//设定文件域的字段名
        ,multiple:true
        ,bindAction: '#testListAction'
        ,choose:function (obj) {
            //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
            obj.preview(function(index, file, result){
                $.each(tableData1,function(i,v){
                    if(v.fileName == file.name){
                        layer.msg("该企业存在重复文件！");
                    }
                });
            });
        }
        ,done: function(res, index, upload){
            if(res.success == true){ //上传成功
                var str = res.data[0].appendixType?res.data[0].appendixType:'';
                var path = res.data[0].path?res.data[0].path:'';
                var fileName = res.data[0].fileName?res.data[0].fileName:'';

                var date = new Date();
                var unique = date.getTime();
                file.push({"seqId":'',"appendixType":0,"fileName":fileName,"path":path,'review':1,"num":unique});
                tableData1.push({
                    "seqId":'',
                    "appendixType":0,
                    "fileName":fileName,
                    "path":path,
                    "num":unique
                });

                table.reload('table1',{data:tableData1});

                table.on('tool(table1)', function(obj){
                    var layEvent = obj.event;
                    if(layEvent === 'deleteFile'){ //删除
                        deleteFile(obj);
                    }
                });
            }else{
                return layer.msg(res.msg);
            }
        }
    });

    // 复审上报多文件列表
    function getTable1Data() {
        tableData1 = [];
        table.render({
            elem: '#table1'
            , height: $("#uploadList .tableContent").height()-20
            , cols: [[
                {field: 'path', minWidth: 200, title: '上传材料路径'}
                , {field: 'fileName', minWidth: 200, title: '上传材料'}
                , {fixed: 'right', title:'操作', toolbar: '#deleteFile', width:300, align: 'center'}
            ]]
            , data: tableData1
            , id: 'table1'
            , page: false
            , limit: 900000
        });
        $.get('/company/getAllAppendix',function(res){
            $.each(res.data,function(i,v){
                tableData1.push({
                    "seqId":v.seqId,
                    "appendixType":0,
                    "fileName":v.fileName,
                    "path":v.path
                });
            });

            table.reload('table1',{"data":tableData1});

            table.on('tool(table1)', function(obj){
                var layEvent = obj.event;
                if(layEvent === 'deleteFile'){ //删除
                    deleteFile(obj);
                }
            });
        });
    }

    //删除
    function deleteFile(obj){
        var data = obj.data; //获得当前行数据
        layer.confirm('真的删除这个文件么', function(index){
            if(data['seqId']){
                //向服务端发送删除指令
                $.ajax({
                    url: "/company/deleteTbSiteApplicationAppendix",
                    type: "POST",
                    data: JSON.stringify({'seqId':data['seqId']}),
                    contentType:"application/json",
                    dataType: "json",
                    success: function (res) {
                        if (res.success == false) {
                            return layer.msg(res.msg);
                        }
                        // getTable1Data();
                        $.each(tableData1,function(i,v){
                            if(v.num == data['num']){
                                tableData1.splice(i,1);
                            }
                        });
                        layer.msg('删除成功！');
                    },
                    error : function(res) {
                        layer.msg(res.msg);
                    }
                });
            }else{
                $.each(file,function(i,v){
                    if(v.num == data['num']){
                        file.splice(i,1);
                    }
                });
                $.each(tableData1,function(i,v){
                    if(v.num == data['num']){
                        tableData1.splice(i,1);
                    }
                });
            }
            obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
            layer.close(index);
        });
    }

    // 企业复审上传文件后保存
    function save(){
        var data = {
            'file':file
        };
        $.ajax({
            url: "/company/insertTbSiteApplicationAppendix",
            type: "POST",
            data: JSON.stringify(data),
            contentType:"application/json",
            dataType: "json",
            success: function (res) {
                if (res.success == false) {
                    return layer.msg(res.msg);
                }
                file = [];
                layer.msg('保存成功！');
                getTable1Data();
            },
            error : function(res) {
                layer.msg(res.msg);
            }
        });
    }

});
