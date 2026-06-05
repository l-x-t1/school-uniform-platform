package com.xfgl.system.service.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.system.domain.SysMenu;

@Component
public class RoleMenuSceneSupport
{
    public static final String MENU_SCENE_SCHOOL = "school";
    public static final String MENU_SCENE_EDUCATION = "education";
    public static final String MENU_SCENE_ADMIN = "admin";

    public static final String MENU_OWNERSHIP_SCHOOL = "school";
    public static final String MENU_OWNERSHIP_EDUCATION = "education";
    public static final String MENU_OWNERSHIP_SUPPLIER = "supplier";
    public static final String MENU_OWNERSHIP_COMMON = "common";

    private static final String DISPLAY_NAME_PLATFORM_USAGE = "\u6559\u80b2\u5c40\u76d1\u7ba1\u7edf\u8ba1";
    private static final String DISPLAY_NAME_GROUP_NOTICE = "\u5b66\u6821\u7edf\u8d2d\u516c\u544a\u67e5\u8be2";
    private static final String DISPLAY_NAME_COMMENT_MANAGE = "\u8bc4\u4ef7\u7ba1\u7406";
    private static final String DISPLAY_NAME_SCHOOL_SETTLEMENT = "\u5b66\u6821\u7aef\u7533\u8bf7\u7ed3\u7b97\u5217\u8868";
    private static final String DISPLAY_NAME_EDUCATION_SETTLEMENT = "\u6559\u80b2\u5c40\u7aef\u7533\u8bf7\u7ed3\u7b97\u5217\u8868";
    private static final String DISPLAY_SUFFIX_SCHOOL = "\uff08\u5b66\u6821\u7aef\uff09";
    private static final String DISPLAY_SUFFIX_EDUCATION = "\uff08\u6559\u80b2\u5c40\u7aef\uff09";
    private static final String DISPLAY_SUFFIX_SUPPLIER = "\uff08\u4f9b\u5e94\u5546\u7aef\uff09";

    private static final Set<String> SCHOOL_ROLE_KEYS = new HashSet<String>(
            Arrays.asList("xuexiao", "ceshixuexiao"));

    private static final Set<String> EDUCATION_ROLE_KEYS = new HashSet<String>(
            Arrays.asList("jyj", "sjyj"));

    private static final Map<String, String> SCHOOL_TONGGOU_MENU_TITLES = new LinkedHashMap<String, String>();

    private static final Set<String> GLOBAL_RETIRED_MENU_PATHS = new HashSet<String>(
            Arrays.asList("cartItem", "userAddress", "comment", "productionOrderItem", "deliveryOrderItem", "orderItem"));

    private static final Set<String> GLOBAL_RETIRED_MENU_COMPONENTS = new HashSet<String>(
            Arrays.asList("school/cartItem/index", "school/userAddress/index", "school/comment/index",
                    "school/productionOrderItem/index", "school/deliveryOrderItem/index", "school/orderItem/index"));

    private static final Set<String> GLOBAL_RETIRED_MENU_TITLES = new HashSet<String>(
            Arrays.asList("购物车明细", "用户收货地址", "商品评价", "生产单明细", "发货单明细", "订单明细"));

    private static final Set<String> GLOBAL_HIDDEN_MENU_PATHS = new HashSet<String>(Arrays.asList("schoolUsage"));

    private static final Set<String> GLOBAL_HIDDEN_MENU_COMPONENTS = new HashSet<String>(
            Arrays.asList("school/schoolUsage/index"));

    private static final Set<String> GLOBAL_HIDDEN_MENU_TITLES = new HashSet<String>(Arrays.asList("学校使用情况"));

    private static final Set<String> SCHOOL_ONLY_RETIRED_PATHS = new HashSet<String>(Arrays.asList("settlementManage"));

    private static final Set<String> SCHOOL_ONLY_RETIRED_COMPONENTS = new HashSet<String>(
            Arrays.asList("school/settlementManage/index", "school/eduSettlementManage/index"));

    private static final Set<String> SCHOOL_ONLY_RETIRED_TITLES = new HashSet<String>(Arrays.asList("结算管理"));

    private static final Set<String> SCHOOL_SCENE_BLOCKED_PATHS = new HashSet<String>(Arrays.asList(
            "supplierMessage", "supplierSettle", "supplierSettleAudit", "supplierGroupNotice", "adminGroupNoticeQuery",
            "platformUsage", "voteResult", "schoolSettlementApply", "dailyIncome"));

    private static final Set<String> SCHOOL_SCENE_BLOCKED_COMPONENTS = new HashSet<String>(Arrays.asList(
            "school/supplierMessage/index", "supplier-settle/index", "jiaoyuju/supplierSettleAudit/index",
            "supplier/groupNotice/index", "school/groupNoticeReadonly/index", "school/platformUsage/index",
            "school/voteResult/index"));

    private static final Set<String> SCHOOL_SCENE_BLOCKED_TITLES = new HashSet<String>(
            Arrays.asList("教育局监管统计", "供应商信息", "供应商入驻申请", "供应商入驻审核", "学校统购公告查询", "投票结果", "每日入账金额"));

    private static final Set<String> ADMIN_ONLY_MENU_PATHS = new HashSet<String>(
            Arrays.asList("schoolSettlementApply", "dailyIncome"));

    private static final Comparator<SysMenu> MENU_COMPARATOR = Comparator
            .comparing((SysMenu menu) -> menu.getParentId() == null ? 0L : menu.getParentId())
            .thenComparing((SysMenu menu) -> menu.getOrderNum() == null ? 0 : menu.getOrderNum())
            .thenComparing((SysMenu menu) -> menu.getMenuId() == null ? 0L : menu.getMenuId());

    static
    {
        SCHOOL_TONGGOU_MENU_TITLES.put("公告管理", "统购公告管理");
        SCHOOL_TONGGOU_MENU_TITLES.put("质检报告管理", "统购质检报告管理");
        SCHOOL_TONGGOU_MENU_TITLES.put("尺码调换记录", "统购尺码调换记录");
        SCHOOL_TONGGOU_MENU_TITLES.put("发货单统计", "统购发货单统计");
    }

    public String resolveMenuScene(String roleKey, String menuScene)
    {
        if (StringUtils.isNotEmpty(menuScene))
        {
            String normalizedMenuScene = menuScene.trim().toLowerCase(Locale.ROOT);
            if (MENU_SCENE_SCHOOL.equals(normalizedMenuScene)
                    || MENU_SCENE_EDUCATION.equals(normalizedMenuScene)
                    || MENU_SCENE_ADMIN.equals(normalizedMenuScene))
            {
                return normalizedMenuScene;
            }
        }
        return resolveMenuSceneByRoleKey(roleKey);
    }

    public String resolveMenuSceneByRoleKey(String roleKey)
    {
        String normalizedRoleKey = StringUtils.isEmpty(roleKey) ? ""
                : roleKey.trim().toLowerCase(Locale.ROOT);
        if (SCHOOL_ROLE_KEYS.contains(normalizedRoleKey))
        {
            return MENU_SCENE_SCHOOL;
        }
        if (EDUCATION_ROLE_KEYS.contains(normalizedRoleKey))
        {
            return MENU_SCENE_EDUCATION;
        }
        return MENU_SCENE_ADMIN;
    }

    public List<SysMenu> normalizeFlatMenus(List<SysMenu> sourceMenus, String menuScene)
    {
        if (sourceMenus == null || sourceMenus.isEmpty())
        {
            return new ArrayList<SysMenu>();
        }

        String resolvedScene = resolveMenuScene(null, menuScene);
        List<SysMenu> menus = sourceMenus.stream().map(this::copyMenu).collect(Collectors.toCollection(ArrayList::new));
        menus.forEach(menu -> normalizeMenu(menu, resolvedScene));

        if (isSchoolScene(resolvedScene))
        {
            promoteSchoolTonggouMenus(menus);
        }
        else if (isAdminScene(resolvedScene))
        {
            nestAdminTonggouMenus(menus);
        }

        Set<Long> removedIds = collectRemovedMenuIds(menus, resolvedScene);
        if (!removedIds.isEmpty())
        {
            menus.removeIf(menu -> removedIds.contains(menu.getMenuId()));
        }

        applyDisplayMetadata(menus, resolvedScene);
        menus.sort(MENU_COMPARATOR);
        return menus;
    }

    public Set<Long> collectMenuIds(List<SysMenu> menus)
    {
        if (menus == null || menus.isEmpty())
        {
            return Collections.emptySet();
        }
        return menus.stream().map(SysMenu::getMenuId).filter(menuId -> menuId != null).collect(Collectors.toSet());
    }

    private SysMenu copyMenu(SysMenu source)
    {
        SysMenu target = new SysMenu();
        BeanUtils.copyProperties(source, target);
        target.setChildren(new ArrayList<SysMenu>());
        return target;
    }

    private void applyDisplayMetadata(List<SysMenu> menus, String menuScene)
    {
        Map<Long, SysMenu> menuById = menus.stream().filter(menu -> menu.getMenuId() != null)
                .collect(Collectors.toMap(SysMenu::getMenuId, menu -> menu, (left, right) -> left, LinkedHashMap::new));
        Map<Long, List<SysMenu>> childrenByParent = groupMenusByParentId(menus);
        Map<Long, String> baseOwnershipCache = new HashMap<Long, String>();
        Map<Long, String> finalOwnershipCache = new HashMap<Long, String>();

        for (SysMenu menu : menus)
        {
            String ownership = resolveFinalMenuOwnership(menu, menuById, childrenByParent, baseOwnershipCache,
                    finalOwnershipCache, menuScene);
            String displayName = resolveDisplayBaseName(menu, menuScene);
            menu.setMenuOwnership(ownership);
            menu.setDisplayMenuName(appendOwnershipSuffix(displayName, ownership));
        }
    }

    private Map<Long, List<SysMenu>> groupMenusByParentId(List<SysMenu> menus)
    {
        return menus.stream().filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId, LinkedHashMap::new, Collectors.toList()));
    }

    private String resolveFinalMenuOwnership(SysMenu menu, Map<Long, SysMenu> menuById,
            Map<Long, List<SysMenu>> childrenByParent, Map<Long, String> baseOwnershipCache,
            Map<Long, String> finalOwnershipCache, String menuScene)
    {
        if (menu.getMenuId() != null && finalOwnershipCache.containsKey(menu.getMenuId()))
        {
            return finalOwnershipCache.get(menu.getMenuId());
        }

        String ownership = resolveBaseMenuOwnership(menu, childrenByParent, baseOwnershipCache, menuScene);
        if (MENU_OWNERSHIP_COMMON.equals(ownership) && menu.getParentId() != null)
        {
            SysMenu parentMenu = menuById.get(menu.getParentId());
            if (parentMenu != null)
            {
                String parentOwnership = resolveFinalMenuOwnership(parentMenu, menuById, childrenByParent,
                        baseOwnershipCache, finalOwnershipCache, menuScene);
                if (!MENU_OWNERSHIP_COMMON.equals(parentOwnership))
                {
                    ownership = parentOwnership;
                }
            }
        }

        if (menu.getMenuId() != null)
        {
            finalOwnershipCache.put(menu.getMenuId(), ownership);
        }
        return ownership;
    }

    private String resolveBaseMenuOwnership(SysMenu menu, Map<Long, List<SysMenu>> childrenByParent,
            Map<Long, String> baseOwnershipCache, String menuScene)
    {
        if (menu.getMenuId() != null && baseOwnershipCache.containsKey(menu.getMenuId()))
        {
            return baseOwnershipCache.get(menu.getMenuId());
        }

        String ownership = resolveDirectMenuOwnership(menu, menuScene);
        if (MENU_OWNERSHIP_COMMON.equals(ownership) && menu.getMenuId() != null)
        {
            Set<String> childOwnerships = new HashSet<String>();
            for (SysMenu child : childrenByParent.getOrDefault(menu.getMenuId(), Collections.emptyList()))
            {
                String childOwnership = resolveBaseMenuOwnership(child, childrenByParent, baseOwnershipCache, menuScene);
                if (!MENU_OWNERSHIP_COMMON.equals(childOwnership))
                {
                    childOwnerships.add(childOwnership);
                }
            }
            if (childOwnerships.size() == 1)
            {
                ownership = childOwnerships.iterator().next();
            }
        }

        if (menu.getMenuId() != null)
        {
            baseOwnershipCache.put(menu.getMenuId(), ownership);
        }
        return ownership;
    }

    private String resolveDirectMenuOwnership(SysMenu menu, String menuScene)
    {
        if (isCommonPlatformMenu(menu))
        {
            return MENU_OWNERSHIP_COMMON;
        }
        if (isEducationOwnedMenu(menu, menuScene))
        {
            return MENU_OWNERSHIP_EDUCATION;
        }
        if (isSupplierOwnedMenu(menu))
        {
            return MENU_OWNERSHIP_SUPPLIER;
        }
        if (isSchoolOwnedMenu(menu, menuScene))
        {
            return MENU_OWNERSHIP_SCHOOL;
        }
        return MENU_OWNERSHIP_COMMON;
    }

    private String resolveDisplayBaseName(SysMenu menu, String menuScene)
    {
        String menuName = StringUtils.defaultString(menu.getMenuName());
        if (isPlatformUsage(menu))
        {
            return DISPLAY_NAME_PLATFORM_USAGE;
        }
        if (isGroupNoticeMenu(menu))
        {
            return DISPLAY_NAME_GROUP_NOTICE;
        }
        if (isCommentReplyMenu(menu))
        {
            return DISPLAY_NAME_COMMENT_MANAGE;
        }
        if (isSettlementApplyMenu(menu, menuScene))
        {
            return isSchoolSettlementMenu(menu, menuScene) ? DISPLAY_NAME_SCHOOL_SETTLEMENT
                    : DISPLAY_NAME_EDUCATION_SETTLEMENT;
        }
        if (isSchoolScene(menuScene) && SCHOOL_TONGGOU_MENU_TITLES.containsKey(menuName))
        {
            return SCHOOL_TONGGOU_MENU_TITLES.get(menuName);
        }
        return menuName;
    }

    private String appendOwnershipSuffix(String menuName, String ownership)
    {
        if (MENU_OWNERSHIP_SCHOOL.equals(ownership))
        {
            return menuName + DISPLAY_SUFFIX_SCHOOL;
        }
        if (MENU_OWNERSHIP_EDUCATION.equals(ownership))
        {
            return menuName + DISPLAY_SUFFIX_EDUCATION;
        }
        if (MENU_OWNERSHIP_SUPPLIER.equals(ownership))
        {
            return menuName + DISPLAY_SUFFIX_SUPPLIER;
        }
        return menuName;
    }

    private void normalizeMenu(SysMenu menu, String menuScene)
    {
        normalizeGroupNoticeMenu(menu);
        normalizeSettlementMenu(menu, menuScene);
    }

    private void normalizeUsageMenu(SysMenu menu)
    {
    }

    private void normalizeGroupNoticeMenu(SysMenu menu)
    {
        if (isGroupNoticeMenu(menu))
        {
            if (StringUtils.equals(menu.getPath(), "adminGroupNoticeQuery"))
            {
                menu.setPath("supplierGroupNotice");
            }
            if (StringUtils.equals(menu.getComponent(), "school/groupNoticeReadonly/index"))
            {
                menu.setComponent("school/groupNotice/index");
            }
        }
    }

    private void normalizeCommentMenu(SysMenu menu)
    {
    }

    private void normalizeSettlementMenu(SysMenu menu, String menuScene)
    {
        String normalizedPath = normalizePath(menu.getPath());
        if ("settlementApply".equals(normalizedPath) || "jsgl/index".equals(menu.getComponent()))
        {
            if (isSchoolScene(menuScene))
            {
                menu.setComponent("school/settlementApply/index");
            }
            else
            {
                menu.setComponent("jsgl/index");
            }
            return;
        }

        if ("schoolSettlementApply".equals(normalizedPath))
        {
            menu.setComponent("school/settlementApply/index");
        }
    }

    private void promoteSchoolTonggouMenus(List<SysMenu> menus)
    {
        Set<Long> tonggouRootIds = menus.stream().filter(this::isTonggouRoot).map(SysMenu::getMenuId)
                .filter(menuId -> menuId != null).collect(Collectors.toSet());
        if (tonggouRootIds.isEmpty())
        {
            return;
        }

        for (SysMenu menu : menus)
        {
            if (menu.getParentId() != null && tonggouRootIds.contains(menu.getParentId()))
            {
                menu.setParentId(0L);
            }
        }
    }

    private void nestAdminTonggouMenus(List<SysMenu> menus)
    {
        SysMenu newFeatureMenu = menus.stream().filter(this::isNewFeatureRoot).findFirst().orElse(null);
        if (newFeatureMenu == null || newFeatureMenu.getMenuId() == null)
        {
            return;
        }

        for (SysMenu menu : menus)
        {
            if (isTonggouRoot(menu))
            {
                menu.setParentId(newFeatureMenu.getMenuId());
            }
        }
    }

    private Set<Long> collectRemovedMenuIds(List<SysMenu> menus, String menuScene)
    {
        Map<Long, List<SysMenu>> childrenByParent = menus.stream().filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        Set<Long> removedIds = new HashSet<Long>();

        for (SysMenu menu : menus)
        {
            if (menu.getMenuId() == null)
            {
                continue;
            }

            if (isSchoolScene(menuScene) && isTonggouRoot(menu))
            {
                removedIds.add(menu.getMenuId());
                continue;
            }

            if (shouldRemoveMenu(menu, menuScene))
            {
                collectDescendantIds(menu.getMenuId(), childrenByParent, removedIds);
            }
        }

        return removedIds;
    }

    private boolean shouldRemoveMenu(SysMenu menu, String menuScene)
    {
        if (isGloballyRetiredMenu(menu) || isGloballyHiddenMenu(menu))
        {
            return true;
        }

        if (isSchoolScene(menuScene))
        {
            return isSchoolOnlyRetiredMenu(menu) || isSchoolSceneBlockedMenu(menu) || isAdminOnlyMenu(menu);
        }

        if (isEducationScene(menuScene))
        {
            return isAdminOnlyMenu(menu);
        }

        return false;
    }

    private void collectDescendantIds(Long menuId, Map<Long, List<SysMenu>> childrenByParent, Set<Long> removedIds)
    {
        if (menuId == null || !removedIds.add(menuId))
        {
            return;
        }

        List<SysMenu> children = childrenByParent.get(menuId);
        if (children == null || children.isEmpty())
        {
            return;
        }

        for (SysMenu child : children)
        {
            collectDescendantIds(child.getMenuId(), childrenByParent, removedIds);
        }
    }

    private boolean isGloballyRetiredMenu(SysMenu menu)
    {
        return matchesMenu(menu, GLOBAL_RETIRED_MENU_PATHS, GLOBAL_RETIRED_MENU_COMPONENTS, GLOBAL_RETIRED_MENU_TITLES);
    }

    private boolean isGloballyHiddenMenu(SysMenu menu)
    {
        return matchesMenu(menu, GLOBAL_HIDDEN_MENU_PATHS, GLOBAL_HIDDEN_MENU_COMPONENTS, GLOBAL_HIDDEN_MENU_TITLES);
    }

    private boolean isSchoolOnlyRetiredMenu(SysMenu menu)
    {
        return matchesMenu(menu, SCHOOL_ONLY_RETIRED_PATHS, SCHOOL_ONLY_RETIRED_COMPONENTS, SCHOOL_ONLY_RETIRED_TITLES);
    }

    private boolean isSchoolSceneBlockedMenu(SysMenu menu)
    {
        return matchesMenu(menu, SCHOOL_SCENE_BLOCKED_PATHS, SCHOOL_SCENE_BLOCKED_COMPONENTS, SCHOOL_SCENE_BLOCKED_TITLES);
    }

    private boolean isAdminOnlyMenu(SysMenu menu)
    {
        return ADMIN_ONLY_MENU_PATHS.contains(normalizePath(menu.getPath()));
    }

    private boolean matchesMenu(SysMenu menu, Set<String> paths, Set<String> components, Set<String> titles)
    {
        return paths.contains(normalizePath(menu.getPath()))
                || components.contains(StringUtils.defaultString(menu.getComponent()))
                || titles.contains(StringUtils.defaultString(menu.getMenuName()));
    }

    private boolean isCommonPlatformMenu(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String component = normalizePath(menu.getComponent());
        String perms = StringUtils.defaultString(menu.getPerms()).toLowerCase(Locale.ROOT);

        return isNewFeatureRoot(menu) || "system".equals(path) || "tool".equals(path) || "monitor".equals(path)
                || "gen".equals(path) || component.startsWith("system/") || component.startsWith("tool/")
                || component.startsWith("monitor/") || component.startsWith("gen/")
                || perms.startsWith("system:") || perms.startsWith("monitor:") || perms.startsWith("tool:")
                || perms.startsWith("gen:");
    }

    private boolean isEducationOwnedMenu(SysMenu menu, String menuScene)
    {
        String path = normalizePath(menu.getPath());
        String component = normalizePath(menu.getComponent());
        return isPlatformUsage(menu) || "supplierSettleAudit".equals(path) || component.startsWith("jiaoyuju/")
                || isEducationSettlementMenu(menu, menuScene);
    }

    private boolean isSupplierOwnedMenu(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String component = normalizePath(menu.getComponent());
        return "supplierMessage".equals(path) || "supplierSettle".equals(path) || component.startsWith("supplier/")
                || component.startsWith("supplier-settle/");
    }

    private boolean isSchoolOwnedMenu(SysMenu menu, String menuScene)
    {
        String path = normalizePath(menu.getPath());
        String component = normalizePath(menu.getComponent());
        return isGroupNoticeMenu(menu) || isCommentReplyMenu(menu) || isSchoolSettlementMenu(menu, menuScene)
                || "schoolSettlementApply".equals(path) || component.startsWith("school/");
    }

    private boolean isSettlementApplyMenu(SysMenu menu, String menuScene)
    {
        return isSchoolSettlementMenu(menu, menuScene) || isEducationSettlementMenu(menu, menuScene);
    }

    private boolean isSchoolSettlementMenu(SysMenu menu, String menuScene)
    {
        String path = normalizePath(menu.getPath());
        String component = normalizePath(menu.getComponent());
        return "schoolSettlementApply".equals(path)
                || ("settlementApply".equals(path) && isSchoolScene(menuScene))
                || "school/settlementApply/index".equals(component);
    }

    private boolean isEducationSettlementMenu(SysMenu menu, String menuScene)
    {
        String path = normalizePath(menu.getPath());
        String component = normalizePath(menu.getComponent());
        return "jsgl/index".equals(component)
                || ("settlementApply".equals(path) && !isSchoolScene(menuScene));
    }

    private boolean isPlatformUsage(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String component = StringUtils.defaultString(menu.getComponent());
        String title = StringUtils.defaultString(menu.getMenuName());
        return "platformUsage".equals(path) || "school/platformUsage/index".equals(component) || "平台使用情况".equals(title)
                || "教育局监管统计".equals(title);
    }

    private boolean isGroupNoticeMenu(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String component = StringUtils.defaultString(menu.getComponent());
        String title = StringUtils.defaultString(menu.getMenuName());
        return "supplierGroupNotice".equals(path) || "adminGroupNoticeQuery".equals(path)
                || "supplier/groupNotice/index".equals(component) || "school/groupNoticeReadonly/index".equals(component)
                || "school/groupNotice/index".equals(component) || "学校统购公告查询".equals(title);
    }

    private boolean isCommentReplyMenu(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String component = StringUtils.defaultString(menu.getComponent());
        String title = StringUtils.defaultString(menu.getMenuName());
        return "commentReply".equals(path) || "evaluation".equals(path) || "school/commentReply/index".equals(component)
                || "商品评论回复".equals(title) || "订单评价回复".equals(title) || "评价管理".equals(title);
    }

    private boolean isTonggouRoot(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String title = StringUtils.defaultString(menu.getMenuName());
        return "tonggou".equals(path) || "统购管理".equals(title);
    }

    private boolean isNewFeatureRoot(SysMenu menu)
    {
        String path = normalizePath(menu.getPath());
        String title = StringUtils.defaultString(menu.getMenuName());
        return "newFeature".equals(path) || "新增功能".equals(title);
    }

    private boolean isSchoolScene(String menuScene)
    {
        return MENU_SCENE_SCHOOL.equals(menuScene);
    }

    private boolean isEducationScene(String menuScene)
    {
        return MENU_SCENE_EDUCATION.equals(menuScene);
    }

    private boolean isAdminScene(String menuScene)
    {
        return MENU_SCENE_ADMIN.equals(menuScene);
    }

    private String normalizePath(String path)
    {
        return StringUtils.defaultString(path).replace("\\", "/").replaceAll("^/+", "");
    }

    public List<Long> filterMenuIds(Collection<Long> menuIds, Collection<Long> allowedMenuIds)
    {
        if (menuIds == null || menuIds.isEmpty() || allowedMenuIds == null || allowedMenuIds.isEmpty())
        {
            return new ArrayList<Long>();
        }
        Set<Long> allowedSet = new HashSet<Long>(allowedMenuIds);
        return menuIds.stream().filter(menuId -> menuId != null && allowedSet.contains(menuId)).distinct()
                .sorted().collect(Collectors.toList());
    }

    public List<SysMenu> filterMenusByQuery(List<SysMenu> menus, SysMenu queryMenu)
    {
        if (menus == null || menus.isEmpty())
        {
            return new ArrayList<SysMenu>();
        }
        if (queryMenu == null)
        {
            return menus;
        }

        String menuName = StringUtils.defaultString(queryMenu.getMenuName()).trim();
        String visible = StringUtils.defaultString(queryMenu.getVisible()).trim();
        String status = StringUtils.defaultString(queryMenu.getStatus()).trim();
        String type = StringUtils.defaultString(queryMenu.getType()).trim();

        return menus.stream().filter(menu -> matchMenuName(menu, menuName))
                .filter(menu -> matchEquals(menu.getVisible(), visible)).filter(menu -> matchEquals(menu.getStatus(), status))
                .filter(menu -> matchEquals(menu.getType(), type)).collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean matchMenuName(SysMenu menu, String query)
    {
        if (StringUtils.isEmpty(query))
        {
            return true;
        }
        return matchText(menu.getDisplayMenuName(), query) || matchText(menu.getMenuName(), query);
    }

    private boolean matchText(String source, String query)
    {
        if (StringUtils.isEmpty(query))
        {
            return true;
        }
        return StringUtils.defaultString(source).toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    private boolean matchEquals(String source, String query)
    {
        if (StringUtils.isEmpty(query))
        {
            return true;
        }
        return StringUtils.equals(StringUtils.defaultString(source), query);
    }
}
