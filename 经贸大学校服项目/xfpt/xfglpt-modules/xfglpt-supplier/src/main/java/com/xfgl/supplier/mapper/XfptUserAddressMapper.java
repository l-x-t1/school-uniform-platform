package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptUserAddress;

/**
 * 用户收货地址Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptUserAddressMapper extends BaseMapper<XfptUserAddress> {
    /**
     * 查询用户收货地址列表
     *
     * @param xfptUserAddress 用户收货地址
     * @return 用户收货地址集合
     */
    public List<XfptUserAddress> selectXfptUserAddressList(XfptUserAddress xfptUserAddress);
}
