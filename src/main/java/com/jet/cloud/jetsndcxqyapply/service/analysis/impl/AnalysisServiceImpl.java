package com.jet.cloud.jetsndcxqyapply.service.analysis.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.SysStreet2;
import com.jet.cloud.jetsndcxqyapply.mapper.AnalysisMapper;
import com.jet.cloud.jetsndcxqyapply.service.analysis.AnalysisService;
import com.jet.cloud.jetsndcxqyapply.service.user.DistrictCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/*
 * 分析serviceImpl
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Autowired
    private AnalysisMapper analysisMapper;
    @Autowired
    private DistrictCommitteeService districtCommitteeService;

    @Override
    public Dto getStreetAndIndustry() {
        List<SysStreet2> streetAndIndustry = analysisMapper.getStreetAndIndustry();
        if (streetAndIndustry != null && !streetAndIndustry.isEmpty()) {
            return new Dto(true, "查询成功", streetAndIndustry);
        } else {
            return new Dto(false, "查询失败", null);
        }
    }

    @Override
    public Dto getTbSySSiteType() {
        List<Map<String, Object>> streetAndIndustry = analysisMapper.getTbSySSiteType();
        if (streetAndIndustry != null && !streetAndIndustry.isEmpty()) {
            return new Dto(true, "查询成功", streetAndIndustry);
        } else {
            return new Dto(false, "查询失败", null);
        }
    }


    @Override
    public Dto getAllScore(JSONObject jsonObject) {
        Map<String, Object> mapAll = new HashMap<>();
        Map<String, Object> mapbefore = new HashMap<>();
        // 选择年份
        String flag = StringHelper.nvl(jsonObject.get("flag"), "");
        String year = StringHelper.nvl(jsonObject.get("year"), "");
        if (year != "") {
            Integer y = Integer.parseInt(year) - 1;
            mapAll.put("year", y);
        } else {
            mapAll.put("year", year);
        }

        // 指标
        List<String> index = jsonObject.getObject("indexIds", List.class);

        // 企业性质
        List<String> siteType = jsonObject.getObject("siteType", List.class);

        // 镇工业园
        List<String> streetId = jsonObject.getObject("streetId", List.class);
        List<String> parkId = jsonObject.getObject("parkId", List.class);

        //所属行业
        List<String> industryInclude = jsonObject.getObject("industryInclude", List.class);

        mapAll.put("flag", flag);
        mapAll.put("streetId", streetId);
        mapAll.put("parkId", parkId);
        mapAll.put("typeId", siteType);
        mapAll.put("industryInclude", industryInclude);

        // 全区申报企业数
        Integer sum = analysisMapper.sumScreeningEnterprises(mapAll);
        List<Map<String, Object>> maps = analysisMapper.screeningEnterprises(mapAll);
        List<String> listSiteId = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            listSiteId.add(map.get("siteId").toString());
        }
        List<Map<String, Object>> indexScore = null;
        List<Map<String, Object>> indexId = analysisMapper.getAllIndexId(index);
        if (maps != null && !maps.isEmpty()) {
            if (index != null && !index.isEmpty()) {
                if (index.size() > 0) {
                    Map<String, Object> map1 = new HashMap<>();
                    List<String> lis = new ArrayList<>();
                    if (indexId != null) {
                        for (Map<String, Object> index1 : indexId) {
                            String index_id = StringHelper.nvl(index1.get("index_id"), "");
                            lis.add(index_id);
                        }
                        map1.put("indexList", lis);
                        map1.put("siteList", listSiteId);
                        indexScore = analysisMapper.getIndexScore(map1);
                    }
                }
                mapbefore.put("count", sum);
                List<BigDecimal> listSelf = new ArrayList<>();
                List<String> listX = new ArrayList<>();
                List<Integer> listY = new ArrayList<>();
                for (Map<String, Object> map : indexScore) {
                    if ("1".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("selfValue").toString()))));
                    }
                    if ("2".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("streetValue").toString()))));
                    }
                    if ("3".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("districtValue").toString()))));
                    }
                    if ("4".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("reviewValue").toString()))));
                    }
                }
                BigDecimal max = Collections.max(listSelf);
                BigDecimal min = Collections.min(listSelf);
                BigDecimal a = (max.subtract(min)).divide(new BigDecimal(Double.toString(10)), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal bd4 = new BigDecimal(Double.toString(sum));

                mapbefore.put("count", sum);
                mapbefore.put("max", max);
                mapbefore.put("min", min);

                // 得分分布
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(0)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(1)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(1)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(2)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(2)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(3)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(3)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(4)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(4)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(5)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(5)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(6)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(6)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(7)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(7)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(8)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(8)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(9)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(9)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(10)))).doubleValue()));
                mapbefore.put("listX", listX);

                Double allSum = 0d;
                for (BigDecimal d : listSelf) {
                    BigDecimal decimal = new BigDecimal(Double.toString(0d));
                    allSum += decimal.add(d).doubleValue();
                }
                BigDecimal bd5 = new BigDecimal(Double.toString(allSum));
                double doubleValue = 0d;
                if (bd4.compareTo(BigDecimal.ZERO) != 0) {
                    doubleValue = bd5.divide(bd4, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                // 平均值
                mapbefore.put("Average", doubleValue);

                // 众数
                List<BigDecimal> list2 = new ArrayList<>();
                LinkedHashMap<BigDecimal, Integer> map = new LinkedHashMap<BigDecimal, Integer>();
                int count = 0;

                if ("1".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                   /* Map<String, Object> map1 = new HashMap<>();
                    List<String> lis = new ArrayList<>();
                    if (indexId != null) {
                        for (Map<String, Object> index1 : indexId) {
                            String index_id = StringHelper.nvl(index1.get("index_id"), "");
                            lis.add(index_id);
                        }
                        map1.put("indexList", lis);
                        map1.put("siteList", listSiteId);
                    } else {
                        map1.put("indexList", index);
                        map1.put("siteList", listSiteId);
                    }
                    Map<String, Object> modeNumber = analysisMapper.getIndexModeNumberSelfValue(map1);
                    String selfValue = StringHelper.nvl(modeNumber.get("selfValue"), "");
                    mapbefore.put("ModeNumber", selfValue);*/
                }
                if ("2".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                   /* Map<String, Object> map1 = new HashMap<>();
                    List<String> lis = new ArrayList<>();
                    if (indexId != null) {
                        for (Map<String, Object> index1 : indexId) {
                            String index_id = StringHelper.nvl(index1.get("index_id"), "");
                            lis.add(index_id);
                        }
                        map1.put("indexList", lis);
                        map1.put("siteList", listSiteId);
                    } else {
                        map1.put("indexList", index);
                        map1.put("siteList", listSiteId);
                    }
                    Map<String, Object> modeNumber = analysisMapper.getIndexModeNumberStreetValue(map1);
                    String streetValue = StringHelper.nvl(modeNumber.get("streetValue"), "");
                    mapbefore.put("ModeNumber", streetValue);*/
                }
                if ("3".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                   /* Map<String, Object> map1 = new HashMap<>();
                    List<String> lis = new ArrayList<>();
                    if (indexId != null) {
                        for (Map<String, Object> index1 : indexId) {
                            String index_id = StringHelper.nvl(index1.get("index_id"), "");
                            lis.add(index_id);
                        }
                        map1.put("indexList", lis);
                        map1.put("siteList", listSiteId);
                    } else {
                        map1.put("indexList", index);
                        map1.put("siteList", listSiteId);
                    }
                    Map<String, Object> modeNumber = analysisMapper.getIndexModeNumberDistrictValue(map1);
                    String districtValue = StringHelper.nvl(modeNumber.get("districtValue"), "");
                    mapbefore.put("ModeNumber", districtValue);*/
                }
                if ("4".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                  /*  Map<String, Object> map1 = new HashMap<>();
                    List<String> lis = new ArrayList<>();
                    if (indexId != null) {
                        for (Map<String, Object> index1 : indexId) {
                            String index_id = StringHelper.nvl(index1.get("index_id"), "");
                            lis.add(index_id);
                        }
                        map1.put("indexList", lis);
                        map1.put("siteList", listSiteId);
                    } else {
                        map1.put("indexList", index);
                        map1.put("siteList", listSiteId);
                    }
                    Map<String, Object> modeNumber = analysisMapper.getIndexModeNumberReviewValue(map1);
                    String reviewValue = StringHelper.nvl(modeNumber.get("reviewValue"), "");
                    mapbefore.put("ModeNumber", reviewValue);*/
                }

                mapbefore.put("Company", listSelf);
                int i1 = 0;
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                int i7 = 0;
                int i8 = 0;
                int i9 = 0;
                int i10 = 0;
                for (BigDecimal bge : listSelf) {
                    if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(9))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(10))))) <= 0) {
                        i10++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(8))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(9))))) <= 0) {
                        i9++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(7))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(8))))) <= 0) {
                        i8++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(6))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(7))))) <= 0) {
                        i7++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(5))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(6))))) <= 0) {
                        i6++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(4))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(5))))) <= 0) {
                        i5++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(3))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(4))))) <= 0) {
                        i4++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(2))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(3))))) <= 0) {
                        i3++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(1))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(2))))) <= 0) {
                        i2++;
                    } else {
                        i1++;
                    }
                }
                listY.add(i1);
                listY.add(i2);
                listY.add(i3);
                listY.add(i4);
                listY.add(i5);
                listY.add(i6);
                listY.add(i7);
                listY.add(i8);
                listY.add(i9);
                listY.add(i10);
                mapbefore.put("listY", listY);
                return new Dto(true, "查询成功", mapbefore);
            } else {
                List<String> listX = new ArrayList<>();
                List<Integer> listY = new ArrayList<>();
                // 第一次默认展开查自评的值
                List<Map<String, Object>> normal = analysisMapper.getNormal(listSiteId);
                List<BigDecimal> listSelf = new ArrayList<>();
                for (Map<String, Object> map : normal) {
                    if ("1".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("selfValue").toString()))));
                    }
                    if ("2".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("streetValue").toString()))));
                    }
                    if ("3".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("districtValue").toString()))));
                    }
                    if ("4".equals(flag)) {
                        listSelf.add(new BigDecimal(Double.toString(Double.parseDouble(map.get("reviewValue").toString()))));
                    }
                }
                BigDecimal max = Collections.max(listSelf);
                BigDecimal min = Collections.min(listSelf);
                BigDecimal a = (max.subtract(min)).divide(new BigDecimal(Double.toString(10)), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal bd4 = new BigDecimal(Double.toString(sum));

                mapbefore.put("count", sum);
                mapbefore.put("max", max);
                mapbefore.put("min", min);

                // 得分分布
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(0)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(1)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(1)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(2)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(2)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(3)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(3)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(4)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(4)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(5)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(5)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(6)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(6)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(7)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(7)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(8)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(8)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(9)))).doubleValue()));
                listX.add(new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(9)))).doubleValue()) + "~" + new DecimalFormat("0.00").format(min.add(a.multiply(new BigDecimal(Double.toString(10)))).doubleValue()));
                mapbefore.put("listX", listX);

                Double allSum = 0d;
                for (BigDecimal d : listSelf) {
                    BigDecimal decimal = new BigDecimal(Double.toString(0d));
                    allSum += decimal.add(d).doubleValue();
                }
                BigDecimal bd5 = new BigDecimal(Double.toString(allSum));
                double doubleValue = 0d;
                if (bd4.compareTo(BigDecimal.ZERO) != 0) {
                    doubleValue = bd5.divide(bd4, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }

                // 平均值
                mapbefore.put("Average", doubleValue);
                // 众数
                List<BigDecimal> list2 = new ArrayList<>();
                LinkedHashMap<BigDecimal, Integer> map = new LinkedHashMap<>();
                int count = 0;
                if ("1".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }
                  /*  Map<String, Object> modeNumber = analysisMapper.getModeNumberSelfValue(listSiteId);
                    String selfValue = StringHelper.nvl(modeNumber.get("selfValue"), "");
                    mapbefore.put("ModeNumber", selfValue);*/
                }
                if ("2".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                  /*  Map<String, Object> modeNumber = analysisMapper.getModeNumberStreetValue(listSiteId);
                    String streetValue = StringHelper.nvl(modeNumber.get("streetValue"), "");
                    mapbefore.put("ModeNumber", streetValue);*/
                }
                if ("3".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                   /* Map<String, Object> modeNumber = analysisMapper.getModeNumberDistrictValue(listSiteId);
                    String districtValue = StringHelper.nvl(modeNumber.get("districtValue"), "");
                    mapbefore.put("ModeNumber", districtValue);*/
                }
                if ("4".equals(flag)) {
                    String ModeList = ModeNumber(listSelf, list2, map, count);
                    if (ModeList != null) {
                        mapbefore.put("ModeNumber", ModeList);
                    } else {
                        mapbefore.put("ModeNumber", "");
                    }

                    /*Map<String, Object> modeNumber = analysisMapper.getModeNumberReviewValue(listSiteId);
                    String reviewValue = StringHelper.nvl(modeNumber.get("reviewValue"), "");
                    mapbefore.put("ModeNumber", reviewValue);*/
                }
                mapbefore.put("Company", listSelf);
                int i1 = 0;
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                int i7 = 0;
                int i8 = 0;
                int i9 = 0;
                int i10 = 0;
                for (BigDecimal bge : listSelf) {
                    if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(9))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(10))))) <= 0) {
                        i10++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(8))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(9))))) <= 0) {
                        i9++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(7))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(8))))) <= 0) {
                        i8++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(6))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(7))))) <= 0) {
                        i7++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(5))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(6))))) <= 0) {
                        i6++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(4))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(5))))) <= 0) {
                        i5++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(3))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(4))))) <= 0) {
                        i4++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(2))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(3))))) <= 0) {
                        i3++;
                    } else if (bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(1))))) > 0 &&
                            bge.compareTo(min.add(a.multiply(new BigDecimal(Double.toString(2))))) <= 0) {
                        i2++;
                    } else {
                        i1++;
                    }
                }
                listY.add(i1);
                listY.add(i2);
                listY.add(i3);
                listY.add(i4);
                listY.add(i5);
                listY.add(i6);
                listY.add(i7);
                listY.add(i8);
                listY.add(i9);
                listY.add(i10);
                mapbefore.put("listY", listY);
                return new Dto(true, "查询成功", mapbefore);
            }
        } else {
            mapbefore.put("count", "");
            mapbefore.put("listX", "");
            mapbefore.put("listY", "");
            mapbefore.put("ModeNumber", "");
            mapbefore.put("Average", "");
            mapbefore.put("max", "");
            mapbefore.put("min", "");
            mapbefore.put("Company", "");
            return new Dto(true, "查询成功", mapbefore);
        }
    }

    private String ModeNumber(List<BigDecimal> listSelf, List<BigDecimal> list2, LinkedHashMap<BigDecimal, Integer> map, int count) {
        System.out.println(listSelf);
        for (int i = 0; i < listSelf.size(); i++) {
            System.out.println(listSelf.get(i));
            for (int j = i + 1; j < listSelf.size(); j++) {
                System.out.println(listSelf.get(j));
                if (listSelf.get(i).compareTo(listSelf.get(j)) == 0) {
                    list2.add(listSelf.get(i));
                    break;
                }
            }
        }
        //统计list2集合中重复数据出现次数,对应放入Map集合
        for (BigDecimal obj : list2) {
            if (map.containsKey(obj)) {
                count++;
                map.put(obj, map.get(obj).intValue() + 1);
            } else {
                map.put(obj, 1);
            }
        }
        System.out.println("count=" + count);
        if (map != null && map.size() > 0) {
            Object value = getMaxValue(map);
            List key = getKey(map, value);
            if (key.size() == 1) {
                return key.get(0).toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Object getMaxValue(Map<BigDecimal, Integer> map) {
        if (map == null) return null;
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return obj[obj.length - 1];
    }

    public static List getKey(LinkedHashMap<BigDecimal, Integer> map, Object value) {
        List list = new ArrayList();
        BigDecimal key = null; //Map,HashMap并没有实现Iteratable接口.不能用于增强for循环.
        for (BigDecimal getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                key = getKey;
                list.add(key);
            }
        }
        return list;
        //这个key肯定是最后一个满足该条件的key.
    }

    @Override
    public Dto getGroupScore(JSONObject jsonObject) {
        Map<String, Object> mapAll = new HashMap<>();
        Map<String, Object> mapbefore = new HashMap<>();

        // 选择年份
        String flag = StringHelper.nvl(jsonObject.get("flag"), "");
        String year = StringHelper.nvl(jsonObject.get("year"), "");
        if (year != "") {
            Integer y = Integer.parseInt(year) - 1;
            mapAll.put("year", y);
        } else {
            mapAll.put("year", year);
        }

        // 指标
        List<String> index = jsonObject.getObject("indexIds", List.class);

        // 企业性质
        List<String> siteType = jsonObject.getObject("siteType", List.class);

        // 镇工业园
        List<String> streetId = jsonObject.getObject("streetId", List.class);
        List<String> parkId = jsonObject.getObject("parkId", List.class);
        String groupingOption = jsonObject.getObject("groupingOption", String.class);

        //所属行业
        List<String> industryInclude = jsonObject.getObject("industryInclude", List.class);

        mapAll.put("flag", flag);
        mapAll.put("year", year);
        mapAll.put("streetId", streetId);
        mapAll.put("parkId", parkId);
        mapAll.put("groupingOption", groupingOption);
        mapAll.put("typeId", siteType);
        mapAll.put("industryInclude", industryInclude);

        // 查询分组后的企业数
        List<Map<String, Object>> list = analysisMapper.screeningEnterprises2(mapAll);
        List<String> siteIds = null;
        if (list != null && !list.isEmpty()) {
            List<Map<String, Object>> divideList = new ArrayList();
            List<Map<String, Object>> factoryList = new ArrayList();

            for (Map<String, Object> map : list) {
                String count = StringHelper.nvl(map.get("count"), "");

                String siteIdCount = StringHelper.nvl(map.get("siteId"), "");
               /* String streetName = StringHelper.nvl(map.get("streetName"), "");
                String parkName = StringHelper.nvl(map.get("parkName"), "");
                String typeName = StringHelper.nvl(map.get("typeName"), "");
                String inTypeName = StringHelper.nvl(map.get("inTypeName"), "");*/

                siteIds = Arrays.asList(siteIdCount.split(","));
                // 根据siteId查询企业的镇，工业园，类型，行业
                List<Map<String, Object>> siteAllName = analysisMapper.getSiteAllName(siteIds);

                if (index != null && !index.isEmpty()) {
                    List<Map<String, Object>> indexId = analysisMapper.getAllIndexId(index);
                    Map<String, Object> map1 = new HashMap<>();
                    List<String> lis = new ArrayList<>();
                    for (Map<String, Object> index1 : indexId) {
                        String index_id = StringHelper.nvl(index1.get("index_id"), "");
                        lis.add(index_id);
                    }
                    map1.put("indexList", lis);
                    map1.put("siteList", siteIds);
                    List<Map<String, Object>> indexScore = analysisMapper.getIndexScoreByNoGroup(map1);
                    for (Map<String, Object> mapNormal : indexScore) {
                        String selfValue = mapNormal.get("selfValue").toString();
                        String streetValue = mapNormal.get("streetValue").toString();
                        String districtValue = mapNormal.get("districtValue").toString();
                        String reviewValue = mapNormal.get("reviewValue").toString();
                        if ("1".equals(flag)) {
                            BigDecimal divide = new BigDecimal(selfValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                        if ("2".equals(flag)) {
                            BigDecimal divide = new BigDecimal(streetValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                        if ("3".equals(flag)) {
                            BigDecimal divide = new BigDecimal(districtValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                        if ("4".equals(flag)) {
                            BigDecimal divide = new BigDecimal(reviewValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                    }
                } else {
                    // siteIds已经是分组过的
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("indexList", null);
                    map1.put("siteList", siteIds);
                    List<Map<String, Object>> normal = analysisMapper.getIndexScoreByNoGroup(map1);
                    for (Map<String, Object> mapNormal : normal) {
                        String selfValue = mapNormal.get("selfValue").toString();
                        String streetValue = mapNormal.get("streetValue").toString();
                        String districtValue = mapNormal.get("districtValue").toString();
                        String reviewValue = mapNormal.get("reviewValue").toString();
                        if ("1".equals(flag)) {
                            BigDecimal divide = new BigDecimal(selfValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                        if ("2".equals(flag)) {
                            BigDecimal divide = new BigDecimal(streetValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                        if ("3".equals(flag)) {
                            BigDecimal divide = new BigDecimal(districtValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                        if ("4".equals(flag)) {
                            BigDecimal divide = new BigDecimal(reviewValue).divide(new BigDecimal(siteIds.size()), 2, BigDecimal.ROUND_HALF_UP);
                            Map<String, Object> mapFinal = new HashMap<>();
                            if ("street".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("streetName"));
                                mapFinal.put("value", divide);
                            } else if ("industrialPark".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("parkName"));
                                mapFinal.put("value", divide);
                            } else if ("siteType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("typeName"));
                                mapFinal.put("value", divide);
                            } else if ("industryType".equals(groupingOption)) {
                                mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                                mapFinal.put("value", divide);
                            }
                            divideList.add(mapFinal);
                        }
                    }
                }

                // 和谐企业数
                String enterprise = analysisMapper.getCountHarmoniousEnterprise(siteIds);
                // 和谐先进企业数
                String countAdvancedEnterprise = analysisMapper.getCountAdvancedEnterprise(siteIds);

                Map<String, Object> mapFinal = new HashMap<>();
                List<String> listValue = new ArrayList<>();

                if ("street".equals(groupingOption)) {
                    mapFinal.put("name", siteAllName.get(0).get("streetName"));
                } else if ("industrialPark".equals(groupingOption)) {
                    mapFinal.put("name", siteAllName.get(0).get("parkName"));
                } else if ("siteType".equals(groupingOption)) {
                    mapFinal.put("name", siteAllName.get(0).get("typeName"));
                } else if ("industryType".equals(groupingOption)) {
                    mapFinal.put("name", siteAllName.get(0).get("inTypeName"));
                }
                listValue.add(count);//企业数
                if (enterprise != null && enterprise.length() != 0) {
                    listValue.add(enterprise);//和谐企业数
                } else {
                    listValue.add("0");//和谐企业数
                }
                if (countAdvancedEnterprise != null && countAdvancedEnterprise.length() != 0) {
                    listValue.add(countAdvancedEnterprise);//和谐企业先进数
                } else {
                    listValue.add("0");//和谐企业先进数
                }
                mapFinal.put("value", listValue);
                factoryList.add(mapFinal);
            }
            mapbefore.put("factory", factoryList);
            mapbefore.put("Average", divideList);
        } else {
            mapbefore.put("factory", "");
            mapbefore.put("Average", "");
        }
        return new Dto(true, "查询成功", mapbefore);
    }
}


