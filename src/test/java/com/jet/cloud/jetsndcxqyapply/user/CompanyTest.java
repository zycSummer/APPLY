package com.jet.cloud.jetsndcxqyapply.user;

import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplication;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplicationAppendix;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplicationIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@Ignore
@SpringBootTest
public class CompanyTest {
    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Autowired
    private CompanySiteService companySiteService;

    /**
     * 如果当前用户的角色属于注册账号，则在进入申报自评页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户当前不允许自评申报，允许自评申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。
     */
    @Test
    public void getUserById() {
        Integer qsfbgs1 = companySiteMapper.checkIsRegisterNo("1001", "qmjd");
        System.out.println(qsfbgs1);
    }

    @Test
    public void getUserById2() {
        Dto dto = companySiteService.checkIsRegisterNo("qmjd");
        System.out.println(dto);
    }

    @Test
    public void getUserById1() {
        Dto qsfbgs1 = companySiteService.checkIsRegisterNo("qmjd");
        System.out.println(qsfbgs1);
    }

    @Test
    public void checkIsCommitSelfEva() {
        TbSiteApplication tbSiteApplication = companySiteMapper.checkIsCommit("10000001");
        System.out.println(tbSiteApplication);
    }

    @Test
    public void insertTbSiteApplicationIndexValue() {
        List<TbSiteApplicationIndexValue> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TbSiteApplicationIndexValue tb = new TbSiteApplicationIndexValue();
            tb.setSiteId("10000009");
            tb.setIndexId("101" + i);
            tb.setSelfValue("1");
            list.add(tb);
        }
        int i = companySiteMapper.insertTbSiteApplicationIndexValue(list);
        System.out.println(i);
    }


    @Test
    public void checkAuthority() {
        Integer integer = companySiteMapper.checkAuthority("1001", "申报自评");
        System.out.println(integer);
    }

    @Test
    public void getTbSysMenuFunc() {
        Integer integer = companySiteMapper.getTbSysMenuFunc("1001", "/company/insertTbSiteApplicationIndexValue");
        System.out.println(integer);
    }

    @Test
    public void getOrgCodeByYear() {
        List<CompanySite> orgCodeByYear = companySiteMapper.getOrgCodeByYear();
        System.out.println(orgCodeByYear);
    }

    @Test
    public void sumScore() {
        Map<String, Object> maps = companySiteMapper.sumScore("10000001");
        System.out.println(maps.get("sumSelfValye"));
    }

    @Test
    public void sumScore1() {
        List<TbSiteApplicationIndexValue> tbSiteApplication = companySiteMapper.getTbSiteApplicationIndexValueByFileName("a.doc");
        for (TbSiteApplicationIndexValue tbSiteApplicationIndexValue : tbSiteApplication) {
            String seqId1 = tbSiteApplicationIndexValue.getSeqId().toString();
            String fileNames = tbSiteApplicationIndexValue.getFileNames();
            System.out.println(fileNames);
            String replace = fileNames.replace("," + "a.doc", "");
            if (replace.equals(fileNames)) {
                replace = fileNames.replace("a.doc" + ",", "");
            }
            System.out.println(replace);
            companySiteMapper.updateIndexValue(replace, seqId1);
        }
    }
}
