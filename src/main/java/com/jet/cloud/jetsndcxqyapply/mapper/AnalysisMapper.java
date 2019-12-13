package com.jet.cloud.jetsndcxqyapply.mapper;

import com.jet.cloud.jetsndcxqyapply.entity.SysStreet2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//分析接口类
@Component
public interface AnalysisMapper {
    /**
     * 分析对象查找
     * 全区、具体镇(街道)、具体工业园 3级树状结构展示（多选、并且级联选择）
     */
    List<SysStreet2> getStreetAndIndustry();

    /**
     * 查询企业类型
     *
     * @return
     */
    List<Map<String, Object>> getTbSySSiteType();

    /**
     * 筛选企业
     * 年份选择 改为 分值类型：自评、测评、评定 复审（需要进一步选择年份） 4种
     * 分析对象默认全区
     * 企业类型
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> screeningEnterprises(Map<String, Object> map);

    /**
     * 企业个数
     *
     * @param map
     * @return
     */
    Integer sumScreeningEnterprises(Map<String, Object> map);

    /**
     * 默认查询自评的值
     *
     * @param list
     * @return
     */
    List<Map<String, Object>> getNormal(List<String> list);

    /**
     * 默认查询自评的值不分组
     *
     * @param list
     * @return
     */
    List<Map<String, Object>> getNormalByNoGroup(List<String> list);

    /**
     * 查询众数
     */
   /* Map<String, Object> getModeNumberSelfValue(List<String> list);

    Map<String, Object> getModeNumberStreetValue(List<String> list);

    Map<String, Object> getModeNumberDistrictValue(List<String> list);

    Map<String, Object> getModeNumberReviewValue(List<String> list);
*/
    /**
     * 查询所有的indexId
     * 返回值是null即是最末级指标
     */
    List<Map<String, Object>> getAllIndexId(List<String> list);

    /**
     * 查询添加指标后的分值
     */
    List<Map<String, Object>> getIndexScore(Map<String, Object> optionMap);

    /**
     * 查询添加指标后的分值不分组
     */
    List<Map<String, Object>> getIndexScoreByNoGroup(Map<String, Object> optionMap);


    // 得分分组统计

    /**
     * 筛选企业
     * 年份选择 改为 分值类型：自评、测评、评定 复审（需要进一步选择年份） 4种
     * 分析对象默认全区
     * 企业类型
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> screeningEnterprises2(Map<String, Object> map);

    /**
     * 查询公司的和谐企业数
     */
    String getCountHarmoniousEnterprise(List<String> list);

    /**
     * 查询公司的和谐先进企业数
     */
    String getCountAdvancedEnterprise(List<String> list);

    /**
     * 指标得分对比1 指标什么都不选 默认选择全部一级指标
     */
    List<Map<String, Object>> getIndexOne();

    /**
     * 查询所有的指标
     */
    List<Map<String, Object>> getIndexAll(List<String> list);

    /**
     * 查询所有的指标标准分总和
     */
    Double getIndexAllStandardSum(List<String> list);

    /**
     * 指标得分对比1 查询所有的指标之和
     */
    Map<String, Object> getIndexAllSum(Map<String, Object> map);

    /**
     * 指标得分对比1 查询所有的指标之和
     */
    String getIndexById(String indexId);

    /**
     * 判断传过来的是几级指标,通过parentId判断
     */
    List<Map<System, Object>> getParentId(String s);

    /**
     * 找到下面的儿子
     */
    Map<System, Object> getSonIndexId(String s);

    /**
     * 通过indexId集合找到对应的indexName
     */
    List<Map<String, Object>> getIndexIdList(List<String> list);

    /**
     * 通过三级指标找到对应的二级指标
     */
    List<Map<String, Object>> getTwoByThreeIndexId(List<String> list);

    /**
     * 通过index_id找到对应的name
     */
    String getIndexNameByIndexId(String indexId);

    /**
     * 根据siteId查询企业的镇，工业园，类型，行业
     */
    List<Map<String, Object>> getSiteAllName(List<String> list);
}