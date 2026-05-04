package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptUserAddressMapper;
import com.xfgl.school.domain.XfptUserAddress;
import com.xfgl.school.service.IXfptUserAddressService;

/**
 * 用户收货地址Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptUserAddressServiceImpl extends ServiceImpl<XfptUserAddressMapper, XfptUserAddress> implements IXfptUserAddressService {

    @Autowired
    private XfptUserAddressMapper xfptUserAddressMapper;

    /**
     * 查询用户收货地址列表
     *
     * @param xfptUserAddress 用户收货地址
     * @return 用户收货地址
     */
    @Override
    public List<XfptUserAddress> selectXfptUserAddressList(XfptUserAddress xfptUserAddress)
    {
        return xfptUserAddressMapper.selectXfptUserAddressList(xfptUserAddress);
    }


}
