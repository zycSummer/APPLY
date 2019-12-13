var userId = sessionStorage.getItem('userId');
var roleId = sessionStorage.getItem('roleId');

var tableData1 = [],changeTabel1 = [],ifSubmit = true,inTime = true;
var tableData2 = [];
var tableDataMemo = [];
var tableData3 = [];
var file=[];//--附件类型

var resignData;//注册信息

//获取url？号后面的参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
var page = getQueryString('page');
$('.rightContent').addClass('layui-hide');
if(!page){
    $('#reportProcess').removeClass('layui-hide');
    $('#content .layui-nav-tree li:nth-child(1)').addClass('layui-this');
}else{
    $('#'+page).removeClass('layui-hide');
    $("#content .layui-nav-tree li").removeClass('layui-this');
    $.each($('#content .layui-nav-tree li'),function(){
        if(page==$(this).find('a').attr('data-id')) $(this).addClass('layui-this');
    });
}

layui.use(['form','element','layer','upload','table','jquery'],function(){
    var form = layui.form,
        table = layui.table,
        upload = layui.upload,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        element = layui.element;
    $ = layui.jquery;

    //监听左侧导航点击
    element.on('nav(progress)', function(elem){
        var idx = elem.attr('data-id');
        $('.rightContent').addClass('layui-hide');
        $('#'+idx).removeClass('layui-hide');
        window.location.href = '?page='+idx;
    });

    // 如果当前用户（企业）没有提交过自评，展示自评申报页面；如果已经提交过，则提示用户
    $.get('/company/checkIsCommitSelfEva', function (res) {
        if(res.success == true){//已经提交过
            if(page == 'reportSelfAssessment'){
                ifSubmit = false;
                $("#submits").addClass('layui-btn-disabled').attr('disabled',true);
                $("#save").addClass('layui-btn-disabled').attr('disabled',true);
                return layer.msg('您已提交自评申报，请到申报进展页面查询进展');
            }
            if(page == 'registerMessage'){
                $("#registerMessage .layui-input,#registerMessage .selectCon select,.reginSave").addClass('layui-btn-disabled').attr('disabled',true);
                return layer.msg('您已提交申报材料，不能修改注册信息');
            }
        }else{
            if(ifSubmit && inTime) {
                $("#save").removeClass('layui-btn-disabled').attr('disabled', false);
            }
            if(page == 'reportProgress') {
                return layer.msg('您还未提交自评申报，没有申报进展信息');
            }
        }
    });

    //如果当前用户的角色属于注册账号，则在进入申报自评页面时需要检查当前时间是否在允许使用的时间范围内，如果不在，则提示
    if(roleId == 1001 && page == 'reportSelfAssessment'){
        $.get('/company/checkIsRegisterNo',{'userId':userId}, function (res) {
            // {msg=日期, data={end_date=2019-02-28, start_date=2019-01-20}, success=true}
            if (res.success == false) {
                inTime = false;
                $("#submits").addClass('layui-btn-disabled').attr('disabled',true);
                $("#save").addClass('layui-btn-disabled').attr('disabled',true);
                return layer.msg('当前不允许自评申报，允许自评申报的时间范围是'+res.data['start_date']+'至'+res.data['end_date']+'。');
            }else{
                if(ifSubmit && inTime) {
                    $("#save").removeClass('layui-btn-disabled').attr('disabled', false);
                }
            }
        });
    }

    getTable1Data();
    getTable2Data();
    getTableMemoData();
    getTable3Data();//申报进展
    getReginMsg();
    getTable4Data();

    //申报自评 下一步
    $('#next').on('click', function () {
        if(ifSubmit && inTime){
            $("#uploadMemo").removeClass('layui-hide');
        }else{
            $("#uploadMemo").addClass('layui-hide');
        }
        $(".table2,.tableMemo,#prev").removeClass('layui-hide');
        $(".table1,#next,#download,#print").addClass('layui-hide');
    });
    //申报自评 上一步
    $('#prev').on('click', function () {
        $(".table2,.tableMemo,#prev,#uploadMemo").addClass('layui-hide');
        $(".table1,#next,#download,#print").removeClass('layui-hide');
    });

    //申报自评 下载
    $('#download').on('click', function () {
        var url = '/file/export';
        window.location.href= encodeURI(url);
    });

    //申报自评 打印
    $('#print').on('click', function () {
        $("#reportSelfAssessment .table1 .layui-table-view .layui-table").jqprint({
            debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
            importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
            printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
            operaSupport: true//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
        });
    });

    //申报自评 上传
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
                $.each(tableDataMemo,function(i,v){
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
                file.push({"seqId":'',"appendixType":0,"fileName":fileName,"path":path,'review':0,"num":unique});
                tableDataMemo.push({
                    "seqId":'',
                    "appendixType":0,
                    "fileName":fileName,
                    "fileType":path,
                    "file":str,
                    "num":unique
                });
                table.reload('tableMemo',{data:tableDataMemo});

                table.on('tool(tableDataMemo)', function(obj){
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

    //申报自评 保存
    $('#save').on('click', function () {
        //不需要所有的数据都输入才能保存，但要检查输入的值是否满足 “指标.sheet”中分值范围列的要求，不满足的不让保存。
        if(!$(this).attr('disabled')) {
            save();
        }
    });

    //申报自评 提交
    $('#submits').on('click', function () {
        //点击提交按钮时检查所有内容是否都已输入并且输入的值满足 “指标.sheet”中分值范围列的要求，
        // 如果有未填内容或者超出范围的，则不允许提交；如果都已填写并且都在范围内，则可以提交，
        var allInput1 = true;
        $.each(tableData1,function(i,v){
            if(v['selfValue'] == ''){
                allInput1=false;
            }
        });
        var allInput2 = true;
        $.each(tableData2,function(i,v){
            if(v['appendixType'] >0 && v['fileName'] == ''){
                allInput2=false;
            }
        });

        // “提交”按钮默认禁用，只有当点击“保存”按钮检查所有内容都已填写并且都在范围内时，才让“提交”按钮可以点击
        // （如果又修改了上面的任意内容，则“提交”按钮再次禁用，需要再次先点击“保存”按钮才行），如果有未填写内容则“提交”按钮还是禁用状态。
        if(!$(this).attr('disabled')) {
            if(allInput1 && allInput2){
                layer.confirm('确定要提交吗？提交之后将不可修改', {
                    icon: 3,
                    title: '提示信息'
                }, function (index) {
                    submits();
                });
            }else{
                return layer.msg('请全部填写');
            }
        }
    });

    //注册信息 保存
    form.on('submit(reginSave)', function(data){
        $.ajax({
            url: "/company/updateSite",
            type: "POST",
            data: JSON.stringify(data.field),
            contentType:"application/json",
            dataType: "json",
            success: function (res) {
                if(res.success == true){
                    layer.msg('保存成功！');
                }else{
                    layer.msg(res.msg);
                }
            },
            error : function(res) {
                layer.msg(res.msg);
            }
        });
        return false; //阻止表单跳转
    });

    function merge(res,field,index,obj) {
        var data = res.data;
        var mergeIndex = 0;//定位需要添加合并属性的行数
        var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
        var columsName = [field];//需要合并的列名称
        var columsIndex = index;//需要合并的列索引值

        for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
            var trArr = $("#"+obj).find(".layui-table-body>.layui-table").find("tr");//所有行
            for (var i = 1; i < res.data.length; i++) { //这里循环表格当前的数据

                var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]);//获取当前行的当前列
                var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]);//获取相同列的第一列

                if (data[i][columsName[k]] === data[i-1][columsName[k]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () {//相同列的第一列增加rowspan属性
                        $(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () {//当前行隐藏
                        $(this).css("display", "none");
                    });
                }else {
                    mergeIndex = i;
                    mark = 1;//一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                }
            }
            mergeIndex = 0;
            mark = 1;
        }
    }

    // 监听修改update到表格中
    form.on('select(enumSelect)', function (data) {
        if(ifSubmit && inTime){
            $("#save").removeClass('layui-btn-disabled').attr('disabled',false);
            $("#submits").addClass('layui-btn-disabled').attr('disabled',true);
        }

        var elem = $(data.elem);
        var trElem = elem.parents('tr');
        // var tableData1 = table.cache['table1'];
        // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
        tableData1[trElem.data('index')][elem.attr('name')] = data.value;
        var hasData = false;
        if(changeTabel1.length>0){
            $.each(changeTabel1,function(i,v){
                if(v.indexId == tableData1[trElem.data('index')]['indexId']){
                    hasData = true;
                    v.selfValue = data.value;
                }
            });
        }
        if(!hasData){
            changeTabel1.push({indexId:tableData1[trElem.data('index')]['indexId'],selfValue:data.value});
        }
    });

    // 申报自评
    function getTable1Data() {
        $.ajax({
            url: '/company/getm?flag=1',
            type: 'get',
            success: function (res) {
                if(res.code != true){
                    return layer.msg(res.msg);
                }
                var cols = [],list = [];
                if(res.count.length>0){
                    $.each(res.count,function(i,v){
                        var w = 150;
                        if(i >= 1){
                            w = 150+100*i;
                        }
                        if(i == (res.count.length-1)){
                            cols.push({field: 'index'+(i+1), minWidth: w, title: v});
                            list.push({field: 'index'+(i+1), minWidth: w, title: v});
                        }else{
                            cols.push({field: 'index'+(i+1), width: w, title: v});
                            list.push({field: 'index'+(i+1), width: w, title: v});
                        }

                    });
                }
                cols.push({field: 'score', width:200,title: '标准分值'});
                cols.push({field: 'selfValue',width: 200, title: '企业自评分值',
                    templet: function (d) {
                        if(d['valueType'] == 'enum'){
                            var op = '<select name="selfValue" lay-filter="enumSelect" data-value="' + d.selfValue + '" lay-verify="required">';
                            var arr = d['enum'].split('/');
                            $.each(arr,function(i,v){
                                op += '<option value="'+v+'">'+v+'</option>';
                            });
                            op += '</select>';
                            return op;
                        }else{
                            return d.selfValue+'<span class="number"></span>';
                            // return '<input type="text" name="selfValue" class="layui-input number" data-value="' + d.selfValue + '" autocomplete="off">';
                        }
                    }
                });
            //     cols.push({field: 'selfValue', edit:'text', title: '企业自评分值', align: 'center'});
                tableData1 = [];
                $.each(res.data,function(i,v){
                    if(v['valueType'] == 'enum'){
                        changeTabel1.push({indexId: v.indexId, selfValue: v.score});
                    }else{
                        tableData1.push(v);
                    }
                });
                // tableData1 = res.data;

                table.render({
                    elem: '#table1'
                    , height: $("#reportSelfAssessment .tableContent").height()-20 //高度最大化减去差值
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list,function(n,m){
                            merge(res,m.field,[n,n+1],"reportSelfAssessment");
                        });
                        var str = '<tr data-index="'+tableData1.length+'">' +
                            '<td align="center" colspan="'+list.length+'">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell laytable-cell-4-score">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell laytable-cell-4-selfScore">'+tableData1[0]['sumSelfValye']+'</div></td></tr>';

                        $("#table1").next('.layui-border-box').find('tbody').append(str);

                        layui.each($('#reportSelfAssessment .number'), function (index, item) {
                            $(item).parents('td').attr('data-edit', 'text').removeAttr('data-content');
                            $(item).remove();
                        });

                        layui.each($('#reportSelfAssessment select'), function (index, item) {
                            var elem = $(item);
                            elem.val(elem.data('value')).parents('div.layui-table-cell').css('overflow', 'visible');
                        });
                        form.render();
                    }
                    , data: tableData1
                    , id: 'table1'
                    , page:false
                    , limit: 1000000
                });

                table.on('edit(table1)', function(obj){ //注：edit是固定事件名，test是table原始容器的属性 lay-filter="对应的值"
                    console.log(obj.value); //得到修改后的值
                    console.log(obj.field); //当前编辑的字段名
                    console.log(obj.data); //所在行的所有相关数据
                    var rowData = obj.data;

                    $("#submits").addClass('layui-btn-disabled').attr('disabled',true);

                    // 企业用户在企业自评分值列输入或选择自评分值，每个指标可输入或选择的有效值范围见“指标.sheet”中的分值范围列。
                    // 指标的分值类型两种（见“指标.sheet”中分值类型列），一种是数值（前台页面通过输入框的形式让用户输入），
                    // 一种是枚举类型（前台页面通过下拉选择框的形式让用户选择）
                    if(rowData['valueType'] == 'number'){
                        var numStr = obj.value.replace(/\s+/g,'');
                        if(!numStr.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/)){
                            $("#save").addClass('layui-btn-disabled').attr('disabled',true);
                            return layer.msg('请输入正确的数字');
                        }else{
                            if(numStr.match(/^\.\d+$/)){
                                numStr = 0 + numStr;
                                $(this).val(numStr);
                            }
                            if(numStr.match(/^\.$/)){
                                numStr = 0;
                                $(this).val(numStr);
                            }
                        }

                        var val= obj.value?numStr:'';
                        var min = parseFloat(rowData.min);
                        var max = parseFloat(rowData.max);
                        if((val <= max && val >= min) || !val){
                            if(ifSubmit && inTime){
                                $("#save").removeClass('layui-btn-disabled').attr('disabled',false);
                            }

                            var hasData = false;
                            if(changeTabel1.length>0){
                                $.each(changeTabel1,function(i,v){
                                    if(v.indexId == rowData['indexId']){
                                        hasData = true;
                                        v.selfValue = val
                                    }
                                });
                            }
                            if(!hasData){
                                changeTabel1.push({indexId: rowData['indexId'], selfValue: val});
                            }
                        }else{
                            $("#save").addClass('layui-btn-disabled').attr('disabled',true);
                            return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                        }
                    }
                });
            },
            error: function(res){
                layer.msg(res.statusText);
            }
        });
    }
    // 申报自评 选择文件
    function getTable2Data() {
        tableData2 = [
            {seqId:'',appendixType:1,fileType:'苏州高新区和谐劳动关系创建活动申报表 (文件格式：doc|xls|xlsx|docx|png|pdf|jpg|jpeg|gif|bmp|zip|rar)',fileName:'',path:''}
            ,{seqId:'',appendixType:2,fileType:'职工满意度调查表 (文件格式：doc|xls|xlsx|docx|png|pdf|jpg|jpeg|gif|bmp|zip|rar)',fileName:'',path:''}
            ,{seqId:'',appendixType:3,fileType:'申报承诺书 (文件格式：doc|xls|xlsx|docx|png|pdf|jpg|jpeg|gif|bmp|zip|rar)',fileName:'',path:''}
        ];
        table.render({
            elem: '#table2'
            , height: 160
            , cols: [[
                {field: 'fileType', minWidth: 200, title: '申报材料'}
                , {field: 'fileName', minWidth: 200, title: '上传材料'}
                , {fixed: 'right', title:'操作', toolbar: '#upload', width:300, align: 'center'}
            ]]
            , data: tableData2
            , id: 'table2'
            , page: false
            , limit: 900000
        });
        $.get('/company/getTbSiteApplicationAppendix',function(res){
            $.each(res.data,function(i,v){
                if(v.appendixType == 1){
                    tableData2[0]['seqId'] = v.seqId;
                    tableData2[0]['path'] = v.path;
                    tableData2[0]['fileName'] = v.fileName;
                }else if(v.appendixType == 2){
                    tableData2[1]['seqId'] = v.seqId;
                    tableData2[1]['path'] = v.path;
                    tableData2[1]['fileName'] = v.fileName;
                }else if(v.appendixType == 3){
                    tableData2[2]['seqId'] = v.seqId;
                    tableData2[2]['path'] = v.path;
                    tableData2[2]['fileName'] = v.fileName;
                }
            });

            table.reload('table2',{data:tableData2});

            if(ifSubmit && inTime) {
                $(".upload").removeClass('layui-btn-disabled').attr('disabled',false);
                uploadFile();//选择文件
            }else{
                $(".upload").addClass('layui-btn-disabled').attr('disabled',true);
            }
        });
    }
    // 申报自评 上传 非必要附件
    function getTableMemoData() {
        tableDataMemo = [];
        table.render({
            elem: '#tableMemo'
            , height: $("#reportSelfAssessment .tableContent").height()-200
            , cols: [[
                 {field: 'fileType', minWidth: 200, title: '审核材料'}
                , {field: 'fileName', minWidth: 200, title: '上传材料'}
                , {fixed: 'right', title:'操作', toolbar: '#deleteFile', width:300, align: 'center'}
            ]]
            , data: tableDataMemo
            , done: function (res, curr, count) {
                if(ifSubmit && inTime) {
                    $(".deleteFile").removeClass('layui-btn-disabled').attr('disabled',false);
                }else{
                    $(".deleteFile").addClass('layui-btn-disabled').attr('disabled',true);
                }
            }
            , id: 'tableMemo'
            , page: false
            , limit: 900000
        });
        $.get('/company/getTbSiteApplicationAppendix',function(res){
            $.each(res.data,function(i,v){
                if(v.appendixType == 0){
                    tableDataMemo.push({
                        "seqId":v.seqId,
                        "appendixType":0,
                        "fileName":v.fileName,
                        "fileType":v.path,
                        "path":v.path
                    });
                }
            });

            table.reload('tableMemo',{data:tableDataMemo});

            table.on('tool(tableMemo)', function(obj){
                var layEvent = obj.event;
                if(layEvent === 'deleteFile'){ //删除
                    if(!$(this).attr('disabled')) {
                        deleteFile(obj);
                    }
                }
            });
        });
    }
    //选择文件
    function uploadFile(){
        upload.render({
            elem: '.upload'
            ,url: '/file/moreFileUpload'
            ,accept: 'file' //允许上传的文件类型
            // ,acceptMime: 'image/*'//只显示图片文件
            // ,size: 50
            ,exts:'zip|rar|doc|xls|xlsx|docx|png|pdf|jpg|jpeg|gif|bmp'
            ,field:'file'//设定文件域的字段名
            ,multiple:false
            ,choose:function (obj) {
                //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
                obj.preview(function(index, file, result){
                    $.each(tableData2,function(i,v){
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

                    var idx = parseFloat(this.item.attr('data-seqId'));
                    var appendixType = parseFloat(this.item.attr('data-appendixType'));

                    var hasData = false;
                    if(file.length>0){
                        $.each(file,function(i,v){
                            if(v.num == 0){
                                if(v.appendixType == appendixType){
                                    hasData = true;
                                    v.path = path;
                                    v.fileName = fileName;
                                }
                            }
                        });
                    }
                    if(!hasData){
                        file.push({"seqId":idx,"appendixType":appendixType,"fileName":fileName,"path":path,'review':0,'num':0});
                    }
                    tableData2[appendixType-1]['fileName'] = str;
                    tableData2[appendixType-1]['path'] = path;
                    this.item.parents('td').prev('td[data-field=fileName]').find('div').text(str);
                }else{
                    return layer.msg(res.msg);
                }
            }
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
                        // getTableMemoData();
                        $.each(tableDataMemo,function(i,v){
                            if(v.seqId == data['seqId']){
                                tableDataMemo.splice(i,1);
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
                $.each(tableDataMemo,function(i,v){
                    if(v.num == data['num']){
                        tableDataMemo.splice(i,1);
                    }
                });
            }
            obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
            layer.close(index);
        });
    }

    //申报进展
    function getTable3Data() {
        $.ajax({
            url: '/company/getDeclarationProgress?flag=2',
            type: 'get',
            success: function (res) {
                if(res.code != true){
                    return layer.msg(res.msg);
                }
                var list = [];
                if(res.count.length>0){
                    $.each(res.count,function(i,v){
                        var w = 150;
                        if(i >= 1){
                            w = 150+100*i;
                        }
                        if(i == (res.count.length-1)){
                            list.push({field: 'index'+(i+1), minWidth: w, title: v});
                        }else{
                            list.push({field: 'index'+(i+1), width: w, title: v});
                        }
                    });
                }
                var arr = [{field: 'score', width: 90, title: '标准分值'}
                    , {field: 'selfValue', width: 120, title: '企业自评分数'}
                    , {field: 'streetValue', width: 160, title: '镇（街道）测评分值'}
                    , {field: 'districtValue',width: 150, title: '区三方评定分数'}];
                var cols = list.concat(arr);

                tableData3 = res.data;

                table.render({
                    elem: '#table3'
                    , height: $("#reportProgress").height() //高度最大化减去差值
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list,function(k,j){
                            merge(res,j.field,[k,k+1],"reportProgress");
                        });
                        var str = '<tr data-index="'+tableData3.length+'">' +
                            '<td align="center" colspan="'+list.length+'">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell laytable-cell-4-score">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell">'+tableData3[0]['sumSelfValye']+'</div></td>' +
                            '<td data-field="streetValue"><div class="layui-table-cell">'+tableData3[0]['sumStreetValue']+'</div></td>' +
                            '<td data-field="districtValue"><div class="layui-table-cell">'+tableData3[0]['sumDistrictValue']+'</div></td></tr>'+'' +
                            '<tr data-index="'+(tableData3.length+1)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">镇（街道）第三方机构测评建议 : '+tableData3[0]['streetSuggestion']+'</div></td></tr>'+
                            '<tr data-index="'+(tableData3.length+2)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">区三方办公室评定建议 : '+tableData3[0]['districtSuggestion']+'</div></td></tr>'+
                            '<tr data-index="'+(tableData3.length+3)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">区三方委员会 : '+tableData3[0]['districtCommitteeTitle']+'</div></td></tr>';

                        $("#table3").next('.layui-border-box').find('tbody').append(str);
                    }
                    , data: tableData3
                    , id: 'table3'
                    , page:false
                    , limit: 1000000
                });
            },
            error: function(res){
                layer.msg(res.statusText);
            }
        });
    }

    // 资料下载
    function getTable4Data() {
        table.render({
            elem: '#table4'
            , height: $("#dataDownload").height()
            , cols: [[
                {field: 'id', width: 100, title: '序号', align: 'center'}
                , {field: 'file', width: 400, title: '资料'}
                , { title:'操作',toolbar: '#downloads',width:300, align: 'center'}
            ]]
            , data: [
                {id:1,file:'苏州高新区和谐劳动关系创建活动申报表.doc'}
                ,{id:2,file:'职工满意度调查表.xls'}
                ,{id:3,file:'申报承诺书.xls'}
                ,{id:4,file:'流转材料清单.xlsx'}
            ]
            , id: 'table4'
            , page:false
            , limit: 1000000
        });

        //监听行工具事件
        table.on('tool(table4)', function(obj){
            var data = obj.data;
            if(obj.event === 'download'){
                var url = '/file/Download?name='+data.file;
                window.location.href= encodeURI(url);
            }
        });
    }

    // 企业申报自评的保存
    function save(){
        var data = {
            'tableData':changeTabel1,//[{'indexId':'','selfValue':''},{'indexId':'','selfValue':''}]
            'file':file//[{'seqId':'','appendixType':'','path':'','review':0}]
        };
        $.ajax({
            url: "/company/insertTbSiteApplicationIndexValue",
            type: "POST",
            data: JSON.stringify(data),
            contentType:"application/json",
            dataType: "json",
            success: function (res) {
                if (res.success == false) {
                    return layer.msg(res.msg);
                }
                changeTabel1 = [];
                file = [];
                layer.msg('保存成功！');

                var allInput1 = true;
                $.each(tableData1,function(i,v){
                    if(v['selfValue'] == ''){
                        allInput1=false;
                    }
                });
                var allInput2 = true;
                $.each(tableData2,function(j,k){
                    if(k['appendixType'] > 0 && k['fileName'] == ''){
                        allInput2=false;
                    }
                });

                if(allInput1 && allInput2){
                    $("#submits").removeClass('layui-btn-disabled').attr('disabled',false);
                }else{
                    $("#submits").addClass('layui-btn-disabled').attr('disabled',true);
                }

                getTable1Data();
                getTable2Data();
                getTableMemoData();
            },
            error : function(res) {
                layer.msg(res.msg);
            }
        });
    }
    //企业自评的提交
    function submits(){
        var data = {
            'file':file
        };
        $.ajax({
            url: "/company/updateTbSiteApplicationIndexValue",
            type: "POST",
            data: JSON.stringify(data),
            contentType:"application/json",
            dataType: "json",
            success: function (res) {
                if (res.success == false) {
                    return layer.msg(res.msg);
                }
                layer.msg('提交成功！');
                $("#submits").addClass('layui-btn-disabled').attr('disabled',true);
                $("#save").addClass('layui-btn-disabled').attr('disabled',true);
                getTable1Data();
                getTable2Data();
                getTableMemoData();
            },
            error : function(res) {
                layer.msg(res.msg);
            }
        });
    }

    //获取注册信息
    function getReginMsg(){
        $.get('/user/getSiteByUserId',{'userId':userId}, function (res) {
            if (!res.success) {
                return layer.msg(res.msg);
            }
            resignData = res.data;
            var val = res.data;
            $("#applicant").val(val.applicant);
            $("#siteName").val(val.siteName);
            $("#orgCode").val(val.orgCode);
            $("#siteTypeId").val(val.siteTypeId);
            $("#industryTypeId").val(val.industryTypeId);
            $("#streetId").val(val.streetId);
            $("#parkId").val(val.parkId);
            $("#addr").val(val.addr);
            $("#contacts").val(val.contacts);
            $("#tel").val(val.tel);
            $("#mobile").val(val.mobile);
            $("#mail").val(val.mail);
            form.render('select', 'regin');
        });
    }

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
        $("#siteTypeId").html(str);
        if(resignData){
            $("#siteTypeId").val(resignData.siteTypeId);
        }

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
        $("#industryTypeId").html(str);
        if(resignData){
            $("#industryTypeId").val(resignData.industryTypeId);
        }
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
        $("#streetId").html(str);
        if(resignData){
            $("#streetId").val(resignData.streetId);
        }
        form.render('select','regin');

        form.on('select(townStreet)', function(data){
            getPark($("#streetId").val());
        });

        getPark($("#streetId").val());

    });

    //所属镇工业园  获取
    function getPark(val){
        $.get('/user/getSysIndustrialPark', function (res) {
            if (!res.success) {
                return layer.msg(res.msg);
            }
            var str = '';
            if(res.data.length>0){
                $.each(res.data,function(i,v){
                    if(val == v.streetId){
                        str += '<option value="'+v.parkId+'">'+v.parkName+'</option>';
                    }
                });
            }
            $("#parkId").html(str);
            if(resignData){
                $("#parkId").val(resignData.parkId);
            }
            form.render('select','regin');
        });
    }

});
