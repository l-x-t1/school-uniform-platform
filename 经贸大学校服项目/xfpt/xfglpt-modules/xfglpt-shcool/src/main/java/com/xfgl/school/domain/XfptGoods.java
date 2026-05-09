package com.xfgl.school.domain;
import java.util.List;
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
 * 商品信息对象 xfpt_goods
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
@TableName("xfpt_goods")
@Schema(description = "商品信息")
public class XfptGoods extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 商品名称 */
            @Excel(name = "商品名称")
    @Schema(description = "商品名称")
    private String goodsName;

    /** 商品编码 */
            @Excel(name = "商品编码")
    @Schema(description = "商品编码")
    private String goodsCode;

    /** 品类名称 */
            @Excel(name = "品类名称")
    @Schema(description = "品类名称")
    private String categoryName;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 供应商名称快照 */
            @Excel(name = "供应商名称快照")
    @Schema(description = "供应商名称快照")
    private String supplierName;

    /** 品牌名称 */
            @Excel(name = "品牌名称")
    @Schema(description = "品牌名称")
    private String brandName;

    /** 主图路径或URL */
            @Excel(name = "主图路径或URL")
    @Schema(description = "主图路径或URL")
    private String mainImage;

    /** 详情图JSON文本 */
    @Schema(description = "详情图JSON文本")
    private String detailImagesJson;

    /** 商品详情富文本 */
    @Schema(description = "商品详情富文本")
    private String detailContent;

    /** 销售类型，1统购 2零售 3统购零售 */
            @Excel(name = "销售类型", readConverterExp = "1=统购,2=零售,3=统购零售")
    @Schema(description = "销售类型，1统购 2零售 3统购零售")
    private String saleType;

    /** 状态，0草稿 1启用 2停用 */
            @Excel(name = "状态", readConverterExp = "0=草稿,1=启用,2=停用")
    @Schema(description = "状态，0草稿 1启用 2停用")
    private String status;

    /** 上架状态，0未上架 1已上架 2已下架 */
            @Excel(name = "上架状态", readConverterExp = "0=未上架,1=已上架,2=已下架")
    @Schema(description = "上架状态，0未上架 1已上架 2已下架")
    private String publishStatus;

    /** 上架时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "上架时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "上架时间")
    private Date publishTime;

    /** 下架时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "下架时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "下架时间")
    private Date offShelfTime;

    /** 排序值 */
            @Excel(name = "排序值")
    @Schema(description = "排序值")
    private Integer sortNum;

/** 商品尺码信息 */
@TableField(exist = false)
@Schema(description = "商品尺码")
private List<XfptGoodsSize> xfptGoodsSizeList;
        }
