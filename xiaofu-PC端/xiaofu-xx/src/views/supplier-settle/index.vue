<template>
  <div>
    <SupplierSettleLogin
      v-if="step === 'login'"
      :noticeId="noticeId"
      @go-register="step = 'register'"
      @go-supplier-info="onGoSupplierInfo"
    />
    <SupplierSettleRegister
      v-else-if="step === 'register'"
      :noticeId="noticeId"
      @go-supplier-info="onGoSupplierInfo"
    />
    <SupplierSettleInfo
      v-else-if="step === 'supplier-info'"
      :noticeId="noticeId"
      :redirect="redirect"
      :accountInfo="accountInfo"
      :supplierData="supplierData"
      @go-back="onGoBack"
    />
  </div>
</template>

<script>
import SupplierSettleLogin from "./login.vue";
import SupplierSettleRegister from "./register.vue";
import SupplierSettleInfo from "./supplier-info.vue";

export default {
  name: "SupplierSettle",
  components: {
    SupplierSettleLogin,
    SupplierSettleRegister,
    SupplierSettleInfo,
  },
  data() {
    return {
      step: "login",
      noticeId: null,
      redirect: "",
      accountInfo: null,
      supplierData: null,
    };
  },
  created() {
    const query = this.$route && this.$route.query;
    this.noticeId = query && query.noticeId ? String(query.noticeId) : null;
    this.redirect = query && query.redirect ? String(query.redirect) : "";
    if (query && query.step) {
      this.step = query.step;
    }
  },
  methods: {
    onGoSupplierInfo({ accountInfo, supplierData }) {
      this.accountInfo = accountInfo || null;
      this.supplierData = supplierData || null;
      this.step = "supplier-info";
    },
    onGoBack() {
      if (this.supplierData) {
        this.step = "login";
      } else {
        this.step = "register";
      }
    },
  },
};
</script>
