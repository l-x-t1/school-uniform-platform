package com.xfgl.school.mapper;

import com.xfgl.school.domain.vo.XfptSizeExchangeQuery;
import com.xfgl.school.domain.vo.XfptSizeExchangeVo;

import java.util.List;

/**
 * Size exchange record mapper.
 */
public interface XfptSizeExchangeMapper {

    public List<XfptSizeExchangeVo> selectSizeExchangeList(XfptSizeExchangeQuery query);
}
