import auth from "@/plugins/auth";
import router, { constantRoutes, dynamicRoutes, homeRoutes } from "@/router";
import { getRouters } from "@/api/menu";
import Layout from "@/layout/index";
import ParentView from "@/components/ParentView";
import InnerLink from "@/layout/components/InnerLink";

function normalizeUsageMenus(routes = []) {
  return routes.map((route) => {
    const current = { ...route };
    const currentPath = current.path || "";
    const currentComponent = current.component || "";
    const currentTitle =
      current.meta && current.meta.title ? current.meta.title : current.name;

    const isPlatformUsage =
      currentPath === "platformUsage" ||
      currentPath === "/school/platformUsage" ||
      currentComponent === "school/platformUsage/index" ||
      currentTitle === "平台使用情况";

    const isSchoolUsage =
      currentPath === "schoolUsage" ||
      currentPath === "/school/schoolUsage" ||
      currentComponent === "school/schoolUsage/index" ||
      currentTitle === "学校使用情况";

    if (current.children && current.children.length) {
      current.children = normalizeUsageMenus(current.children);
    }

    if (isPlatformUsage) {
      current.name = "使用情况";
      current.meta = {
        ...(current.meta || {}),
        title: "使用情况",
      };
    }

    if (isSchoolUsage) {
      current.hidden = true;
    }

    return current;
  });
}

function ensureSupplierMessageMenu(routes = []) {
  const targetTitle = "新增功能";
  const exists = (children = []) =>
    children.some((item) => item.path === "supplierMessage" || item.path === "/newFeature/supplierMessage");

  const appendMenu = (route) => {
    if (!route.children) {
      route.children = [];
    }
    if (exists(route.children)) {
      return;
    }
    route.children.push({
      name: "SupplierMessage",
      path: "supplierMessage",
      component: "school/supplierMessage/index",
      meta: {
        title: "供应商信息",
        icon: "tree-table",
      },
    });
  };

  routes.forEach((route) => {
    const title = route.meta && route.meta.title ? route.meta.title : route.name;
    if (route.path === "newFeature" || route.path === "/newFeature" || title === targetTitle) {
      appendMenu(route);
    }
    if (route.children && route.children.length) {
      ensureSupplierMessageMenu(route.children);
    }
  });

  return routes;
}

function ensureSupplierGroupNoticeMenu(routes = []) {
  const manageMenuTitle = "统购公告管理";
  const queryMenuTitle = "学校统购公告查询";
  const manageMenuComponent = "school/groupNotice/index";
  const queryMenuComponent = "school/groupNoticeReadonly/index";
  const getTitle = (item) => (item.meta && item.meta.title) || item.name || "";
  const isManageMenu = (item) => {
    const title = getTitle(item);
    return (
      item.path === "groupNotice" ||
      item.path === "/newFeature/groupNotice" ||
      item.component === manageMenuComponent ||
      title === "公告管理" ||
      title === "统购公告"
    );
  };
  const isQueryMenu = (item) => {
    const title = getTitle(item);
    return (
      item.path === "supplierGroupNotice" ||
      item.path === "adminGroupNoticeQuery" ||
      item.path === "/newFeature/supplierGroupNotice" ||
      item.path === "/newFeature/adminGroupNoticeQuery" ||
      item.component === queryMenuComponent ||
      title === queryMenuTitle
    );
  };

  const normalizeManageMenu = (item) => {
    item.name = "GroupNoticeManage";
    item.path = item.path === "/newFeature/groupNotice" ? item.path : "groupNotice";
    item.component = manageMenuComponent;
    item.meta = {
      ...(item.meta || {}),
      title: manageMenuTitle,
      icon: (item.meta && item.meta.icon) || "documentation",
    };
  };

  const normalizeQueryMenu = (item) => {
    item.name = "AdminGroupNoticeQuery";
    item.path =
      item.path === "/newFeature/adminGroupNoticeQuery"
        ? item.path
        : "adminGroupNoticeQuery";
    item.component = queryMenuComponent;
    item.meta = {
      ...(item.meta || {}),
      title: queryMenuTitle,
      icon: (item.meta && item.meta.icon) || "message",
    };
  };

  routes.forEach((route) => {
    if (isManageMenu(route)) {
      normalizeManageMenu(route);
    }
    if (isQueryMenu(route)) {
      normalizeQueryMenu(route);
    }
    if (route.children && route.children.length) {
      ensureSupplierGroupNoticeMenu(route.children);
    }
  });

  return routes;
}

function ensureSupplierWithdrawRecordMenu(routes = []) {
  const menuTitle = "供应商提现记录";
  const menuComponent = "school/supplierWithdrawRecord/index";
  const isWithdrawMenu = (item) =>
    item.path === "supplierWithdrawRecord" ||
    item.path === "/newFeature/supplierWithdrawRecord" ||
    item.path === "/school/supplierWithdrawRecord" ||
    item.component === "school/supplierWithdrawRecord/index" ||
    item.component === menuComponent ||
    (item.meta && item.meta.title === menuTitle);

  const normalizeMenu = (item) => {
    item.name = "SupplierWithdrawRecord";
    item.path = item.path === "/newFeature/supplierWithdrawRecord" ? item.path : "supplierWithdrawRecord";
    item.component = menuComponent;
    item.meta = {
      ...(item.meta || {}),
      title: menuTitle,
      icon: (item.meta && item.meta.icon) || "money",
    };
  };

  routes.forEach((route) => {
    if (isWithdrawMenu(route)) {
      normalizeMenu(route);
    }
    if (route.children && route.children.length) {
      ensureSupplierWithdrawRecordMenu(route.children);
    }
  });

  return routes;
}

function ensureUsageMenu(routes = []) {
  const exists = (children = []) =>
    children.some(
      (item) =>
        item.path === "platformUsage" ||
        item.path === "/newFeature/platformUsage" ||
        item.component === "school/platformUsage/index"
    );

  const appendMenu = (route) => {
    if (!route.children) {
      route.children = [];
    }
    if (exists(route.children)) {
      return;
    }
    route.children.push({
      name: "PlatformUsage",
      path: "platformUsage",
      component: "school/platformUsage/index",
      meta: {
        title: "使用情况",
        icon: "chart",
      },
    });
  };

  routes.forEach((route) => {
    if (route.path === "newFeature" || route.path === "/newFeature") {
      appendMenu(route);
    }
    if (route.children && route.children.length) {
      ensureUsageMenu(route.children);
    }
  });

  return routes;
}

function hasSupplierRole(userRoles = []) {
  return userRoles.some((role) => {
    const roleKey = (role || "").toLowerCase();
    return roleKey.includes("gys") || roleKey.includes("changshang");
  });
}

function hasCityEducationRole(userRoles = []) {
  return userRoles.some((role) => (role || "").toLowerCase().includes("sjyj"));
}

function routeTreeSome(routes = [], predicate) {
  return routes.some((route) => {
    if (predicate(route)) {
      return true;
    }
    return route.children && route.children.length
      ? routeTreeSome(route.children, predicate)
      : false;
  });
}

function isAccountBalanceMenu(route = {}) {
  return (
    route.path === "accountBalance" ||
    route.path === "/newFeature/accountBalance" ||
    route.component === "school/accountBalance/index" ||
    (route.meta && route.meta.title === "账户余额")
  );
}

function isSupplierMessageMenu(route = {}) {
  return (
    route.path === "supplierMessage" ||
    route.path === "/newFeature/supplierMessage" ||
    route.component === "school/supplierMessage/index" ||
    (route.meta && route.meta.title === "供应商信息")
  );
}

function isUsageMenu(route = {}) {
  return (
    route.path === "platformUsage" ||
    route.path === "/newFeature/platformUsage" ||
    route.component === "school/platformUsage/index" ||
    (route.meta && route.meta.title === "使用情况")
  );
}

function isSupplierWithdrawRecordMenu(route = {}) {
  return (
    route.path === "supplierWithdrawRecord" ||
    route.path === "/newFeature/supplierWithdrawRecord" ||
    route.path === "/school/supplierWithdrawRecord" ||
    route.component === "school/supplierWithdrawRecord/index" ||
    (route.meta && route.meta.title === "供应商提现记录")
  );
}

function isFinanceFlowMenu(route = {}) {
  return (
    route.path === "financeFlow" ||
    route.path === "/newFeature/financeFlow" ||
    route.component === "school/financeFlow/index" ||
    (route.meta && route.meta.title === "资金流水")
  );
}

function isWithdrawApplyMenu(route = {}) {
  return (
    route.path === "withdrawApply" ||
    route.path === "/newFeature/withdrawApply" ||
    route.component === "school/withdrawApply/index" ||
    (route.meta && route.meta.title === "供应商提现申请")
  );
}

function isGroupNoticeManageMenu(route = {}) {
  const title = route.meta && route.meta.title ? route.meta.title : "";
  return (
    route.path === "groupNotice" ||
    route.path === "/newFeature/groupNotice" ||
    route.component === "school/groupNotice/index" ||
    title === "统购公告" ||
    title === "公告管理" ||
    title === "统购公告管理"
  );
}

function isCommentMenu(route = {}) {
  return (
    route.path === "comment" ||
    route.path === "/newFeature/comment" ||
    route.component === "school/comment/index" ||
    (route.meta && route.meta.title === "商品评价")
  );
}

function sanitizeSupplierMenus(routes = []) {
  const hasAccountBalance = routeTreeSome(routes, isAccountBalanceMenu);
  const hasWithdrawRecord = routeTreeSome(routes, isSupplierWithdrawRecordMenu);

  const normalize = (list = []) =>
    list.reduce((result, route) => {
      const current = { ...route };
      if (isCommentMenu(current)) {
        return result;
      }

      if (current.children && current.children.length) {
        current.children = normalize(current.children);
      }

      if (hasAccountBalance && isFinanceFlowMenu(current)) {
        current.hidden = true;
      }

      if (isSupplierMessageMenu(current) || isUsageMenu(current)) {
        current.hidden = true;
      }

      if (hasWithdrawRecord && isWithdrawApplyMenu(current)) {
        current.hidden = true;
      }

      if (isGroupNoticeManageMenu(current)) {
        current.hidden = true;
      }

      result.push(current);
      return result;
    }, []);

  return normalize(routes);
}

function isSupplierGroupNoticeQueryMenu(route = {}) {
  return (
    route.path === "supplierGroupNotice" ||
    route.path === "adminGroupNoticeQuery" ||
    route.path === "/newFeature/supplierGroupNotice" ||
    route.path === "/newFeature/adminGroupNoticeQuery" ||
    route.component === "school/groupNoticeReadonly/index" ||
    (route.meta && route.meta.title === "学校统购公告查询")
  );
}

function sanitizeCityEducationMenus(routes = []) {
  return routes.reduce((result, route) => {
    const current = { ...route };

    if (
      isSupplierGroupNoticeQueryMenu(current) ||
      isSupplierWithdrawRecordMenu(current)
    ) {
      return result;
    }

    if (current.children && current.children.length) {
      current.children = sanitizeCityEducationMenus(current.children);
    }

    result.push(current);
    return result;
  }, []);
}

function isNewFeatureRoute(route = {}) {
  const title = route.meta && route.meta.title ? route.meta.title : route.name;
  return (
    route.path === "newFeature" ||
    route.path === "/newFeature" ||
    title === "新增功能"
  );
}

function buildPromotedSidebarRoute(route = {}, parentPath = "") {
  const routePath = route.path || "";
  const fullPath = routePath.startsWith("/")
    ? routePath
    : `${parentPath}/${routePath}`.replace(/\/{2,}/g, "/");
  const promotedChild = {
    ...route,
    path: fullPath,
  };

  return {
    path: fullPath,
    hidden: route.hidden,
    children: [promotedChild],
  };
}

function promoteSchoolSidebarMenus(routes = []) {
  return collectFlattenedSchoolSidebarMenus(routes);
}

function cloneSidebarRoute(route = {}) {
  const current = { ...route };

  if (route.children && route.children.length) {
    current.children = route.children.map((child) => cloneSidebarRoute(child));
  }

  return current;
}

function resolveSchoolSidebarPath(routePath = "", parentPath = "") {
  if (!routePath) {
    return parentPath || "/";
  }
  if (routePath.startsWith("/")) {
    return routePath;
  }
  return `${parentPath}/${routePath}`.replace(/\/{2,}/g, "/");
}

function isSchoolSidebarMenuEntry(route = {}) {
  return (
    !!route.component &&
    route.component !== Layout &&
    route.component !== ParentView
  );
}

function buildFlattenedSchoolSidebarRoute(
  route = {},
  fullPath = "",
  fallbackIcon = ""
) {
  const promotedChild = {
    ...route,
    path: fullPath,
    meta: {
      ...(route.meta || {}),
      icon: (route.meta && route.meta.icon) || fallbackIcon,
    },
  };

  delete promotedChild.children;
  delete promotedChild.redirect;

  return {
    path: fullPath,
    hidden: route.hidden,
    children: [promotedChild],
  };
}

function collectFlattenedSchoolSidebarMenus(
  routes = [],
  parentPath = "",
  parentIcon = ""
) {
  const flattenedRoutes = [];

  routes.forEach((route) => {
    if (!route || route.hidden) {
      return;
    }

    const fullPath = resolveSchoolSidebarPath(route.path || "", parentPath);
    const currentIcon =
      route.meta && route.meta.icon ? route.meta.icon : parentIcon;

    if (isSchoolSidebarMenuEntry(route)) {
      flattenedRoutes.push(
        buildFlattenedSchoolSidebarRoute(route, fullPath, parentIcon)
      );
    }

    if (route.children && route.children.length) {
      flattenedRoutes.push(
        ...collectFlattenedSchoolSidebarMenus(
          route.children,
          fullPath,
          currentIcon
        )
      );
    }
  });

  return flattenedRoutes;
}

function projectSchoolSidebarRoutes(routes = []) {
  const projectedRoutes = [];

  routes.forEach((route) => {
    if (!route) {
      return;
    }

    if (!isNewFeatureRoute(route)) {
      projectedRoutes.push(cloneSidebarRoute(route));
      return;
    }

    const parentPath = route.path || "";
    const parentIcon = route.meta && route.meta.icon ? route.meta.icon : "";
    const directChildren = route.children || [];

    if (!directChildren.length) {
      projectedRoutes.push(cloneSidebarRoute(route));
      return;
    }

    directChildren.forEach((child) => {
      const projectedChild = cloneSidebarRoute(child);
      projectedChild.path = resolveSchoolSidebarPath(
        child.path || "",
        parentPath
      );
      projectedChild.meta = {
        ...(projectedChild.meta || {}),
        icon:
          (projectedChild.meta && projectedChild.meta.icon) || parentIcon,
      };
      projectedRoutes.push(projectedChild);
    });
  });

  return projectedRoutes;
}

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: [],
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes;
      state.routes = constantRoutes.concat(routes);
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes);
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes;
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes;
    },
  },
  actions: {
    GenerateRoutes({ commit, rootGetters }) {
      return new Promise((resolve) => {
        const userRoles = rootGetters.roles || [];
        const hasXuexiaoRole = userRoles.some((role) =>
          role.includes("xuexiao")
        );
        const isSupplierUser = hasSupplierRole(userRoles);
        const isCityEducationUser = hasCityEducationRole(userRoles);

        const homeRoute = hasXuexiaoRole
          ? homeRoutes.schoolHome
          : homeRoutes.normalHome;

        getRouters().then((res) => {
          let normalizedBaseRoutes = normalizeUsageMenus(
            JSON.parse(JSON.stringify(res.data || []))
          );
          normalizedBaseRoutes = ensureSupplierGroupNoticeMenu(
            normalizedBaseRoutes
          );
          normalizedBaseRoutes = ensureSupplierWithdrawRecordMenu(
            normalizedBaseRoutes
          );
          if (isCityEducationUser) {
            normalizedBaseRoutes = sanitizeCityEducationMenus(
              normalizedBaseRoutes
            );
          }
          const normalizedRoutes = isSupplierUser
            ? sanitizeSupplierMenus(normalizedBaseRoutes)
            : normalizedBaseRoutes;
          const sdata = JSON.parse(JSON.stringify(normalizedRoutes));
          const rdata = JSON.parse(JSON.stringify(normalizedRoutes));
          const sidebarRoutes = filterAsyncRouter(sdata);
          const rewriteRoutes = filterAsyncRouter(rdata, false, true);

          rewriteRoutes.unshift(homeRoute);

          const asyncRoutes = filterDynamicRoutes(dynamicRoutes);
          rewriteRoutes.push({ path: "*", redirect: "/404", hidden: true });
          router.addRoutes(asyncRoutes);
          commit("SET_ROUTES", rewriteRoutes);

          const schoolSidebarRoutes = hasXuexiaoRole
            ? projectSchoolSidebarRoutes(sidebarRoutes)
            : isSupplierUser
              ? promoteSchoolSidebarMenus(sidebarRoutes)
              : sidebarRoutes;
          const sidebarRoutesWithHome = [homeRoute].concat(schoolSidebarRoutes);
          commit(
            "SET_SIDEBAR_ROUTERS",
            constantRoutes.concat(sidebarRoutesWithHome)
          );
          commit("SET_DEFAULT_ROUTES", sidebarRoutesWithHome);
          commit("SET_TOPBAR_ROUTES", sidebarRoutesWithHome);
          resolve(rewriteRoutes);
        });
      });
    },
  },
};

function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter((route) => {
    if (type && route.children) {
      route.children = filterChildren(route.children);
    }
    if (route.component) {
      if (route.component === "Layout") {
        route.component = Layout;
      } else if (route.component === "ParentView") {
        route.component = ParentView;
      } else if (route.component === "InnerLink") {
        route.component = InnerLink;
      } else {
        route.component = loadView(route.component);
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type);
    } else {
      delete route.children;
      delete route.redirect;
    }
    return true;
  });
}

function filterChildren(childrenMap, lastRouter = false) {
  let children = [];
  childrenMap.forEach((el) => {
    el.path = lastRouter ? `${lastRouter.path}/${el.path}` : el.path;
    if (el.children && el.children.length && el.component === "ParentView") {
      children = children.concat(filterChildren(el.children, el));
    } else {
      children.push(el);
    }
  });
  return children;
}

export function filterDynamicRoutes(routes) {
  const res = [];
  routes.forEach((route) => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route);
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route);
      }
    }
  });
  return res;
}

export const loadView = (view) => {
  if (process.env.NODE_ENV === "development") {
    return (resolve) => require([`@/views/${view}`], resolve);
  }
  return () => import(`@/views/${view}`);
};

export default permission;
