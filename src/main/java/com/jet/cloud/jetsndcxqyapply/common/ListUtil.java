package com.jet.cloud.jetsndcxqyapply.common;

import java.util.*;

public class ListUtil {
    public static void main(String[] args) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(new HashMap() {{
            put("a", "1");
            put("b", "1");
            put("siteId", "1");
        }});
        list.add(new HashMap() {{
            put("c", "1");
            put("d", "1");
            put("siteId", "1");
        }});
        list.add(new HashMap() {{
            put("c", "1");
            put("d", "1");
            put("siteId", "2");
        }});
        list.add(new HashMap() {{
            put("e", "1");
            put("f", "1");
            put("siteId", "2");
        }});

        ListUtil.func(list);

        System.out.println(dic);

        List list1 = new ArrayList();
        for (Object value : dic.values()) {
            list1.add(value);
            System.out.println("Value = " + value);
        }
        System.out.println(list1);
    }

    public static Map<String, Map> dic = new HashMap<String, Map>();


    public static void func(List<Map<String, Object>> list) {
        Map son;
        for (Map map : list) {
            String siteId = (String) map.get("siteId");
            if (!ListUtil.isEmpty(siteId)) {
                if (ListUtil.isEmpty(son = dic.get(siteId))) {
                    son = new HashMap();
                    son.putAll(map);
                    dic.put(siteId, son);
                } else {
                    son.putAll(map);
                }
            }
        }

    }

    public static boolean isEmpty(Object pObj) {
        if (pObj == null)
            return true;
        if (pObj == "")
            return true;
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }
}
