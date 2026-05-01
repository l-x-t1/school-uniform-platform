package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptQuestionnaireSignature;

/**
 * 问卷签名Mapper接口
 *
 * @author yinet
 * @date 2025-06-04
 */
public interface XfptQuestionnaireSignatureMapper extends BaseMapper<XfptQuestionnaireSignature> {
    /**
     * 查询问卷签名列表
     *
     * @param xfptQuestionnaireSignature 问卷签名
     * @return 问卷签名集合
     */
    public List<XfptQuestionnaireSignature> selectXfptQuestionnaireSignatureList(XfptQuestionnaireSignature xfptQuestionnaireSignature);
}
