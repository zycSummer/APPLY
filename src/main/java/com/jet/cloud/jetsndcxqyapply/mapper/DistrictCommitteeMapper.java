package com.jet.cloud.jetsndcxqyapply.mapper;

import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.entity.UserRole;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReview;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSySPoints;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSysTitle;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//区委员会接口类
@Component
public interface DistrictCommitteeMapper {
    // 汇总页面展示
    List<Map<String, Object>> getAllDistrictCommittee(Map<String, Object> map);

    // 查询总条数
    Integer getDistrictCommitteeSum(Map<String, Object> map);

    // 查询称号列表
    List<TbSysTitle> getTbSysTitle();

    // 根据称号查询
    TbSysTitle getTbSysTitleByTitleId(String titleId);

    // 复审页面展示
    List<Map<String, Object>> getAllReviewDistrictCommittee(Map<String, Object> map);

    //根据year,siteId查询年度复审值
    List<TbSiteReviewIndexValue> getByYearAndSiteId(Map<String, Object> map);

    // 复审页面展示总数
    Integer getSumReviewDistrictCommittee(Map<String, Object> map);

    //查询企业年度复审指标分值表
    TbSiteReviewIndexValue getTbSiteReviewIndexValue(TbSiteReviewIndexValue TbSiteReviewIndexValue);
    List<TbSiteReviewIndexValue> getTbSiteReviewIndexValue1(TbSiteReviewIndexValue TbSiteReviewIndexValue);

    //查询企业年度复审信息表
    TbSiteReview getTbSiteReview(TbSiteReview tbSiteReview);

    // 复审页面(保存)--企业年度复审指标分值表
    Integer replaceTbSiteReviewIndexValue(List<TbSiteReviewIndexValue> tbSiteReviewIndexValue);

    // 复审页面(保存)--企业年度复审信息表
    Integer replaceTbSiteReview(TbSiteReview tbSiteReview);

    // 账户管理页面查询
    List<SystemUser> getSystemUser(Map<String, Object> map);

    // 账户管理页面求和
    Integer getSumSystemUser(Map<String, Object> map);

    // 账户时限管理查询
    List<UserRole> getUserRole(Map<String, Object> map);

    // 账户时限管理求和
    Integer getSumUserRole(Map<String, Object> map);

    // 账户时限管理修改
    Integer updateUserRole(UserRole userRole);

    // 查看系统提示
    TbSySPoints getSysPoints();

    // 保存系统提示
    Integer insertSysPoints(TbSySPoints tbSySPoints);

    // 更新系统提示
    Integer updateSysPoints(TbSySPoints tbSySPoints);

    // 系统日志查询
    List<TbSysLog> getSysLog(Map<String, Object> map);

    // 系统日志求和
    Integer getSumSysLog(Map<String, Object> map);

    // 根据siteId 查询reviewValue，review_result
    List<Map<String,Object>> getReValueAndResult(Map<String, Object> map);

    // 更新复审信息tb_site_review
    Integer updateSiteReview(Map<String, Object> map);
}
