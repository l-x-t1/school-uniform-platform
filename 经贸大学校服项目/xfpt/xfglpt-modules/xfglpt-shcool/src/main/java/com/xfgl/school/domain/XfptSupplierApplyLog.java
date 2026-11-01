package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplier_apply_log")
@Schema(description = "供应商报名日志")
public class XfptSupplierApplyLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "报名ID")
    private Long applyId;

    @Schema(description = "公告ID")
    private Long noticeId;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "版本号")
    private Integer versionNo;

    @Schema(description = "动作备注")
    private String actionRemark;

    @Schema(description = "动作人")
    private String actionUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "动作时间")
    private Date actionTime;

    @Schema(description = "材料快照JSON")
    private String snapshotJson;
}
