package com.jet.cloud.jetsndcxqyapply.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.jet.cloud.jetsndcxqyapply.entity.Company.Muji;
import com.jet.cloud.jetsndcxqyapply.entity.SysStreet2;
import com.jet.cloud.jetsndcxqyapply.mapper.AnalysisMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictCommitteeMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnalysisTest {
    @Autowired
    private AnalysisMapper analysisMapper;
    @Autowired
    private CompanySiteMapper companySiteMapper;
    @Autowired
    private CompanySiteService companySiteService;
    @Autowired
    private DistrictCommitteeMapper districtCommitteeMapper;

    @Test
    public void getStreetAndIndustry() {
        List<SysStreet2> streetAndIndustry = analysisMapper.getStreetAndIndustry();
        String str = JSON.toJSONString(streetAndIndustry);
        System.out.println(str);
    }

    @Test
    public void ScreeningEnterprises() {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", "4");
        map.put("year", "2018");
        List<Map<String, Object>> maps = analysisMapper.screeningEnterprises(map);
        for (Map<String, Object> map1 : maps) {
            System.out.println(map1);
        }
    }

    @Test
    public void getm() {
        Map<String, Object> mapbefore = new HashMap<>();
        List<String> siteList = new ArrayList<>();
        siteList.add("10000001");
        siteList.add("10000007");
        //查询全部的一级指标
        List<Map<String, Object>> indexOne = analysisMapper.getIndexOne();
        // 默认找到全部的一级指标下面全面的末极指标
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
        System.out.println(list);
        // 所有一级的indexId
        List<String> indexIdOne = new ArrayList<>();
        for (Map<String, Object> map : indexOne) {
            String indexId = map.get("indexId").toString();
            indexIdOne.add(indexId);
        }
        Map<String, Object> map1 = new HashMap<>();
        // 遍历所有的一级指标拿到所有的末级指标
        for (int i = 0; i < indexIdOne.size(); i++) {
            List<String> list1 = (List) list.get(i).get(indexIdOne.get(i));
            // 所有末级指标
            System.out.println(list1);
            // 查询所有的指标数据
            List<Map<String, Object>> indexAll = analysisMapper.getIndexAll(list1);

            map1.put("indexList", list1);
            map1.put("siteList", siteList);
            // 查询指标得分
            Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);
            // 查询所有的指标标准分总和
            Double indexAllSum = analysisMapper.getIndexAllStandardSum(list1);
            Map<String, Object> map = new HashMap<>();
            if (true) {
                System.out.println(indexAllSum);
                map.put("standard", new BigDecimal(indexAllSum));
                map.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(5), 3, BigDecimal.ROUND_HALF_UP));
                String indexName = analysisMapper.getIndexById(indexIdOne.get(i).toString());
                mapbefore.put(indexName, map);
            }
        }
        System.out.println(mapbefore);
    }

    @Test
    public void getTbSySSiteType() {
        List<String> listSiteId = new ArrayList<>();
        listSiteId.add("10000001");
        listSiteId.add("10000007");
        List<Muji> getm = companySiteMapper.getm("1001");
        List<String> listThree = new ArrayList<>();
        for (Muji muji : getm) {
            List<Muji> getm1 = companySiteMapper.getm(muji.getIndexId());
            System.out.println(getm1);
            for (Muji muji1 : getm1) {
                listThree.add(muji1.getIndexId());
            }
        }
        System.out.println(listThree);
        List<Map<String, Object>> twoByThreeIndexId = analysisMapper.getTwoByThreeIndexId(listThree);
        Map<String, Object> map1 = new HashMap<>();
        for (Map<String, Object> map : twoByThreeIndexId) {
            String parentId = map.get("parentId").toString();
            String sec = map.get("sec").toString();
            // 二级指标对应每个三级指标
            List<String> stringList = Arrays.asList(sec.split(","));
            // 查询标准分
            Double indexAllSum = analysisMapper.getIndexAllStandardSum(stringList);

            map1.put("indexList", stringList);
            map1.put("siteList", listSiteId);
            // 查询指标得分
            Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);

        }

      /*  List<Map<String, Object>> streetAndIndustry = analysisMapper.getTbSySSiteType();
        String str = JSON.toJSONString(streetAndIndustry);
        System.out.println(str);*/
    }

    @Test
    public void getAllScore() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("flag", "1");
        List<Map<String, Object>> maps = analysisMapper.screeningEnterprises(map1);
        System.out.println(maps);
        List<Double> listSelf = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            listSelf.add(Double.parseDouble(map.get("selfValue").toString()));
        }
        Double max = Collections.max(listSelf);
        Double min = Collections.min(listSelf);
        System.out.println(max + "," + min);
    }

    @Test
    public void getNormal() {
        /*Double a = (95.6 - 53)/10;
        Double b =  53 + a*2;

        BigDecimal bd4 = new BigDecimal(Double.toString(a));
        BigDecimal bd2 = new BigDecimal(Double.toString(2));
        BigDecimal bd1 = new BigDecimal(Double.toString(53));
        BigDecimal add = bd1.add(bd4.multiply(bd2));*/

        List<String> list = new ArrayList<>();
        list.add("10000008");
        list.add("10000012");
        List<Map<String, Object>> normal = analysisMapper.getNormal(list);
//        Map<String, Object> modeNumber = analysisMapper.getModeNumberReviewValue(list);
        System.out.println(normal);
    }

    @Test
    public void getAllIndexId() {
        List<String> list = new ArrayList<>();
        list.add("1003");
//        list.add("1002");
        List<Map<String, Object>> indexId = analysisMapper.getAllIndexId(list);
        System.out.println(indexId);
    }

    @Test
    public void getIndexScore() {
        String s = "1002,1008,1003,1017";
        List<String> lis = Arrays.asList(s.split(","));
        List<String> siteList = new ArrayList<>();
        siteList.add("10000001");
        siteList.add("10000007");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("indexList", lis);
        map1.put("siteList", siteList);
        List<Map<String, Object>> score = analysisMapper.getIndexScore(map1);
        System.out.println(score);
    }

    @Test
    public void getCountHarmoniousEnterprise() {
        List<String> bigDecimalList = new ArrayList<>();
        bigDecimalList.add("10000015");
        String countHarmoniousEnterprise = analysisMapper.getCountHarmoniousEnterprise(bigDecimalList);
        System.out.println(countHarmoniousEnterprise);
    }

    @Test
    public void getIndexOne() {
        List<Map<String, Object>> indexOne = analysisMapper.getIndexOne();
        System.out.println(indexOne);
    }

    @Test
    public void getIndexAllStandardSum() {
        String s = "1003,1017";
        List<String> lis = Arrays.asList(s.split(","));
        List<String> siteList = new ArrayList<>();
        siteList.add("10000001");
        siteList.add("10000007");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("indexList", lis);
        map1.put("siteList", siteList);
        Map<String, Object> indexOne = analysisMapper.getIndexAllSum(map1);
        System.out.println(indexOne);
    }

    @Test
    public void getIndexOne1() {
        List<String> index = new ArrayList<>();
        index.add("1002");
        index.add("1008");

        List<String> indexList = new ArrayList<>();
        List<Muji> getm = companySiteMapper.getm("1002");
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
        System.out.println(indexList);
    }

    @Test
    public void getSonIndexId() {
        List<String> listSiteId = new ArrayList<>();
        listSiteId.add("10000001");
        listSiteId.add("10000007");

        Map<String, Object> mapbefore = new HashMap<>();

        List<Map<System, Object>> parentId = analysisMapper.getParentId("1002");
        Map<System, Object> sonIndexId = analysisMapper.getSonIndexId("1002");
        String sec = sonIndexId.get("sec").toString();
        List<String> stringList = Arrays.asList(sec.split(","));
        List<Map<String, Object>> indexIdList = analysisMapper.getIndexIdList(stringList);
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        for (Map<String, Object> map : indexIdList) {
            if ("1".equals("1")) {
                String max = map.get("max").toString();
                String indexName = map.get("indexName").toString();

                map1.put("indexList", stringList);
                map1.put("siteList", listSiteId);
                // 查询指标得分
                Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);

                map2.put("standard", new BigDecimal(max));
                map2.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(2), 2, BigDecimal.ROUND_HALF_UP));
                mapbefore.put(indexName, map2);
            }
        }
        System.out.println(indexIdList.size());
        System.out.println(indexIdList);
        System.out.println(mapbefore);
    }

    @Test
    public void getSonIndexId1() {
        List<String> listSiteId = new ArrayList<>();
        listSiteId.add("10000001");
        listSiteId.add("10000007");

        Map<String, Object> mapbefore = new HashMap<>();

        List<Map<System, Object>> parentId = analysisMapper.getParentId("1002");
        Map<System, Object> sonIndexId = analysisMapper.getSonIndexId("1002");
        String sec = sonIndexId.get("sec").toString();
        List<String> stringList = Arrays.asList(sec.split(","));
        List<Map<String, Object>> indexIdList = analysisMapper.getIndexIdList(stringList);
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        for (Map<String, Object> map : indexIdList) {
            if ("1".equals("1")) {
                String max = map.get("max").toString();
                String indexName = map.get("indexName").toString();

                map1.put("indexList", stringList);
                map1.put("siteList", listSiteId);
                // 查询指标得分
                Map<String, Object> indexScore = analysisMapper.getIndexAllSum(map1);

                map2.put("standard", new BigDecimal(max));
                map2.put("average", new BigDecimal(Double.toString(Double.parseDouble(indexScore.get("selfValue").toString()))).divide(new BigDecimal(2), 2, BigDecimal.ROUND_HALF_UP));
                mapbefore.put(indexName, map2);
            }
        }
        System.out.println(indexIdList.size());
        System.out.println(indexIdList);
        System.out.println(mapbefore);
    }

    @Test
    public void geSpilit() {
        String s1 = "[1003,1004]";
        String s2 = s1.substring(1, s1.length() - 1);
        String s = s2.replaceAll(" ", " ");
        List<String> list = Arrays.asList(s.split(","));
        System.out.println(s2);
        System.out.println(s);
        System.out.println(list);
    }

    @Test
    public void compare() {
       /* BigDecimal bigDecimal = new BigDecimal(51.5);
        BigDecimal bigDecimal1 = new BigDecimal(51.5);
        if (bigDecimal.compareTo(bigDecimal1) == 0) {
            System.out.println(111111);
        }*/
        List<String> stringList = new ArrayList<>();
        stringList.add("1003");
        System.out.println(stringList);
    }

    @Test
    public void screeningEnterprises2() {
        List<String> list = new ArrayList<>();
        list.add("1003");
        list.add("1002");
        list.add("1001");
        Map<String, Object> map = new HashMap<>();
        map.put("typeId", list);
        map.put("industryInclude", list);
        map.put("groupingOption", "street");
        List<Map<String, Object>> indexId = analysisMapper.screeningEnterprises2(map);
        System.out.println(indexId);
    }

    @Test
    public void listTest() throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
//        map.put("year", "2018");
//        map.put("reviewResult", "0");
        map.put("siteId", "10008");
//        map.put("reviewValue", "8.0");
        map.put("121", 12);
        list.add(map);

        List<Map<String, Object>> list1 = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();

        map1.put("f", 2);
        map1.put("g", 3);
        map1.put("siteId", "10008");
        list1.add(map1);

        JSONArray objects = JSON.parseArray(JSON.toJSONString(list));
        JSONArray objects1 = JSON.parseArray(JSON.toJSONString(list1));

        JSONArray jsonArray = joinJSONArray(objects, objects1);
        List listAll = new ArrayList<>();
        for (Object jstr : jsonArray) {
            listAll.add(jstr);
        }
        System.out.println(listAll);
    }

    public static JSONArray joinJSONArray(JSONArray... arrays) throws Exception {
        JSONArray ret = new JSONArray();
        Set<String> ids = new HashSet<String>();
        for (JSONArray array : arrays) {
            if (array == null) continue;
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (ids.contains(obj.get("siteId"))) continue;
                ret.add(obj);
                ids.add(obj.getString("siteId"));
            }
        }
        return ret;
    }

    @Test
    public void updateSiteReview() {
        Map<String, Object> map = new HashMap<>();
        map.put("year", Calendar.getInstance().get(Calendar.YEAR) - 1);
        Integer integer = districtCommitteeMapper.updateSiteReview(map);
        System.out.println(integer);
    }
}
