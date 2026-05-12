package com.xfgl.school.mapper;

import com.xfgl.school.domain.vo.XfptSizeExchangeQuery;
import com.xfgl.school.domain.vo.XfptSizeExchangeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Size exchange record mapper.
 */
public interface XfptSizeExchangeMapper {

    public List<XfptSizeExchangeVo> selectSizeExchangeList(XfptSizeExchangeQuery query);

    public XfptSizeExchangeVo selectSizeExchangeById(@Param("id") Long id, @Param("deptId") Long deptId);
}
