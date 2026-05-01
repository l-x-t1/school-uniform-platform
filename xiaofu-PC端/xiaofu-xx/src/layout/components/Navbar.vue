<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb
      v-if="!topNav"
      id="breadcrumb-container"
      class="breadcrumb-container"
    />
    <top-nav v-if="topNav" id="topmenu-container" class="topmenu-container" />

    <div class="right-menu">
      <div class="nav-info">
        <el-tooltip
          v-if="roles && roles.includes('xuexiao')"
          content="手机端访问二维码"
          placement="bottom"
        >
          <i
            class="el-icon-mobile-phone mobile-qr-btn"
            @click.stop="openMobileQrDialog"
          />
        </el-tooltip>
        <span class="user-name"
          >{{ greeting ? greeting + "，" : "" }}{{ nickName }}</span
        >
        <!-- <span class="date-text"></span> -->
      </div>

      <template v-if="device !== 'mobile'">
        <!-- <search id="header-search" class="right-menu-item" /> -->

        <!-- <el-tooltip content="源码地址" effect="dark" placement="bottom">
          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />
        </el-tooltip>

        <el-tooltip content="文档地址" effect="dark" placement="bottom">
          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />
        </el-tooltip> -->

        <!-- <screenfull id="screenfull" class="right-menu-item hover-effect" /> -->

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <!-- <size-select id="size-select" class="right-menu-item hover-effect" /> -->
        </el-tooltip>
      </template>

      <el-dropdown
        class="avatar-container right-menu-item hover-effect"
        trigger="click"
      >
        <div class="avatar-wrapper">
          <img src="@/assets/images/xuexiao.png" class="user-avatar" />
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            @click.native="handleSettingSchool"
            v-hasPermi="['xfpt:szxxxx']"
          >
            <span>设置学校信息</span>
          </el-dropdown-item>
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <!-- 学校信息设置对话框 -->
    <el-dialog
      title="设置学校信息"
      :visible.sync="schoolDialogVisible"
      width="640px"
      :close-on-click-modal="false"
      append-to-body
    >
      <el-form
        ref="schoolForm"
        :model="schoolForm"
        :rules="schoolRules"
        label-width="120px"
      >
        <el-form-item label="学校名称:" prop="schoolName">
          <el-input
            v-model="schoolForm.schoolName"
            placeholder="请输入学校名称"
          ></el-input>
        </el-form-item>

        <el-form-item label="院校类型:" prop="academyType">
          <el-radio-group v-model="schoolForm.academyType">
            <el-radio
              v-for="item of dict.type.academy_type"
              :key="item.value"
              :label="item.value"
              >{{ item.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="负责人姓名:" prop="principalName">
          <el-input
            v-model="schoolForm.principalName"
            placeholder="请输入负责人姓名"
          ></el-input>
        </el-form-item>

        <el-form-item label="负责人电话:" prop="principalPhone">
          <el-input
            v-model="schoolForm.principalPhone"
            placeholder="请输入负责人电话"
          ></el-input>
        </el-form-item>

        <el-form-item label="学校地区:" prop="area">
          <el-row>
            <!-- <el-col :span="12">
              <el-input
                v-model="schoolForm.regionCity"
                placeholder="请输入市"
              ></el-input>
            </el-col> -->
            <el-col :span="24">
              <el-select
                style="width: 100%"
                v-model="schoolForm.area"
                placeholder="请选择区县"
              >
                <el-option
                  v-for="item of areaList"
                  :key="item.deptId"
                  :value="item.deptId"
                  :label="item.deptName"
                ></el-option>
              </el-select>
              <!-- <el-input
                v-model="schoolForm.area"
                placeholder=""
                class="ml10"
              ></el-input> -->
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="学校地址:" prop="detailedAddress">
          <el-input
            v-model="schoolForm.detailedAddress"
            placeholder="请输入学校地址"
          ></el-input>
        </el-form-item>

        <el-form-item label="学校类别:" prop="schoolType">
          <el-radio-group
            v-if="schoolForm.academyType == '1'"
            v-model="schoolForm.schoolType"
          >
            <el-radio
              v-for="item of dict.type.sc_sc_type"
              :key="item.value"
              :label="item.value"
              >{{ item.label }}</el-radio
            >
          </el-radio-group>

          <el-radio-group v-else v-model="schoolForm.schoolType">
            <el-radio :label="4">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学校人数(人):" prop="peopleCount">
          <el-input
            v-model="schoolForm.peopleCount"
            placeholder="请输入学校人数"
          ></el-input>
        </el-form-item>
        <el-form-item label="学校校徽:" prop="schoolBadge">
          <FileUpload
            :fileType="['jpg', 'png', 'jpeg']"
            v-model="schoolForm.schoolBadge"
          />
        </el-form-item>
        <el-form-item label="学校简介:" prop="remark">
          <el-input
            type="textarea"
            :rows="4"
            v-model="schoolForm.remark"
            placeholder="请输入学校简介"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSchoolForm">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 修改密码 -->
    <el-dialog
      title="修改密码"
      :visible.sync="changePasswordDialogVisible"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <resetPwd :showClose="false" @success="resetPassword" />
    </el-dialog>

    <!-- 手机端二维码 -->
    <el-dialog
      title="手机端访问"
      :visible.sync="mobileQrDialogVisible"
      width="360px"
      append-to-body
      @opened="onMobileQrDialogOpened"
      @closed="mobileQrDataUrl = ''"
    >
      <div v-loading="mobileQrLoading" class="mobile-qr-dialog-body">
        <img
          v-if="mobileQrDataUrl"
          class="mobile-qr-img"
          :src="mobileQrDataUrl"
          alt="手机端二维码"
        />
        <p v-if="mobileH5FullUrl" class="mobile-qr-url">
          {{ mobileH5FullUrl }}
        </p>
        <p v-else-if="!mobileQrLoading" class="mobile-qr-tip">
          无法生成：请配置环境变量
          VUE_APP_MOBILE_H5_URL，并确保当前账号有部门信息。
        </p>
      </div>
    </el-dialog>

    <!-- 完善手机号 -->
    <el-dialog
      title="完善手机号"
      :visible.sync="phoneDialogVisible"
      width="400px"
      :close-on-click-modal="false"
      :show-close="false"
      append-to-body
    >
      <el-form
        ref="phoneFormRef"
        :model="phoneForm"
        :rules="phoneRules"
        label-width="0"
      >
        <el-form-item prop="phone">
          <el-input
            v-model.trim="phoneForm.phone"
            maxlength="11"
            placeholder="请输入手机号"
          ></el-input>
        </el-form-item>
        <div class="upload-tip">
          注：该手机号用于登录与找回密码，请务必填写本人常用号码。
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPhone">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDeptList } from "@/api/publicIndex";
import { mapGetters } from "vuex";
import Breadcrumb from "@/components/Breadcrumb";
import TopNav from "@/components/TopNav";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import Search from "@/components/HeaderSearch";
import RuoYiGit from "@/components/RuoYi/Git";
import RuoYiDoc from "@/components/RuoYi/Doc";
import { getToken } from "@/utils/auth";
import { isChangePassword } from "@/api/index";
import {
  getSchoolInfo,
  saveSchoolInfo,
  getSchoolInfoDetail,
} from "@/api/questionnaire";
import resetPwd from "@/views/system/user/profile/resetPwd";
import { addPhone } from "@/api/login";
import QRCode from "qrcode";

export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc,
    resetPwd,
  },
  dicts: ["sc_sc_type", "academy_type"],
  data() {
    return {
      currentDate: "",
      greeting: "",
      // 学校信息对话框
      schoolDialogVisible: false,
      schoolForm: {
        id: null,
        deptId: null,
        schoolName: "",
        academyType: "1",
        schoolType: "0",
        regionCity: "石家庄市",
        regionCounty: "",
        detailedAddress: "",
        schoolArea: "",
        schoolBadge: "",
        principalName: "",
        principalPhone: "",
        remark: "",
      },
      areaList: [],
      schoolRules: {
        schoolName: [
          { required: true, message: "请输入学校名称", trigger: "blur" },
        ],
        regionCity: [
          { required: true, message: "请选择学校地区市", trigger: "change" },
        ],
        regionCounty: [
          { required: true, message: "请选择学校地区县区", trigger: "change" },
        ],
        detailedAddress: [
          { required: true, message: "请输入学校地址", trigger: "blur" },
        ],
        schoolArea: [
          { required: true, message: "请选择学校类别", trigger: "change" },
        ],
        principalName: [
          { required: true, message: "请输入负责人姓名", trigger: "blur" },
        ],
        principalPhone: [
          { required: true, message: "请输入负责人电话", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号码",
            trigger: "blur",
          },
        ],
        schoolBadge: [
          { required: true, message: "请上传学校校徽", trigger: "blur" },
        ],
        remark: [
          { required: true, message: "请输入学校简介", trigger: "blur" },
        ],
        peopleCount: [
          { required: true, message: "请输入学校人数", trigger: "blur" },
        ],
      },
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      changePasswordDialogVisible: false,
      // 完善手机号
      phoneDialogVisible: false,
      phoneForm: {
        phone: "",
      },
      phoneRules: {
        phone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号码",
            trigger: "blur",
          },
        ],
      },
      mobileQrDialogVisible: false,
      mobileQrDataUrl: "",
      mobileQrLoading: false,
    };
  },
  computed: {
    ...mapGetters([
      "sidebar",
      "avatar",
      "device",
      "name",
      "nickName",
      "roles",
      "userName",
      "userInfo",
      "isPhone",
      "deptId",
    ]),
    /** 手机端完整链接：.env 中 VUE_APP_MOBILE_H5_URL + deptId */
    mobileH5FullUrl() {
      const base = (process.env.VUE_APP_MOBILE_H5_URL || "").trim();
      if (!base) return "";
      const id = this.deptId;
      if (id === undefined || id === null || id === "") return "";
      const sep = base.includes("?") ? "&" : "?";
      return `${base}${sep}deptId=${encodeURIComponent(id)}`;
    },
    setting: {
      get() {
        return this.$store.state.settings.showSettings;
      },
      set(val) {
        this.$store.dispatch("settings/changeSetting", {
          key: "showSettings",
          value: val,
        });
      },
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav;
      },
    },
  },
  created() {
    if (this.roles.includes("admin") == true) {
      return;
    }
    this.isChangePassword(this.userInfo.userName);

    // 每分钟更新一次日期和问候语
  },
  methods: {
    //判断学校是否需要完善信息
    wanShanSchoolInfo() {
      getSchoolInfo({ deptId: this.$store.state.user.deptId }).then((res) => {
        if (res.code === 200) {
          if (res.data == false) {
            this.schoolDialogVisible = true;
          } else {
            getSchoolInfoDetail(this.$store.state.user.deptId).then((res) => {
              if (res.data) {
                this.schoolForm = res.data;
              }
              this.checkAndPromptPhone();
            });
          }
        }
      });
      this.updateDate();
      getDeptList().then((res) => {
        this.areaList = res.data;
      });
    },
    //判断是否需要修改密码
    isChangePassword() {
      isChangePassword(this.name).then((res) => {
        if (res.data == 0) {
          this.changePasswordDialogVisible = true;
        } else {
          if (this.roles.includes("xuexiao") == true) {
            this.wanShanSchoolInfo();
          } else {
            this.checkAndPromptPhone();
          }
        }
      });
    },
    //设置学校信息
    handleSettingSchool() {
      getSchoolInfoDetail(this.$store.state.user.deptId).then((res) => {
        if (res.data) {
          this.schoolForm = res.data;
          this.schoolForm.area = Number(res.data.area);
          this.schoolDialogVisible = true;
        }
      });
    },
    // 重置学校表单
    resetSchoolForm() {
      this.$refs.schoolForm.resetFields();
    },
    // 提交学校表单
    submitSchoolForm() {
      this.$refs.schoolForm.validate((valid) => {
        if (valid) {
          this.schoolForm.deptId = this.$store.state.user.deptId;
          saveSchoolInfo({
            ...this.schoolForm,
            regionCounty: this.areaList.find(
              (item) => item.deptId == this.schoolForm.area
            ).deptName,
          }).then((res) => {
            if (res.code === 200) {
              this.$message.success("保存成功");
              this.schoolDialogVisible = false;
              this.checkAndPromptPhone();
            } else {
              this.$message.error(res.msg);
            }
          });
        } else {
          return false;
        }
      });
    },
    // 处理区域变化
    handleCityChange() {
      // 当市区变化时，重置县区选择
      this.schoolForm.regionCounty = "";
      // 这里可以添加获取对应县区的逻辑
    },
    // 处理上传校徽成功
    handleSchoolBadgeSuccess(res, file) {
      if (res.code === 200) {
        this.schoolForm.schoolBadge = res.url;
      } else {
        this.$message.error("上传失败");
      }
    },
    // 上传前校验
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg" || file.type === "image/png";
      const isLt300K = file.size / 1024 < 300;

      if (!isJPG) {
        this.$message.error("校徽只能是JPG或PNG格式!");
      }
      if (!isLt300K) {
        this.$message.error("校徽大小不能超过300KB!");
      }
      return isJPG && isLt300K;
    },
    updateDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      this.currentDate = `${year}年${month}月${day}日`;
    },
    updateGreeting() {
      const hour = new Date().getHours();
      if (hour < 6) {
        this.greeting = "凌晨好";
      } else if (hour < 9) {
        this.greeting = "早上好";
      } else if (hour < 12) {
        this.greeting = "上午好";
      } else if (hour < 14) {
        this.greeting = "中午好";
      } else if (hour < 18) {
        this.greeting = "下午好";
      } else if (hour < 22) {
        this.greeting = "晚上好";
      } else {
        this.greeting = "夜深了";
      }
    },
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
    },
    async logout() {
      this.$confirm("确定注销并退出系统吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$store.dispatch("LogOut").then(() => {
            location.href = "/";
          });
        })
        .catch(() => {});
    },
    // 修改密码
    resetPassword() {
      this.changePasswordDialogVisible = false;
      if (this.roles.includes("xuexiao") == true) {
        this.wanShanSchoolInfo();
      } else {
        this.checkAndPromptPhone();
      }
    },

    // 检查并提示完善手机号
    checkAndPromptPhone() {
      // const phone = this.userInfo && this.userInfo.phone;
      const phone = this.isPhone == 0 ? false : true;
      if (!phone) {
        this.phoneDialogVisible = true;
      }
    },

    // 提交手机号
    submitPhone() {
      this.$refs.phoneFormRef &&
        this.$refs.phoneFormRef.validate((valid) => {
          if (!valid) return;
          const userId = this.$store.state.user.id;
          addPhone({ phone: this.phoneForm.phone, userId }).then((res) => {
            if (res.code === 200) {
              this.$message.success("手机号已保存");
              this.phoneDialogVisible = false;
              this.$store.dispatch("GetInfo");
            } else {
              this.$message.error(res.msg || "保存失败");
            }
          });
        });
    },

    openMobileQrDialog() {
      if (!(process.env.VUE_APP_MOBILE_H5_URL || "").trim()) {
        this.$message.warning(
          "未配置手机端地址，请在 .env 中设置 VUE_APP_MOBILE_H5_URL"
        );
        return;
      }
      if (
        this.deptId === undefined ||
        this.deptId === null ||
        this.deptId === ""
      ) {
        this.$message.warning("当前账号无部门信息，无法生成二维码");
        return;
      }
      this.mobileQrDialogVisible = true;
    },

    async onMobileQrDialogOpened() {
      const url = this.mobileH5FullUrl;
      if (!url) return;
      this.mobileQrLoading = true;
      this.mobileQrDataUrl = "";
      try {
        this.mobileQrDataUrl = await QRCode.toDataURL(url, {
          width: 220,
          margin: 2,
        });
      } catch (e) {
        this.$message.error("二维码生成失败");
      } finally {
        this.mobileQrLoading = false;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.navbar {
  height: 56px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  transition: all 0.3s ease;

  .hamburger-container {
    height: 100%;
    float: left;
  }

  .breadcrumb-container {
    float: left;
    line-height: 56px;
    margin-left: 8px;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 56px;
    margin-right: 10px;
    display: flex;
    align-items: center;

    .nav-info {
      display: flex;
      flex-direction: row;
      align-items: center;
      gap: 10px;
      margin-right: 15px;
      line-height: 1.2;
      padding: 8px 0;

      .mobile-qr-btn {
        font-size: 20px;
        color: #5a5e66;
        cursor: pointer;
        padding: 6px;
        border-radius: 8px;
        transition: all 0.3s ease;
        &:hover {
          color: #3b82f6;
          background: rgba(0, 0, 0, 0.04);
        }
      }

      .user-name {
        font-size: 14px;
        font-weight: 500;
        color: black;
      }

      .date-text {
        font-size: 12px;
        color: #666;
        margin-top: 3px;
      }
    }

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 10px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;
      position: relative;

      &.hover-effect {
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
          color: #3b82f6;
        }
      }
    }

    .avatar-container {
      margin-right: 20px;
      position: relative;
      transition: all 0.3s ease;
      height: 100%;

      &:hover {
        .user-avatar {
          box-shadow: 0 0 10px rgba(59, 130, 246, 0.3);
          transform: translateY(-2px);
        }
      }

      .avatar-wrapper {
        margin-top: 8px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 12px;
          transition: all 0.3s ease;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
          border: 2px solid rgba(59, 130, 246, 0.3);
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -18px;
          top: 23px;
          font-size: 12px;
          color: #5a5e66;
          transition: all 0.3s;
        }
      }
    }
  }

  &::after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background: linear-gradient(
      to right,
      transparent,
      rgba(59, 130, 246, 0.2),
      transparent
    );
    opacity: 0.7;
  }
}

@media (max-width: 768px) {
  .nav-info {
    display: none !important;
  }
}

.ml10 {
  margin-left: 10px;
}

.mobile-qr-dialog-body {
  min-height: 120px;
  text-align: center;
}
.mobile-qr-img {
  width: 220px;
  height: 220px;
  display: block;
  margin: 0 auto 12px;
}
.mobile-qr-url {
  font-size: 12px;
  color: #606266;
  word-break: break-all;
  margin: 0;
  line-height: 1.5;
}
.mobile-qr-tip {
  font-size: 13px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

/* 校徽上传样式 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
.upload-tip {
  font-size: 12px;
  color: #ff4949;
  margin-top: 5px;
}
</style>

<style lang="scss">
.el-dropdown-menu {
  padding: 6px 0;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.98);
  border: none;

  .el-dropdown-menu__item {
    padding: 8px 20px;
    font-size: 14px;
    transition: all 0.3s;
    line-height: 1.4;
    height: auto;

    &:hover {
      background-color: rgba(96, 165, 250, 0.1);
      color: #3b82f6;
    }
  }
}
</style>
