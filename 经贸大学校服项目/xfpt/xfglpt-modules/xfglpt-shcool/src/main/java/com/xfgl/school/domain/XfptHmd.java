package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 黑名单对象 xfpt_hmd
 *
 * @author yinet
 * @date 2025-10-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_hmd")
@Schema(description = "黑名单")
public class XfptHmd extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** $column.columnComment */
        @TableId(type= IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 厂商名称 */
            @Excel(name = "厂商名称")
    @Schema(description = "厂商名称")
    private String name;

    /** 企业信用代码 */
            @Excel(name = "企业信用代码")
    @Schema(description = "企业信用代码")
    private String xydm;

    /** 法定代表人 */
            @Excel(name = "法定代表人")
    @Schema(description = "法定代表人")
    private String userName;

        }
