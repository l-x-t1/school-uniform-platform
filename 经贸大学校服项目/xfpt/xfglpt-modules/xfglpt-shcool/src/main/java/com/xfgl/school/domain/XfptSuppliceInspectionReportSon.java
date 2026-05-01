package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 学校与供应商检验报告从对象 xfpt_supplice_inspection_report_son
 *
 * @author yinet
 * @date 2025-04-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplice_inspection_report_son")
@Schema(description = "学校与供应商检验报告从")
public class XfptSuppliceInspectionReportSon extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 质检报告标题
     */
    @Excel(name = "质检报告标题")
    @Schema(description = "质检报告标题")
    private String inspectionReportTitle;

    /**
     * 附件路径
     */
    @Excel(name = "附件路径")
    @Schema(description = "附件路径")
    private String url;
    /**
     * 附件路径
     */
    @Excel(name = "学校与供应商检验报告关联表id")
    @Schema(description = "学校与供应商检验报告关联表id")
    private String reportId;


    /**
     * 附件路径
     */
    @Excel(name = "附件名字")
    @Schema(description = "附件名字")
    private String fileName;
    /**
     * 附件类型
     */
    @Excel(name = "附件类型")
    @Schema(description = "附件类型")
    private String fileType;

    private String type;
}
