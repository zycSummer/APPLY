package com.jet.cloud.jetsndcxqyapply.mapper;

import com.jet.cloud.jetsndcxqyapply.entity.Company.TbIndex;
import com.jet.cloud.jetsndcxqyapply.entity.SysIndustrialPark;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReview;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//镇街道接口类
@Component
public interface StreetMapper {
    //查询当前登录的镇（街道）账号所关联的具体的镇（街道）下的工业园列表
    List<SysIndustrialPark> getSysIndustrialPark(String streetId);

    // 即默认查询当前登录的镇（街道）账号所关联的具体的镇（街道）下的所有工业园的所有企业
    List<Map<String, Object>> getTownStreet(Map<String, Object> map);

    // 查询总条数
    Integer getTownStreetSum(Map<String, Object> map);

    // 复审测评
    List<Map<String, Object>> streetReview(Map<String, Object> map);

    Integer sumStreetReview(Map<String, Object> map);

    // 复审测评保存
    Integer insertStreetReview(List<Map<String, Object>> tbSiteReviewIndexValue);

    TbSiteReview getTbSiteReview(TbSiteReview tbSiteReview);

    Integer insertSiteReview(TbSiteReview tbSiteReview);

    // 复审测评上报
    Integer updateStreetReview(TbSiteReview tbSiteReview);

    // 查询标准值
    List<Map<String, Object>> getIndex();

    // 通过siteId查询上传的文件seqIds(复审)
    List<Map<String, Object>> getSeqIdsBySiteId(String siteId);

    // 通过siteId查询上传的文件seqIds(申报)
    List<Map<String, Object>> getSeqIdsBySiteIdZero(String siteId);
}