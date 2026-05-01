package com.xfgl.supplier.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 学生尺码扩展对象 xfpt_student_size_ext
 *
 * @author yinet
 * @date 2026-05-01
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_student_size_ext")
@Schema(description = "学生尺码扩展")
public class XfptStudentSizeExt extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 学生ID */
            @Excel(name = "学生ID")
    @Schema(description = "学生ID")
    private Long studentId;

    /** 身高 */
            @Excel(name = "身高")
    @Schema(description = "身高")
    private BigDecimal height;

    /** 体重 */
            @Excel(name = "体重")
    @Schema(description = "体重")
    private BigDecimal weight;

    /** 胸围 */
            @Excel(name = "胸围")
    @Schema(description = "胸围")
    private BigDecimal chest;

    /** 腰围 */
            @Excel(name = "腰围")
    @Schema(description = "腰围")
    private BigDecimal waistline;

    /** 臀围 */
            @Excel(name = "臀围")
    @Schema(description = "臀围")
    private BigDecimal hipline;

    /** 推荐尺码ID */
            @Excel(name = "推荐尺码ID")
    @Schema(description = "推荐尺码ID")
    private Long sizeId;

    /** 量体时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "量体时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "量体时间")
    private Date measureTime;

    /** 量体备注 */
            @Excel(name = "量体备注")
    @Schema(description = "量体备注")
    private String measureRemark;

        }
