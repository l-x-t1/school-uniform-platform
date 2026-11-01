package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学校与供应商采购合同从对象 xfpt_supplice_contract_son
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
@TableName("xfpt_supplice_contract_son")
@Schema(description = "学校与供应商采购合同从")
public class XfptSuppliceContractSon extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */

    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 学校与供应商关联表id
     */
    @Excel(name = "学校与供应商关联表id")
    @Schema(description = "学校与供应商关联表id")
    private String suppliceContractId;

    /**
     * 合同名称
     */
    @Excel(name = "合同名称")
    @Schema(description = "合同名称")
    private String contractName;

    /**
     * 合同服务期限
     */
    @Excel(name = "合同服务期限")
    @Schema(description = "合同服务期限")
    private String contractDeadline;

    /**
     * 附件路径
     */
    @Excel(name = "附件路径")
    @Schema(description = "附件路径")
    private String url;

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

    /**
     * 厂商负责人
     */
    private String supplierPrincipal;

    /**
     * 厂商负责人联系电话
     */
    private String supplierPhone;

    /**
     * 订购人数
     */
    private String numberOrderers;
    /**
     * 单价
     */
    private String unitPrice;
    /**
     * 总额
     */
    private String totalAmount;

    /**
     * 负责人
     */
    private String fzr;

    /**
     * 负责人电话
     */
    private String fzrdh;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 单价
     */
    private Double dj;


    /**
     * 总价
     */
    private Double zj;

    /**
     * 电子合同内容
     */
    private String electronicContent;

    /**
     * 合同模板文件
     */
    private String templateFile;

    /**
     * 纸质合同图片
     */
    private String paperContractFiles;

    @TableField(exist = false)
    private List<TeamMeetingSon> teamMeetingSonList;

    private String integrity;

}
