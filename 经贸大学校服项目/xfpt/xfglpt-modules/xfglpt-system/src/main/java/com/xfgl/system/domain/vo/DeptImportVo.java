package com.xfgl.system.domain.vo;


import com.xfgl.common.core.annotation.Excel;
import lombok.Data;

@Data
public class DeptImportVo {

    /**
     * 区县
     */
    @Excel(name = "区县")
    public String district;


    /**
     * 学校名称
     */
    @Excel(name = "学校名称")
    public String schoolName;

    /**
     * 学校类别
     */
    @Excel(name = "学校类别")
    public String schoolType;

    /**
     * 学校区域
     */
    @Excel(name = "学校区域")
    public String schoolRegion;
}
