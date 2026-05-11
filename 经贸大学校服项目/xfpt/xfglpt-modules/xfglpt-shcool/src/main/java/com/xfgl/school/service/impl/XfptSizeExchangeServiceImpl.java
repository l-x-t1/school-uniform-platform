package com.xfgl.school.service.impl;

import com.xfgl.school.domain.vo.XfptSizeExchangeQuery;
import com.xfgl.school.domain.vo.XfptSizeExchangeVo;
import com.xfgl.school.mapper.XfptSizeExchangeMapper;
import com.xfgl.school.service.IXfptSizeExchangeService;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Size exchange record service implementation.
 */
@Service
public class XfptSizeExchangeServiceImpl implements IXfptSizeExchangeService {

    @Autowired
    private XfptSizeExchangeMapper xfptSizeExchangeMapper;

    @Override
    public List<XfptSizeExchangeVo> selectSizeExchangeList(XfptSizeExchangeQuery query) {
        query.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        return xfptSizeExchangeMapper.selectSizeExchangeList(query);
    }
}
