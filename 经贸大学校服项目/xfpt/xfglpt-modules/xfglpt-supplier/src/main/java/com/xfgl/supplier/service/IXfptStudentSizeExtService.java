package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptStudentSizeExt;

/**
 * 学生尺码扩展Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptStudentSizeExtService extends IService<XfptStudentSizeExt> {

    /**
     * 查询学生尺码扩展列表
     *
     * @param xfptStudentSizeExt 学生尺码扩展
     * @return 学生尺码扩展集合
     */
    public List<XfptStudentSizeExt> selectXfptStudentSizeExtList(XfptStudentSizeExt xfptStudentSizeExt);

}
