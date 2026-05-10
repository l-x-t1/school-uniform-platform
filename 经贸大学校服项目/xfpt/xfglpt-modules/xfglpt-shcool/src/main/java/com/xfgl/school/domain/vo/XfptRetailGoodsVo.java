package com.xfgl.school.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.web.domain.BaseEntity;
import com.xfgl.school.domain.XfptGoodsSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 学校端零售商品上下架视图对象
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(description = "学校端零售商品上下架")
public class XfptRetailGoodsVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 关系主键 */
    @Schema(description = "关系主键")
    private Long id;

    /** 合作关系主表ID */
    @Schema(description = "合作关系主表ID")
    private Long relId;

    /** 学校ID */
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 供应商名称快照 */
    @Schema(description = "供应商名称快照")
    private String supplierName;

    /** 商品ID */
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 商品名称 */
    @Schema(description = "商品名称")
    private String goodsName;

    /** 商品款号 */
    @Schema(description = "商品款号")
    private String goodsCode;

    /** 品类名称 */
    @Schema(description = "品类名称")
    private String categoryName;

    /** 品牌名称 */
    @Schema(description = "品牌名称")
    private String brandName;

    /** 主图路径或URL */
    @Schema(description = "主图路径或URL")
    private String mainImage;

    /** 详情图JSON文本 */
    @Schema(description = "详情图JSON文本")
    private String detailImagesJson;

    /** 商品详情富文本 */
    @Schema(description = "商品详情富文本")
    private String detailContent;

    /** 销售类型，1统购 2零售 3统购零售 */
    @Schema(description = "销售类型，1统购 2零售 3统购零售")
    private String saleType;

    /** 学校上下架状态，0下架 1上架 */
    @Schema(description = "学校上下架状态，0下架 1上架")
    private String status;

    /** 商品状态，0草稿 1启用 2停用 */
    @Schema(description = "商品状态，0草稿 1启用 2停用")
    private String goodsStatus;

    /** 全局上架状态，0未上架 1已上架 2已下架 */
    @Schema(description = "全局上架状态，0未上架 1已上架 2已下架")
    private String publishStatus;

    /** 操作人 */
    @Schema(description = "操作人")
    private String operateBy;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private Date operateTime;

    /** 零售价最低值 */
    @Schema(description = "零售价最低值")
    private BigDecimal retailMinPrice;

    /** 零售价最高值 */
    @Schema(description = "零售价最高值")
    private BigDecimal retailMaxPrice;

    /** 库存总数 */
    @Schema(description = "库存总数")
    private Integer stockTotal;

    /** 零售价区间 */
    @Schema(description = "零售价区间")
    private String retailPriceRange;

    /** 商品尺码信息 */
    @Schema(description = "商品尺码信息")
    private List<XfptGoodsSize> xfptGoodsSizeList;
}
