package com.xfgl.school.service;

import com.xfgl.school.domain.vo.XfptSizeExchangeQuery;
import com.xfgl.school.domain.vo.XfptSizeExchangeVo;

import java.util.List;

/**
 * Size exchange record service.
 */
public interface IXfptSizeExchangeService {

    public List<XfptSizeExchangeVo> selectSizeExchangeList(XfptSizeExchangeQuery query);
}
