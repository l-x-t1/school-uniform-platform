package com.xfgl.system.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import com.xfgl.system.domain.SysMenu;
import com.xfgl.system.domain.vo.RouterVo;
import com.xfgl.system.domain.vo.TreeSelect;

/**
 * 菜单 业务层
 *
 * @author ruoyi
 */
public interface ISysMenuService
{
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 按角色场景查询菜单列表
     *
     * @param menu 查询条件
     * @param targetRoleKey 目标角色标识
     * @param menuScene 菜单场景
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuListByScene(SysMenu menu, String targetRoleKey, String menuScene);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId,String type);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 按目标角色过滤可分配菜单ID
     *
     * @param menuIds 待过滤菜单ID
     * @param roleKey 角色标识
     * @return 合法菜单ID
     */
    public List<Long> filterMenuIdsByRoleKey(Collection<Long> menuIds, String roleKey);

    /**
     * 按场景过滤可分配菜单ID
     *
     * @param menuIds 待过滤菜单ID
     * @param menuScene 菜单场景
     * @return 合法菜单ID
     */
    public List<Long> filterMenuIdsByScene(Collection<Long> menuIds, String menuScene);

    /**
     * 解析角色对应菜单场景
     *
     * @param roleKey 角色标识
     * @return 菜单场景
     */
    public String resolveMenuSceneByRoleKey(String roleKey);
}
