package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptUserAddress;

/**
 * 用户收货地址Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptUserAddressService extends IService<XfptUserAddress> {

    /**
     * 查询用户收货地址列表
     *
     * @param xfptUserAddress 用户收货地址
     * @return 用户收货地址集合
     */
    public List<XfptUserAddress> selectXfptUserAddressList(XfptUserAddress xfptUserAddress);

}
