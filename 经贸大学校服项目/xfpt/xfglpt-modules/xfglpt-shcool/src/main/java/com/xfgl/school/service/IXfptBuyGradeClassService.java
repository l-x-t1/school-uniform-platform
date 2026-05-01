package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptBuyGradeClass;

/**
 * 采购年级Service接口
 *
 * @author wxm
 * @date 2025-04-27
 */
public interface IXfptBuyGradeClassService extends IService<XfptBuyGradeClass> {

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

    /**
     * 物理删除采购年级数据
     *
     * @param gradeClassIdList 未勾选的
     * @param pid              关联的主业务表主键
     * @param category         区分业务数据
     * @return
     */
    int physicDeleteDataWithGradeClassIdList(List<Long> gradeClassIdList,Long pid,Long category);

}
