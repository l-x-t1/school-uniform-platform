<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="采购公告信息名称：" prop="noticeTitle">
                <el-input v-model="queryParams.noticeTitle" placeholder="输入采购公告信息名称进行搜索" clearable
                    @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="采购类型：" prop="purchaseType">
                <el-select v-model="queryParams.purchaseType" placeholder="全部">
                    <el-option label="全部" value="" />
                    <el-option v-for="dict in purchaseTypeOptions" :key="dict.value" :label="dict.label"
                        :value="dict.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="发布时间：" prop="publishTime">
                <el-date-picker v-model="queryParams.publishTime" type="date" placeholder="选择日期"
                    value-format="yyyy-MM-dd" />
            </el-form-item>
            <el-form-item label="选购模式：" prop="purchaseMode">
                <el-select v-model="queryParams.purchaseMode" placeholder="全部">
                    <el-option label="全部" value="" />
                    <el-option v-for="dict in purchaseModeOptions" :key="dict.value" :label="dict.label"
                        :value="dict.value" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
            </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="noticeList" @row-click="handleRowClick">
            <el-table-column label="采购公告信息名称" align="center" prop="noticeTitle" show-overflow-tooltip />
            <el-table-column label="选购模式" align="center" prop="purchaseMode" width="120" />
            <el-table-column label="发布时间" align="center" prop="publishTime" width="120" />
            <el-table-column label="采购类型" align="center" prop="purchaseType" width="120" />
            <el-table-column label="操作" align="center" width="80">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click.stop="handleSelect(scope.row)">选择</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>
</template>

<script>
import { listNotice } from "@/api/cg/notice";

export default {
    name: "NoticeSelection",
    data() {
        return {
            // 遮罩层
            loading: false,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 采购公告列表
            noticeList: [],
            // 选购模式选项
            purchaseModeOptions: [
                { label: "委托招标机构购买企业", value: "1" },
                { label: "学校自主选购企业", value: "2" }
            ],
            // 采购类型选项
            purchaseTypeOptions: [
                { label: "校服采购规范", value: "1" },
                { label: "学校需求支撑", value: "2" }
            ],
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                noticeTitle: null,
                purchaseType: "",
                publishTime: null,
                purchaseMode: ""
            }
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /** 查询采购公告列表 */
        getList() {
            this.loading = true;
            // 模拟数据，实际项目中应使用 API 数据
            setTimeout(() => {
                this.noticeList = [
                    {
                        id: 1,
                        noticeTitle: "河北幸集中学2024-2025招标公告",
                        purchaseMode: "委托招标机构购买企业",
                        publishTime: "2024-10-23 20:38:20",
                        purchaseType: "校服采购规范"
                    },
                    {
                        id: 2,
                        noticeTitle: "XX市第一中学2024-2025校服采购公告",
                        purchaseMode: "学校自主选购企业",
                        publishTime: "2024-10-15 14:20:30",
                        purchaseType: "学校需求支撑"
                    },
                    {
                        id: 3,
                        noticeTitle: "XX区实验学校2024年秋季校服采购公告",
                        purchaseMode: "委托招标机构购买企业",
                        publishTime: "2024-09-28 09:15:45",
                        purchaseType: "校服采购规范"
                    }
                ];
                this.total = this.noticeList.length;
                this.loading = false;
            }, 500);

            // 注释掉API调用，等API准备好后可以取消注释
            /*
            listNotice(this.queryParams).then(response => {
                this.noticeList = response.rows;
                this.total = response.total;
                this.loading = false;
            });
            */
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.queryParams.pageNum = 1;
            this.getList();
        },
        /** 选择行 */
        handleRowClick(row) {
            this.handleSelect(row);
        },
        /** 选择按钮操作 */
        handleSelect(row) {
            const selected = {
                id: row.id,
                noticeTitle: row.noticeTitle
            };
            this.$emit("selected", selected);
        }
    }
};
</script>

<style scoped>
.app-container {
    padding: 10px;
}
</style>
