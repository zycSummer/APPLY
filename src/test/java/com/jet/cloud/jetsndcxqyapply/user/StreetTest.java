package com.jet.cloud.jetsndcxqyapply.user;

import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplication;
import com.jet.cloud.jetsndcxqyapply.entity.SysIndustrialPark;
import com.jet.cloud.jetsndcxqyapply.entity.UserRole;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSySPoints;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictCommitteeMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.StreetMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.StreetService;
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
public class StreetTest {
    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Autowired
    private StreetMapper streetMapper;

    @Autowired
    private StreetService streetService;

    @Autowired
    private DistrictCommitteeMapper districtCommitteeMapper;

    @Test
    public void getSysIndustrialPark() {
        List<SysIndustrialPark> industrialPark = streetMapper.getSysIndustrialPark("1003");
        System.out.println(industrialPark);
    }

    @Test
    public void getTownStreet() {
        Map<String, Object> map = new HashMap<>();
        map.put("streetId", "1003");
        map.put("page", 0);
        map.put("limit", 10);
       /* map.put("parkId", "");
        map.put("siteName", "");*/
//        map.put("siteName","朗吉科技有限公司");
        List<Map<String, Object>> street = streetMapper.getTownStreet(map);
        System.out.println(street.size());
        System.out.println(street);
    }

    @Test
    public void getTownStreetSum() {
        Map<String, Object> map = new HashMap<>();
        map.put("streetId", "1003");
        Integer i = streetMapper.getTownStreetSum(map);
        System.out.println(i);
    }

    @Test
    public void replaceTbSiteReviewIndexValue() {
        List<TbSiteReviewIndexValue> list = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            TbSiteReviewIndexValue tbSiteReviewIndexValue = new TbSiteReviewIndexValue();
            tbSiteReviewIndexValue.setYear("2019");
            tbSiteReviewIndexValue.setSiteId("1000001");
            tbSiteReviewIndexValue.setIndexId("101" + i);
            tbSiteReviewIndexValue.setReviewValue("2");
            list.add(tbSiteReviewIndexValue);
        }
        Integer siteReviewIndexValue = districtCommitteeMapper.replaceTbSiteReviewIndexValue(list);
        System.out.println(siteReviewIndexValue);
    }

    @Test
    public void getSystemUser() {
      /*  List<SystemUser> siteReviewIndexValue = districtCommitteeMapper.getSystemUser("qmjd", "青牧");
        System.out.println(siteReviewIndexValue);*/
    }

    @Test
    public void getUserRole() {
        Map<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("limit", 10);
        List<Map<String, Object>> allDistrictCommittee = districtCommitteeMapper.getAllDistrictCommittee(map);
        System.out.println(allDistrictCommittee);
    }

    @Test
    public void updateUserRole() {
        try {
            UserRole u = new UserRole();
            u.setRoleId("1001");
            u.setUpdateUserId("zz");
            u.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            Integer integer = districtCommitteeMapper.updateUserRole(u);
            System.out.println(integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertSysPoints() throws Exception {
        TbSySPoints sysPoints = districtCommitteeMapper.getSysPoints();
        sysPoints.setPoints("dfssfds11111");
        sysPoints.setCreateTime(DateUtils.dateFormatYYMMDD());
        sysPoints.setCreateUserId("1");
        Integer integer = districtCommitteeMapper.updateSysPoints(sysPoints);
        System.out.println(integer);
    }

    @Test
    public void getSysLog() {
        try {
            Map<String, Object> map = new HashMap<>();
            List<TbSysLog> sysLog = districtCommitteeMapper.getSysLog(map);
            System.out.println(sysLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getByYearAndSiteId() {
        try {
            List<TbSiteApplication> listTbSiteApplication = new ArrayList<>();
            TbSiteApplication application = companySiteMapper.checkIsCommit("10000015");
            application.setStreetSuggestion("111");
            listTbSiteApplication.add(application);
            companySiteMapper.insertTbSiteApplication(listTbSiteApplication);
            System.out.println("11111");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        List<BigDecimal> listSelf = new ArrayList<>();
        listSelf.add(new BigDecimal(Double.toString(1.1)));
        listSelf.add(new BigDecimal(Double.toString(1.9)));
        listSelf.add(new BigDecimal(Double.toString(3.1)));
        BigDecimal decimal = new BigDecimal(Double.toString(0d));
        Double dou = 0d;
        for (BigDecimal d : listSelf) {
            dou += decimal.add(d).doubleValue();
        }
        BigDecimal bd4 = new BigDecimal(Double.toString(5));
        BigDecimal bd5 = new BigDecimal(Double.toString(dou));
        double doubleValue = bd5.divide(bd4).doubleValue();
        System.out.println(doubleValue);
    }

    @Test
    public void test1() {
        String s = "1002,1008,1013,1017";
        List<String> lis = Arrays.asList(s.split(","));
        System.out.println(lis);
    }
}
