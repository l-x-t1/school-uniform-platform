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
      current.name = "教育局监管统计";
      current.meta = {
        ...(current.meta || {}),
        title: "教育局监管统计",
      };
    }

    if (isSchoolUsage) {
      current.hidden = true;
    }

    return current;
  });
}

function normalizeGroupNoticeMenus(routes = []) {
  return routes.map((route) => {
    const current = { ...route };
    const currentPath = current.path || "";
    const currentComponent = current.component || "";

    const isReadonlyGroupNotice =
      currentPath === "supplierGroupNotice" ||
      currentPath === "/newFeature/supplierGroupNotice" ||
      currentPath === "adminGroupNoticeQuery" ||
      currentPath === "/newFeature/adminGroupNoticeQuery" ||
      currentPath === "/supplier/groupNotice" ||
      currentPath === "/school/groupNoticeReadonly" ||
      currentComponent === "supplier/groupNotice/index" ||
      currentComponent === "school/groupNoticeReadonly/index";

    const isEditableGroupNotice =
      currentPath === "groupNotice" ||
      currentPath === "/tonggou/groupNotice" ||
      currentPath === "/schoolGroupNoticeEntry/index" ||
      currentPath === "/schoolGroupNoticeEntry" ||
      currentComponent === "school/groupNotice/index";

    if (current.children && current.children.length) {
      current.children = normalizeGroupNoticeMenus(current.children);
    }

    if (isReadonlyGroupNotice) {
      current.meta = {
        ...(current.meta || {}),
        title: "学校统购公告查询",
      };
    }

    if (isEditableGroupNotice) {
      current.meta = {
        ...(current.meta || {}),
        title: "公告管理",
      };
    }

    return current;
  });
}

function normalizeSupplierApplyMenus(routes = []) {
  return routes.map((route) => {
    const current = { ...route };
    const currentPath = current.path || "";
    const currentComponent = current.component || "";
    const currentTitle =
      current.meta && current.meta.title ? current.meta.title : current.name;

    const isSupplierApplyAudit =
      currentPath === "supplierApply" ||
      currentPath === "/newFeature/supplierApply" ||
      currentComponent === "school/supplierApply/index" ||
      currentTitle === "供应商报名";

    if (current.children && current.children.length) {
      current.children = normalizeSupplierApplyMenus(current.children);
    }

    if (isSupplierApplyAudit) {
      current.meta = {
        ...(current.meta || {}),
        title: "供应商报名审核",
      };
    }

    return current;
  });
}

function buildRouteFullPath(parentPath = "", routePath = "") {
  if (!routePath) {
    return parentPath || "/";
  }
  if (/^https?:/i.test(routePath)) {
    return routePath;
  }

  const normalizedParent = String(parentPath)
    .replace(/\/+/g, "/")
    .replace(/\/$/, "");
  const normalizedPath = String(routePath).replace(/\/+/g, "/");

  if (normalizedPath.startsWith("/")) {
    return normalizedPath.replace(/\/+/g, "/");
  }
  if (!normalizedParent) {
    return `/${normalizedPath}`.replace(/\/+/g, "/");
  }
  return `${normalizedParent}/${normalizedPath}`.replace(/\/+/g, "/");
}

function sanitizeRouteNameSegment(value = "") {
  const segment = String(value)
    .replace(/^\/+|\/+$/g, "")
    .replace(/[^A-Za-z0-9_]+/g, "_");
  return segment || "route";
}

function buildUniqueRouteName(baseName, route, fullPath, usedNames) {
  if (!usedNames.has(baseName)) {
    return baseName;
  }

  const suffixSource = fullPath || route.component || route.path || baseName;
  const suffix = sanitizeRouteNameSegment(suffixSource);
  let candidate = `${baseName}__${suffix}`;
  let index = 2;

  while (usedNames.has(candidate)) {
    candidate = `${baseName}__${suffix}_${index}`;
    index += 1;
  }

  return candidate;
}

function dedupeRouteNames(routes = [], parentPath = "", usedNames = new Set()) {
  return routes.map((route) => {
    const current = { ...route };
    const fullPath = buildRouteFullPath(parentPath, current.path || "");

    if (current.name) {
      current.name = buildUniqueRouteName(
        current.name,
        current,
        fullPath,
        usedNames
      );
      usedNames.add(current.name);
    }

    if (current.children && current.children.length) {
      current.children = dedupeRouteNames(current.children, fullPath, usedNames);
    }

    return current;
  });
}

const schoolTonggouMenuTitles = {
  质检报告管理: "统购质检报告管理",
  尺码调换记录: "统购尺码调换记录",
  发货单统计: "统购发货单统计",
};

function promoteSchoolTonggouMenus(routes = []) {
  return routes.flatMap((route) => {
    const currentTitle =
      route.meta && route.meta.title ? route.meta.title : route.name;
    const isTonggouRoot =
      route.path === "/tonggou" ||
      route.path === "//tonggou" ||
      currentTitle === "统购管理";

    if (!isTonggouRoot) {
      return [route];
    }

    const children = Array.isArray(route.children) ? route.children : [];
    return children.map((child) => {
      const childTitle =
        child.meta && child.meta.title ? child.meta.title : child.name;
      const promotedTitle = schoolTonggouMenuTitles[childTitle] || childTitle;
      const childPath = (child.path || "").replace(/^\/+/, "");

      return {
        path: "/",
        hidden: false,
        component: "Layout",
        children: [
          {
            ...child,
            path: `tonggou/${childPath}`,
            meta: {
              ...(child.meta || {}),
              title: promotedTitle,
            },
          },
        ],
      };
    });
  });
}

const schoolRetiredMenuPaths = new Set([
  "cartItem",
  "userAddress",
  "comment",
  "productionOrderItem",
  "deliveryOrderItem",
  "orderItem",
]);

const schoolRetiredMenuComponents = new Set([
  "school/cartItem/index",
  "school/userAddress/index",
  "school/comment/index",
  "school/productionOrderItem/index",
  "school/deliveryOrderItem/index",
  "school/orderItem/index",
]);

const schoolRetiredMenuTitles = new Set([
  "购物车明细",
  "用户收货地址",
  "商品评价",
  "生产单明细",
  "发货单明细",
  "订单明细",
]);

const schoolReadonlyGroupNoticePaths = new Set([
  "supplierGroupNotice",
  "adminGroupNoticeQuery",
]);

const schoolReadonlyGroupNoticeComponents = new Set([
  "supplier/groupNotice/index",
  "school/groupNoticeReadonly/index",
]);

const schoolReadonlyGroupNoticeTitles = new Set(["学校统购公告查询"]);

function isSchoolRetiredRoute(route = {}) {
  const normalizedPath = String(route.path || "").replace(/^\/+/, "");
  const title = getRouteTitle(route);

  return (
    schoolRetiredMenuPaths.has(normalizedPath) ||
    schoolRetiredMenuComponents.has(route.component || "") ||
    schoolRetiredMenuTitles.has(title)
  );
}

function isSchoolReadonlyGroupNoticeRoute(route = {}) {
  const normalizedPath = String(route.path || "").replace(/^\/+/, "");
  const title = getRouteTitle(route);

  return (
    schoolReadonlyGroupNoticePaths.has(normalizedPath) ||
    schoolReadonlyGroupNoticeComponents.has(route.component || "") ||
    schoolReadonlyGroupNoticeTitles.has(title)
  );
}

function shouldPruneEmptySchoolRoute(route = {}) {
  const hasChildren = Array.isArray(route.children) && route.children.length > 0;
  if (hasChildren) {
    return false;
  }

  return (
    route.component === "Layout" ||
    route.component === "ParentView" ||
    route.path === "/"
  );
}

function filterSchoolRetiredMenus(routes = []) {
  return routes.reduce((result, route) => {
    const current = { ...route };

    if (current.children && current.children.length) {
      current.children = filterSchoolRetiredMenus(current.children);
    }

    if (isSchoolRetiredRoute(current)) {
      return result;
    }

    if (shouldPruneEmptySchoolRoute(current)) {
      return result;
    }

    if (Array.isArray(current.children) && current.children.length === 0) {
      delete current.children;
      delete current.redirect;
    }

    result.push(current);
    return result;
  }, []);
}

function filterSchoolReadonlyGroupNoticeRoutes(routes = []) {
  return routes.reduce((result, route) => {
    const current = { ...route };

    if (current.children && current.children.length) {
      current.children = filterSchoolReadonlyGroupNoticeRoutes(current.children);
    }

    if (isSchoolReadonlyGroupNoticeRoute(current)) {
      return result;
    }

    if (shouldPruneEmptySchoolRoute(current)) {
      return result;
    }

    if (Array.isArray(current.children) && current.children.length === 0) {
      delete current.children;
      delete current.redirect;
    }

    result.push(current);
    return result;
  }, []);
}

function renameSchoolCommentMenu(routes = []) {
  return routes.map((route) => {
    const current = { ...route };
    const currentPath = String(current.path || "").replace(/^\/+/, "");
    const currentComponent = current.component || "";
    const currentTitle = getRouteTitle(current);

    if (
      currentPath === "commentReply" ||
      currentPath === "evaluation" ||
      currentComponent === "school/commentReply/index" ||
      currentTitle === "商品评论回复" ||
      currentTitle === "订单评价回复"
    ) {
      current.meta = {
        ...(current.meta || {}),
        title: "评价管理",
      };
    }

    if (current.children && current.children.length) {
      current.children = renameSchoolCommentMenu(current.children);
    }

    return current;
  });
}

const adminNewFeatureMenuTitles = new Set([
  "统购公告商品",
  "学校供应商合作商品",
  "学校供应商合作关系",
  "统购管理",
  "评价管理",
  "结算管理",
  "申请结算列表",
  "每日入账金额",
]);

const adminNewFeatureLeafPaths = new Set([
  "groupNoticeItem",
  "schoolSupplierGoodsRel",
  "schoolSupplierRel",
  "evaluation",
  "settlementManage",
  "settlementApply",
  "dailyIncome",
]);

const adminNewFeatureLeafComponents = new Set([
  "school/groupNoticeItem/index",
  "school/schoolSupplierGoodsRel/index",
  "school/schoolSupplierRel/index",
  "school/commentReply/index",
  "school/eduSettlementManage/index",
  "jsgl/index",
  "school/financeFlow/index",
]);

function getRouteTitle(route = {}) {
  return route.meta && route.meta.title ? route.meta.title : route.name;
}

function isNewFeatureRoute(route = {}) {
  const title = getRouteTitle(route);
  return (
    route.path === "newFeature" ||
    route.path === "/newFeature" ||
    title === "新增功能"
  );
}

function isAdminTonggouRoot(route = {}) {
  const title = getRouteTitle(route);
  return (
    route.path === "/tonggou" ||
    route.path === "//tonggou" ||
    title === "统购管理"
  );
}

function isAdminNewFeatureLeafWrapper(route = {}) {
  if (route.path !== "/" || route.component !== "Layout") {
    return false;
  }

  const children = Array.isArray(route.children) ? route.children : [];
  if (children.length !== 1) {
    return false;
  }

  const child = children[0] || {};
  const childPath = String(child.path || "").replace(/^\/+/, "");
  const childTitle = getRouteTitle(child);

  return (
    adminNewFeatureLeafPaths.has(childPath) ||
    adminNewFeatureLeafComponents.has(child.component || "") ||
    adminNewFeatureMenuTitles.has(childTitle)
  );
}

function shouldNestAdminNewFeatureRoute(route = {}) {
  return isAdminTonggouRoot(route) || isAdminNewFeatureLeafWrapper(route);
}

function toAdminNewFeatureChild(route = {}) {
  if (isAdminTonggouRoot(route)) {
    return {
      ...route,
      path: "tonggou",
      component: "ParentView",
      redirect: "noRedirect",
      hidden: false,
      alwaysShow: true,
    };
  }

  if (isAdminNewFeatureLeafWrapper(route)) {
    return { ...(route.children[0] || {}) };
  }

  return null;
}

function buildAdminNewFeatureRouteKey(route = {}) {
  return [
    String(route.path || "").replace(/^\/+/, ""),
    route.component || "",
    getRouteTitle(route) || "",
  ].join("__");
}

function nestAdminNewFeatureMenus(routes = []) {
  const newFeatureRoute = routes.find(isNewFeatureRoute);
  if (!newFeatureRoute) {
    return routes;
  }

  const nestedRoutes = [];
  const remainingRoutes = [];

  routes.forEach((route) => {
    if (isNewFeatureRoute(route)) {
      remainingRoutes.push(route);
      return;
    }

    if (shouldNestAdminNewFeatureRoute(route)) {
      nestedRoutes.push(route);
      return;
    }

    remainingRoutes.push(route);
  });

  if (!nestedRoutes.length) {
    return routes;
  }

  const existingChildren = Array.isArray(newFeatureRoute.children)
    ? [...newFeatureRoute.children]
    : [];
  const existingKeys = new Set(
    existingChildren.map((child) => buildAdminNewFeatureRouteKey(child))
  );
  const appendedChildren = [];

  nestedRoutes.forEach((route) => {
    const child = toAdminNewFeatureChild(route);
    if (!child) {
      return;
    }

    const key = buildAdminNewFeatureRouteKey(child);
    if (existingKeys.has(key)) {
      return;
    }

    existingKeys.add(key);
    appendedChildren.push(child);
  });

  newFeatureRoute.children = existingChildren.concat(appendedChildren);
  return remainingRoutes;
}

function normalizeNonSchoolRoutes(routes = []) {
  return normalizeSupplierApplyMenus(
    normalizeGroupNoticeMenus(normalizeUsageMenus(routes))
  );
}

function normalizeRuntimeRoutes(
  routes = [],
  { useSchoolTonggouLayout = false, nestAdminNewFeature = false } = {}
) {
  let normalizedRoutes = filterSchoolRetiredMenus(
    renameSchoolCommentMenu(normalizeNonSchoolRoutes(routes))
  );

  if (useSchoolTonggouLayout) {
    normalizedRoutes = filterSchoolReadonlyGroupNoticeRoutes(normalizedRoutes);
    normalizedRoutes = promoteSchoolTonggouMenus(normalizedRoutes);
  }

  if (nestAdminNewFeature) {
    normalizedRoutes = nestAdminNewFeatureMenus(normalizedRoutes);
  }

  return normalizedRoutes;
}

function buildSupplierPortalRoutes() {
  return [
    {
      path: "/",
      component: "Layout",
      children: [
        {
          path: "supplier/applyCenter",
          component: "supplier/applyCenter/index",
          name: "SupplierApplyCenterMenu",
          meta: { title: "报名中心", icon: "message" },
        },
      ],
    },
    {
      path: "/",
      component: "Layout",
      children: [
        {
          path: "supplier/myApply",
          component: "supplier/myApply/index",
          name: "SupplierMyApplyMenu",
          meta: { title: "我的报名", icon: "form" },
        },
      ],
    },
  ];
}

function hasRouteComponent(routes = [], component = "") {
  return routes.some((route) => {
    if (route.component === component) {
      return true;
    }
    if (Array.isArray(route.children) && route.children.length) {
      return hasRouteComponent(route.children, component);
    }
    return false;
  });
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
          String(role || "").toLowerCase().includes("xuexiao")
        );
        const hasAdminRole = userRoles.some((role) =>
          String(role || "").toLowerCase().includes("admin")
        );
        const hasSupplierRole = userRoles.some((role) => {
          const roleKey = String(role || "").toLowerCase();
          return roleKey.includes("gys") || roleKey.includes("changshang");
        });

        const homeRoute = hasXuexiaoRole
          ? homeRoutes.schoolHome
          : homeRoutes.normalHome;

        getRouters().then((res) => {
          const serverRoutes = JSON.parse(JSON.stringify(res.data || []));
          let normalizedRoutes = normalizeRuntimeRoutes(serverRoutes, {
            useSchoolTonggouLayout: hasXuexiaoRole,
            nestAdminNewFeature: hasAdminRole && !hasXuexiaoRole,
          });
          if (hasSupplierRole) {
            buildSupplierPortalRoutes().forEach((route) => {
              const child = Array.isArray(route.children) ? route.children[0] : null;
              if (child && !hasRouteComponent(normalizedRoutes, child.component)) {
                normalizedRoutes.push(route);
              }
            });
          }
          const dedupedRoutes = dedupeRouteNames(normalizedRoutes);
          const sdata = JSON.parse(JSON.stringify(dedupedRoutes));
          const rdata = JSON.parse(JSON.stringify(dedupedRoutes));
          const sidebarRoutes = filterAsyncRouter(sdata);
          const rewriteRoutes = filterAsyncRouter(rdata, false, true);

          rewriteRoutes.unshift(homeRoute);

          const asyncRoutes = filterDynamicRoutes(dynamicRoutes);
          rewriteRoutes.push({ path: "*", redirect: "/404", hidden: true });
          router.addRoutes(asyncRoutes);
          commit("SET_ROUTES", rewriteRoutes);

          const sidebarRoutesWithHome = [homeRoute].concat(sidebarRoutes);
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
