package com.jet.cloud.jetsndcxqyapply.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ExcelController {
    @Autowired
    private CompanySiteService companySiteService;

    @RequestMapping(value = "/file/export", method = RequestMethod.GET)
    public void excel(HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("苏州高新区劳动关系和谐企业和工业园申报管理系统");

        //合并的单元格样式
        HSSFCellStyle boderStyle = workbook.createCellStyle();
        //垂直居中
        boderStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        boderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        // 设置一个边框
        //设置垂直对齐的样式为居中对齐;
        boderStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        CellRangeAddress region = new CellRangeAddress(1, 14, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region1 = new CellRangeAddress(15, 28, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region2 = new CellRangeAddress(29, 44, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region3 = new CellRangeAddress(29, 44, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region4 = new CellRangeAddress(45, 58, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region5 = new CellRangeAddress(59, 64, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region6 = new CellRangeAddress(65, 70, 0, 0);//下标从0开始 起始行号，终止行号， 起始列号，终止列号

        CellRangeAddress two1 = new CellRangeAddress(1, 5, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two2 = new CellRangeAddress(6, 9, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two3 = new CellRangeAddress(10, 12, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two4 = new CellRangeAddress(13, 14, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two5 = new CellRangeAddress(16, 18, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two6 = new CellRangeAddress(19, 21, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two7 = new CellRangeAddress(22, 23, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two8 = new CellRangeAddress(24, 28, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two9 = new CellRangeAddress(29, 33, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two10 = new CellRangeAddress(34, 36, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two11 = new CellRangeAddress(37, 40, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two12 = new CellRangeAddress(41, 44, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two13 = new CellRangeAddress(45, 58, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two14 = new CellRangeAddress(59, 60, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two15 = new CellRangeAddress(61, 64, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two16 = new CellRangeAddress(65, 66, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two17 = new CellRangeAddress(67, 68, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress two18 = new CellRangeAddress(69, 70, 1, 1);//下标从0开始 起始行号，终止行号， 起始列号，终止列号

        sheet.addMergedRegion(region);
        sheet.addMergedRegion(region1);
        sheet.addMergedRegion(region2);
        sheet.addMergedRegion(region3);
        sheet.addMergedRegion(region4);
        sheet.addMergedRegion(region5);
        sheet.addMergedRegion(region6);

        sheet.addMergedRegion(two1);
        sheet.addMergedRegion(two2);
        sheet.addMergedRegion(two3);
        sheet.addMergedRegion(two4);
        sheet.addMergedRegion(two5);
        sheet.addMergedRegion(two6);
        sheet.addMergedRegion(two7);
        sheet.addMergedRegion(two8);
        sheet.addMergedRegion(two9);
        sheet.addMergedRegion(two10);
        sheet.addMergedRegion(two11);
        sheet.addMergedRegion(two12);
        sheet.addMergedRegion(two13);
        sheet.addMergedRegion(two14);
        sheet.addMergedRegion(two15);
        sheet.addMergedRegion(two16);
        sheet.addMergedRegion(two17);
        sheet.addMergedRegion(two18);

        int rowNum = 1;
        String[] headers = {"一级指标", "二级指标", "三级指标", "标准分值", "企业自评分值", "镇(街道)测评分值", "区三方评定分值"};

        HSSFRow row = sheet.createRow(0);

        //在excel表中添加表头
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellStyle(boderStyle);
        }

        /*String string = "{\"data\":[{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"用工管理\",\"index3\":\"企业建立职工名册\",\"indexId\":\"1003\",\"max\":\"2.5\",\"min\":\"0\",\"score\":\"2.5\",\"selfValue\":\"2.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"用工管理\",\"index3\":\"依法签订劳动合同，签订率达100%\",\"indexId\":\"1004\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"用工管理\",\"index3\":\"劳动合同依法履行、变更、解除与终止\",\"indexId\":\"1005\",\"max\":\"1.5\",\"min\":\"0\",\"score\":\"1.5\",\"selfValue\":\"1.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"用工管理\",\"index3\":\"企业建立和完善规章制度\",\"indexId\":\"1006\",\"max\":\"4.5\",\"min\":\"0\",\"score\":\"4.5\",\"selfValue\":\"4.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"用工管理\",\"index3\":\"企业依法使用劳务派遣\",\"indexId\":\"1007\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"工作时间和福利\",\"index3\":\"执行国家工时制度\",\"indexId\":\"1009\",\"max\":\"3\",\"min\":\"0\",\"score\":\"3\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"工作时间和福利\",\"index3\":\"按时足额发放工资\",\"indexId\":\"1010\",\"max\":\"4\",\"min\":\"0\",\"score\":\"4\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"工作时间和福利\",\"index3\":\"执行国家休假制度\",\"indexId\":\"1011\",\"max\":\"3\",\"min\":\"0\",\"score\":\"3\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"工作时间和福利\",\"index3\":\"依法足额提取、合理使用职工福利费\",\"indexId\":\"1012\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"社会保障\",\"index3\":\"依法办理社会保险登记\",\"indexId\":\"1014\",\"max\":\"4\",\"min\":\"0\",\"score\":\"4\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"社会保障\",\"index3\":\"依法按时足额缴纳社会保险费\",\"indexId\":\"1015\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"社会保障\",\"index3\":\"依法缴纳住房公积金\",\"indexId\":\"1016\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"劳动保护和劳动安全卫生\",\"index3\":\"劳动保护措施和劳动安全卫生条件符合国家规定的标准\",\"indexId\":\"1018\",\"max\":\"3.5\",\"min\":\"0\",\"score\":\"3.5\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"规范性指标\",\"index2\":\"劳动保护和劳动安全卫生\",\"index3\":\"符合劳动安全卫生\",\"indexId\":\"1019\",\"max\":\"5\",\"min\":\"0\",\"score\":\"5\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"党组织建制\",\"index3\":\"建立党组织\",\"indexId\":\"1022\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"工会建制\",\"index3\":\"依法组建工会组织\",\"indexId\":\"1024\",\"max\":\"1.5\",\"min\":\"0\",\"score\":\"1.5\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"工会建制\",\"index3\":\"依法按时足额拨缴工会经费\",\"indexId\":\"1025\",\"max\":\"0.5\",\"min\":\"0\",\"score\":\"0.5\",\"selfValue\":\"0.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"工会建制\",\"index3\":\"依法保障工会开展活动\",\"indexId\":\"1026\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"民主管理建制\",\"index3\":\"依法建立职工代表大会制度\",\"indexId\":\"1028\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"民主管理建制\",\"index3\":\"实行厂务公开制度或建立与企业相适应的其他民主管理制度\",\"indexId\":\"1029\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"民主管理建制\",\"index3\":\"对职工所提建议及时反馈\",\"indexId\":\"1030\",\"max\":\"0.5\",\"min\":\"0\",\"score\":\"0.5\",\"selfValue\":\"0.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"集体协商建制\",\"index3\":\"集体合同的协商与履行\",\"indexId\":\"1032\",\"max\":\"4.5\",\"min\":\"0\",\"score\":\"4.5\",\"selfValue\":\"4\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"集体协商建制\",\"index3\":\"签订工资专项集体合同\",\"indexId\":\"1033\",\"max\":\"0.5\",\"min\":\"0\",\"score\":\"0.5\",\"selfValue\":\"0.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"劳动争议协调建制\",\"index3\":\"完善调解组织机制\",\"indexId\":\"1035\",\"max\":\"5\",\"min\":\"0\",\"score\":\"5\",\"selfValue\":\"4\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"劳动争议协调建制\",\"index3\":\"建立劳动争议预警机制 \",\"indexId\":\"1036\",\"max\":\"2.5\",\"min\":\"0\",\"score\":\"2.5\",\"selfValue\":\"2\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"劳动争议协调建制\",\"index3\":\"具备职工表达诉求的沟通渠道\",\"indexId\":\"1037\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"劳动争议协调建制\",\"index3\":\"普法宣传与和谐劳动关系系列培训\",\"indexId\":\"1038\",\"max\":\"2.5\",\"min\":\"0\",\"score\":\"2.5\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"建制性指标\",\"index2\":\"劳动争议协调建制\",\"index3\":\"积极配合劳动保障行政部门检查与调查\",\"indexId\":\"1039\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"工资福利\",\"index3\":\"工资协商、增长及工资水平\",\"indexId\":\"1042\",\"max\":\"5\",\"min\":\"0\",\"score\":\"5\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"工资福利\",\"index3\":\"建立企业职业年金制度\",\"indexId\":\"1043\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"工资福利\",\"index3\":\"建立补充医疗保险制度\",\"indexId\":\"1044\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"工资福利\",\"index3\":\"参加职工互助保障计划\",\"indexId\":\"1045\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"工资福利\",\"index3\":\"其他福利待遇\",\"indexId\":\"1046\",\"max\":\"0.5\",\"min\":\"0\",\"score\":\"0.5\",\"selfValue\":\"0.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"职业培训\",\"index3\":\"建立职工培训制度\",\"indexId\":\"1048\",\"max\":\"1.5\",\"min\":\"0\",\"score\":\"1.5\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"职业培训\",\"index3\":\"按规定提取和使用职工教育培训经费\",\"indexId\":\"1049\",\"max\":\"1.5\",\"min\":\"0\",\"score\":\"1.5\",\"selfValue\":\"1.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"职业培训\",\"index3\":\"组织开展职业培训和岗位练兵技能竞赛活动\",\"indexId\":\"1050\",\"max\":\"2\",\"min\":\"0\",\"score\":\"2\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"文化建设\",\"index3\":\"建立“职工之家”、“职工书屋”等，开展企业文化建设\",\"indexId\":\"1052\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"文化建设\",\"index3\":\"组织开展劳动竞赛\",\"indexId\":\"1053\",\"max\":\"0.5\",\"min\":\"0\",\"score\":\"0.5\",\"selfValue\":\"0.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"文化建设\",\"index3\":\"组织开展职工合理化建议活动\",\"indexId\":\"1054\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"文化建设\",\"index3\":\"为职工配备文娱体育设施和场所\",\"indexId\":\"1055\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"社会责任\",\"index3\":\"吸纳就业困难人员\",\"indexId\":\"1057\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"社会责任\",\"index3\":\"吸纳残疾人就业或缴纳残疾人就业保障金\",\"indexId\":\"1058\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"社会责任\",\"index3\":\"根据企业特点给予其他形式的人文关怀\",\"indexId\":\"1059\",\"max\":\"0.5\",\"min\":\"0\",\"score\":\"0.5\",\"selfValue\":\"0.5\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"发展性指标\",\"index2\":\"社会责任\",\"index3\":\"积极参加公益、慈善活动\",\"indexId\":\"1060\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"企业同事间关系融洽\",\"indexId\":\"1063\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"企业与职工沟通顺畅\",\"indexId\":\"1064\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"对职业发展机会满意\",\"indexId\":\"1065\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"有较强参与度和归属感\",\"indexId\":\"1066\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"对企业日常管理行为满意\",\"indexId\":\"1067\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"工作认可度高\",\"indexId\":\"1068\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"了解并认同企业的愿景\",\"indexId\":\"1069\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"业务上能得到有效指导\",\"indexId\":\"1070\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"认同企业文化和价值观\",\"indexId\":\"1071\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"管理变动中得到有效支持\",\"indexId\":\"1072\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"报酬与本人付出相符\",\"indexId\":\"1073\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"薪酬制度公平公正\",\"indexId\":\"1074\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"工作能发挥本人才能\",\"indexId\":\"1075\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0\",\"index1\":\"感受性指标\",\"index2\":\"职工满意度\",\"index3\":\"企业保障提供职工开展工作所需各项资源\",\"indexId\":\"1076\",\"max\":\"1\",\"min\":\"0\",\"score\":\"1\",\"selfValue\":\"1\",\"streetValue\":null,\"valueType\":\"number\"},{\"districtValue\":null,\"enum\":\"0/-3\",\"index1\":\"减分指标\",\"index2\":\"存在建制不全情形\",\"index3\":\"接触职业病危害作业的职工未定期进行健康检查\",\"indexId\":\"1079\",\"max\":\"0\",\"min\":\"0\",\"score\":\"0\",\"selfValue\":\"0\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"0/-3\",\"index1\":\"减分指标\",\"index2\":\"存在建制不全情形\",\"index3\":\"职工董事和职工监事制度建制不全\",\"indexId\":\"1080\",\"max\":\"0\",\"min\":\"0\",\"score\":\"0\",\"selfValue\":\"0\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"0/-2/-4/-6\",\"index1\":\"减分指标\",\"index2\":\"劳动关系表现不稳定\",\"index3\":\"劳动保障监察投诉举报多\",\"indexId\":\"1082\",\"max\":\"0\",\"min\":\"0\",\"score\":\"0\",\"selfValue\":\"-4\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"0/-2/-4/-6\",\"index1\":\"减分指标\",\"index2\":\"劳动关系表现不稳定\",\"index3\":\"劳动争议数量多\",\"indexId\":\"1083\",\"max\":\"0\",\"min\":\"0\",\"score\":\"0\",\"selfValue\":\"0\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"0/-2/-4/-6\",\"index1\":\"减分指标\",\"index2\":\"劳动关系表现不稳定\",\"index3\":\"工伤事故多\",\"indexId\":\"1084\",\"max\":\"0\",\"min\":\"0\",\"score\":\"0\",\"selfValue\":\"-2\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"0/-2\",\"index1\":\"减分指标\",\"index2\":\"劳动关系表现不稳定\",\"index3\":\"员工流失率高\",\"indexId\":\"1085\",\"max\":\"0\",\"min\":\"0\",\"score\":\"0\",\"selfValue\":\"-2\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"Y/N\",\"index1\":\"一票否决指标\",\"index2\":\"行政处罚重大事故\",\"index3\":\"逾期未履行劳动保障监察行政处理决定或受到劳动保障监察行政处罚\",\"indexId\":\"1088\",\"max\":\"N\",\"min\":\"0\",\"score\":\"N\",\"selfValue\":\"N\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"Y/N\",\"index1\":\"一票否决指标\",\"index2\":\"行政处罚重大事故\",\"index3\":\"住房公积金管理部门行政处罚\",\"indexId\":\"1090\",\"max\":\"N\",\"min\":\"0\",\"score\":\"N\",\"selfValue\":\"N\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"Y/N\",\"index1\":\"一票否决指标\",\"index2\":\"重大事故\",\"index3\":\"重大安全生产责任事故\",\"indexId\":\"1091\",\"max\":\"N\",\"min\":\"0\",\"score\":\"N\",\"selfValue\":\"N\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"Y/N\",\"index1\":\"一票否决指标\",\"index2\":\"重大事故\",\"index3\":\"重大职业病危害事故\",\"indexId\":\"1092\",\"max\":\"N\",\"min\":\"0\",\"score\":\"N\",\"selfValue\":\"N\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"Y/N\",\"index1\":\"一票否决指标\",\"index2\":\"其他\",\"index3\":\"因劳动保障问题引发的群体性事件\",\"indexId\":\"1094\",\"max\":\"N\",\"min\":\"0\",\"score\":\"N\",\"selfValue\":\"N\",\"streetValue\":null,\"valueType\":\"enum\"},{\"districtValue\":null,\"enum\":\"Y/N\",\"index1\":\"一票否决指标\",\"index2\":\"其他\",\"index3\":\"创建过程提供虚假材料\",\"indexId\":\"1095\",\"max\":\"N\",\"min\":\"0\",\"score\":\"N\",\"selfValue\":\"N\",\"streetValue\":null,\"valueType\":\"enum\"}],\"count\":[\"一级指标\",\"二级指标\",\"三级指标\"]}";
        JSONObject object = JSONObject.parseObject(string);*/
        Dto getm = companySiteService.getm("1");
        String str = JSONObject.toJSONString(getm);
        JSONObject object = (JSONObject) JSONObject.parse(str);
        List<Map<String, Object>> list = object.getObject("data", List.class);
        Set<String> indexOne = new HashSet();
        Set<String> indexTwo = new HashSet();
        for (int i = 0; i < list.size(); i++) {
            indexOne.add(list.get(i).get("index1").toString());
            indexTwo.add(list.get(i).get("index2").toString());
        }

        for (String index_1 : indexOne) {
            boolean bo_first = true;
            for (Map<String, Object> map : list) {
                if (index_1.equals(map.get("index1").toString())) {
                    if (bo_first) {
                        bo_first = false;
                    } else {
                        map.put("index1", "");
                    }
                }
            }
        }

        for (String index_2 : indexTwo) {
            boolean bo_first = true;
            for (Map<String, Object> map : list) {
                if (index_2.equals(map.get("index2").toString())) {
                    if (bo_first) {
                        bo_first = false;
                    } else {
                        map.put("index2", "");
                    }
                }
            }
        }

        for (Map<String, Object> map : list) {
            HSSFRow row1 = sheet.createRow(rowNum);
            Cell index1 = row1.createCell(0);
            index1.setCellValue(StringHelper.nvl(map.get("index1"), ""));
            index1.setCellStyle(boderStyle);

            Cell index2 = row1.createCell(1);
            index2.setCellValue(StringHelper.nvl(map.get("index2"), ""));
            index2.setCellStyle(boderStyle);

            Cell index3 = row1.createCell(2);
            index3.setCellValue(StringHelper.nvl(map.get("index3"), ""));
            index3.setCellStyle(boderStyle);

            Cell max = row1.createCell(3);
            max.setCellValue(StringHelper.nvl(map.get("max"), ""));
            max.setCellStyle(boderStyle);

            Cell selfValue = row1.createCell(4);
            selfValue.setCellValue(StringHelper.nvl(map.get("selfValue"), ""));
            selfValue.setCellStyle(boderStyle);

            Cell streetValue = row1.createCell(5);
            streetValue.setCellValue(StringHelper.nvl(map.get("streetValue"), ""));
            streetValue.setCellStyle(boderStyle);

            Cell districtValue = row1.createCell(6);
            districtValue.setCellValue(StringHelper.nvl(map.get("districtValue"), ""));
            districtValue.setCellStyle(boderStyle);

            rowNum++;
        }

        SimpleDateFormat fdate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String fileName = fdate.format(new Date()) + new String("-苏州高新区劳动关系和谐企业自评申报表（企业）".getBytes("utf-8"), "iso-8859-1") + ".xls";

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }
}
