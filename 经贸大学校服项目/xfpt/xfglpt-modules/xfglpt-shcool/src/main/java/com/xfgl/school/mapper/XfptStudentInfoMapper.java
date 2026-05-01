package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptStudentInfo;

/**
 * 家长绑定学生信息Mapper接口
 *
 * @author yinet
 * @date 2025-05-13
 */
public interface XfptStudentInfoMapper extends BaseMapper<XfptStudentInfo> {
    /**
     * 查询家长绑定学生信息列表
     *
     * @param xfptStudentInfo 家长绑定学生信息
     * @return 家长绑定学生信息集合
     */
    public List<XfptStudentInfo> selectXfptStudentInfoList(XfptStudentInfo xfptStudentInfo);
}
