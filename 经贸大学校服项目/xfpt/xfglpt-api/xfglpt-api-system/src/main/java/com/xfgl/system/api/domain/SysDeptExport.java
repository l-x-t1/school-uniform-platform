package com.xfgl.system.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 * 
 * @author ruoyi
 */
public class SysDeptExport
{

    @Excel(name = "区县名称")
    /** 部门名称 */
    private String deptName;

    @Excel(name = "已开户学校数量（所）")
    //服务学校数量
    private String schoolSum;
    @Excel(name = "已完善信息学校数量（所）")
    //完善信息数量
    private String schoolComplete;
    @Excel(name = "未完善信息学校数量（所）")
    //未完善信息数量
    private String schoolNotComplete;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSchoolSum() {
        return schoolSum;
    }

    public void setSchoolSum(String schoolSum) {
        this.schoolSum = schoolSum;
    }

    public String getSchoolComplete() {
        return schoolComplete;
    }

    public void setSchoolComplete(String schoolComplete) {
        this.schoolComplete = schoolComplete;
    }

    public String getSchoolNotComplete() {
        return schoolNotComplete;
    }

    public void setSchoolNotComplete(String schoolNotComplete) {
        this.schoolNotComplete = schoolNotComplete;
    }

    @Override
    public String toString() {
        return "SysDeptExport{" +
                "deptName='" + deptName + '\'' +
                ", schoolSum='" + schoolSum + '\'' +
                ", schoolComplete='" + schoolComplete + '\'' +
                ", schoolNotComplete='" + schoolNotComplete + '\'' +
                '}';
    }
}
