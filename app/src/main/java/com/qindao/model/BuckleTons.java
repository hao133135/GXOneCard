package com.qindao.model;

/**
 * Created by admin on 2017/9/28.
 */

public class BuckleTons {
    private String ID;
    private String COALBYTRUCKID;
    private String WEIGHT;
    private String REMARK;
    private String name;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCOALBYTRUCKID() {
        return COALBYTRUCKID;
    }

    public void setCOALBYTRUCKID(String COALBYTRUCKID) {
        this.COALBYTRUCKID = COALBYTRUCKID;
    }

    public String getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(String WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BuckleTons{" +
                "ID='" + ID + '\'' +
                ", COALBYTRUCKID='" + COALBYTRUCKID + '\'' +
                ", WEIGHT='" + WEIGHT + '\'' +
                ", REMARK='" + REMARK + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
