<template>
  <div :class="{ 'has-logo': showLogo }"
    :style="{ backgroundColor: settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }"
    class="sidebar-container">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar :class="settings.sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu :default-active="activeMenu" :collapse="isCollapse"
        :background-color="settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="settings.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true" :active-text-color="settings.theme" :collapse-transition="false" mode="vertical"
        class="sidebar-menu">
        <sidebar-item v-for="(route, index) in sidebarRouters" :key="route.path + index" :item="route"
          :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/assets/styles/variables.scss";

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapState(["settings"]),
    ...mapGetters(["sidebarRouters", "sidebar"]),
    activeMenu() {
      const route = this.$route;
      const { meta, path } = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo;
    },
    variables() {
      return variables;
    },
    isCollapse() {
      return !this.sidebar.opened;
    }
  }
};
</script>

<style lang="scss" scoped>
.sidebar-container {
  transition: all 0.3s ease;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      radial-gradient(circle at 10% 10%, rgba(99, 102, 241, 0.08) 0%, transparent 30%),
      radial-gradient(circle at 90% 90%, rgba(59, 130, 246, 0.08) 0%, transparent 30%);
    pointer-events: none;
    z-index: -1;
  }

  .theme-dark {
    .scrollbar-wrapper {
      &::-webkit-scrollbar-track {
        background: rgba(0, 0, 0, 0.1);
      }

      &::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.1);
        border-radius: 4px;

        &:hover {
          background: rgba(255, 255, 255, 0.15);
        }
      }
    }
  }

  .sidebar-menu {
    border: none;

    ::v-deep .el-submenu {
      &.is-active {
        >.el-submenu__title {
          position: relative;

          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            height: 100%;
            width: 4px;
            background: linear-gradient(to bottom, #3b82f6, #6366f1);
            box-shadow: 0 0 8px rgba(99, 102, 241, 0.6);
          }
        }
      }

      .el-submenu__title {
        transition: all 0.3s ease;

        &:hover {
          background: rgba(99, 102, 241, 0.1) !important;
        }

        i {
          color: inherit;
        }
      }
    }

    ::v-deep .el-menu-item {
      transition: all 0.3s ease;
      position: relative;

      &.is-active {
        background-color: rgba(99, 102, 241, 0.2) !important;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          height: 100%;
          width: 4px;
          background: linear-gradient(to bottom, #3b82f6, #6366f1);
          box-shadow: 0 0 8px rgba(99, 102, 241, 0.6);
        }
      }

      &:hover {
        background-color: rgba(99, 102, 241, 0.1) !important;
      }
    }
  }
}
</style>
