package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptNoticUser;
import com.xfgl.system.api.domain.XfptNotice;


/**
 * 收藏Mapper接口
 *
 * @author yinet
 * @date 2025-09-15
 */
public interface XfptNoticUserMapper extends BaseMapper<XfptNoticUser> {
    /**
     * 查询收藏列表
     *
     * @param xfptNoticUser 收藏
     * @return 收藏集合
     */
    public List<XfptNoticUser> selectXfptNoticUserList(XfptNoticUser xfptNoticUser);

    List<XfptNotice> collectList(Long userId);
}
