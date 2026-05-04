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
 * 用户收货地址对象 xfpt_user_address
 *
 * @author yinet
 * @date 2026-05-04
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_user_address")
@Schema(description = "用户收货地址")
public class XfptUserAddress extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 家长用户ID */
            @Excel(name = "家长用户ID")
    @Schema(description = "家长用户ID")
    private Long userId;

    /** 收货人姓名 */
            @Excel(name = "收货人姓名")
    @Schema(description = "收货人姓名")
    private String receiverName;

    /** 收货手机号 */
            @Excel(name = "收货手机号")
    @Schema(description = "收货手机号")
    private String receiverPhone;

    /** 省 */
            @Excel(name = "省")
    @Schema(description = "省")
    private String provinceName;

    /** 市 */
            @Excel(name = "市")
    @Schema(description = "市")
    private String cityName;

    /** 区县 */
            @Excel(name = "区县")
    @Schema(description = "区县")
    private String districtName;

    /** 详细地址 */
            @Excel(name = "详细地址")
    @Schema(description = "详细地址")
    private String detailAddress;

    /** 地址类型，1家庭 2学校 */
            @Excel(name = "地址类型，1家庭 2学校")
    @Schema(description = "地址类型，1家庭 2学校")
    private String addressType;

    /** 是否默认，0否 1是 */
            @Excel(name = "是否默认，0否 1是")
    @Schema(description = "是否默认，0否 1是")
    private String isDefault;

        }
