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
 * 学校端零售商品展示对象。
 *
 * @author yinet
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(description = "学校端零售商品")
public class XfptRetailGoodsVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "学校商品关联主键")
    private Long id;

    @Schema(description = "学校供应商合作关系ID")
    private Long relId;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "商品ID")
    private Long goodsId;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "商品编码")
    private String goodsCode;

    @Schema(description = "品类名称")
    private String categoryName;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "商品主图")
    private String mainImage;

    @Schema(description = "详情图JSON文本")
    private String detailImagesJson;

    @Schema(description = "商品详情富文本")
    private String detailContent;

    @Schema(description = "销售类型，1统购 2零售 3统购零售")
    private String saleType;

    @Schema(description = "学校端上下架状态，0下架 1上架")
    private String status;

    @Schema(description = "商品库状态")
    private String goodsStatus;

    @Schema(description = "商品库上架状态")
    private String publishStatus;

    @Schema(description = "操作人")
    private String operateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private Date operateTime;

    @Schema(description = "最低零售价")
    private BigDecimal retailMinPrice;

    @Schema(description = "最高零售价")
    private BigDecimal retailMaxPrice;

    @Schema(description = "总库存")
    private Integer stockTotal;

    @Schema(description = "零售价区间")
    private String retailPriceRange;

    @Schema(description = "商品尺码")
    private List<XfptGoodsSize> xfptGoodsSizeList;
}
