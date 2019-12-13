package com.jet.cloud.jetsndcxqyapply.mapper;

import com.jet.cloud.jetsndcxqyapply.entity.Company.*;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//企业接口类
@Component
public interface CompanySiteMapper {
    /**
     * 如果当前用户的角色属于注册账号，则在进入申报自评页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户“当前不允许自评申报，允许自评申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。
     */
    Integer checkIsRegisterNo(@Param("roldId") String roldId, @Param("userId") String userId);

    Map<String, Object> getRoleDate(@Param("roleId") String roleId);

    //查询如果用户有没有提交自评申报

    /**
     * 如果当前用户（企业）没有提交过自评，展示自评申报页面；
     * 如果已经提交过，则提示用户“您已提交自评申报，请到申报进展页面查询进展”。
     */
    TbSiteApplication checkIsCommit(@Param(value = "siteId") String siteId);

    //查询所有指标和标准值
    List<Muji> getm(String parent_id);

    // 企业申报自评的保存方法(企业申报指标分值表)
    int insertTbSiteApplicationIndexValue(List<TbSiteApplicationIndexValue> tbSiteApplicationIndexValue);

    // 企业申报自评的保存更新方法(企业申报指标分值表)
    int updateTbSiteApplicationIndexValue(List<TbSiteApplicationIndexValue> tbSiteApplicationIndexValue);

    int updateIndexValue(@Param("fileNames") String fileNames, @Param("seqId") String seqId);

    // 企业申报自评的保存方法(企业申报信息表)
    void insertTbSiteApplication(List<TbSiteApplication> TbSiteApplication);

    // 增加企业申报附件表
    void insertTbSiteApplicationAppendix(TbSiteApplicationAppendix t);

    void updateTbSiteApplicationAppendix(TbSiteApplicationAppendix t);

    // 添加log
    void insertLog(TbSysLog tbSysLog);

    //菜单权限检查
    Integer checkAuthority(@Param("roleId") String roleId, @Param("menuName") String menuName);

    //查询企业申报指标分值表
    TbSiteApplicationIndexValue getTbSiteApplicationIndexValue(@Param("siteId") String siteId, @Param("indexId") String indexId);

    //通过filename查询申报指标分值表
    List<TbSiteApplicationIndexValue> getTbSiteApplicationIndexValueByFileName(@Param("fileName") String fileName);

    //查询企业申报附件表
    TbSiteApplicationAppendix getTbSiteApplicationAppendix(@Param("seqId") String seqId);

    List<TbSiteApplicationAppendix> getTbSiteApplicationAppendix1(@Param("siteId") String sited);

    //按钮权限检查
    Integer getTbSysMenuFunc(@Param("roleId") String roleId, @Param("url") String url);

    //判断当年已经申请过的组织机构代码不能进行申报
    List<CompanySite> getOrgCodeByYear();

    // 校验企业是否有复审权限
    TbSiteApplication checkCompanyReview(@Param(value = "siteId") String siteId);

    // 企业镇区三方求和
    Map<String, Object> sumScore(String siteId);

    // 删除附件
    Integer deleteTbSiteApplicationAppendix(String seqId);

    // 复审材料页面查看
    List<TbSiteApplicationAppendix> getAllAppendix(String siteId);

    // 检查上传材料是否存在
    TbSiteApplicationAppendix checkIsExistAppendix(@Param(value = "siteId") String siteId, @Param(value = "fileName") String fileName);

    // 检查指标信息表的文件名称
    List<TBIndexMappingFileName> getFileName(String fileName);

}