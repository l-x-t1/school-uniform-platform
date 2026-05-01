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
 * 家长建议征集活动配置对象 xfpt_jzbuy_opinions_config
 *
 * @author wxm
 * @date 2025-04-26
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_jzbuy_opinions_config")
@Schema(description = "家长建议征集活动配置")
public class XfptJzbuyOpinionsConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板类型
     */
    @Excel(name = "模板类型")
    @Schema(description = "模板类型")
    private String category;

    /**
     * 对应模板类型的模板值
     */
    @Excel(name = "对应模板类型的模板值")
    @Schema(description = "对应模板类型的模板值")
    private String content;

    /**
     * 部门id
     */
    @Schema(description = "部门id")
    private Long deptId;

    /**
     * 祖籍id
     */
    @Schema(description = "祖籍id")
    private String ancestors;

    /**
     * 类型  0教育局模版  1学校模版
     */
    private String type;

}
