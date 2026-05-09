import { listFinanceFlow } from "@/api/school/financeFlow";
import { addWithdrawApply, listWithdrawApply } from "@/api/school/withdrawApply";

export function listAccountBalanceFinance(query) {
  return listFinanceFlow(query);
}

export function listAccountBalanceWithdraw(query) {
  return listWithdrawApply(query);
}

export function createAccountBalanceWithdraw(data) {
  return addWithdrawApply(data);
}
