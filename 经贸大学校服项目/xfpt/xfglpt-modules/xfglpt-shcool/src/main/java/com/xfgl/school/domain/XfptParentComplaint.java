package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_parent_complaint")
@Schema(description = "Parent complaint")
public class XfptParentComplaint extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String userName;

    private String deptId;

    private String deptName;

    private Long orderId;

    private String orderNo;

    private Long orderItemId;

    private Long goodsId;

    private String goodsName;

    private Long supplierId;

    private String supplierName;

    private String complaintType;

    private String complaintReason;

    private String complaintImages;

    private String contactPhone;

    private String status;

    private String handleContent;

    private String handleBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;
}
