package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSimUser;

/**
 * 石i民用户Mapper接口
 *
 * @author yinet
 * @date 2025-05-13
 */
public interface XfptSimUserMapper extends BaseMapper<XfptSimUser> {
    /**
     * 查询石i民用户列表
     *
     * @param xfptSimUser 石i民用户
     * @return 石i民用户集合
     */
    public List<XfptSimUser> selectXfptSimUserList(XfptSimUser xfptSimUser);
}
