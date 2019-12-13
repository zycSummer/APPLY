package com.jet.cloud.jetsndcxqyapply.entity.Company;

import java.util.List;

public class Muji {
    private String indexId;
    private String indexName;
    private String parentId;
    private String valueType;
    private String enum1;
    private String max;
    private List<Muji> mujis;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getEnum1() {
        return enum1;
    }

    public void setEnum1(String enum1) {
        this.enum1 = enum1;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    private String min;

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<Muji> getMujis() {
        return mujis;
    }

    public void setMujis(List<Muji> mujis) {
        this.mujis = mujis;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "Muji{" +
                "indexId='" + indexId + '\'' +
                ", indexName='" + indexName + '\'' +
                ", parentId='" + parentId + '\'' +
                ", valueType='" + valueType + '\'' +
                ", enum1='" + enum1 + '\'' +
                ", mujis=" + mujis +
                ", max='" + max + '\'' +
                ", min='" + min + '\'' +
                '}';
    }
}
