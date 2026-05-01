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

import java.util.List;

/**
 * 家长购买意见征集活动子对象 xfpt_jz_buy_opinions_son
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
@TableName("xfpt_jz_buy_opinions_son")
@Schema(description = "家长购买意见征集活动子")
public class XfptJzBuyOpinionsSon extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主表id
     */
    @Excel(name = "主表id")
    @Schema(description = "主表id")
    private Long pId;

    /**
     * 采购年级
     */
    @Excel(name = "采购年级")
    @Schema(description = "采购年级")
    private String cgnj;

    /**
     * 年级id
     */
    private Long cgnjId;

    /**
     * 校服品类
     */
    @Excel(name = "校服品类")
    @Schema(description = "校服品类")
    private String xfpl;

    /**
     * 学生拟购买套数
     */
    @Excel(name = "学生拟购买套数")
    @Schema(description = "学生拟购买套数")
    private String xsngmts;

    /**
     * 每套价格区间
     */
    @Excel(name = "每套价格区间")
    @Schema(description = "每套价格区间")
    private String jgqj;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @Schema(description = "备注")
    private String bz;

    @TableField(exist = false)
    private List<Long> pidList;

}
