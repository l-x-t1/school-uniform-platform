package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptQuestionnaireSignatureMapper;
import com.xfgl.school.domain.XfptQuestionnaireSignature;
import com.xfgl.school.service.IXfptQuestionnaireSignatureService;

/**
 * 问卷签名Service业务层处理
 *
 * @author yinet
 * @date 2025-06-04
 */
@Service
public class XfptQuestionnaireSignatureServiceImpl extends ServiceImpl<XfptQuestionnaireSignatureMapper, XfptQuestionnaireSignature> implements IXfptQuestionnaireSignatureService {

    @Autowired
    private XfptQuestionnaireSignatureMapper xfptQuestionnaireSignatureMapper;

    /**
     * 查询问卷签名列表
     *
     * @param xfptQuestionnaireSignature 问卷签名
     * @return 问卷签名
     */
    @Override
    public List<XfptQuestionnaireSignature> selectXfptQuestionnaireSignatureList(XfptQuestionnaireSignature xfptQuestionnaireSignature)
    {
        return xfptQuestionnaireSignatureMapper.selectXfptQuestionnaireSignatureList(xfptQuestionnaireSignature);
    }


}
