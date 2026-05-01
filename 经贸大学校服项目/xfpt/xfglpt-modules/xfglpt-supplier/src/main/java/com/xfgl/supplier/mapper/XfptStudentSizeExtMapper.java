package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptStudentSizeExt;

/**
 * 学生尺码扩展Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptStudentSizeExtMapper extends BaseMapper<XfptStudentSizeExt> {
    /**
     * 查询学生尺码扩展列表
     *
     * @param xfptStudentSizeExt 学生尺码扩展
     * @return 学生尺码扩展集合
     */
    public List<XfptStudentSizeExt> selectXfptStudentSizeExtList(XfptStudentSizeExt xfptStudentSizeExt);
}
