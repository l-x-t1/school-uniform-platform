package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptStudentInfo;

/**
 * 家长绑定学生信息Service接口
 *
 * @author yinet
 * @date 2025-05-13
 */
public interface IXfptStudentInfoService extends IService<XfptStudentInfo> {

    /**
     * 查询家长绑定学生信息列表
     *
     * @param xfptStudentInfo 家长绑定学生信息
     * @return 家长绑定学生信息集合
     */
    public List<XfptStudentInfo> selectXfptStudentInfoList(XfptStudentInfo xfptStudentInfo);

}
