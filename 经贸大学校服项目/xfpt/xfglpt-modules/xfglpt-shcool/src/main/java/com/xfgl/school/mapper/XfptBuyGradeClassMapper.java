package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptBuyGradeClass;
import org.apache.ibatis.annotations.Param;

/**
 * 采购年级Mapper接口
 *
 * @author wxm
 * @date 2025-04-27
 */
public interface XfptBuyGradeClassMapper extends BaseMapper<XfptBuyGradeClass> {
    /**
     * 查询采购年级列表
     *
     * @param xfptBuyGradeClass 采购年级
     * @return 采购年级集合
     */
    public List<XfptBuyGradeClass> selectXfptBuyGradeClassList(XfptBuyGradeClass xfptBuyGradeClass);

    XfptBuyGradeClass selectXfptBuyGradeClassById(Long id);

    int insertXfptBuyGradeClass(XfptBuyGradeClass xfptBuyGradeClass);

    int updateXfptBuyGradeClass(XfptBuyGradeClass xfptBuyGradeClass);

    int deleteXfptBuyGradeClassById(Long id);

    int deleteXfptBuyGradeClass(XfptBuyGradeClass xfptBuyGradeClass);

    int deleteXfptBuyGradeClassByIds(Long[] ids);

    int physicDeleteDataWithGradeClassIdList(@Param("gradeClassIdList") List<Long> gradeClassIdList,
                                             @Param("pid") Long pid, @Param("category") Long category);
}
