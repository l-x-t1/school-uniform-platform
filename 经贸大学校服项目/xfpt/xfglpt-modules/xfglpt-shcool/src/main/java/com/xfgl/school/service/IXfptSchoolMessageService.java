package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysDictData;

/**
 * 学校信息Service接口
 *
 * @author yinet
 * @date 2025-05-08
 */
public interface IXfptSchoolMessageService extends IService<XfptSchoolMessage> {

    /**
     * 查询学校信息列表
     *
     * @param xfptSchoolMessage 学校信息
     * @return 学校信息集合
     */
    public List<XfptSchoolMessage> selectXfptSchoolMessageList(XfptSchoolMessage xfptSchoolMessage);

    List<SysDictData> selectDictDate(String dictType);

    List<SysDept> selectDeptList(SysDept dept);

    List<SysDept> selectDeptList1(SysDept dept);

    String selectSchoolNumber(String deptId);
}
