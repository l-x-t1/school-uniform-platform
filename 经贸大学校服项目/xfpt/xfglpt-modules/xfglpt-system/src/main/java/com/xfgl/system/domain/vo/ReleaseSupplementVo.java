package com.xfgl.system.domain.vo;

import lombok.Data;

@Data
public class ReleaseSupplementVo {

    private Long teamId;

    private String title;

    /**
     * 选购公告/合同公告 CGGG  企业入围公告QYRWGG
     */
    private String catagory;

    private String file;

    /**
     * 补发公告的采购id
     */
    private Long id;


    /**
     * 0采购公告  1合同公告 2入围公告
     */
    private String type;
}
