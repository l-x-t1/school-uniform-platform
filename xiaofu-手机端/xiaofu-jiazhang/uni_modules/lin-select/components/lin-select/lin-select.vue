<template>
  <view>
    <view class="lin-fixed" v-show="showComboxSelect" @click="gclick"></view>
    <view class="lin-combox" @click="showDropdown">
      <uni-easyinput
        ref="uni-easyinput"
        :placeholder="placeholder"
        type="text"
        @input="oninput"
        @focus="onfocus"
      />
      <view class="lin-combox-select" v-show="showComboxSelect">
        <view class="lin-popper__arrow"></view>
        <scroll-view scroll-y="true" :style="'max-height:' + maxHeight + 'px;'">
          <view v-if="loading" class="fedback-popper_loading">{{ loadingText }}</view>
          <template v-else>
            <view v-if="!list.length" class="fedback-popper_nodata">暂无数据</view>
            <view
              v-else
              class="items"
              v-for="(item, key) in list"
              :key="key"
              :id="key"
              @click.stop="comboxCheckHandel(item)"
            >
              {{ item[nameKey] }}
            </view>
          </template>
        </scroll-view>
      </view>
    </view>
  </view>
</template>
<script>
export default {
  emits: ["update:modelValue", "input", "change", "confirm", "focus"],
  props: {
    loading: {
      type: Boolean,
      default: false,
    },
    maxHeight: {
      type: String || Number,
      default: 125,
    },
    valueKey: {
      type: String,
      default: "value",
    },
    nameKey: {
      type: String,
      default: "name",
    },
    placeholder: {
      type: String,
      default: "请输入",
    },
    loadingText: {
      type: String,
      default: "加载中",
    },
    modelValue: [Number, String],
    value: [Number, String],
    list: {
      type: Array,
      default: () => [],
    },
    type: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      showComboxSelect: false,
      checkValue: "",
    };
  },
  mounted() {
    if (!this.$refs["uni-easyinput"]) {
      console.error("请先导入uni-easyinput插件");
      return;
    }
    this.watchInitialValue();
  },
  watch: {
    // #ifdef VUE2
    value(val) {
      this.checkValue = val;
      this.getInitText();
    },
    // #endif
    // #ifdef VUE3
    modelValue(val) {
      this.checkValue = val;
      this.getInitText();
    },
    // #endif
  },
  methods: {
    gclick() {
      this.showComboxSelect = false;
    },
    onfocus() {
      this.$emit("focus");
      this.showDropdown();
    },
    showDropdown() {
      if (!this.list.length) return;
      this.showComboxSelect = true;
    },
    watchInitialValue() {
      if (this.list.length) {
        this.getInitText();
        return;
      }
      const unwatchList = this.$watch("list", () => {
        this.getInitText();
        unwatchList();
      });
    },
    getInitText() {
      const initialValue =
        this.modelValue === undefined || this.modelValue === null
          ? this.value
          : this.modelValue;
      this.checkValue = initialValue;

      if (!this.list.length) return;
      if (
        this.checkValue === "" ||
        this.checkValue === undefined ||
        this.checkValue === null
      ) {
        return;
      }
      if (this.showComboxSelect) return;

      const currentValue = Number.isNaN(Number(this.checkValue))
        ? this.checkValue
        : +this.checkValue;
      const item = this.list.find((option) => option[this.valueKey] === currentValue);
      if (item) {
        this.$refs["uni-easyinput"].val = item[this.nameKey];
      }
    },
    reset() {
      this.checkValue = "";
      this.$emit("change", "");
      // #ifdef VUE3
      this.$emit("update:modelValue", "");
      // #endif
      this.$nextTick(() => {
        this.$refs["uni-easyinput"].val = "";
      });
    },
    comboxCheckHandel(item) {
      const text = item[this.nameKey];
      const value = item[this.valueKey];
      this.checkValue = value;
      this.showComboxSelect = false;
      // #ifdef VUE3
      this.$emit("update:modelValue", value);
      // #endif
      this.$emit("change", value);
      this.$nextTick(() => {
        this.$refs["uni-easyinput"].val = text;
      });
      this.$emit("confirm", value);
    },
    oninput(val) {
      if (!val) {
        this.checkValue = "";
        this.showComboxSelect = this.list.length > 0;
      } else {
        this.showComboxSelect = true;
      }
      this.$emit("input", val);
    },
  },
};
</script>
<style lang="less">
.lin-fixed {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  z-index: 1;
}

.lin-combox {
  position: relative;

  .lin-combox-select {
    position: absolute;
    top: 45px;
    left: 0;
    right: 0;
    background-color: #fff;
    z-index: 8;
    border-radius: 6px;
    padding: 3px 0;
    border: 1px solid #ebeef5;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .fedback-popper_nodata {
      font-size: 13px;
      padding: 5px;
      color: #5d5959;
      text-align: center;
    }

    .lin-popper__arrow {
      position: absolute;
      top: -13px;
      left: 32px;
      z-index: 3;
      content: "";
      width: 0;
      height: 0;
      display: block;
      border-color: transparent;
      border-style: solid;
      border-width: 6px;
      border-bottom-color: #ebeef5;

      &::before {
        content: "";
        position: absolute;
        display: block;
        width: 0;
        height: 0;
        border-color: transparent;
        border-style: solid;
        border-width: 6px;
        top: 1px;
        margin-left: -6px;
        border-top-width: 0;
        border-bottom-color: #fff;
      }
    }

    .items {
      height: 35px;
      line-height: 35px;
      padding: 0 10px;
      font-size: 15px;
    }

    .fedback-popper_loading {
      text-align: center;
      font-size: 13px;
      padding: 5px;
      color: #5d5959;
    }
  }
}
</style>
