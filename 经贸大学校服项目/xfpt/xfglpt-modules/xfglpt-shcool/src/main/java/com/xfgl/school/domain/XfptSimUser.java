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
 * 石i民用户对象 xfpt_sim_user
 *
 * @author yinet
 * @date 2025-05-13
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_sim_user")
@Schema(description = "石i民用户")
public class XfptSimUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    @Schema(description = "姓名")
    private String name;

    /**
     * 身份证号
     */
    @Excel(name = "身份证号")
    @Schema(description = "身份证号")
    private String idCard;

    /**
     * 字段一
     */
    @Excel(name = "手机号")
    @Schema(description = "手机号")
    private String parm1;

    /**
     * 字段二
     */
    @Excel(name = "字段二")
    @Schema(description = "字段二")
    private String parm_2;

    /**
     * 字段三
     */
    @Excel(name = "字段三")
    @Schema(description = "字段三")
    private String parm_3;



}
