package com.xfgl.supplier.domain;

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
 * 商品上下架日志对象 xfpt_goods_publish_log
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
@TableName("xfpt_goods_publish_log")
@Schema(description = "商品上下架日志")
public class XfptGoodsPublishLog extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 操作类型，1上架 2下架 */
            @Excel(name = "操作类型，1上架 2下架")
    @Schema(description = "操作类型，1上架 2下架")
    private String operateType;

    /** 操作人 */
            @Excel(name = "操作人")
    @Schema(description = "操作人")
    private String operateBy;

    /** 操作时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "操作时间")
    private Date operateTime;

        }
