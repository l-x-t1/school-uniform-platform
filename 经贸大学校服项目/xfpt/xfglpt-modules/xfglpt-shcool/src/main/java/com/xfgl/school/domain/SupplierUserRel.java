package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;

/**
 * 供应商用户关联对象 xfpt_supplier_user_rel
 *
 * @date 2026-05-31
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplier_user_rel")
@Schema(description = "供应商用户关联")
public class SupplierUserRel {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "厂商ID → xfpt_supplier_message.ID")
    private Long supplierId;

    @Schema(description = "用户ID → sys_user.user_id")
    private Long userId;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private java.util.Date createTime;

    @Schema(description = "用户名称")
    @TableField(exist = false)
    private String userName;

    @Schema(description = "用户昵称")
    @TableField(exist = false)
    private String nickName;

    @Schema(description = "用户手机号")
    @TableField(exist = false)
    private String phonenumber;
}
