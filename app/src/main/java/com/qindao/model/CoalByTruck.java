package com.qindao.model;

import java.io.File;
import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public class CoalByTruck {
    private String COALBYTRUCKID;//单号
    private String RECLCOALFIELDID;//实际区域id
    private String AD;//目测热值
    private String REMARK;//备注
    private String CHECKPHOTO1;
    private String CHECKPHOTO2;
    private String CHECKPHOTO3;
    private String CHECKPHOTO4;
    private File IMAGEFILE1;
    private File IMAGEFILE2;
    private File IMAGEFILE3;
    private File IMAGEFILE4;


    private List<com.qindao.model.BuckleTons> BuckleTons;

    public String getCOALBYTRUCKID() {
        return COALBYTRUCKID;
    }

    public void setCOALBYTRUCKID(String COALBYTRUCKID) {
        this.COALBYTRUCKID = COALBYTRUCKID;
    }

    public String getRECLCOALFIELDID() {
        return RECLCOALFIELDID;
    }

    public void setRECLCOALFIELDID(String RECLCOALFIELDID) {
        this.RECLCOALFIELDID = RECLCOALFIELDID;
    }

    public String getAD() {
        return AD;
    }

    public void setAD(String AD) {
        this.AD = AD;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getCHECKPHOTO1() {
        return CHECKPHOTO1;
    }

    public void setCHECKPHOTO1(String CHECKPHOTO1) {
        this.CHECKPHOTO1 = CHECKPHOTO1;
    }

    public String getCHECKPHOTO2() {
        return CHECKPHOTO2;
    }

    public void setCHECKPHOTO2(String CHECKPHOTO2) {
        this.CHECKPHOTO2 = CHECKPHOTO2;
    }

    public String getCHECKPHOTO3() {
        return CHECKPHOTO3;
    }

    public void setCHECKPHOTO3(String CHECKPHOTO3) {
        this.CHECKPHOTO3 = CHECKPHOTO3;
    }

    public String getCHECKPHOTO4() {
        return CHECKPHOTO4;
    }

    public void setCHECKPHOTO4(String CHECKPHOTO4) {
        this.CHECKPHOTO4 = CHECKPHOTO4;
    }

    public List<com.qindao.model.BuckleTons> getBuckleTons() {
        return BuckleTons;
    }

    public void setBuckleTons(List<com.qindao.model.BuckleTons> buckleTons) {
        BuckleTons = buckleTons;
    }

    public File getIMAGEFILE1() {
        return IMAGEFILE1;
    }

    public void setIMAGEFILE1(File IMAGEFILE1) {
        this.IMAGEFILE1 = IMAGEFILE1;
    }

    public File getIMAGEFILE2() {
        return IMAGEFILE2;
    }

    public void setIMAGEFILE2(File IMAGEFILE2) {
        this.IMAGEFILE2 = IMAGEFILE2;
    }

    public File getIMAGEFILE3() {
        return IMAGEFILE3;
    }

    public void setIMAGEFILE3(File IMAGEFILE3) {
        this.IMAGEFILE3 = IMAGEFILE3;
    }

    public File getIMAGEFILE4() {
        return IMAGEFILE4;
    }

    public void setIMAGEFILE4(File IMAGEFILE4) {
        this.IMAGEFILE4 = IMAGEFILE4;
    }

    @Override
    public String toString() {
        return "CoalByTruck{" +
                "COALBYTRUCKID='" + COALBYTRUCKID + '\'' +
                ", RECLCOALFIELDID='" + RECLCOALFIELDID + '\'' +
                ", AD='" + AD + '\'' +
                ", REMARK='" + REMARK + '\'' +
                ", CHECKPHOTO1='" + CHECKPHOTO1 + '\'' +
                ", CHECKPHOTO2='" + CHECKPHOTO2 + '\'' +
                ", CHECKPHOTO3='" + CHECKPHOTO3 + '\'' +
                ", CHECKPHOTO4='" + CHECKPHOTO4 + '\'' +
                ", IMAGEFILE1=" + IMAGEFILE1 +
                ", IMAGEFILE2=" + IMAGEFILE2 +
                ", IMAGEFILE3=" + IMAGEFILE3 +
                ", IMAGEFILE4=" + IMAGEFILE4 +
                ", BuckleTons=" + BuckleTons +
                '}';
    }
}
