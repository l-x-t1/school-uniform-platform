package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 确定供应商名单对象 xfpt_supplier_list
 *
 * @author yinet
 * @date 2025-05-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplier_list")
@Schema(description = "确定供应商名单")
public class XfptSupplierList extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "$column.columnComment")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    /**
     * 价格
     */
    @Excel(name = "价格")
    @Schema(description = "价格")
    private String price;

    /**
     * 文件
     */
    @Excel(name = "文件")
    @Schema(description = "文件")
    private String file;

    /**
     * 模式id
     */
    @Excel(name = "模式id")
    @Schema(description = "模式id")
    private String modeId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String ancestors;

    /**
     * 视频
     */
    private String videoUrl;

    /**
     * 链接地址
     */
    private String url;

}
