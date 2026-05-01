package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptQuestionnaireSignature;

/**
 * 问卷签名Service接口
 *
 * @author yinet
 * @date 2025-06-04
 */
public interface IXfptQuestionnaireSignatureService extends IService<XfptQuestionnaireSignature> {

    /**
     * 查询问卷签名列表
     *
     * @param xfptQuestionnaireSignature 问卷签名
     * @return 问卷签名集合
     */
    public List<XfptQuestionnaireSignature> selectXfptQuestionnaireSignatureList(XfptQuestionnaireSignature xfptQuestionnaireSignature);

}
