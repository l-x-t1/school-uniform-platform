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
  const targetTitle = "新增功能";
  const exists = (children = []) =>
    children.some(
      (item) =>
        item.path === "supplierGroupNotice" ||
        item.path === "/newFeature/supplierGroupNotice" ||
        item.component === "supplier/groupNotice/index"
    );

  const appendMenu = (route) => {
    if (!route.children) {
      route.children = [];
    }
    if (exists(route.children)) {
      return;
    }
    route.children.push({
      name: "SupplierGroupNotice",
      path: "supplierGroupNotice",
      component: "supplier/groupNotice/index",
      meta: {
        title: "学校统购公告查询",
        icon: "message",
      },
    });
  };

  routes.forEach((route) => {
    const title = route.meta && route.meta.title ? route.meta.title : route.name;
    if (route.path === "newFeature" || route.path === "/newFeature" || title === targetTitle) {
      appendMenu(route);
    }
    if (route.children && route.children.length) {
      ensureSupplierGroupNoticeMenu(route.children);
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
        title: "教育局监管统计",
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

function ensureVoteResultMenu(routes = []) {
  const targetTitle = "鏂板鍔熻兘";
  const exists = (children = []) =>
    children.some(
      (item) =>
        item.path === "voteResult" ||
        item.path === "/newFeature/voteResult" ||
        item.component === "school/voteResult/index"
    );

  const appendMenu = (route) => {
    if (!route.children) {
      route.children = [];
    }
    if (exists(route.children)) {
      return;
    }
    route.children.push({
      name: "VoteResult",
      path: "voteResult",
      component: "school/voteResult/index",
      meta: {
        title: "投票结果",
        icon: "form",
      },
    });
  };

  routes.forEach((route) => {
    const title = route.meta && route.meta.title ? route.meta.title : route.name;
    if (route.path === "newFeature" || route.path === "/newFeature" || title === targetTitle) {
      appendMenu(route);
    }
    if (route.children && route.children.length) {
      ensureVoteResultMenu(route.children);
    }
  });

  return routes;
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

        const homeRoute = hasXuexiaoRole
          ? homeRoutes.schoolHome
          : homeRoutes.normalHome;

        getRouters().then((res) => {
          const normalizedRoutes = ensureVoteResultMenu(
            ensureUsageMenu(
              ensureSupplierMessageMenu(
                ensureSupplierGroupNoticeMenu(
                  normalizeUsageMenus(JSON.parse(JSON.stringify(res.data || [])))
                )
              )
            )
          );
          const sdata = JSON.parse(JSON.stringify(normalizedRoutes));
          const rdata = JSON.parse(JSON.stringify(normalizedRoutes));
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
