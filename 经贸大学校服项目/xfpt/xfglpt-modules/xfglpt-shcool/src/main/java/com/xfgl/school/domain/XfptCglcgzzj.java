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
 * 学校校服采购工作总结对象 xfpt_cglcgzzj
 *
 * @author yinet
 * @date 2025-07-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_cglcgzzj")
@Schema(description = "学校校服采购工作总结")
public class XfptCglcgzzj extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** $column.columnComment */
        @TableId(type= IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /** $column.columnComment */
            @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String title;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private String file;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long teamId;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long deptId;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private String ancestors;

        }
