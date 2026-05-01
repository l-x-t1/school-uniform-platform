<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password/>
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password/>
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close" v-if="showClose">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserPwd } from "@/api/system/user";

export default {
  props: {
    showClose:{
      type: Boolean,
      default: true
    }
  },
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.user.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      // 检查长度
      if (value.length < 8 || value.length > 20) {
        callback(new Error("密码长度必须在8-20位之间"))
        return
      }

      // 检查是否包含字母
      if (!/[a-zA-Z]/.test(value)) {
        callback(new Error("密码必须包含至少一个字母"))
        return
      }

      // 检查是否包含数字
      if (!/\d/.test(value)) {
        callback(new Error("密码必须包含至少一个数字"))
        return
      } 

      //必须包含一个特殊字符
      if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~`]/.test(value)) {
        callback(new Error("密码必须包含至少一个特殊字符"))
        return
      } 

      callback()
    }
    return {
      user: {
        oldPassword: undefined,
        newPassword: undefined,
        confirmPassword: undefined
      },
      // 表单校验
      rules: {
        oldPassword: [
          { required: true, message: "旧密码不能为空", trigger: "blur" }
        ],
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          { required: true, validator: validatePassword, trigger: ["blur", "change"] }
        ],
        confirmPassword: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.user.oldPassword = null;
            this.user.newPassword = null;
            this.user.confirmPassword = null;
            this.$emit("success");
          });
        }
      });
    },
    close() {
      this.$tab.closePage();
    }
  }
};
</script>
