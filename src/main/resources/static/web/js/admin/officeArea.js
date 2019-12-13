var userId = sessionStorage.getItem('userId');
var roleId = sessionStorage.getItem('roleId');
var userName = sessionStorage.getItem('userName');

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
    $('#assess').removeClass('layui-hide');
    $('#content .layui-nav-tree li:nth-child(1)').addClass('layui-this');
}else{
    $('#'+page).removeClass('layui-hide');
    $("#content .layui-nav-tree li").removeClass('layui-this');
    $.each($('#content .layui-nav-tree li'),function(){
        if(page==$(this).find('a').attr('data-id')) $(this).addClass('layui-this');
    });
}
var registerNo = true;//当前时间是否在允许使用的时间范围内

var tableData1 = [];
var diaTableData1 = [];
var diaTableData2 = [],changeTabel2 = [],ifSave = true;

var tableData2 = [],searchYear = '';
var diaTableData3 = [];
var diaTableData4 = [],changeTabel4 = [],ifSave4 = true;

layui.use(['form','element','table','laydate','layer','jquery'],function(){
    var form = layui.form,
        table = layui.table,
        laydate = layui.laydate,
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
    /* -------------------------------------  common   ---------------------------------  */
    // 所属镇（街道） 获取
    $.get('/user/getSysStreet', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        var str = '<option value="">全部</option>';
        if(res.data.length>0){
            $.each(res.data,function(i,v){
                str += '<option value="'+v.streetId+'">'+v.streetName+'</option>';
            });
        }
        $("#streetId").html(str);
        form.render('select','search');

        form.on('select(streetId)', function(data){
            getPark($("#streetId").val(),'search');
        });
        getPark($("#streetId").val(),'search');

        $("#streetId2").html(str);
        form.render('select','review');

        form.on('select(streetId2)', function(data){
            getPark($("#streetId2").val(),'review');
        });
        getPark($("#streetId2").val(),'review');

    });

    //所属工业园  获取
    function getPark(val,type){
        $.get('/user/getSysIndustrialPark', function (res) {
            if (!res.success) {
                return layer.msg(res.msg);
            }
            var str = '<option value="" data-streetId="">全部</option>';
            if(res.data.length>0){
                $.each(res.data,function(i,v){
                    if(val){
                        if(val == v.streetId){
                            str += '<option value="'+v.parkId+'" data-streetId="'+v.streetId+'">'+v.parkName+'</option>';
                        }
                    }else{
                        str += '<option value="'+v.parkId+'" data-streetId="'+v.streetId+'">'+v.parkName+'</option>';
                    }
                });
            }
            if(type == 'search'){
                $("#parkId").html(str);
                form.render('select','search');

                form.on('select(parkId)', function(data){
                    var val = $(data.elem).find('option:selected').attr('data-streetId');
                    $("#streetId").val(val);
                    form.render('select','search');
                });
            }else{
                $("#parkId2").html(str);
                form.render('select','review');

                form.on('select(parkId2)', function(data){
                    var val = $(data.elem).find('option:selected').attr('data-streetId');
                    $("#streetId2").val(val);
                    form.render('select','review');
                });
            }
        });
    }

    var date = new Date();
    // 复审年份
    laydate.render({
        elem: '#year'
        , type: 'year'
        , btns: ['now', 'confirm']
        , value: new Date()
    });
    $("#year").val(date.getFullYear());

    // 如果当前用户的角色属于评定账号，则在进入评定页面时需要检查当前时间是否在允许使用的时间范围内，如果不在，则提示
    if(roleId == 1003){
        $.get('/district/checkIsRegisterNo',{'userId':userId}, function (res) {
            if (res.success == false) {
                registerNo = false;
                $(".reported,.reported2").addClass('layui-btn-disabled').attr('disabled',true);
                layer.msg('当前不允许评定申报，允许评定申报的时间范围是'+res.data['start_date']+'至'+res.data['end_date']+'。');
            }else{
                registerNo = true;
                $(".reported,.reported2").removeClass('layui-btn-disabled').attr('disabled',false);
            }

            if(!page || page=='assess') {
                getTable1Data();
            }else{
                getTable2Data();
            }
        });
    }

    /* -------------------------------------  申报评定   ---------------------------------  */
    //查询
    $('.search').on('click', function () {
        getTable1Data();
    });

    //上报
    $('.reported').on('click', function () {
        var checkStatus = table.checkStatus('table1');
        var checkDatas = [];
        $.each(checkStatus.data,function(n,m){
            if(m){
                if(m['status'] == 1 || m['districtValue']==0){
                    // console.log(n);
                }else{
                    checkDatas.push(m);
                }
            }
        });
        if(!$(this).attr('disabled')) {
            if (checkDatas.length == 0) return layer.msg('请选择要上报的申报单位');

            layer.confirm('确定要上报吗？上报之后将不可再进行评定，只能查看详情', {
                icon: 3,
                title: '提示信息'
            }, function (index) {
                reported(checkDatas);
            });
        }
    });

    function getTable1Data() {
        table.render({
            elem: '#table1'
            , url: '/district/getAllDistrict'
            , method: 'get'
            , height: 'full-208' //高度最大化减去差值
            , where: {
                'parkId': $("#parkId").val(),
                'streetId': $("#streetId").val(),
                'siteName': $("#businessName").val()
            }
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'siteName', title: '申报单位', align: 'center'}
                , {field: 'selfValue', width: 200, title: '自评得分', align: 'center'}
                , {field: 'streetValue', width: 200, title: '测评得分', align: 'center'}
                , {field: 'districtValue', width: 200, title: '评定得分', align: 'center',
                    templet: function (d) {
                        if(d['districtValue'] == 0){
                            return '';
                        }else{
                            return d['districtValue'];
                        }
                    }}
                , {field: 'status',  title: '上报状态', align: 'center',
                    templet: function (d) {
                        if(d['status'] == 1){
                            return '已上报';
                        }else{
                            return '未上报';
                        }
                    }}
                , {fixed: 'right', title:'操作', toolbar: '#btn', width:200,align: 'center'}
            ]]
            ,done:function(res, curr, count){
                if(res.code == true){
                    tableData1 = res.data;

                    if(!registerNo){
                        $("#assess .assess").addClass('layui-btn-disabled').attr('disabled',true);
                    }else{
                        $("#assess .assess").removeClass('layui-btn-disabled').attr('disabled',false);
                    }

                    $.each(tableData1,function(i,v){
                        if(v['status'] == 1 || v['districtValue']==0){
                            var obj = $("#assess .tableContent .layui-table-body tbody").find('tr[data-index="'+i+'"]');
                            obj.find('input').attr('disabled',true);
                            form.render('checkbox');
                        }
                    });
                }
            }
            ,response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                ,statusCode: true //规定成功的状态码，默认：0
                ,msgName: 'msg' //规定状态信息的字段名称，默认：msg
                ,countName: 'count' //规定数据总数的字段名称，默认：count
                ,dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , id: 'table1'
            , page: true
            , limit: 10
        });

        //监听行工具事件
        table.on('tool(table1)', function(obj){
            var rowData = obj.data;

            if(obj.event === 'seeDetails'){
                $("#tableCon1").removeClass('layui-hide');
                $("#tableCon2").addClass('layui-hide');

                getDialogData1(rowData);
            } else if(obj.event === 'assess'){
                if(!$(this).attr('disabled')) {
                    $("#tableCon2").removeClass('layui-hide');
                    $("#tableCon1").addClass('layui-hide');

                    getDialogData2(rowData);
                }
            } else if(obj.event === 'downloadFile'){
                var files = rowData.file,names = [];
                $.each(files,function(i,v){
                    names.push(v.fileName);
                });
                var url = '/file/downloadAll?names='+names;
                window.location.href= encodeURI(url);
            }
        });

        // 监听复选框选择
        table.on('checkbox(table1)', function(obj){
            // console.log(obj.checked); //当前是否选中状态
            // console.log(obj.data); //选中行的相关数据
            // console.log(obj.type); //如果触发的是全选，则为：all，如果触发的是单选，则为：one

            if(!obj.checked && obj.type == 'one'){
                if(obj.data['status'] == 1 || obj.data['districtValue']==0){
                    return layer.msg('已经上报或者评定得分为空的不允许勾选');
                }
            }else if(obj.checked && obj.type == 'all'){
                var tbodyObj = $("#assess .layui-table-fixed .layui-table-body tbody");
                $.each(tableData1,function(i,v){
                    if(v['status'] == 1 || v['districtValue']==0){
                        var trObj = tbodyObj.find('tr[data-index="'+i+'"]');
                        trObj.find('.layui-unselect').removeClass("layui-form-checked");
                    }
                });
            }
        });
    }

    //勾选的记录上报
    function reported(checkData){
        var data = {
            'tableData':checkData
        };
        $.ajax({
            url: "/district/updateDistrict",
            type: "POST",
            data: JSON.stringify(data),
            contentType:"application/json",
            dataType: "json",
            success: function (res) {
                if (res.success == false) {
                    return layer.msg(res.msg);
                }
                getTable1Data();
                layer.msg('上报成功！');
            },
            error : function(res) {
                layer.msg(res.msg);
            }
        });
    }

    // 查看详情 弹框
    function getDialogData1(rowData){
        var height = $("#content").height();
        $("#officeAssess").height(height-160);
        layui.layer.open({
            type: 1,
            title: '区三方办公室评定详情',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#officeAssess"),
            btn: ['取消'],
            success: function () {
                $("#officeAssess").removeClass('layui-hide');
                $("#officeAssess h3").text(rowData['siteName']+'企业');
                diaTable1Data(rowData);
            },
            yes: function (index) {
                layui.layer.close(index);
            },
            end: function (index) {
                $("#officeAssess").addClass('layui-hide');
            }
        });
    }

    //评定保存 弹框
    function getDialogData2(rowData){
        var height = $("#content").height();
        $("#officeAssess").height(height-160);
        layui.layer.open({
            type: 1,
            title: '区三方办公室评定',
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#officeAssess"),
            btn: ['保存'],
            success: function () {
                $("#officeAssess").removeClass('layui-hide');
                $("#officeAssess h3").text(rowData['siteName']+'企业');
                changeTabel2 =[];
                diaTable2Data(rowData);
            },
            yes: function (index) {
                var data = {
                    "siteId":rowData['siteId'],
                    "districtSuggestion":$(".districtSuggestion").val(),
                    'tableData':changeTabel2//[{'indexId':'','districtValue':''},{'indexId':'','districtValue':''}]
                };

                if(ifSave == false){
                    return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                }

                var lay1 = layer.load(1, {shade: [0.1, '#fff']});
                $.ajax({
                    url: '/district/insertDistrict',
                    type: 'post',
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(data),
                    success: function (res) {
                        layer.close(lay1);
                        if (res.success == false) {
                            return layer.msg(res.msg);
                        }
                        if(res.data && res.data.length>0){
                            $.each(diaTableData2,function(k,j){
                                if($.inArray(j.indexId,res.data)>-1){
                                    var redObj = $("#tableCon2 .layui-table-body tbody tr:nth-child("+(k+1)+")");
                                    redObj.find('td[data-field="districtValue"] .layui-select-title input').css('color','red');
                                }
                            });
                            return layer.msg(res.msg);
                        }else{
                            getTable1Data();
                            layui.layer.close(index);
                        }
                    },
                    error: function (res) {
                        layer.close(lay1);
                        layui.layer.msg(res.msg);
                    }
                });
            },
            end: function (index) {
                $("#officeAssess").addClass('layui-hide');
            }
        });
    }

    // 查看详情
    function diaTable1Data(rowData) {
        $.ajax({
            url: '/district/getm?siteId='+rowData['siteId']+'&year=',
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
                var arr = [{field: 'score', width: 88, title: '标准分值'}
                    , {field: 'selfValue', width: 115, title: '企业自评分数'}
                    , {field: 'streetValue', width: 160, title: '镇（街道）测评分值'}
                    , {field: 'districtValue', width: 170, title: '区三方办公室评定分值',
                        templet: function (d) {
                            var dis = d.districtValue?d.districtValue:'';
                            if(d['red'] == 0){
                                return '<span style="color:red;">'+dis+'</span>';
                            }else{
                                return dis;
                            }
                        }
                    }
                    , {field: 'fileName', title:'文件', toolbar: '#downloadBtn1', width:100,align: 'center'}
                ];
                var cols = list.concat(arr);

                diaTableData1 = res.data;

                table.render({
                    elem: '#diaTable1'
                    , height: $("#officeAssess").height()
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list,function(k,j){
                            merge(res,j.field,[k,k+1],"tableCon1");
                        });
                        var str = '<tr data-index="'+diaTableData1.length+'">' +
                            '<td align="center" colspan="'+list.length+'">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell">'+rowData['selfValue']+'</div></td>' +
                            '<td data-field="streetValue"><div class="layui-table-cell">'+rowData['streetValue']+'</div></td>' +
                            '<td data-field="districtValue"><div class="layui-table-cell">'+rowData['districtValue']+'</div></td>' +
                            '<td data-field="fileName"><div class="layui-table-cell"></div></td></tr>'+'' +
                            '<tr data-index="'+(diaTableData1.length+1)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">镇（街道）第三方机构测评建议 : '+rowData['streetSuggestion']+'</div></td></tr>'+
                            '<tr data-index="'+(diaTableData1.length+2)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">区三方办公室评定建议 : '+rowData['districtSuggestion']+'</div></td></tr>';

                        $("#diaTable1").next('.layui-border-box').find('tbody').append(str);
                    }
                    , data: diaTableData1
                    , id: 'diaTable1'
                    , page:false
                    , limit: 1000000
                });

                //监听行工具事件
                table.on('tool(diaTable1)', function(obj){
                    var rowData = obj.data;

                    if(obj.event === 'downloadFile1'){
                        var files = rowData.fileNames;
                        var url = '/file/downloadAll?names='+files;
                        window.location.href= encodeURI(url);
                    }
                });
            },
            error: function(res){
                layer.msg(res.msg);
            }
        });
    }

    //评定
    function diaTable2Data(rowData) {
        $.ajax({
            url: '/district/getm?siteId='+rowData['siteId']+'&year=',
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
                cols.push({field: 'score', width:88,title: '标准分值'});
                cols.push({field: 'selfValue', width:115,title: '企业自评分值'});
                cols.push({field: 'streetValue',width: 160, title: '镇（街道）测评分值'});
                cols.push({field: 'districtValue',width: 170, title: '区三方办公室评定分值',
                    templet: function (d) {
                        var color = '#000';
                        if(d['red'] == 0){
                            color = 'red';
                        }
                        if(d['valueType'] == 'enum'){
                            var op = '<select name="districtValue" style="color:'+color+';" lay-filter="enumSelect" data-value="' + d.districtValue + '">';
                            var arr = d['enum'].split('/');
                            $.each(arr,function(i,v){
                                op += '<option value="'+v+'">'+v+'</option>';
                            });
                            op += '</select>';
                            return op;
                        }else{
                            var val = d.districtValue?d.districtValue:'';
                            return val+'<span class="number" style="color:'+color+';"></span>';
                        }
                    }
                });
                cols.push({field: 'fileName', title:'文件', toolbar: '#downloadBtn2', width:100,align: 'center'});

                diaTableData2 = res.data;

                table.render({
                    elem: '#diaTable2'
                    , height: $("#officeAssess").height() //高度最大化减去差值
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list,function(n,m){
                            merge(res,m.field,[n,n+1],"tableCon2");
                        });
                        var str = '<tr data-index="'+diaTableData2.length+'">' +
                            '<td align="center" colspan="'+list.length+'">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell">'+rowData['selfValue']+'</div></td>'+
                            '<td data-field="streetValue"><div class="layui-table-cell">'+rowData['streetValue']+'</div></td>' +
                            '<td data-field="districtValue"><div class="layui-table-cell">'+rowData['districtValue']+'</div></td>' +
                            '<td data-field="fileName"><div class="layui-table-cell"></div></td></tr>'+
                            '<tr data-index="'+(diaTableData2.length+1)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">镇（街道）第三方机构测评建议 : '+rowData['streetSuggestion']+'</div></td></tr>'+
                            '<tr data-index="'+(diaTableData2.length+2)+'"><td colspan="'+cols.length+'"><div style="margin-left:8px;">' +
                            '区三方办公室评定建议 : <input type="text" class="layui-input districtSuggestion" value="'+rowData['districtSuggestion']+'" /></div></td></tr>';

                        $("#diaTable2").next('.layui-border-box').find('tbody').append(str);

                        layui.each($('#tableCon2 .number'), function (index, item) {
                            $(item).parents('td').attr('data-edit', 'text').removeAttr('data-content');
                            $(item).remove();
                        });

                        layui.each($('#tableCon2 select'), function (index, item) {
                            var elem = $(item);
                            elem.val(elem.data('value')).parents('div.layui-table-cell').css('overflow', 'visible');
                        });
                        form.render('select');
                        layui.each($('#tableCon2 select'), function (index, item) {
                            var elem = $(item);
                            var color = elem.css('color');
                            elem.parents('td').find('.layui-unselect .layui-select-title input').css('color',color);
                        });
                    }
                    , data: diaTableData2
                    , id: 'diaTable2'
                    , page:false
                    , limit: 1000000
                });

                //监听行工具事件
                table.on('tool(diaTable2)', function(obj){
                    var rowData = obj.data;

                    if(obj.event === 'downloadFile2'){
                        var files = rowData.fileNames;
                        var url = '/file/downloadAll?names='+files;
                        window.location.href= encodeURI(url);
                    }
                });

                table.on('edit(diaTable2)', function(obj){
                    var rowData = obj.data;
                    ifSave = true;

                    if(rowData['valueType'] == 'number'){
                        var numStr = obj.value.replace(/\s+/g,'');
                        if(!numStr.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/)){
                            ifSave = false;
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
                            var hasData = false;
                            if(changeTabel2.length>0){
                                $.each(changeTabel2,function(i,v){
                                    if(v.indexId == rowData['indexId']){
                                        hasData = true;
                                        v.districtValue = val
                                    }
                                });
                            }
                            if(!hasData){
                                changeTabel2.push({
                                    indexId: rowData['indexId']
                                    ,districtValue: val
                                    ,streetValue:rowData['streetValue']
                                    ,selfValue:rowData['selfValue']
                                });
                            }
                        }else{
                            ifSave = false;
                            return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                        }
                    }
                });

            },
            error: function(res){
                layer.msg(res.msg);
            }
        });
    }

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
        var redObj = $("#tableCon2 .layui-table-body tbody tr");
        redObj.find('td[data-field="districtValue"] .layui-select-title input').css('color','#000');

        var elem = $(data.elem);
        var trElem = elem.parents('tr');
        // var tableData1 = table.cache['diaTable1'];
        // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
        diaTableData2[trElem.data('index')][elem.attr('name')] = data.value;
        var hasData = false;
        if(changeTabel2.length>0){
            $.each(changeTabel2,function(i,v){
                if(v.indexId == diaTableData2[trElem.data('index')]['indexId']){
                    hasData = true;
                    v.districtValue = data.value;
                }
            });
        }
        if(!hasData){
            changeTabel2.push({
                indexId:diaTableData2[trElem.data('index')]['indexId']
                ,districtValue:data.value
                ,streetValue:diaTableData2[trElem.data('index')]['streetValue']
                ,selfValue:diaTableData2[trElem.data('index')]['selfValue']});
        }
    });

    /* -------------------------------------  复审评定   ---------------------------------  */
    //复审评定 查询
    $('.search2').on('click', function () {
        var year = $("#year").val();
        if(!year){
            return layer.msg('请选择年份！');
        }
        getTable2Data();
    });

    //复审评定 上报
    $('.reported2').on('click', function () {
        var checkStatus = table.checkStatus('table2');
        var checkDatas = [];
        $.each(checkStatus.data,function(n,m){
            if(m){
                if(m['districtReviewResult'] == 1 || m['districtReviewValue']==0){
                    // console.log(n);
                }else{
                    checkDatas.push(m);
                }
            }
        });
        if(!$(this).attr('disabled')) {

            if (checkDatas.length == 0) return layer.msg('请选择要上报的申报单位');

            layer.confirm('确定要上报吗？上报之后将不可再进行复审评定，只能查看详情', {
                icon: 3,
                title: '提示信息'
            }, function (index) {
                reported2(checkDatas);
            });
        }
    });

    function getTable2Data() {
        table.render({
            elem: '#table2'
            , url: '/district/districtReview'
            , method: 'post'
            , contentType:"application/json"
            , dataType: "json"
            , height: 'full-208' //高度最大化减去差值
            , where: {
                'parkId': $("#parkId2").val(),
                'streetId': $("#streetId2").val(),
                'siteName': $("#businessName2").val(),
                'year': $("#year").val()
            }
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'siteName', title: '申报单位', align: 'center'}
                , {field: 'districtCommitteeTitle', width: 300, title: '称号', align: 'center'}
                , {field: 'districtValue', width: 300, title: '申报得分', align: 'center'}
                , {field: 'districtReviewValue', width: 300, title: '复审评定得分', align: 'center',
                    templet: function (d) {
                        if(d['districtReviewValue'] == 0){
                            return '';
                        }else{
                            return d['districtReviewValue'];
                        }
                    }}
                , {fixed: 'right', title:'操作', toolbar: '#btn2', width:200,align: 'center'}
            ]]
            ,done:function(res, curr, count){
                if(res.code == true){
                    tableData2 = res.data;
                    searchYear = $("#year").val();

                    if(!registerNo){
                        $("#review .assess").addClass('layui-btn-disabled').attr('disabled',true);
                    }else{
                        $("#review .assess").removeClass('layui-btn-disabled').attr('disabled',false);
                    }

                    $.each(tableData2,function(i,v){
                        if(v['districtReviewResult'] == 1 || v['districtReviewValue']==0){
                            var obj = $("#review .tableContent .layui-table-body tbody").find('tr[data-index="'+i+'"]');
                            obj.find('input').attr('disabled',true);
                            form.render('checkbox');
                        }
                    });
                }
            }
            ,response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                ,statusCode: true //规定成功的状态码，默认：0
                ,msgName: 'msg' //规定状态信息的字段名称，默认：msg
                ,countName: 'count' //规定数据总数的字段名称，默认：count
                ,dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , id: 'table2'
            , page: true
            , limit: 10
        });

        //监听行工具事件
        table.on('tool(table2)', function(obj){
            var rowData = obj.data;

            if(obj.event === 'seeDetails'){
                $("#tableCon3").removeClass('layui-hide');
                $("#tableCon4").addClass('layui-hide');

                getDialogData3(rowData);
            } else if(obj.event === 'assess'){
                if(!$(this).attr('disabled')) {
                    $("#tableCon4").removeClass('layui-hide');
                    $("#tableCon3").addClass('layui-hide');

                    getDialogData4(rowData);
                }
            } else if(obj.event === 'downloadFile'){
                var files = rowData.file,names = [];
                $.each(files,function(i,v){
                    names.push(v.fileName);
                });
                var url = '/file/downloadAll?names='+names;
                window.location.href= encodeURI(url);
            }
        });

        // 监听复选框选择
        table.on('checkbox(table2)', function(obj){
            if(!obj.checked && obj.type == 'one'){
                if(obj.data['districtReviewResult'] == 1 || obj.data['districtReviewValue']==0){
                    return layer.msg('已经上报或者复审评定得分为空的不允许勾选');
                }
            }else if(obj.checked && obj.type == 'all'){
                var tbodyObj = $("#review .layui-table-fixed .layui-table-body tbody");
                $.each(tableData2,function(i,v){
                    if(v['districtReviewResult'] == 1 || v['districtReviewValue']==0){
                        var trObj = tbodyObj.find('tr[data-index="'+i+'"]');
                        trObj.find('.layui-unselect').removeClass("layui-form-checked");
                    }
                });
            }
        });
    }

    //复审评定 勾选的记录上报
    function reported2(checkData){
        var siteIds = [];
        $.each(checkData,function(i,v){
            siteIds.push(v.siteId);
        });
        var data = {
            'siteIds':siteIds,
            'year':searchYear
        };
        $.ajax({
            url: "/district/updateDistrictReview",
            type: "POST",
            data: JSON.stringify(data),
            contentType:"application/json",
            dataType: "json",
            success: function (res) {
                if (res.success == false) {
                    return layer.msg(res.msg);
                }
                getTable2Data();
                layer.msg('上报成功！');
            },
            error : function(res) {
                layer.msg(res.msg);
            }
        });
    }

    // 复审评定 查看详情 弹框
    function getDialogData3(rowData){
        var height = $("#content").height();
        $("#reviewAssess").height(height-160);
        layui.layer.open({
            type: 1,
            title: '镇（街道）三方组织详情',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#reviewAssess"),
            btn: ['取消'],
            success: function () {
                $("#reviewAssess").removeClass('layui-hide');
                $("#reviewAssess h3").text(rowData['siteName']+'企业');
                diaTable3Data(rowData);
            },
            yes: function (index) {
                layui.layer.close(index);
            },
            end: function (index) {
                $("#reviewAssess").addClass('layui-hide');
            }
        });
    }

    //复审评定保存 弹框
    function getDialogData4(rowData){
        var height = $("#content").height();
        $("#reviewAssess").height(height-160);
        layui.layer.open({
            type: 1,
            title: '镇（街道）三方组织测评',
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#reviewAssess"),
            btn: ['保存'],
            success: function () {
                $("#reviewAssess").removeClass('layui-hide');
                $("#reviewAssess h3").text(rowData['siteName']+'企业');
                changeTabel4 =[];
                diaTable4Data(rowData);
            },
            yes: function (index) {
                var data = {
                    "siteId":rowData['siteId'],
                    "year":$("#year").val(),
                    // "streetSuggestion":$(".streetSuggestion").val(),
                    'tableData':changeTabel4//[{'indexId':'','districtReviewValue':''}]
                };

                if(ifSave4 == false){
                    return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                }

                var lay1 = layer.load(1, {shade: [0.1, '#fff']});
                $.ajax({
                    url: '/district/insertDistrictReview',
                    type: 'post',
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(data),
                    success: function (res) {
                        layer.close(lay1);
                        if (res.success == false) {
                            return layer.msg(res.msg);
                        }
                        if(res.data && res.data.length>0 ){
                            $.each(diaTableData4,function(k,j){
                                if($.inArray(j.indexId,res.data)>-1){
                                    var redObj = $("#tableCon4 .layui-table-body tbody tr:nth-child("+(k+1)+")");
                                    redObj.find('td[data-field="districtReviewValue"] .layui-select-title input').css('color','red');
                                }
                            });
                            return layer.msg(res.msg);
                        }else{
                            layer.msg('保存成功');
                            getTable2Data();
                            layui.layer.close(index);
                        }
                    },
                    error: function (res) {
                        layer.close(lay1);
                        layui.layer.msg(res.msg);
                    }
                });
            },
            end: function (index) {
                $("#reviewAssess").addClass('layui-hide');
            }
        });
    }

    // 复审评定 查看详情
    function diaTable3Data(rowData) {
        var year = $("#year").val();
        if(!year){
            return layer.msg('请选择年份！');
        }
        $.ajax({
            url: '/district/getm?siteId='+rowData['siteId']+'&year='+year,
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
                var arr = [{field: 'score', width: 88, title: '标准分值'}
                    , {field: 'districtReviewValue', width: 200, title: '区三方办公室复审评定分值',
                        templet: function (d) {
                            var strret = d.districtReviewValue?d.districtReviewValue:'';
                            if(d['red'] == 0){
                                return '<span style="color:red;">'+strret+'</span>';
                            }else{
                                return strret;
                            }
                        }
                    }
                ];
                var cols = list.concat(arr);

                diaTableData3 = res.data;

                table.render({
                    elem: '#diaTable3'
                    , height: $("#reviewAssess").height()
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list,function(k,j){
                            merge(res,j.field,[k,k+1],"tableCon3");
                        });
                        var str = '<tr data-index="'+diaTableData3.length+'">' +
                            '<td align="center" colspan="'+list.length+'">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell laytable-cell-4-score">100</div></td>' +
                            '<td data-field="districtReviewValue"><div class="layui-table-cell">'+rowData['districtReviewValue']+'</div></td></tr>'+
                            '<tr data-index="'+(diaTableData3.length+1)+'"><td colspan="'+cols.length+'">' +
                            '<div class="layui-table-cell">镇（街道）第三方机构测评建议 : '+rowData['streetSuggestion']+'</div></td></tr>';

                        $("#diaTable3").next('.layui-border-box').find('tbody').append(str);
                    }
                    , data: diaTableData3
                    , id: 'diaTable3'
                    , page:false
                    , limit: 1000000
                });
            },
            error: function(res){
                layer.msg(res.statusText);
            }
        });
    }

    //复审评定
    function diaTable4Data(rowData) {
        var year = $("#year").val();
        if(!year){
            return layer.msg('请选择年份！');
        }
        $.ajax({
            url: '/district/getm?siteId='+rowData['siteId']+'&year='+year,
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
                cols.push({field: 'score', width:88,title: '标准分值'});
                cols.push({field: 'districtReviewValue',width: 200, title: '区三方办公室复审评定分值',
                    templet: function (d) {
                        var color = '#000';
                        if(d['red'] == 0){
                            color = 'red';
                        }
                        if(d['valueType'] == 'enum'){
                            var op = '<select name="districtReviewValue" style="color:'+color+';" lay-filter="enumSelect4" data-value="' + d.districtReviewValue + '" lay-verify="required">';
                            var arr = d['enum'].split('/');
                            $.each(arr,function(i,v){
                                op += '<option value="'+v+'">'+v+'</option>';
                            });
                            op += '</select>';
                            return op;
                        }else{
                            var val = d.districtReviewValue?d.districtReviewValue:'';
                            return val+'<span class="number" style="color:'+color+';"></span>';
                        }
                    }
                });

                diaTableData4 = res.data;

                table.render({
                    elem: '#diaTable4'
                    , height: $("#reviewAssess").height()
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list,function(n,m){
                            merge(res,m.field,[n,n+1],"tableCon4");
                        });
                        var str = '<tr data-index="'+diaTableData4.length+'">' +
                            '<td align="center" colspan="'+list.length+'">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell">100</div></td>' +
                            '<td data-field="districtReviewValue"><div class="layui-table-cell">'+rowData['districtReviewValue']+'</div></td></tr>'+
                            '<tr data-index="'+(diaTableData4.length+1)+'"><td colspan="'+cols.length+'"><div class="layui-table-cell">' +
                            '镇（街道）第三方机构测评建议 : '+rowData['streetSuggestion']+'</div></td></tr>';

                        $("#diaTable4").next('.layui-border-box').find('tbody').append(str);

                        layui.each($('#tableCon4 .number'), function (index, item) {
                            $(item).parents('td').attr('data-edit', 'text').removeAttr('data-content');
                            $(item).remove();
                        });

                        layui.each($('#tableCon4 select'), function (index, item) {
                            var elem = $(item);
                            elem.val(elem.data('value')).parents('div.layui-table-cell').css('overflow', 'visible');
                        });
                        form.render('select');
                        layui.each($('#tableCon4 select'), function (index, item) {
                            var elem = $(item);
                            var color = elem.css('color');
                            elem.parents('td').find('.layui-unselect .layui-select-title input').css('color',color);
                        });
                    }
                    , data: diaTableData4
                    , id: 'diaTable4'
                    , page:false
                    , limit: 1000000
                });

                table.on('edit(diaTable4)', function(obj){
                    var rowData = obj.data;
                    ifSave4 = true;

                    if(rowData['valueType'] == 'number'){
                        var numStr = obj.value.replace(/\s+/g,'');
                        if(!numStr.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/)){
                            ifSave4 = false;
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
                            var hasData = false;
                            if(changeTabel4.length>0){
                                $.each(changeTabel4,function(i,v){
                                    if(v.indexId == rowData['indexId']){
                                        hasData = true;
                                        v.districtReviewValue = val;
                                    }
                                });
                            }
                            if(!hasData){
                                changeTabel4.push({
                                    indexId: rowData['indexId'],
                                    districtReviewValue: val
                                });
                            }
                        }else{
                            ifSave4 = false;
                            return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                        }
                    }
                });

            },
            error: function(res){
                layer.msg(res.msg);
            }
        });
    }

    // 复审 监听修改update到表格中
    form.on('select(enumSelect4)', function (data) {
        var redObj = $("#tableCon4 .layui-table-body tbody tr");
        redObj.find('td[data-field="districtReviewValue"] .layui-select-title input').css('color','#000');

        var elem = $(data.elem);
        var trElem = elem.parents('tr');
        // var tableData1 = table.cache['diaTable1'];
        // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
        diaTableData4[trElem.data('index')][elem.attr('name')] = data.value;
        var hasData = false;
        if(changeTabel4.length>0){
            $.each(changeTabel4,function(i,v){
                if(v.indexId == diaTableData4[trElem.data('index')]['indexId']){
                    hasData = true;
                    v.districtReviewValue = data.value;
                }
            });
        }
        if(!hasData){
            changeTabel4.push({
                indexId:diaTableData4[trElem.data('index')]['indexId'],
                districtReviewValue:data.value
            });
        }
    });

});
