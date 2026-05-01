package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.vo.DeptMesVo;
import com.xfgl.school.domain.vo.OrderManagementStatisticsVo;
import com.xfgl.system.api.domain.SysDept;
import org.apache.ibatis.annotations.Select;

/**
 * 采购汇总信息Mapper接口
 *
 * @author yinet
 * @date 2025-05-09
 */
public interface XfptProcSummaryMapper extends BaseMapper<XfptProcSummary> {
    /**
     * 查询采购汇总信息列表
     *
     * @param xfptProcSummary 采购汇总信息
     * @return 采购汇总信息集合
     */
    public List<XfptProcSummary> selectXfptProcSummaryList(XfptProcSummary xfptProcSummary);

    List<XfptProcSummary> statistics(OrderManagementStatisticsVo orderManagementStatisticsVo);

    @Select("select *from  sys_dept where parent_id= (SELECT dept_id FROM sys_dept WHERE parent_id=0 ) and del_flag=0 ")
    List<SysDept> selectDeptBytypeList();


    List<OrderManagementStatisticsVo> statisticsInfo(OrderManagementStatisticsVo orderManagementStatisticsVo);

    List<OrderManagementStatisticsVo> statisticsTypeInfo(OrderManagementStatisticsVo orderManagementStatistics);

    @Select("SELECT proc.*,COALESCE(mes.school_type, 4) as schoolType FROM XFPT_PROC_SUMMARY proc \n" +
            "LEFT JOIN XFPT_SCHOOL_MESSAGE mes \n" +
            "ON PROC.dept_id =mes.dept_id  WHERE find_in_set(#{deptId},proc.ancestors)")
    List<XfptProcSummary> selectProcSummByDept(Long deptId);

    @Select("SELECT dept.*, COALESCE(mes.school_type, 4) as schoolType FROM SYS_DEPT dept\n" +
            "LEFT JOIN XFPT_SCHOOL_MESSAGE mes \n" +
            "ON dept.dept_id =mes.dept_id  WHERE find_in_set(#{deptId},dept.ancestors)")
    List<DeptMesVo> selectDeptByDept(Long deptId);

    @Select("select * from sys_dept where parent_id=#{deptId} order by order_num desc")
    List<SysDept> selectDeptList(Long deptId);

    @Select("select * from sys_dept where dept_id=#{deptId} order by order_num desc")
    List<SysDept> selectDeptList2(Long deptId);
}
