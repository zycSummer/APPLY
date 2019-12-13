package com.jet.cloud.jetsndcxqyapply.common;

import java.util.HashMap;

public class Dto<T> extends HashMap {
    public Dto(T data) {
        this.put("success", true);
        this.put("msg", "success");
        this.put("data", data);
    }

    public Dto(boolean success) {
        this.put("msg", success);
        if (!success)
            this.put("msg", "fail");
    }

    public Dto(boolean success, String msg, T data) {
        this.put("success", success);
        this.put("msg", msg);
        this.put("data", data);
    }

    public Dto(boolean success, String msg, T data, T data2) {
        this.put("code", success);
        this.put("msg", msg);
        this.put("data", data);
        this.put("count", data2);
    }

    public static void main(String[] args) {
        System.out.println(new Dto(null));
    }
}
