package com.jet.cloud.jetsndcxqyapply.service.analysis.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.Company.Muji;
import com.jet.cloud.jetsndcxqyapply.mapper.AnalysisMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.service.analysis.AnalysisIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AnalysisIndexServiceImpl implements AnalysisIndexService {
    @Autowired
    private AnalysisMapper analysisMapper;

    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Override
    public Dto getIndexScore1(JSONObject jsonObject) {
        Map<String, Object> mapAll = new HashMap<>();
        List<Map<String, Object>> mapbefore = new ArrayList<>();
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
        String index = jsonObject.getObject("indexIds", String.class);

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


        Integer sum = analysisMapper.sumScreeningEnterprises(mapAll);
        List<Map<String, Object>> maps = analysisMapper.screeningEnterprises(mapAll);
        List<String> listSiteId = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            listSiteId.add(map.get("siteId").toString());
        }
        if (maps != null && !maps.isEmpty()) {
            if (index != null && !index.isEmpty()) {
                List<Map<String, Object>> dto = getDto(mapbefore, flag, index, sum, listSiteId);
                System.out.println(dto);
                if (dto != null && dto.size() > 0) {
                    return new Dto(true, "查询成功", dto);
                } else {
                    return new Dto(false, "查询失败", null);
                }
            } else {
                //查询全部的一级指标
                List<Map<String, Object>> indexOne = analysisMapper.getIndexOne();
                // 默认找到全部的一级指标下面全部的末极指标
                List<Muji> getm = companySiteMapper.getm(null);
                List<Map<String, Object>> list = new ArrayList<>();
                for (Muji map1 : getm) {
                    List<String> stringList = new ArrayList<>();
                    String indexId = map1.getIndexId();
                    List<Muji> mujis = map1.getMujis();
                    for (Muji map2 : mujis) {
                        List<Muji> mujis2 = map2.getMujis();
                        for (Muji map3 : mujis2) {
                            String indexId1 = map3.getIndexId();
                            stringList.add(indexId1);
                        }
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put(indexId, stringList);
                    list.add(map);
                }
                // 所有一级的indexId
                List<String> indexIdOne = new ArrayList<>();
                for (Map<String, Object> map : indexOne) {
                    String indexId = map.get("indexId").toString();
                    indexIdOne.add(indexId);
                }
                Map<String, Object> map1 = new HashMap<>();
                // 遍历所有的一级指标拿到所有的末级指标
                for (int i = 0; i < indexIdOne.size(); i++) {
                    String string = list.get(i).get(indexIdOne.get(i)).toString();
                    String s2 = string.substring(1, string.length() - 1);
                    String s = s2.replaceAll(" ", "");
                    // 所有末级指标
                    List<String> stringList = Arrays.asList(s.split(","));
                    // 查询所有的指标数据
                    List<Map<String, Object>> indexAll = analysisMapper.getIndexAll(stringList);

                    map1.put("indexList", stringList);
                    map1.put("siteList", listSiteId);
                    // 查询指标得分
                    Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
                    // 查询所有的指标标准分总和
                    Double indexAllSum = analysisMapper.getIndexAllStandardSum(stringList);
                    Map<String, Object> map = new HashMap<>();
                    if ("1".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("2".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("streetValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("3".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("districtValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("4".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("reviewValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                }
                return new Dto(true, "查询成功", mapbefore);
            }
        } else {
            return new Dto(true, "无企业", mapbefore);
        }
    }

    private List<Map<String, Object>> getDto(List<Map<String, Object>> mapbefore, String flag, String index, Integer sum, List<String> listSiteId) {
        try {
            // 判断传过来的是几级指标,通过parentId判断
            List<Map<System, Object>> parentId = analysisMapper.getParentId(index);
            // 不为空就是二级指标或者三级指标
            if (parentId != null && !parentId.isEmpty()) {
                // 找到所有的三级指标
                Map<System, Object> sonIndexId = analysisMapper.getSonIndexId(index);
                System.out.println(sonIndexId);
                String sec = StringHelper.nvl(sonIndexId.get("sec"), "");
                List<String> stringList = new ArrayList<>();
                if (sec == null || "".equals(sec)) {
                    System.out.println(sonIndexId.get("index_id").toString());
                    stringList.add(sonIndexId.get("index_id").toString());
                } else {
                    // 拆分三级指标变为list
                    stringList = Arrays.asList(sec.split(","));
                }
                // 找到对应三级指标所有值
                List<Map<String, Object>> indexIdList = analysisMapper.getIndexIdList(stringList);
                System.out.println(indexIdList);
                Map<String, Object> map1 = new HashMap<>();
                if (indexIdList.size() == stringList.size()) {
                    for (int i = 0; i < indexIdList.size(); i++) {
                        if ("1".equals(flag)) {
                            String max = indexIdList.get(i).get("max").toString();
                            String indexName = indexIdList.get(i).get("indexName").toString();

                            Map<String, Object> map2 = new HashMap<>();
                            map1.put("indexList", Arrays.asList(stringList.get(i).toString().split(",")));
                            map1.put("siteList", listSiteId);
                            // 查询指标得分
                            Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
                            map2.put("standard", new BigDecimal(max));
                            map2.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                            map2.put("name", indexName);
                            mapbefore.add(map2);
                        }
                        if ("2".equals(flag)) {
                            String max = indexIdList.get(i).get("max").toString();
                            String indexName = indexIdList.get(i).get("indexName").toString();

                            Map<String, Object> map2 = new HashMap<>();
                            map1.put("indexList", Arrays.asList(stringList.get(i).toString().split(",")));
                            map1.put("siteList", listSiteId);
                            // 查询指标得分
                            Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
                            map2.put("standard", new BigDecimal(max));
                            map2.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("streetValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                            map2.put("name", indexName);
                            mapbefore.add(map2);
                        }
                        if ("3".equals(flag)) {
                            String max = indexIdList.get(i).get("max").toString();
                            String indexName = indexIdList.get(i).get("indexName").toString();

                            Map<String, Object> map2 = new HashMap<>();
                            map1.put("indexList", Arrays.asList(stringList.get(i).toString().split(",")));
                            map1.put("siteList", listSiteId);
                            // 查询指标得分
                            Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
                            map2.put("standard", new BigDecimal(max));
                            map2.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("districtValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                            map2.put("name", indexName);
                            mapbefore.add(map2);
                        }
                        if ("4".equals(flag)) {
                            String max = indexIdList.get(i).get("max").toString();
                            String indexName = indexIdList.get(i).get("indexName").toString();

                            Map<String, Object> map2 = new HashMap<>();
                            map1.put("indexList", Arrays.asList(stringList.get(i).toString().split(",")));
                            map1.put("siteList", listSiteId);
                            // 查询指标得分
                            Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
                            map2.put("standard", new BigDecimal(max));
                            map2.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("reviewValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                            map2.put("name", indexName);
                            mapbefore.add(map2);
                        }
                    }
                }
            }
            // 为空就是一级指标
            else {
                // 找到二级指标下面对应的三级指标listThree
                List<Muji> getm = companySiteMapper.getm(index);
                List<String> listThree = new ArrayList<>();
                for (Muji muji : getm) {
                    List<Muji> getm1 = companySiteMapper.getm(muji.getIndexId());
                    for (Muji muji1 : getm1) {
                        listThree.add(muji1.getIndexId());
                    }
                }
                // 通过三级指标找到对应的二级指标
                List<Map<String, Object>> twoByThreeIndexId = analysisMapper.getTwoByThreeIndexId(listThree);
                Map<String, Object> map1 = new HashMap<>();
                Map<String, Object> map2 = new HashMap<>();
                for (Map<String, Object> map : twoByThreeIndexId) {
                    String parentId1 = map.get("parentId").toString();
                    // 找到二级指标的名称
                    String indexName = analysisMapper.getIndexNameByIndexId(parentId1);
                    String sec = map.get("sec").toString();
                    // 拆分二级指标对应每个三级指标
                    List<String> stringList1 = Arrays.asList(sec.split(","));
                    // 查询标准分
                    Double indexAllSum = analysisMapper.getIndexAllStandardSum(stringList1);

                    map1.put("indexList", stringList1);
                    map1.put("siteList", listSiteId);
                    // 查询指标得分
                    Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);

                    if ("1".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("2".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("streetValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("3".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("districtValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("4".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("reviewValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                }
            }
            return mapbefore;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Dto getIndexScore2(JSONObject jsonObject) {
        Map<String, Object> mapAll = new HashMap<>();
        List<Map<String, Object>> mapbefore = new ArrayList<>();
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
        List<String> typeId = jsonObject.getObject("siteType", List.class);

        // 镇工业园
        List<String> streetId = jsonObject.getObject("streetId", List.class);
        List<String> parkId = jsonObject.getObject("parkId", List.class);

        //所属行业
        List<String> industryInclude = jsonObject.getObject("industryInclude", List.class);

        mapAll.put("flag", flag);
        mapAll.put("streetId", streetId);
        mapAll.put("parkId", parkId);
        mapAll.put("typeId", typeId);
        mapAll.put("industryInclude", industryInclude);

        Integer sum = analysisMapper.sumScreeningEnterprises(mapAll);
        List<Map<String, Object>> maps = analysisMapper.screeningEnterprises(mapAll);
        List<String> listSiteId = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            listSiteId.add(map.get("siteId").toString());
        }
        if (maps != null && !maps.isEmpty()) {
            if (index != null && !index.isEmpty()) {
                List<Map<String, Object>> map = null;
                for (String string : index) {
                    map = getDto(mapbefore, flag, string, sum, listSiteId);
                }
                if (map != null) {
                    return new Dto(true, "查询成功", map);
                } else {
                    return new Dto(false, "查询失败", null);
                }
            } else {
                //查询全部的一级指标
                List<Map<String, Object>> indexOne = analysisMapper.getIndexOne();
                // 默认找到全部的一级指标下面全部的末极指标
                List<Muji> getm = companySiteMapper.getm(null);
                List<Map<String, Object>> list = new ArrayList<>();
                for (Muji map1 : getm) {
                    List<String> stringList = new ArrayList<>();
                    String indexId = map1.getIndexId();
                    List<Muji> mujis = map1.getMujis();
                    for (Muji map2 : mujis) {
                        List<Muji> mujis2 = map2.getMujis();
                        for (Muji map3 : mujis2) {
                            String indexId1 = map3.getIndexId();
                            stringList.add(indexId1);
                        }
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put(indexId, stringList);
                    list.add(map);
                }
                // 所有一级的indexId
                List<String> indexIdOne = new ArrayList<>();
                for (Map<String, Object> map : indexOne) {
                    String indexId = map.get("indexId").toString();
                    indexIdOne.add(indexId);
                }
                Map<String, Object> map1 = new HashMap<>();
                // 遍历所有的一级指标拿到所有的末级指标
                for (int i = 0; i < indexIdOne.size(); i++) {
                    String string = list.get(i).get(indexIdOne.get(i)).toString();
                    String s2 = string.substring(1, string.length() - 1);
                    String s = s2.replaceAll(" ", "");
                    // 所有末级指标
                    List<String> stringList = Arrays.asList(s.split(","));
                    // 查询所有的指标数据
                    List<Map<String, Object>> indexAll = analysisMapper.getIndexAll(stringList);

                    map1.put("indexList", stringList);
                    map1.put("siteList", listSiteId);
                    // 查询指标得分
                    Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
                    // 查询所有的指标标准分总和
                    Double indexAllSum = analysisMapper.getIndexAllStandardSum(stringList);
                    Map<String, Object> map = new HashMap<>();
                    if ("1".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("2".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("streetValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("3".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("districtValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                    if ("4".equals(flag)) {
                        map.put("standard", new BigDecimal(indexAllSum));
                        map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("reviewValue").toString()))).divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
                        String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                        map.put("name", indexName);
                        mapbefore.add(map);
                    }
                }
                return new Dto(true, "查询成功", mapbefore);
            }
        } else {
            return new Dto(true, "无企业", mapbefore);
        }
    }

    private void function(String index) {
        List<String> indexList = new ArrayList<>();
        List<Muji> getm = companySiteMapper.getm(index);
        if (getm != null && !getm.isEmpty()) {
            for (Muji muji : getm) {
                List<Muji> getm1 = companySiteMapper.getm(muji.getIndexId());
                if (getm1 != null && !getm1.isEmpty()) {
                    for (Muji muj : getm1) {
                        List<Muji> getm2 = companySiteMapper.getm(muj.getIndexId());
                        if (getm2 != null && !getm2.isEmpty()) {
                        } else {
                            indexList.add(muj.getIndexId());
                        }
                    }
                } else {
                    indexList.add(muji.getIndexId());
                }
            }
        }
    }
}
