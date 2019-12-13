//获取url？号后面的参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

var page = getQueryString('page');
$('.rightContent').addClass('layui-hide');
if (!page) {
    $('#collect').removeClass('layui-hide');
    $('#content .layui-nav-tree li:nth-child(1)').addClass('layui-this');
} else {
    $('#' + page).removeClass('layui-hide');
    $("#content .layui-nav-tree li").removeClass('layui-this');
    $.each($('#content .layui-nav-tree li'), function () {
        if (page == $(this).find('a').attr('data-id')) $(this).addClass('layui-this');
    });
}
var parkData = [];
var tableData1 = [];
var diaTableData1 = [];
var diaTableData2 = [];
var diaTableData3 = [], changeTabel3 = [], ifSave = true;
var tableChecked3 = '';//账号管理 修改选中的行
var tableChecked4 = '';//账号时限管理 修改选中的行
var editor;

layui.use(['form', 'element', 'layer', 'table', 'laydate', 'jquery'], function () {
    var form = layui.form,
        table = layui.table,
        laydate = layui.laydate,
        formSelects = layui.formSelects;
    layer = parent.layer === undefined ? layui.layer : top.layer,
        element = layui.element;
    $ = layui.jquery;

    //监听左侧导航点击
    element.on('nav(progress)', function (elem) {
        var idx = elem.attr('data-id');
        $('.rightContent').addClass('layui-hide');
        $('#' + idx).removeClass('layui-hide');
        // window.location.href = '?page=' + idx;
        switch (idx){
            case 'collect':
                getTable1Data();// 汇总
                break;
            case 'recheck':
                getTable2Data();// 复审
                break;
            case 'account':
                getTable3Data();// 账号管理
                getUserRoles();// 所属角色 用户类型 获取
                break;
            case 'accountTimeLimit':
                getTable4Data();// 账号时限管理
                break;
            case 'systemMsg':
                editorCreate();// 系统提示
                break;
            case 'systemLog':
                getTable5Data();// 系统日志
                break;
            case 'statAnalysis':
                getAnObject();// 统计分析 分析对象 获取
                getIndexData();// 统计分析 指标选择 获取
                break;
            default:
        }
    });

    //汇总 查询
    $('.search1').on('click', function () {
        getTable1Data();
    });
    //汇总 授予称号
    $('.dub').on('click', function () {
        var checkStatus = table.checkStatus('table1'); //idTest 即为基础参数 id 对应的值
        var checkDatas = [];
        $.each(checkStatus.data, function (n, m) {
            if (m) {
                checkDatas.push(m);
            }
        });

        if (checkDatas.length == 0) return layer.msg('请选择要授予称号的申报单位');
        dubbed(checkDatas);
    });

    //复审 查询
    $('.search2').on('click', function () {
        getTable2Data();
    });

    //账号管理 新增
    $('.add3').on('click', function () {
        addAccount('增加账号');
    });
    //账号管理 修改
    $('.modify3').on('click', function () {
        if (tableChecked3) {
            addAccount('修改账号',tableChecked3);
        } else {
            return layer.msg('请选择要修改的账号');
        }
    });
    //账号管理 删除
    $('.delete3').on('click', function () {
        if (tableChecked3) {
            deleteAccount(tableChecked3);
        } else {
            return layer.msg('请选择要删除的账号');
        }
    });
    //账号管理 查询
    $('.search3').on('click', function () {
        getTable3Data();
    });

    //账号时限管理 查询
    $('.search4').on('click', function () {
        getTable4Data();
    });
    //账号时限管理 修改
    $('.modify').on('click', function () {
        if (tableChecked4) {
            modify(tableChecked4);
        } else {
            return layer.msg('请选择要修改的角色信息');
        }
    });

    // 系统提示 保存
    $('.systemMsgSave').on('click', function () {
        systemMsgSave();
    });

    // 系统日志
    $('.search5').on('click', function () {
        getTable5Data();
    });

    // 复审年份
    laydate.render({
        elem: '#year'
        , type: 'year'
        , value: new Date()
    });

    // 系统日志 昨天0点至明天0点，时间格式yyyy-MM-dd HH:mm:ss
    var nowTime = new Date();
    var time2 = formateDate(Date.parse(nowTime) - 24 * 60 * 60 * 1000);
    var time1 = formateDate(Date.parse(nowTime) + 24 * 60 * 60 * 1000);
    laydate.render({
        elem: '#startTime'
        , type: 'datetime'
        , value: time2
    });
    laydate.render({
        elem: '#endTime'
        , type: 'datetime'
        , value: time1
    });

    $('#collect').removeClass('layui-hide');
    $('#content .layui-nav-tree li:nth-child(1)').addClass('layui-this');

    //所属镇（街道） 获取
    $.get('/user/getSysStreet', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        var str = '<option value=""></option>';
        if (res.data.length > 0) {
            $.each(res.data, function (i, v) {
                str += '<option value="' + v.streetId + '">' + v.streetName + '</option>';
            });
        }
        getStreet('collect',str);
        getStreet('recheck',str);
        getStreet('addAccount',str);//增加账号
    });
    //所属镇工业园  获取
    $.get('/user/getSysIndustrialPark', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        if (res.data.length > 0) {
            parkData = res.data;
        }
    });

    getTable1Data();// 汇总

    //所属镇（街道） 获取
    function getStreet(filter,str){
        if (filter == "collect") {
            $('#streetId').html(str);
            form.render('select', 'collect');

            form.on('select(streetId)', function (data) {
                getPark(data.value, filter);
            });

            getPark($('#streetId').val(), filter);
        } else if(filter == "recheck"){
            $('#streetId2').html(str);
            form.render('select', 'recheck');

            form.on('select(streetId2)', function (data) {
                getPark(data.value, filter);
            });

            getPark($('#streetId2').val(), filter);
        } else if(filter == "addAccount"){
            $('#accountTown').html(str);
            form.render('select', 'addAccount');
        }
    }

    //所属镇工业园  获取
    function getPark(val, filter) {
        var str = '<option value="" data-streetId=""></option>';
        if (parkData.length > 0) {
            $.each(parkData, function (i, v) {
                if (val) {
                    if (val == v.streetId) {
                        str += '<option value="' + v.parkId + '" data-streetId="' + v.streetId + '">' + v.parkName + '</option>';
                    }
                } else {
                    str += '<option value="' + v.parkId + '" data-streetId="' + v.streetId + '">' + v.parkName + '</option>';
                }
            });
        }
        if (filter == "collect") {
            $("#parkId").html(str);
            form.render('select', 'collect');
            //选择园区获取所属镇
            form.on('select(parkId)', function (data) {
                var val = $(data.elem).find('option:selected').attr('data-streetId');
                $("#streetId").val(val);
                form.render('select', 'collect');
            });
        } else {
            $("#parkId2").html(str);
            form.render('select', 'recheck');

            form.on('select(parkId2)', function (data) {
                var val = $(data.elem).find('option:selected').attr('data-streetId');
                $("#streetId2").val(val);
                form.render('select', 'recheck');
            });
        }
    }

    // 所属角色 用户类型 获取
    function getUserRoles(){
        $.get('/user/getUserRoles', function (res) {
            if (!res.success) {
                return layer.msg(res.msg);
            }
            var str = '';
            if(res.data.length>0){
                $.each(res.data,function(i,v){
                    if(v.roleId != 1001) str += '<option value="'+v.roleId+'">'+v.roleName+'</option>';
                });
            }
            $("#accountRole").html(str);
            form.render('select','addAccount');
            form.on('select(accountRole)', function (data) {
                if(data.value == 1002){
                    $(".accountTown").removeClass('layui-hide');
                }else{
                    $(".accountTown").val('').addClass('layui-hide');
                }
            });
        });
    }

    // 汇总
    function getTable1Data() {
        table.render({
            elem: '#table1'
            , height: $("#collect .tableContent").height()
            , url: '/districtCommittee/getAllDistrictCommittee'
            , method: 'get'
            , where: {
                streetId: $("#streetId").val(),
                parkId: $("#parkId").val(),
                siteName: $("#businessName").val()
            }
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'siteName', title: '申报单位', align: 'center'}
                , {field: 'streetName', width: 140, title: '所属街道', align: 'center'}
                , {field: 'parkName', minWidth: 200, title: '所属工业园', align: 'center'}
                , {field: 'selfValue', width: 88, title: '自评得分', align: 'center'}
                , {field: 'streetValue', width: 160, title: '镇（街道）测评得分', align: 'center'}
                , {field: 'districtValue', width: 130, title: '区三方评定得分', align: 'center'}
                , {field: 'districtCommitteeTitle', width: 120, title: '授予称号', align: 'center'}
                , {field: 'districtCommitteeTime', width: 164, title: '授予时间', align: 'center'}
                , {field: 'districtCommitteeUserId', width: 120, title: '授予者', align: 'center'}
                , {fixed: 'right', width: 80, title: '操作', toolbar: '#seeDetails', align: 'center'}
            ]]
            , response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                , statusCode: true //规定成功的状态码，默认：0
                , msgName: 'msg' //规定状态信息的字段名称，默认：msg
                , countName: 'count' //规定数据总数的字段名称，默认：count
                , dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , id: 'table1'
            , page: true
            , limit: 17
        });

        //监听行工具事件
        table.on('tool(table1)', function (obj) {
            var rowData = obj.data;
            if (obj.event === 'seeDetails') {
                $("#tableCon1").removeClass('layui-hide');
                $("#tableCon2,#tableCon3").addClass('layui-hide');

                getDialogData1(rowData);
            }
        });
    }

    // 复审
    function getTable2Data() {
        table.render({
            elem: '#table2'
            , height: $("#recheck .tableContent").height()
            , url: '/districtCommittee/getAllReviewDistrictCommittee'
            , method: 'get'
            , where: {
                year: $("#year").val(),
                streetId: $("#streetId2").val(),
                parkId: $("#parkId2").val(),
                siteName: $("#businessName2").val()
            }
            , cols: [[
                {field: 'siteName', width: 120, title: '申报单位', align: 'center'}
                , {field: 'streetName', width: 120, title: '所属街道', align: 'center'}
                , {field: 'parkName', title: '所属工业园', align: 'center'}
                , {field: 'selfValue', width: 88, title: '自评得分', align: 'center'}
                , {field: 'streetReviewValue', width: 190, title: '镇（街道）复审测评得分', align: 'center'}
                , {field: 'districtReviewValue', width: 160, title: '区三方复审评定得分', align: 'center'}
                , {field: 'districtCommitteeTitle', width: 115, title: '授予称号', align: 'center'}
                , {field: 'districtCommitteeTime', width: 164, title: '授予时间', align: 'center'}
                , {field: 'districtCommitteeUserId', width: 120, title: '授予者', align: 'center'}
                , {field: 'year', width: 88, title: '复审年度', align: 'center'}
                , {field: 'reviewValue', width: 120, title: '年度复审得分', align: 'center'}
                , {
                    field: 'reviewResult', width: 120, title: '年度复审结论', align: 'center',
                    templet: function (d) {
                        if (d['reviewResult'] == 0) {
                            return '通过';
                        } else if (d['reviewResult'] == 1) {
                            return '撤销';
                        } else {
                            return '';
                        }
                    }
                }
                , {fixed: 'right', title: '操作', toolbar: '#recheckBtn', width: 80, align: 'center'}
            ]]
            , response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                , statusCode: true //规定成功的状态码，默认：0
                , msgName: 'msg' //规定状态信息的字段名称，默认：msg
                , countName: 'count' //规定数据总数的字段名称，默认：count
                , dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , id: 'table2'
            , page: true
            , limit: 17
        });

        //监听行工具事件
        table.on('tool(table2)', function (obj) {
            var rowData = obj.data;

            if (obj.event === 'seeDetails2') {
                $("#tableCon2").removeClass('layui-hide');
                $("#tableCon1,#tableCon3").addClass('layui-hide');

                getDialogData2(rowData, $("#year").val());
            } else if (obj.event === 'reAssess') {
                $("#tableCon3").removeClass('layui-hide');
                $("#tableCon1,#tableCon2").addClass('layui-hide');

                getDialogData3(rowData, $("#year").val());
            }
        });
    }

    // 账号管理
    function getTable3Data() {
        table.render({
            elem: '#table3'
            , height: 'full-186'
            , url: '/districtCommittee/getSystemUser'
            , method: 'get'
            , where: {
                userId: $("#userID").val(),
                userName: $("#user").val()
            }
            , cols: [[
                {field: 'userId', width: 120, title: '用户ID', align: 'center'}
                , {field: 'userName', width: 140, title: '用户名', align: 'center'}
                , {field: 'roleName', width: 100, title: '所属角色', align: 'center'}
                , {field: 'siteName', width: 190, title: '所属企业', align: 'center'}
                , {field: 'streetName', width: 180, title: '所属镇（街道）', align: 'center'}
                , {field: 'registrationTime', width: 180, title: '注册时间', align: 'center'}
                , {field: 'lastLoginIp', width: 130, title: '上次登录IP', align: 'center'}
                , {field: 'lastLoginTime', width: 180, title: '上次登录时间', align: 'center'}
                , {field: 'memo', title: '备注', align: 'center'}
                , {field: 'createUserId', width: 120, title: '创建者', align: 'center'}
                , {field: 'createTime', width: 180, title: '创建时间', align: 'center'}
                , {field: 'updateUserId', width: 120, title: '修改者', align: 'center'}
                , {field: 'updateTime', width: 180, title: '修改时间', align: 'center'}
            ]]
            , response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                , statusCode: true //规定成功的状态码，默认：0
                , msgName: 'msg' //规定状态信息的字段名称，默认：msg
                , countName: 'count' //规定数据总数的字段名称，默认：count
                , dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , done: function () {
                tableChecked3 = '';
            }
            , id: 'table3'
            , page: true
            , limit: 17
        });

        table.on('row(table3)', function (obj) {
            tableChecked3 = obj.data;
            //标注选中样式
            obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');
        });
    }

    // 账号时限管理
    function getTable4Data() {
        table.render({
            elem: '#table4'
            , height: 'full-186'
            , url: '/districtCommittee/getUserRole'
            , method: 'get'
            , where: {
                roleName: $("#roleName").val()
            }
            , cols: [[
                {field: 'seqId', rowspan: 2, width: 40, title: '', align: 'center'}
                , {field: 'roleId', rowspan: 2, width: 80, title: '角色ID', align: 'center'}
                , {field: 'roleName', rowspan: 2, width: 100, title: '角色名称', align: 'center'}
                , {field: '', colspan: 2, title: '允许使用时间', align: 'center'}
                , {field: 'memo', rowspan: 2, title: '备注', align: 'center'}
                , {field: 'createUserId', rowspan: 2, width: 120, title: '创建者', align: 'center'}
                , {field: 'createTime', rowspan: 2, width: 175, title: '创建时间', align: 'center'}
                , {field: 'updateUserId', rowspan: 2, width: 120, title: '修改者', align: 'center'}
                , {field: 'updateTime', rowspan: 2, width: 175, title: '修改时间', align: 'center'}
            ], [
                {field: 'startDate', width: 180, title: '开始日期', align: 'center'}
                , {field: 'endDate', width: 180, title: '结束日期', align: 'center'}
            ]]
            , response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                , statusCode: true //规定成功的状态码，默认：0
                , msgName: 'msg' //规定状态信息的字段名称，默认：msg
                , countName: 'count' //规定数据总数的字段名称，默认：count
                , dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , done: function () {
                tableChecked4 = '';
            }
            , id: 'table4'
            , page: true
            , limit: 18
        });

        table.on('row(table4)', function (obj) {
            tableChecked4 = obj.data;
            //标注选中样式
            obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');
        });
    }

    // 系统日志
    function getTable5Data() {
        table.render({
            elem: '#table5'
            , height: 'full-186'
            , url: '/districtCommittee/getSysLog'
            , method: 'get'
            , where: {
                operatorId: $("#operatorId").val(),
                beginDate: $("#startTime").val(),
                endDate: $("#endTime").val()
            }
            , cols: [[
                {field: 'operatorId', width: 150, title: '操作者ID', align: 'center'}
                , {field: 'operateTime', width: 170, title: '操作时间', align: 'center'}
                , {field: 'operateIp', width: 140, title: '操作者IP', align: 'center'}
                , {field: 'operateContent', width: 160, title: '操作内容', align: 'center'}
                , {field: 'operateResult', width: 90, title: '操作结果', align: 'center'}
                , {field: 'url', minWidth: 270, title: '请求URL', align: 'center'}
                , {field: 'method', width: 88, title: '请求方式', align: 'center'}
                , {field: 'meunId', width: 90, title: '菜单', align: 'center'}
                , {field: 'funcId', width: 90, title: '功能', align: 'center'}
                , {field: 'memo', title: '备注', align: 'center'}
            ]]
            , response: {
                statusName: 'code' //规定数据状态的字段名称，默认：code
                , statusCode: true //规定成功的状态码，默认：0
                , msgName: 'msg' //规定状态信息的字段名称，默认：msg
                , countName: 'count' //规定数据总数的字段名称，默认：count
                , dataName: 'data' //规定数据列表的字段名称，默认：data
            }
            , id: 'table5'
            , page: true
            , limit: 18
        });
    }

    // 汇总 查看详情 弹框
    function getDialogData1(rowData) {
        var height = $("#content").height();
        $("#committeeDetail").height(height-120);
        layui.layer.open({
            type: 1,
            title: rowData['siteName'] + '详情',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#committeeDetail"),
            btn: ['取消'],
            success: function () {
                $("#committeeDetail").removeClass('layui-hide');
                diaTable1Data(rowData);
            },
            yes: function (index) {
                layui.layer.close(index);
            },
            end: function (index) {
                $("#committeeDetail").addClass('layui-hide');
            }
        });
    }

    // 汇总 查看详情 表格
    function diaTable1Data(rowData) {
        $.ajax({
            url: '/districtCommittee/getm?siteId=' + rowData['siteId']+'&year=',
            type: 'get',
            success: function (res) {
                if (res.code != true) {
                    return layer.msg(res.msg);
                }
                var list = [];
                if (res.count.length > 0) {
                    $.each(res.count, function (i, v) {
                        var w = 150;
                        if(i >= 1){
                            w = 150+100*i;
                        }
                        if(i == (res.count.length-1)){
                            list.push({field: 'index' + (i + 1), minWidth: w, title: v});
                        }else{
                            list.push({field: 'index' + (i + 1), width: w, title: v});
                        }
                    });
                }
                var arr = [{field: 'score', width: 88, title: '标准分值'}
                    , {field: 'selfValue', width: 120, title: '企业自评分值'}
                    , {field: 'streetValue', width: 160, title: '镇（街道）测评分值'}
                    , {field: 'districtValue', width: 130, title: '区三方评定分值'}
                    , {field: 'fileName', title:'文件', toolbar: '#downloadBtn1', width:100,align: 'center'}];
                var cols = list.concat(arr);

                diaTableData1 = res.data;

                table.render({
                    elem: '#diaTable1'
                    , height: $("#committeeDetail").height()
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list, function (k, j) {
                            merge(res, j.field, [k, k + 1], "tableCon1");
                        });
                        var str = '<tr data-index="' + diaTableData1.length + '">' +
                            '<td align="center" colspan="' + list.length + '">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell laytable-cell-4-score">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell">' + rowData['selfValue'] + '</div></td>' +
                            '<td data-field="streetValue"><div class="layui-table-cell">' + rowData['streetValue'] + '</div></td>' +
                            '<td data-field="districtValue"><div class="layui-table-cell">' + rowData['districtValue'] + '</div></td>' +
                            '<td data-field="fileName"><div class="layui-table-cell"></div></td></tr>' + '' +
                            '<tr data-index="' + (diaTableData1.length + 1) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">镇（街道）第三方机构测评建议 : ' + rowData['streetSuggestion'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData1.length + 2) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">区三方办公室评定建议 : ' + rowData['districtSuggestion'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData1.length + 3) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">区三方委员会 : ' + rowData['districtCommitteeTitle'] + '</div></td></tr>';

                        $("#diaTable1").next('.layui-border-box').find('tbody').append(str);
                    }
                    , data: diaTableData1
                    , id: 'diaTable1'
                    , page: false
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
            error: function (res) {
                layer.msg(res.statusText);
            }
        });
    }

    // 汇总 授予称号 保存
    function dubbed(checkData) {
        layui.layer.open({
            type: 1,
            title: '授予称号',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['800px', '400px'],
            content: $("#dubbed"),
            btn: ['保存'],
            success: function () {
                $("#dubbed").removeClass('layui-hide');
                var str = '';
                $.each(checkData, function (i, v) {
                    str += '<li>' + v.siteName + '</li>';
                });
                $("#siteList").html(str);

                var op = '';
                $.get('/districtCommittee/getTbSysTitle', function (res) {
                    if (res.success) {
                        $.each(res.data, function (i, v) {
                            op += '<option value="' + v.titleId + '">' + v.titleName + '</option>';
                        });
                        $("#dub").html(op);
                        form.render('select', 'dubbed');
                    }
                });
            },
            yes: function (index) {
                var data = {
                    'tableData': checkData,
                    'titleId': $("#dub").val()
                };
                $.ajax({
                    url: "/districtCommittee/updateDistrictCommitteeTitle",
                    type: "POST",
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    dataType: "json",
                    success: function (res) {
                        if (res.success == false) {
                            return layer.msg(res.msg);
                        }
                        getTable1Data();
                        layer.msg('提交成功！');
                    },
                    error: function (res) {
                        layer.msg(res.msg);
                    }
                });
                layui.layer.close(index);
            },
            end: function (index) {
                $("#dubbed").addClass('layui-hide');
            }
        });
    }

    // 复审 详情 弹框
    function getDialogData2(rowData, year) {
        var height = $("#content").height();
        $("#committeeDetail").height(height-120);
        layui.layer.open({
            type: 1,
            title: '【' + rowData['siteName'] + '】' + year + '年度复审详情',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#committeeDetail"),
            btn: ['取消'],
            success: function () {
                $("#committeeDetail").removeClass('layui-hide');
                diaTable2Data(rowData, year);
            },
            yes: function (index) {
                layui.layer.close(index);
            },
            end: function (index) {
                $("#committeeDetail").addClass('layui-hide');
            }
        });
    }

    // 复审 详情 表格
    function diaTable2Data(rowData, year) {
        $.ajax({
            url: '/districtCommittee/getm1?siteId=' + rowData['siteId'] + '&year=' + year,
            type: 'get',
            success: function (res) {
                if (res.code != true) {
                    return layer.msg(res.msg);
                }
                var list = [];
                if (res.count.length > 0) {
                    $.each(res.count, function (i, v) {
                        var w = 150;
                        if(i >= 1){
                            w = 150+100*i;
                        }
                        if(i == (res.count.length-1)){
                            list.push({field: 'index' + (i + 1), minWidth: w, title: v});
                        }else{
                            list.push({field: 'index' + (i + 1), width: w, title: v});
                        }
                    });
                }
                var arr = [{field: 'score', width: 88, title: '标准分值'}
                    , {field: 'selfValue', width: 120, title: '企业自评分值'}
                    , {field: 'streetReviewValue', width: 190, title: '镇（街道）复审测评分值'}
                    , {field: 'districtReviewValue', width: 200, title: '区三方办公室复审评定分值'}
                    , {
                        field: 'reviewValue', width: 230, title: '区三方委员会年度复审评定分值',
                        templet: function (d) {
                            return d.reviewValue ? d.reviewValue : '';
                        }
                    }
                ];
                var cols = list.concat(arr);

                diaTableData2 = res.data;

                table.render({
                    elem: '#diaTable2'
                    , height: $("#committeeDetail").height()
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list, function (k, j) {
                            merge(res, j.field, [k, k + 1], "tableCon2");
                        });
                        var str = '<tr data-index="' + diaTableData2.length + '">' +
                            '<td align="center" colspan="' + list.length + '">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell laytable-cell-4-score">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell">' + rowData['selfValue'] + '</div></td>' +
                            '<td data-field="streetReviewValue"><div class="layui-table-cell">' + rowData['streetReviewValue'] + '</div></td>' +
                            '<td data-field="districtReviewValue"><div class="layui-table-cell">' + rowData['districtReviewValue'] + '</div></td>' +
                            '<td data-field="reviewValue"><div class="layui-table-cell">' + rowData['reviewValue'] + '</div></td>' +
                            '</tr>' +
                            '<tr data-index="' + (diaTableData2.length + 1) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">镇（街道）第三方机构测评建议 : ' + rowData['streetSuggestion'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData2.length + 2) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">区三方办公室评定建议 : ' + rowData['districtSuggestion'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData2.length + 3) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">区三方委员会 : ' + rowData['districtCommitteeTitle'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData2.length + 4) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell layui-form review2" lay-filter="review2">年度复审结论 : ' +
                            '<div class="reviewCon2">' +
                            '<select name="" id="review2" disabled>' +
                            '<option value="0">通过</option>' +
                            '<option value="1">撤销</option></select>' +
                            '</div></div></td></tr>';

                        $("#diaTable2").next('.layui-border-box').find('tbody').append(str);

                        $("#review2").val(rowData['reviewResult']);
                        form.render('select', 'review2');
                    }
                    , data: diaTableData2
                    , id: 'diaTable2'
                    , page: false
                    , limit: 1000000
                });
            },
            error: function (res) {
                layer.msg(res.statusText);
            }
        });
    }

    // 复审 复审 弹框
    function getDialogData3(rowData, year) {
        var height = $("#content").height();
        $("#committeeDetail").height(height-120);
        layui.layer.open({
            type: 1,
            title: '【' + rowData['siteName'] + '】' + year + '年度复审',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['1000px', height+'px'],
            content: $("#committeeDetail"),
            btn: ['保存'],
            success: function () {
                $("#committeeDetail").removeClass('layui-hide');
                changeTabel3 = [];
                diaTable3Data(rowData, year);
            },
            yes: function (index) {
                var data = {
                    "year": year,
                    "siteId": rowData['siteId'],
                    'tableData': changeTabel3,
                    'reviewResult': $("#review3").val()
                };

                if (ifSave == false) {
                    return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                }

                save3(data, index);
            },
            end: function () {
                $("#committeeDetail").addClass('layui-hide');
            }
        });
    }

    function save3(data, index) {
        var lay1 = layer.load(1, {shade: [0.1, '#fff']});
        $.ajax({
            url: '/districtCommittee/replaceReviewDistrictCommittee',
            type: 'post',
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(data),
            success: function (res) {
                layer.close(lay1);
                if (res.success == false) {
                    if (res.data.length > 0) {
                        $.each(diaTableData3, function (k, j) {
                            if ($.inArray(j.indexId, res.data) > -1) {
                                var redObj = $("#tableCon3 .layui-table-body tbody tr:nth-child(" + (k + 1) + ")");
                                redObj.find('td[data-field="reviewValue"] .layui-select-title input').css('color', 'red');
                            }
                        });

                        return layer.confirm('满足 “指标.sheet”中一票否决条件列设置的条件，则年度复审结论的值固定为撤销,是否继续保存？', {
                            icon: 3,
                            title: '提示信息'
                        }, function (index2) {
                            data['reviewResult'] = 1;
                            save3(data, index);
                        });
                    } else {
                        return layer.msg(res.msg);
                    }
                }
                layer.msg('保存成功');
                getTable2Data();
                layui.layer.close(index);
            },
            error: function (res) {
                layer.close(lay1);
                layer.msg(res.msg);
            }
        });
    }

    // 复审 复审 表格
    function diaTable3Data(rowData, year) {
        $.ajax({
            url: '/districtCommittee/getm1?siteId=' + rowData['siteId'] + "&year=" + year,
            type: 'get',
            success: function (res) {
                if (res.code != true) {
                    return layer.msg(res.msg);
                }
                var cols = [], list = [];
                if (res.count.length > 0) {
                    $.each(res.count, function (i, v) {
                        var w = 150;
                        if(i >= 1){
                            w = 150+100*i;
                        }
                        if(i == (res.count.length-1)){
                            cols.push({field: 'index' + (i + 1), minWidth: w, title: v});
                            list.push({field: 'index' + (i + 1), minWidth: w, title: v});
                        }else{
                            cols.push({field: 'index' + (i + 1), width: w, title: v});
                            list.push({field: 'index' + (i + 1), width: w, title: v});
                        }
                    });
                }

                cols.push({field: 'score', width: 88, title: '标准分值'});
                cols.push({field: 'selfValue', width: 115, title: '企业自评分值'});
                cols.push({field: 'streetReviewValue', width: 190, title: '镇（街道）复审测评分值'});
                cols.push({field: 'districtReviewValue', width: 200, title: '区三方办公室复审评定分值'});
                cols.push({
                    field: 'reviewValue', width: 230, title: '区三方委员会年度复审评定分值',
                    templet: function (d) {
                        if (d['valueType'] == 'enum') {
                            var op = '<select name="reviewValue" lay-filter="enumSelect" data-value="' + d.reviewValue + '">';
                            var arr = d['enum'].split('/');
                            $.each(arr, function (i, v) {
                                op += '<option value="' + v + '">' + v + '</option>';
                            });
                            op += '</select>';
                            return op;
                        } else {
                            var val = d.reviewValue ? d.reviewValue : '';
                            return val + '<span class="number"></span>';
                        }
                    }
                });

                diaTableData3 = res.data;

                table.render({
                    elem: '#diaTable3'
                    , height: $("#committeeDetail").height() //高度最大化减去差值
                    , cols: [cols]
                    , done: function (res, curr, count) {
                        $.each(list, function (n, m) {
                            merge(res, m.field, [n, n + 1], "tableCon3");
                        });
                        var str = '<tr data-index="' + diaTableData3.length + '">' +
                            '<td align="center" colspan="' + list.length + '">合计得分</td>' +
                            '<td data-field="score"><div class="layui-table-cell laytable-cell-4-score">100</div></td>' +
                            '<td data-field="selfValue"><div class="layui-table-cell">' + rowData['selfValue'] + '</div></td>' +
                            '<td data-field="streetReviewValue"><div class="layui-table-cell">' + rowData['streetReviewValue'] + '</div></td>' +
                            '<td data-field="districtReviewValue"><div class="layui-table-cell">' + rowData['districtReviewValue'] + '</div></td>' +
                            '<td data-field="reviewValue"><div class="layui-table-cell">' + rowData['reviewValue'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData3.length + 1) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">镇（街道）第三方机构测评建议 : ' + rowData['streetSuggestion'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData3.length + 2) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">区三方办公室评定建议 : ' + rowData['districtSuggestion'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData3.length + 3) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell">区三方委员会 : ' + rowData['districtCommitteeTitle'] + '</div></td></tr>' +
                            '<tr data-index="' + (diaTableData3.length + 4) + '"><td colspan="' + cols.length + '"><div class="layui-table-cell review3 layui-form" lay-filter="review3">年度复审结论 : ' +
                            '<div class="reviewCon3">' +
                            '<select name="" id="review3">' +
                            '<option value="0">通过</option>' +
                            '<option value="1">撤销</option></select>' +
                            '</div></div></td></tr>';

                        $("#diaTable3").next('.layui-border-box').find('tbody').append(str);

                        layui.each($('#tableCon3 .number'), function (index, item) {
                            $(item).parents('td').attr('data-edit', 'text').removeAttr('data-content');
                            $(item).remove();
                        });

                        layui.each($('#tableCon3 select'), function (index, item) {
                            var elem = $(item);
                            elem.val(elem.data('value')).parents('div.layui-table-cell').css('overflow', 'visible');
                        });
                        form.render('select');

                        $("#review3").val('0');
                        form.render('select', 'review3');
                    }
                    , data: diaTableData3
                    , id: 'diaTable3'
                    , page: false
                    , limit: 1000000
                });

                table.on('edit(diaTable3)', function (obj) {
                    var rowData = obj.data;
                    ifSave = true;

                    if (rowData['valueType'] == 'number') {
                        var numStr = obj.value.replace(/\s+/g, '');
                        if (!numStr.match(/^(?:[\+\-]?\d+(?:\.\d+)?|\.\d*?)?$/)) {
                            ifSave = false;
                            return layer.msg('请输入正确的数字');
                        } else {
                            if (numStr.match(/^\.\d+$/)) {
                                numStr = 0 + numStr;
                                $(this).val(numStr);
                            }
                            if (numStr.match(/^\.$/)) {
                                numStr = 0;
                                $(this).val(numStr);
                            }
                        }

                        var val = obj.value ? numStr : '';
                        var min = parseFloat(rowData.min);
                        var max = parseFloat(rowData.max);
                        if ((val <= max && val >= min) || !val) {
                            var hasData = false;
                            if (changeTabel3.length > 0) {
                                $.each(changeTabel3, function (i, v) {
                                    if (v.indexId == rowData['indexId']) {
                                        hasData = true;
                                        v.reviewValue = val
                                    }
                                });
                            }
                            if (!hasData) {
                                changeTabel3.push({
                                    indexId: rowData['indexId']
                                    , reviewValue: val
                                    , districtReviewValue: rowData['districtReviewValue']
                                    , streetReviewValue: rowData['streetReviewValue']
                                    , selfValue: rowData['selfValue']
                                });
                            }
                        } else {
                            ifSave = false;
                            return layer.msg('输入的值不满足 “指标.sheet”中分值范围列的要求');
                        }
                    }
                });

            },
            error: function (res) {
                layer.msg(res.msg);
            }
        });
    }

    //账号管理 新增/修改
    function addAccount(title,tData){
        layui.layer.open({
            type: 1,
            title: title,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['500px', '600px'],
            content: $("#addAccount"),
            btn: ['确定','取消'],
            success: function () {
                $("#addAccount").removeClass('layui-hide');
                if(tData){
                    $("#accountId").val(tData['userId']);
                    $("#accountName").val(tData['userName']);
                    $("#accountPwd").val(tData['userPwd']);
                    $("#accountRole").val(tData['roleId']);
                    $("#accountTown").val(tData['streetId']);
                    $("#accountMemo").val(tData['memo']);
                    form.render('select', 'addAccount');
                    if(tData['roleId'] == 1002){
                        $(".accountTown").removeClass('layui-hide');
                    }else{
                        $(".accountTown").val('').addClass('layui-hide');
                    }

                    $("#accountId").addClass('layui-disabled').attr('disabled',true);
                }else{
                    $("#accountId").removeClass('layui-disabled').attr('disabled',false);
                }
            },
            yes: function (index) {
                var pwd = $("#accountPwd").val();
                var confirmPwd = $("#accountConfirmPwd").val();

                if(!pwd) return layer.msg('请输入密码');
                if(!confirmPwd) return layer.msg('请输入确认密码');

                if(pwd != confirmPwd){
                    layer.msg('确认密码和密码不一致，请重新输入。');
                    return false;
                }

                var data = {
                    'userId': $("#accountId").val(),
                    'userName': $("#accountName").val(),
                    'userPwd': $("#accountPwd").val(),
                    'roleId': $("#accountRole").val(),
                    'memo': $("#accountMemo").val()
                };
                if(data.roleId == 1002){
                    data['streetId'] = $("#accountTown").val();
                }

                if(!data.userId) return layer.msg('请输入用户ID');
                if(!data.userName) return layer.msg('请输入用户名');
                if(!data.streetId && data.roleId == 1002) return layer.msg('请选择所属镇(街道)');

                var url = '';
                if(tData){
                    url = '/user/updateUser';
                    data['userId'] = tData.userId;
                }else{
                    url = '/user/insertUser';
                }
                $.ajax({
                    url: url,
                    type: "post",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(data),
                    success: function (res) {
                        if (res.success == false) {
                            return layer.msg(res.msg);
                        }
                        getTable3Data();
                        layer.msg('提交成功！');
                        layui.layer.close(index);
                    },
                    error: function (res) {
                        layer.msg(res.msg);
                    }
                });
            },
            end: function (index) {
                $("#addAccount").addClass('layui-hide');
                $("#accountId").val('');
                $("#accountName").val('');
                $("#accountPwd").val('');
                $("#accountConfirmPwd").val('');
                // $("#accountRole").val('');
                // $("#accountTown").val('');
                $("#accountMemo").val('');
            }
        });
    }

    //账号管理 删除
    function deleteAccount(tableChecked3){
        layer.confirm('确定要删除选中的账号？', {
            icon: 3,
            title: '提示信息'
        }, function (index) {
            var data = {
                'userId': tableChecked3.userId
            };
            var lay1 = layer.load(1, {shade: [0.1, '#fff']});
            $.ajax({
                url: '/user/deleteUser',
                type: "DELETE",
                contentType:"application/json",//设置请求参数类型为json字符串
                dataType: "json",
                data: JSON.stringify(data),
                success: function (res) {
                    layer.close(lay1);
                    if (res.code > 0) {
                        return layer.msg(res.msg);
                    }
                    getTable3Data();
                    layer.msg(res.msg);
                    layer.close(index);
                },
                error: function (res) {
                    layer.close(lay1);
                    layer.msg(res.statusText);
                }
            })
        });
    }

    // 账号时限管理 修改
    function modify(checkData) {
        layui.layer.open({
            type: 1,
            title: checkData['roleName'] + '修改',
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['500px', '350px'],
            content: $("#timeLimitChange"),
            btn: ['保存'],
            success: function () {
                $("#timeLimitChange").removeClass('layui-hide');
                $("#memo").val(checkData['memo']);

                laydate.render({
                    elem: '#startDate'
                    , type: 'date'
                    , value: checkData['startDate']
                });
                laydate.render({
                    elem: '#endDate'
                    , type: 'date'
                    , value: checkData['endDate']
                });
            },
            yes: function (index) {
                var data = {
                    'roleId': checkData['roleId'],
                    'startDate': $("#startDate").val(),
                    'endDate': $("#endDate").val(),
                    'memo': $("#memo").val()
                };
                $.ajax({
                    url: "/districtCommittee/updateUserRole",
                    type: "get",
                    data: data,
                    success: function (res) {
                        if (res.success == false) {
                            return layer.msg(res.msg);
                        }
                        getTable4Data();
                        layer.msg('保存成功！');
                        layui.layer.close(index);
                    },
                    error: function (res) {
                        layer.msg(res.msg);
                    }
                });
            },
            end: function (index) {
                $("#timeLimitChange").addClass('layui-hide');
            }
        });
    }

    // 保存系统提示
    function systemMsgSave() {
        var content = editor.txt.html(); // 读取 html
        var content2 = editor.txt.text(); // 读取 text
        console.log(content);
        console.log(content2);
        $.ajax({
            url: "/districtCommittee/insertSysPoints",
            type: "post",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify({"points": content}),
            success: function (res) {
                if (res.success == false) {
                    return layer.msg(res.msg);
                }
                layer.msg('保存成功！');
            },
            error: function (res) {
                layer.msg(res.msg);
            }
        });
    }

    function editorCreate() {
        var E = window.wangEditor;
        editor = new E('#editor');
        // 或者 var editor = new E( document.getElementById('editor') )
        editor.create();

        // 自定义菜单配置
        editor.customConfig.menus = [
            'head',  // 标题
            'bold',  // 粗体
            'fontSize',  // 字号
            'fontName',  // 字体
            'italic',  // 斜体
            'underline',  // 下划线
            'strikeThrough',  // 删除线
            'foreColor',  // 文字颜色
            'backColor',  // 背景颜色
            'link',  // 插入链接
            'list',  // 列表
            'justify',  // 对齐方式
            'quote',  // 引用
            'emoticon',  // 表情
            'image',  // 插入图片
            'table',  // 表格
            'video',  // 插入视频
            'code',  // 插入代码
            'undo',  // 撤销
            'redo'  // 重复
        ];

        $.get('/districtCommittee/getSysPoints', function (res) {
            if (res.success == false) {
                return layer.msg(res.msg);
            }
            editor.txt.html(res.data['points']);
        });
    }

    function formateDate(timestamp) {
        var date = new Date(timestamp);
        var y = date.getFullYear();
        var M = date.getMonth() + 1;
        M = M < 10 ? ('0' + M) : M;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;

        return y + '-' + M + '-' + d + ' 00:00:00';
    }

    function merge(res, field, index, obj) {
        var data = res.data;
        var mergeIndex = 0;//定位需要添加合并属性的行数
        var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
        var columsName = [field];//需要合并的列名称
        var columsIndex = index;//需要合并的列索引值

        for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
            var trArr = $("#" + obj).find(".layui-table-body>.layui-table").find("tr");//所有行
            for (var i = 1; i < res.data.length; i++) { //这里循环表格当前的数据

                var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]);//获取当前行的当前列
                var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]);//获取相同列的第一列

                if (data[i][columsName[k]] === data[i - 1][columsName[k]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () {//相同列的第一列增加rowspan属性
                        $(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () {//当前行隐藏
                        $(this).css("display", "none");
                    });
                } else {
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
        var redObj = $("#tableCon3 .layui-table-body tbody tr");
        redObj.find('td[data-field="reviewValue"] .layui-select-title input').css('color', '#000');

        var elem = $(data.elem);
        var trElem = elem.parents('tr');
        // var tableData1 = table.cache['diaTable1'];
        // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
        diaTableData3[trElem.data('index')][elem.attr('name')] = data.value;
        var hasData = false;
        if (changeTabel3.length > 0) {
            $.each(changeTabel3, function (i, v) {
                if (v.indexId == diaTableData3[trElem.data('index')]['indexId']) {
                    hasData = true;
                    v.reviewValue = data.value;
                }
            });
        }
        if (!hasData) {
            changeTabel3.push({
                indexId: diaTableData3[trElem.data('index')]['indexId']
                , reviewValue: data.value
                , districtReviewValue: diaTableData3[trElem.data('index')]['districtReviewValue']
                , streetReviewValue: diaTableData3[trElem.data('index')]['streetReviewValue']
                , selfValue: diaTableData3[trElem.data('index')]['selfValue']
            });
        }
    });

    // ------------------------------------------------------- 统计分析 ----------------------------------------
    var chart1, chartData1 = {};//统计分析
    var chart2, chartData2 = [], pie, pieData = [];
    var chart3, chartData3 = {};
    var chart4, chartData4 = {};
    var analysis = 1;//总得分分布、总得分分组统计、指标得分对比1、指标得分对比2
    var treeObj, zNodesObj = [];// 分析对象
    var treeSite, zNodesSite = [];// 企业性质
    var treeIndusty, zNodesIndusty = [];// 所属行业
    var tree1, zNodes1 = [];// 指标选择 得分分布 分组统计
    var tree2, zNodes2 = [];// 指标选择 得分对比1
    var tree3, zNodes3 = [];// 指标选择 得分对比2

    // 年份
    laydate.render({
        elem: '#allYear'
        , type: 'year'
        , value: new Date()
    });

    $("#chart1,.tableAn").show();
    $("#chart2,#pie,#chart3,#chart4").hide();

    // 分值类型
    form.render('select', 'analysis');
    form.on('select(valueType)', function (data) {
        if (data.value == 4) {
            $(".anYear").removeClass('layui-hide');
        } else {
            $(".anYear").addClass('layui-hide');
        }
        laydate.render({
            elem: '#allYear'
            , type: 'year'
            , value: new Date()
        });
    });

    // 企业性质 获取
    $.get('/user/getSysSiteType', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }

        zNodesSite = res.data;
        createSite();
    });
    function createSite(){
        var setting = {
            view: {
                addDiyDom: null,//用于在节点上固定显示用户自定义控件
                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: true//设置是否允许同时选中多个节点。
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: {"Y": "ps", "N": "s"}
            },
            data: {
                keep: {
                    leaf: false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent: false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key: {
                    children: "children",//节点数据中保存子节点数据的属性名称。
                    name: "typeName",//节点数据保存节点名称的属性名称。
                    title: "typeId"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "typeId",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                onCheck: function (event, treeId, treeNode) {
                    var val = treeSite.getCheckedNodes();
                    var item1Name = [];
                    if (val.length > 0) {
                        $.each(val, function (m, n) {
                            item1Name.push(n.typeName);
                        });
                        $(".siteSelect input.layui-unselect").val(item1Name.join(','));
                    } else {
                        $(".siteSelect input.layui-unselect").val('');
                    }
                },
                onClick: function (event, treeId, treeNode) {
                    treeSite.checkNode(treeNode);

                    var val = treeSite.getCheckedNodes();
                    var item1Name = [];
                    if (val.length > 0) {
                        $.each(val, function (m, n) {
                            item1Name.push(n.typeName);
                        });
                        $(".siteSelect input.layui-unselect").val(item1Name.join(','));
                    } else {
                        $(".siteSelect input.layui-unselect").val('');
                    }
                }
            }
        };

        $(".siteSelect .layui-anim").html(
            '<div class="layui-form checkAllForm">' +
            '    <input class="checkAll" type="checkbox" name="" title="全选" lay-filter="checkAll" checked>'+
            '</div>'
        ).append(
            '<div class="grid-parent calculation_tree1">' +
            '     <ul class="ztree" id="siteTree"></ul>' +
            '</div>'
        );

        treeSite = $.fn.zTree.init($("#siteTree"), setting, zNodesSite);
        treeSite.expandAll(true);
        treeSite.checkAllNodes(true);
        if (zNodesSite.length == 0) {
            $(".siteSelect input.layui-unselect").val('');
        }else{
            var val = treeSite.getCheckedNodes();
            var item1Name = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    item1Name.push(n.typeName);
                });
                $(".siteSelect input.layui-unselect").val(item1Name.join(','));
            } else {
                $(".siteSelect input.layui-unselect").val('');
            }
        }
        //复选框不消失
        $(".siteSelect").on("click", ".layui-select-title", function (e) {
            $(".layui-form-select").not($(this).parents(".layui-form-select")).removeClass("layui-form-selected");
            $(this).parents(".siteSelect").toggleClass("layui-form-selected");

            var val = treeSite.getCheckedNodes();
            var itemName = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    itemName.push(n.typeName);
                });
                $(".siteSelect input.layui-unselect").val(itemName.join(','));
            }else{
                $(".siteSelect input.layui-unselect").val('');
            }
            layui.stope(e);
        }).on("click", "dl", function (e) {
            layui.stope(e);
        });
        $(document).not($(".siteSelect .layui-anim")).on("click", function (e) {
            $(".siteSelect").removeClass("layui-form-selected");
        });

        //全选
        form.render('checkbox');
        form.on('checkbox(checkAll)', function(data){
            if(data.elem.checked){
                treeSite.checkAllNodes(true);

                var val = treeSite.getCheckedNodes();
                var item1Name = [];
                $.each(val, function (m, n) {
                    item1Name.push(n.typeName);
                });
                $(".siteSelect input.layui-unselect").val(item1Name.join(','));
            }else{
                treeSite.checkAllNodes(false);
                $(".siteSelect input.layui-unselect").val('');
            }
        });
    }

    // 所属行业 获取
    $.get('/user/getSysIndustyType', function (res) {
        if (!res.success) {
            return layer.msg(res.msg);
        }
        zNodesIndusty = res.data;
        createIndusty();
    });
    function createIndusty(){
        var setting = {
            view: {
                addDiyDom: null,//用于在节点上固定显示用户自定义控件
                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: true//设置是否允许同时选中多个节点。
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: {"Y": "ps", "N": "s"}
            },
            data: {
                keep: {
                    leaf: false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent: false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key: {
                    children: "children",//节点数据中保存子节点数据的属性名称。
                    name: "typeName",//节点数据保存节点名称的属性名称。
                    title: "typeId"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "typeId",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                onCheck: function (event, treeId, treeNode) {
                    var val = treeIndusty.getCheckedNodes();
                    var item1Name = [];
                    if (val.length > 0) {
                        $.each(val, function (m, n) {
                            item1Name.push(n.typeName);
                        });
                        $(".industrySelect input.layui-unselect").val(item1Name.join(','));
                    } else {
                        $(".industrySelect input.layui-unselect").val('');
                    }
                },
                onClick: function (event, treeId, treeNode) {
                    treeIndusty.checkNode(treeNode);

                    var val = treeIndusty.getCheckedNodes();
                    var item1Name = [];
                    if (val.length > 0) {
                        $.each(val, function (m, n) {
                            item1Name.push(n.typeName);
                        });
                        $(".industrySelect input.layui-unselect").val(item1Name.join(','));
                    } else {
                        $(".industrySelect input.layui-unselect").val('');
                    }
                }
            }
        };

        $(".industrySelect .layui-anim").html(
            '<div class="layui-form checkAllForm2">' +
            '    <input class="checkAll" type="checkbox" name="" title="全选" lay-filter="checkAll2" checked>'+
            '</div>'
        ).append(
            '<div class="grid-parent calculation_tree1">' +
            '    <ul class="ztree" id="industryTree"></ul>' +
            '</div>'
        );

        treeIndusty = $.fn.zTree.init($("#industryTree"), setting, zNodesIndusty);
        treeIndusty.expandAll(true);
        treeIndusty.checkAllNodes(true);
        if (zNodesIndusty.length == 0) {
            $(".industrySelect input.layui-unselect").val('');
        }else{
            var val = treeIndusty.getCheckedNodes();
            var item1Name = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    item1Name.push(n.typeName);
                });
                $(".industrySelect input.layui-unselect").val(item1Name.join(','));
            } else {
                $(".industrySelect input.layui-unselect").val('');
            }
        }
        //复选框不消失
        $(".industrySelect").on("click", ".layui-select-title", function (e) {
            $(".layui-form-select").not($(this).parents(".layui-form-select")).removeClass("layui-form-selected");
            $(this).parents(".industrySelect").toggleClass("layui-form-selected");

            var val = treeIndusty.getCheckedNodes();
            var itemName = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    itemName.push(n.typeName);
                });
                $(".industrySelect input.layui-unselect").val(itemName.join(','));
            }else{
                $(".industrySelect input.layui-unselect").val('');
            }
            layui.stope(e);
        }).on("click", "dl", function (e) {
            layui.stope(e);
        });
        $(document).not($(".industrySelect .layui-anim")).on("click", function (e) {
            $(".industrySelect").removeClass("layui-form-selected");
        });

        //全选
        form.render('checkbox');
        form.on('checkbox(checkAll2)', function(data){
            if(data.elem.checked){
                treeIndusty.checkAllNodes(true);

                var val = treeIndusty.getCheckedNodes();
                var item1Name = [];
                $.each(val, function (m, n) {
                    item1Name.push(n.typeName);
                });
                $(".industrySelect input.layui-unselect").val(item1Name.join(','));
            }else{
                treeIndusty.checkAllNodes(false);
                $(".industrySelect input.layui-unselect").val('');
            }
        });
    }

    // 分析对象 全区、具体镇(街道)、具体工业园 3级树状结构展示（多选、并且级联选择）
    function getAnObject() {
        $.get('/Analysis/getStreetAndIndustry', function (res) {
            if (res.success != true) {
                return layer.msg(res.msg);
            }
            zNodesObj = res.data;
            createObjTree();
        });
    }

    function createObjTree() {
        var setting = {
            view: {
                addDiyDom: null,//用于在节点上固定显示用户自定义控件
                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: true//设置是否允许同时选中多个节点。
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: {"Y": "ps", "N": "s"}
            },
            data: {
                keep: {
                    leaf: false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent: false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key: {
                    children: "children",//节点数据中保存子节点数据的属性名称。
                    name: "name",//节点数据保存节点名称的属性名称。
                    title: "value"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "value",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "streetId",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                onCheck: function (event, treeId, treeNode) {
                    var val = treeObj.getCheckedNodes();
                    var item1Name = [];
                    if (val.length > 0) {
                        $.each(val, function (m, n) {
                            item1Name.push(n.name);
                        });
                        $(".objSelect input.layui-unselect").val(item1Name.join(','));
                    } else {
                        $(".objSelect input.layui-unselect").val('');
                    }
                },
                onClick: function (event, treeId, treeNode) {
                    treeObj.checkNode(treeNode);

                    var val = treeObj.getCheckedNodes();
                    var item1Name = [];
                    if (val.length > 0) {
                        $.each(val, function (m, n) {
                            item1Name.push(n.name);
                        });
                        $(".objSelect input.layui-unselect").val(item1Name.join(','));
                    } else {
                        $(".objSelect input.layui-unselect").val('');
                    }
                }
            }
        };

        $(".objSelect .layui-anim").html('').append('<div class="grid-parent calculation_tree1">' +
            '           <ul class="ztree" id="objectTree"></ul>' +
            '     </div>');

        treeObj = $.fn.zTree.init($("#objectTree"), setting, zNodesObj);
        treeObj.expandAll(true);
        if (zNodesObj.length == 0) {
            $(".objSelect input.layui-unselect").val('');
        }
        //复选框不消失
        $(".objSelect").on("click", ".layui-select-title", function (e) {
            $(".layui-form-select").not($(this).parents(".layui-form-select")).removeClass("layui-form-selected");
            $(this).parents(".objSelect").toggleClass("layui-form-selected");

            var val = treeObj.getCheckedNodes();
            var itemName = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    itemName.push(n.name);
                });
                $(".objSelect input.layui-unselect").val(itemName.join(','));
            }else{
                $(".objSelect input.layui-unselect").val('');
            }
            layui.stope(e);
        }).on("click", "dl", function (e) {
            layui.stope(e);
        });
        $(document).not($(".objSelect .layui-anim")).on("click", function (e) {
            $(".objSelect").removeClass("layui-form-selected");
        });
    }

    function getIndexData() {
        $.get('/Analysis/getm', function (res) {
            if (res.success != true) {
                return layer.msg(res.msg);
            }
            zNodes1 = res.data;
            createIndexTree1();
            getChartData1();// 统计分析
        })
    }

    // 指标选择 得分分布 分组统计 获取
    function createIndexTree1() {
        var setting = {
            view: {
                addDiyDom: null,//用于在节点上固定显示用户自定义控件
                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: true//设置是否允许同时选中多个节点。
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: {"Y": "ps", "N": "ps"}
            },
            data: {
                keep: {
                    leaf: false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent: false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key: {
                    children: "mujis",//节点数据中保存子节点数据的属性名称。
                    name: "indexName",//节点数据保存节点名称的属性名称。
                    title: "indexId"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "indexId",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                onCheck: function (event, treeId, treeNode) {
                    clickTree1();
                },
                onClick: function (event, treeId, treeNode) {
                    if(treeNode.checked){
                        tree1.checkNode(treeNode,false,true,false);
                    }else{
                        tree1.checkNode(treeNode,true,true,false);
                    }
                    clickTree1();
                }
            }
        };

        $(".indexSelect .layui-anim").html('').append('<div class="grid-parent calculation_tree2">' +
            '           <ul class="ztree" id="selectTree1"></ul>' +
            '     </div>');

        tree1 = $.fn.zTree.init($("#selectTree1"), setting, zNodes1);
        var nodes = tree1.getNodes();   //默认展开到第二级
        for (var i = 0; i < nodes.length; i++) {
            tree1.expandNode(nodes[i], true, false, true);
        }
        $(".indexSelect input.layui-unselect").val('');

        //复选框不消失
        $(".indexSelect").on("click", ".layui-select-title", function (e) {
            $(".layui-form-select").not($(this).parents(".layui-form-select")).removeClass("layui-form-selected");
            $(this).parents(".indexSelect").toggleClass("layui-form-selected");

            var val = tree1.getCheckedNodes();
            var itemName = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    itemName.push(n.indexName);
                });
                $(".indexSelect input.layui-unselect").val(itemName.join(','));
            }else{
                $(".indexSelect input.layui-unselect").val('');
            }
            layui.stope(e);
        }).on("click", "dl", function (e) {
            layui.stope(e);
        });
        $(document).not($(".indexSelect .layui-anim")).on("click", function (e) {
            $(".indexSelect").removeClass("layui-form-selected");
        });
    }

    function clickTree1() {
        var val = tree1.getCheckedNodes();
        var itemName = [];
        if (val.length > 0) {
            $.each(val, function (m, n) {
                itemName.push(n.indexName);
            });

            $(".indexSelect input.layui-unselect").val(itemName.join(','));
        } else {
            $(".indexSelect input.layui-unselect").val('');
        }
    }

    // 指标选择 得分对比1 获取
    function createIndexTree2() {
        zNodes2 = zNodes1;
        var setting = {
            view: {
                addDiyDom: null,//用于在节点上固定显示用户自定义控件
                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: false//设置是否允许同时选中多个节点。
            },
            data: {
                keep: {
                    leaf: false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent: false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key: {
                    children: "mujis",//节点数据中保存子节点数据的属性名称。
                    name: "indexName",//节点数据保存节点名称的属性名称。
                    title: "indexId"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "indexId",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                beforeClick: function(treeId, treeNode, clickFlag){
                    if(!treeNode.mujis || treeNode.mujis.length==0){
                        layer.msg('叶子节点不能选择');
                        return false;
                    }
                },
                onClick: function (event, treeId, treeNode) {
                    var val = tree2.getSelectedNodes();
                    if (val.length > 0) {
                        $(".indexSelect input.layui-unselect").val(val[0].indexName);
                    } else {
                        $(".indexSelect input.layui-unselect").val('');
                    }
                }
            }
        };

        $(".indexSelect .layui-anim").html('').append('<div class="grid-parent calculation_tree2">' +
            '           <ul class="ztree" id="selectTree2"></ul>' +
            '     </div>');

        tree2 = $.fn.zTree.init($("#selectTree2"), setting, zNodes2);
        var nodes = tree2.getNodes();   //默认展开到第二级
        for (var i = 0; i < nodes.length; i++) {
            tree2.expandNode(nodes[i], true, false, true);
        }
        $(".indexSelect input.layui-unselect").val('');

        //复选框不消失
        $(".indexSelect").on("click", ".layui-select-title", function (e) {
            $(".layui-form-select").not($(this).parents(".layui-form-select")).removeClass("layui-form-selected");
            $(this).parents(".indexSelect").toggleClass("layui-form-selected");

            var val = tree2.getSelectedNodes();
            $(this).find('.layui-unselect').val(val.length>0?val[0].indexName:'');
            layui.stope(e);
        }).on("click", "dl", function (e) {
            layui.stope(e);
        });
        $(document).not($(".indexSelect .layui-anim")).on("click", function (e) {
            $(".indexSelect").removeClass("layui-form-selected");
        });
    }

    // 指标选择 得分对比2 获取
    function createIndexTree3() {
        zNodes3 = zNodes1;
        var setting = {
            view: {
                addDiyDom: null,//用于在节点上固定显示用户自定义控件
                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: true//设置是否允许同时选中多个节点。
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: {"Y": "", "N": ""}
            },
            data: {
                keep: {
                    leaf: false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent: false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key: {
                    children: "mujis",//节点数据中保存子节点数据的属性名称。
                    name: "indexName",//节点数据保存节点名称的属性名称。
                    title: "indexId"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "indexId",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                onCheck: function (event, treeId, treeNode) {
                    checkTree3(treeNode);
                },
                onClick: function (event, treeId, treeNode) {
                    tree3.checkNode(treeNode);
                    checkTree3(treeNode);
                }
            }
        };

        $(".indexSelect .layui-anim").html('').append('<div class="grid-parent calculation_tree2">' +
            '           <ul class="ztree" id="selectTree3"></ul>' +
            '     </div>');

        tree3 = $.fn.zTree.init($("#selectTree3"), setting, zNodes3);
        var nodes = tree3.getNodes();   //默认展开到第二级
        for (var i = 0; i < nodes.length; i++) {
            tree3.expandNode(nodes[i], true, false, true);
        }
        $(".indexSelect input.layui-unselect").val('');

        //复选框不消失
        $(".indexSelect").on("click", ".layui-select-title", function (e) {
            $(".layui-form-select").not($(this).parents(".layui-form-select")).removeClass("layui-form-selected");
            $(this).parents(".indexSelect").toggleClass("layui-form-selected");

            var val = tree3.getCheckedNodes();
            var itemName = [];
            if (val.length > 0) {
                $.each(val, function (m, n) {
                    itemName.push(n.indexName);
                });
                $(".indexSelect input.layui-unselect").val(itemName.join(','));
            }else{
                $(".indexSelect input.layui-unselect").val('');
            }
            layui.stope(e);
        }).on("click", "dl", function (e) {
            layui.stope(e);
        });
        $(document).not($(".indexSelect .layui-anim")).on("click", function (e) {
            $(".indexSelect").removeClass("layui-form-selected");
        });
    }

    function checkTree3(treeNode) {
        var val = tree3.getCheckedNodes();

        var nodes = tree3.getNodes();
        if(treeNode.checked == true){
            if(val.length == 1){//选择一个
                circle(nodes,treeNode.parentId,treeNode.level);
            }
        }else{
            if(val.length == 0){//取消选择全部
                $.each(nodes, function (k, j) {
                    tree3.setChkDisabled(j, false,true,true);
                });
            }
        }

        var itemName = [];
        if (val.length > 0) {
            $.each(val, function (m, n) {
                itemName.push(n.indexName);
            });
            $(".indexSelect input.layui-unselect").val(itemName.join(','));
        } else {
            $(".indexSelect input.layui-unselect").val('');
        }
    }

    function circle(nodes,parent,level){
        $.each(nodes,function(a,b){
            if(b.level != level){
                tree3.setChkDisabled(b, true,false,false);
            }else{
                if(b.parentId != parent){
                    tree3.setChkDisabled(b, true,false,false);
                }
            }
            if(b.mujis) circle(b.mujis,parent,level);
        });
    }

    // 总得分分布、总得分分组统计、指标得分对比1、指标得分对比2
    $('#chartsTitle .layui-nav-item').on('click', function () {
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        var idx = $(this).index() + 1;
        if (idx !== analysis) {
            analysis = idx;
        }

        if (analysis == 1) {// 统计分析
            $("#chart1,.tableAn").show();
            $("#chart2,#pie,#chart3,#chart4").hide();
            $(".itemClass").addClass('layui-hide');

            $("#valueType").val('1');
            form.render('select', 'analysis');
            $(".anYear").addClass('layui-hide');

            createObjTree();
            createIndexTree1();
            createSite();
            createIndusty();
            getChartData1();
        } else if (analysis == 2) {// 分组统计
            $("#chart2,#pie").show();
            $("#chart1,.tableAn,#chart3,#chart4").hide();
            $(".itemClass").removeClass('layui-hide');

            $("#valueType").val('1');
            form.render('select', 'analysis');
            $(".anYear").addClass('layui-hide');

            createObjTree();
            createIndexTree1();
            createSite();
            createIndusty();
            getChartData2();
        } else if (analysis == 3) {// 得分对比1
            $("#chart3").show();
            $("#chart1,.tableAn,#chart2,#pie,#chart4").hide();
            $(".itemClass").addClass('layui-hide');

            $("#valueType").val('1');
            form.render('select', 'analysis');
            $(".anYear").addClass('layui-hide');

            createObjTree();
            createIndexTree2();
            createSite();
            createIndusty();
            getChartData3();
        } else if(analysis == 4){// 得分对比2
            $("#chart4").show();
            $("#chart1,.tableAn,#chart2,#pie,#chart3").hide();
            $(".itemClass").addClass('layui-hide');

            $("#valueType").val('1');
            form.render('select', 'analysis');
            $(".anYear").addClass('layui-hide');

            createObjTree();
            createIndexTree3();
            createSite();
            createIndusty();
            getChartData4();
        }
        return false;
    });

    //统计分析 查询
    $(".search6").on('click', function () {
        if (analysis == 1) {
            getChartData1();
        } else if (analysis == 2) {
            getChartData2();
        } else if(analysis == 3){
            getChartData3();
        } else if(analysis == 4){
            getChartData4();
        }
    });

    // 统计分析 总得分分布
    function getChartData1() {
        var valueType = $('#valueType').val();//分值类型
        var object = treeObj?treeObj.getCheckedNodes():[];//分析对象
        var indexTree = tree1?tree1.getCheckedNodes():[];//指标选择
        var businessType = treeSite?treeSite.getCheckedNodes():[];//企业性质
        var industryChecked = treeIndusty?treeIndusty.getCheckedNodes():[];//所属行业

        var indexIds = [];
        $.each(indexTree,function(i,v){
            indexIds.push(v.indexId);
        });

        var streetId = [],parkId = [];
        $.each(object,function(i,v){
            if(v.level == 0){
                streetId.push(v.value);
            }else{
                parkId.push(v.parkId);
            }
        });

        var siteType = [];
        $.each(businessType,function(i,v){
            siteType.push(v.typeId);
        });

        var industryInclude = [];
        $.each(industryChecked,function(i,v){
            industryInclude.push(v.typeId);
        });

        var data = {
            "flag": valueType, //分值类型
            "streetId": streetId,
            "parkId": parkId,
            "indexIds": indexIds,
            "siteType":siteType, //企业性质
            "industryInclude":industryInclude //所属行业
        };

        if (valueType == 4) {
            var year = $("#allYear").val();
            data['year'] = year;
        }

        var lay1 = layer.load(1, {shade: [0.1, '#fff']});
        $.ajax({
            url: '/Analysis/getAllScore',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                layer.close(lay1);
                if (res.success != true) {
                    $("#chart1").html('');
                    return layer.msg(res.msg);
                }
                if (res.data) {
                    var result = res.data;

                    chartData1 = {xAxis: [], data: [],count:result.count};
                    chartData1.xAxis = result.listX;
                    chartData1.data = result.listY;

                    init1();

                    table.render({
                        elem: '#tableAna'
                        , cols: [[
                            {field: 'count', minWidth: 150, title: '全区申报企业数', align: 'center'}
                            , {field: 'Average', minWidth: 120, title: '平均分数', align: 'center'}
                            , {field: 'ModeNumber', minWidth: 140, title: '分值众数', align: 'center'}
                            , {field: 'max', minWidth: 100, title: '最大分值', align: 'center'}
                            , {field: 'min', minWidth: 190, title: '最小分值', align: 'center'}
                        ]]
                        , data: [
                            {
                                'count': result.count,
                                'Average': result.Average,
                                'ModeNumber': result.ModeNumber,
                                'max': result.max,
                                'min': result.min
                            }
                        ]
                        , id: 'tableAna'
                        , page: false
                        , limit: 10000
                    });
                } else {
                    $("#chart1").html('');
                    layer.msg('没有数据');
                }
            },
            error: function (res) {
                layer.close(lay1);
                layer.msg(res.msg);
            }
        });
    }

    function init1() {
        if (chart1 != null && chart1 != "" && chart1 != undefined) {
            chart1.dispose();
        }

        var option = {
            title: {
                text: '自评得分分布',
                left:'center',
                bottom:'auto'
            },
            color:['#91c7ae'],
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['企业数'],
                left:'5%',
                top:'3%'
            },
            grid: {
                left: '2%',
                right: '3%',
                bottom: '2%',
                top:'8%',
                containLabel: true
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    name: '分数',
                    boundaryGap:true,
                    axisTick: {
                        show: true,
                        alignWithLabel: false
                    },
                    data: chartData1.xAxis
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name:'个数'
                }
            ],
            series: [
                {
                    name: '企业数',
                    type: 'bar',
                    barGap: 0,
                    label: {
                        normal: {
                            show: true,
                            position: 'top',
                            formatter: function(params){
                                var num = parseFloat(params.value);
                                var pre = num*100/chartData1.count;
                                return pre.toFixed(2)+'%';
                            }
                        }
                    },
                    data: chartData1.data
                }
            ]
        };

        chart1 = echarts.init(document.getElementById('chart1'));
        chart1.setOption(option, true);
    }

    // 统计分析 总得分分组统计
    function getChartData2() {
        var valueType = $('#valueType').val();//分值类型
        var object = treeObj.getCheckedNodes();//分析对象
        var indexTree = tree1.getCheckedNodes();//指标选择
        var businessType = treeSite?treeSite.getCheckedNodes():[];//企业性质
        var industryChecked = treeIndusty?treeIndusty.getCheckedNodes():[];//所属行业
        var groupOption = $('#anClass').val();//分组选择项

        var indexIds = [];
        $.each(indexTree,function(i,v){
            indexIds.push(v.indexId);
        });

        var streetId = [],parkId = [];
        $.each(object,function(i,v){
            if(v.level == 0){
                streetId.push(v.value);
            }else{
                parkId.push(v.parkId);
            }
        });

        var siteType = [];
        $.each(businessType,function(i,v){
            siteType.push(v.typeId);
        });

        var industryInclude = [];
        $.each(industryChecked,function(i,v){
            industryInclude.push(v.typeId);
        });

        var data = {
            "flag": valueType, //分值类型
            "streetId": streetId,
            "parkId": parkId,
            "indexIds": indexIds,
            "groupingOption": groupOption, //分组选择项
            "siteType":siteType, //企业性质
            "industryInclude":industryInclude //所属行业
        };

        if (valueType == 4) {
            var year = $("#allYear").val();
            data['year'] = year;
        }

        var lay1 = layer.load(1, {shade: [0.1, '#fff']});
        $.ajax({
            url: '/Analysis/getGroupScore',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                layer.close(lay1);
                if (res.success != true) {
                    $("#chart2,#pie").html('');
                    return layer.msg(res.msg);
                }
                if (res.data) {
                    var result = res.data;
                    chartData2 = [];
                    $.each(result.factory,function(i,v){
                        chartData2.push({
                            name: v.name,
                            type: 'bar',
                            data: v.value,
                            label: {
                                normal: {
                                    show: true,
                                    position: 'top'
                                }
                            }
                        })
                    });

                    pieData = [];
                    $.each(result.Average,function(i,v){
                        pieData.push({'name':v.name,'value':v.value});
                    });

                    init2();
                } else {
                    $("#chart2,#pie").html('');
                    layer.msg('没有数据');
                }
            },
            error: function (res) {
                layer.close(lay1);
                layer.msg(res.msg);
            }
        });
    }

    function init2() {
        if (chart2 != null && chart2 != "" && chart2 != undefined) {
            chart2.dispose();
        }
        if (pie != null && pie != "" && pie != undefined) {
            pie.dispose();
        }

        var option = {
            title: {
                text: '',
                left:'center',
                bottom:'auto'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                left:'5%',
                top:'0'
            },
            toolbox: {
                show: false
            },
            grid: {
                left: '2%',
                right: '4%',
                bottom: '3%',
                top:'8%',
                containLabel: true
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    name:'分类',
                    axisTick: {show: false},
                    data: ['企业数', '和谐企业数', '和谐先进企业数']
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name:'个数'
                }
            ],
            series: chartData2
        };

        chart2 = echarts.init(document.getElementById('chart2'));
        chart2.setOption(option, true);

        var xData = [];
        $.each(pieData,function(i,v){
            xData.push(v.name);
        });

        var option = {
            title: {
                text: '',
                subtext: '',
                x: 'center',
                bottom: 10
            },
            color:['#91c7ae'],
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['平均得分']
            },
            grid: {
                left: '2%',
                right: '9.5%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value',
                name:'平均得分'
            },
            yAxis: {
                type: 'category',
                data: xData
            },
            series: [
                {
                    name: '平均得分',
                    type: 'bar',
                    data: pieData,
                    label: {
                        normal: {
                            show: true,
                            position: 'insideRight'
                        }
                    }
                }
            ]
        };
        pie = echarts.init(document.getElementById('pie'));
        pie.setOption(option, true);
    }

    // 统计分析 指标得分对比1
    function getChartData3() {
        var valueType = $('#valueType').val();//分值类型
        var object = treeObj.getCheckedNodes();//分析对象
        var indexTree = tree2.getSelectedNodes();//指标选择
        var indexId = indexTree.length > 0?indexTree[0].indexId:'';
        var businessType = treeSite?treeSite.getCheckedNodes():[];//企业性质
        var industryChecked = treeIndusty?treeIndusty.getCheckedNodes():[];//所属行业

        var streetId = [],parkId = [];
        $.each(object,function(i,v){
            if(v.level == 0){
                streetId.push(v.value);
            }else{
                parkId.push(v.parkId);
            }
        });

        var siteType = [];
        $.each(businessType,function(i,v){
            siteType.push(v.typeId);
        });

        var industryInclude = [];
        $.each(industryChecked,function(i,v){
            industryInclude.push(v.typeId);
        });

        var data = {
            "flag": valueType, //分值类型
            "streetId": streetId,
            "parkId": parkId,
            "indexIds": indexId,
            "siteType": siteType, //企业性质
            "industryInclude":industryInclude //所属行业
        };

        if (valueType == 4) {
            var year = $("#allYear").val();
            data['year'] = year;
        }

        var lay1 = layer.load(1, {shade: [0.1, '#fff']});
        $.ajax({
            url: '/Analysis/getIndexScore1',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                layer.close(lay1);
                if (res.success != true) {
                    $("#chart3").html('');
                    return layer.msg(res.msg);
                }
                if (res.data) {
                    var result = res.data;console.log(result);
                    chartData3 = {name: [], data1: [],data2:[]};

                    $.each(result,function(i,v){
                        chartData3.name.push(v['name']);
                        chartData3.data1.push(v['average']);
                        chartData3.data2.push(v['standard']);
                    });

                    init3();
                } else {
                    $("#chart3").html('');
                    layer.msg('没有数据');
                }
            },
            error: function (res) {
                layer.close(lay1);
                layer.msg(res.msg);
            }
        });
    }

    function init3() {
        if (chart3 != null && chart3 != "" && chart3 != undefined) {
            chart3.dispose();
        }

        var option = {
            title: {
                text: '指标得分对比1',
                left:'center',
                bottom:'auto'
            },
            color:['#37a2da','#ffd85c'],
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['平均值', '标准值'],
                left:'5%',
                top:'3%'
            },
            grid: {
                left: '2%',
                right: '3%',
                bottom: '2%',
                top:'8%',
                containLabel: true
            },
            toolbox: {
                show: false
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    name:'指标',
                    axisTick: {show: false},
                    data: chartData3.name
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name:'分值'
                }
            ],
            series: [
                {
                    name: '平均值',
                    type: 'bar',
                    barGap: 0,
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: chartData3.data1
                },
                {
                    name: '标准值',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: chartData3.data2
                }
            ]
        };

        chart3 = echarts.init(document.getElementById('chart3'));
        chart3.setOption(option, true);
    }

    // 统计分析 指标得分对比2
    function getChartData4() {
        var valueType = $('#valueType').val();//分值类型
        var object = treeObj.getCheckedNodes();//分析对象
        var indexTree = tree3.getCheckedNodes();//指标选择
        var businessType = treeSite?treeSite.getCheckedNodes():[];//企业性质
        var industryChecked = treeIndusty?treeIndusty.getCheckedNodes():[];//所属行业

        var indexIds = [];
        $.each(indexTree,function(i,v){
            indexIds.push(v.indexId);
        });

        var streetId = [],parkId = [];
        $.each(object,function(i,v){
            if(v.level == 0){
                streetId.push(v.value);
            }else{
                parkId.push(v.parkId);
            }
        });

        var siteType = [];
        $.each(businessType,function(i,v){
            siteType.push(v.typeId);
        });

        var industryInclude = [];
        $.each(industryChecked,function(i,v){
            industryInclude.push(v.typeId);
        });

        var data = {
            "flag": valueType, //分值类型
            "streetId": streetId,
            "parkId": parkId,
            "indexIds": indexIds,
            "siteType":siteType, //企业性质
            "industryInclude":industryInclude //所属行业
        };

        if (valueType == 4) {
            var year = $("#allYear").val();
            data['year'] = year;
        }

        var lay1 = layer.load(1, {shade: [0.1, '#fff']});
        $.ajax({
            url: '/Analysis/getIndexScore2',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                layer.close(lay1);
                if (res.success != true) {
                    $("#chart4").html('');
                    return layer.msg(res.msg);
                }
                if (res.data) {
                    var result = res.data;
                    chartData4 = {name: [], data1: [],data2:[]};

                    $.each(result,function(i,v){
                        chartData4.name.push(v['name']);
                        chartData4.data1.push(v['average']);
                        chartData4.data2.push(v['standard']);
                    });

                    init4();
                } else {
                    $("#chart4").html('');
                    layer.msg('没有数据');
                }
            },
            error: function (res) {
                layer.close(lay1);
                layer.msg(res.msg);
            }
        });
    }

    function init4() {
        if (chart4 != null && chart4 != "" && chart4 != undefined) {
            chart4.dispose();
        }

        var option = {
            title: {
                text: '指标得分对比2',
                left:'center',
                bottom:'auto'
            },
            color:['#37a2da','#ffd85c'],
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['平均值', '标准值'],
                left:'5%',
                top:'3%'
            },
            grid: {
                left: '2%',
                right: '3%',
                bottom: '2%',
                top:'8%',
                containLabel: true
            },
            toolbox: {
                show: false
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    name:'指标',
                    axisTick: {show: false},
                    data: chartData4.name
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name:'分值'
                }
            ],
            series: [
                {
                    name: '平均值',
                    type: 'bar',
                    barGap: 0,
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: chartData4.data1
                },
                {
                    name: '标准值',
                    type: 'bar',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: chartData4.data2
                }
            ]
        };

        chart4 = echarts.init(document.getElementById('chart4'));
        chart4.setOption(option, true);
    }

});
