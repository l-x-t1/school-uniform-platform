<template>
  <div class="app-container vote-result-page">
    <el-row :gutter="20">
      <el-col :xl="10" :lg="11" :md="24" :sm="24" :xs="24">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <div>
              <div class="panel-title">投票结果</div>
              <div class="panel-subtitle">
                这里只展示线上供应商选择投票结果，最终采用哪家供应商仍由负责人手动确定。
              </div>
            </div>
            <el-button icon="el-icon-refresh" @click="getList">刷新</el-button>
          </div>

          <el-table
            v-loading="loading"
            :data="voteList"
            highlight-current-row
            border
            height="560"
            row-key="id"
            @current-change="handleCurrentChange"
          >
            <el-table-column label="标题" prop="title" min-width="220" show-overflow-tooltip />
            <el-table-column label="已投" width="90" align="center" prop="votedCount" />
            <el-table-column label="总人数" width="100" align="center" prop="count" />
            <el-table-column label="投票率" width="110" align="center">
              <template slot-scope="scope">
                <span>{{ formatRate(scope.row.votingRate) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" width="180" align="center">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}") }}</span>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </el-col>

      <el-col :xl="14" :lg="13" :md="24" :sm="24" :xs="24">
        <el-card v-loading="detailLoading" shadow="never" class="panel-card detail-card">
          <template v-if="currentVote && currentVote.id">
            <div class="detail-header">
              <div>
                <div class="detail-title">{{ currentVote.title || "投票详情" }}</div>
                <div class="detail-subtitle">
                  票数结果只作为参考，不会按第一名自动选中供应商。
                </div>
              </div>
              <el-button
                v-if="currentVote.questId"
                type="text"
                icon="el-icon-data-analysis"
                @click="openStatistics"
              >
                查看原始统计
              </el-button>
            </div>

            <el-alert
              title="当前业务只有线上投票，页面不会展示线下投票内容。"
              type="info"
              :closable="false"
              show-icon
              class="hint-alert"
            />

            <el-row :gutter="12" class="overview-cards">
              <el-col :span="8">
                <div class="overview-card">
                  <div class="overview-label">投票人数</div>
                  <div class="overview-value">{{ currentVote.votedCount || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="overview-card">
                  <div class="overview-label">总人数</div>
                  <div class="overview-value">{{ currentVote.count || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="overview-card">
                  <div class="overview-label">投票率</div>
                  <div class="overview-value">{{ formatRate(currentVote.votingRate) }}</div>
                </div>
              </el-col>
            </el-row>

            <el-descriptions :column="2" border class="summary-descriptions">
              <el-descriptions-item label="投票类型">线上投票</el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ parseTime(currentVote.createTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
              </el-descriptions-item>
              <el-descriptions-item label="候选供应商数">
                {{ companyList.length }}
              </el-descriptions-item>
              <el-descriptions-item label="已保存选择">
                {{ savedSelectionLabel }}
              </el-descriptions-item>
            </el-descriptions>

            <div class="section-title">投票情况</div>
            <el-table
              :data="supplierVoteStats"
              border
              class="vote-table"
              empty-text="暂无可展示的投票结果"
            >
              <el-table-column label="排名" width="70" align="center">
                <template slot-scope="scope">
                  <el-tag v-if="scope.$index === 0 && scope.row.voteCount > 0" type="danger" size="mini">
                    第1名
                  </el-tag>
                  <span v-else>{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="供应商" prop="name" min-width="180" show-overflow-tooltip />
              <el-table-column label="票数" prop="voteCount" width="90" align="center" />
              <el-table-column label="占比" width="120" align="center">
                <template slot-scope="scope">
                  {{ scope.row.percentageText }}
                </template>
              </el-table-column>
              <el-table-column label="可视化" min-width="220">
                <template slot-scope="scope">
                  <div class="progress-line">
                    <div class="progress-track">
                      <div class="progress-bar" :style="{ width: scope.row.percentageText }"></div>
                    </div>
                    <span class="progress-text">{{ scope.row.percentageText }}</span>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <div class="section-title">最终选择</div>
            <el-form label-width="108px" class="final-form">
              <el-form-item label="最终供应商">
                <el-radio-group v-model="selectedSupplierKey" class="supplier-radio-group">
                  <el-radio
                    v-for="item in selectionOptions"
                    :key="item.key"
                    :label="item.key"
                    class="supplier-radio"
                  >
                    <span class="supplier-radio-name">{{ item.name }}</span>
                    <span class="supplier-radio-meta">{{ item.voteCount }}票 / {{ item.percentageText }}</span>
                  </el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button
                  v-hasPermi="['xfpg:pdxzqdcs-edit']"
                  type="primary"
                  icon="el-icon-check"
                  @click="saveSelection"
                >
                  保存本页选择
                </el-button>
                <el-button icon="el-icon-refresh-left" @click="restoreSelection">恢复已保存</el-button>
              </el-form-item>
            </el-form>
          </template>

          <el-empty v-else description="请先在左侧选择一条线上投票记录" />
        </el-card>
      </el-col>
    </el-row>

    <survey-statistics :visible.sync="statisticsVisible" :selected-survey-id="selectedSurveyId" />
  </div>
</template>

<script>
import { listPdxzSupplier, getCompanyInfo } from '@/api/cg/pdxzSupplier'
import { getSurveyStatistics } from '@/api/school/survey'
import SurveyStatistics from '@/views/wjdc/tongji'

const STORAGE_KEY = 'school-vote-result-final-selection'

export default {
  name: 'VoteResult',
  components: {
    SurveyStatistics
  },
  data() {
    return {
      loading: false,
      detailLoading: false,
      total: 0,
      voteList: [],
      currentVote: null,
      companyList: [],
      surveyData: {
        sonList: []
      },
      selectedSupplierKey: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: '0'
      },
      statisticsVisible: false,
      selectedSurveyId: null
    }
  },
  computed: {
    supplierVoteStats() {
      const optionEntries = this.getSurveyOptionEntries()
      const supplierStats = this.companyList.map((item) => {
        const voteCount = this.findVoteCountBySupplier(item.name, optionEntries)
        return {
          key: this.buildSupplierKey(item),
          id: item.id,
          name: item.name,
          voteCount
        }
      })

      const extraOptions = optionEntries
        .filter((entry) => !supplierStats.some((item) => this.normalizeText(item.name) === this.normalizeText(entry.name)))
        .map((entry, index) => ({
          key: 'extra-' + index + '-' + this.normalizeText(entry.name),
          id: null,
          name: entry.name,
          voteCount: entry.voteCount
        }))

      const merged = supplierStats.concat(extraOptions)
      const totalVotes = merged.reduce((sum, item) => sum + Number(item.voteCount || 0), 0)

      return merged
        .map((item) => {
          const percentage = totalVotes ? ((Number(item.voteCount || 0) / totalVotes) * 100).toFixed(2) : '0.00'
          return {
            ...item,
            percentage,
            percentageText: percentage + '%'
          }
        })
        .sort((a, b) => {
          if (Number(b.voteCount) !== Number(a.voteCount)) {
            return Number(b.voteCount) - Number(a.voteCount)
          }
          return String(a.name || '').localeCompare(String(b.name || ''), 'zh-CN')
        })
    },
    selectionOptions() {
      if (this.supplierVoteStats.length > 0) {
        return this.supplierVoteStats
      }
      return this.companyList.map((item) => ({
        key: this.buildSupplierKey(item),
        id: item.id,
        name: item.name,
        voteCount: 0,
        percentageText: '0.00%'
      }))
    },
    savedSelectionLabel() {
      const saved = this.getSavedSelection()
      if (!saved || !saved.supplierName) {
        return '未保存'
      }
      return saved.supplierName + '（' + this.formatSavedTime(saved.savedAt) + '）'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPdxzSupplier(this.queryParams)
        .then((response) => {
          const rows = (response.rows || []).filter((item) => String(item.type) === '0')
          this.voteList = rows
          this.total = rows.length
          if (this.voteList.length > 0) {
            const nextRow = this.currentVote && this.currentVote.id
              ? this.voteList.find((item) => item.id === this.currentVote.id) || this.voteList[0]
              : this.voteList[0]
            this.handleCurrentChange(nextRow)
          } else {
            this.currentVote = null
            this.companyList = []
            this.surveyData = { sonList: [] }
            this.selectedSupplierKey = ''
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleCurrentChange(row) {
      if (!row || !row.id) {
        return
      }
      this.currentVote = row
      this.loadVoteDetail(row)
    },
    loadVoteDetail(row) {
      this.detailLoading = true
      const tasks = [getCompanyInfo(row.teamId)]
      if (row.questId) {
        tasks.push(getSurveyStatistics({ id: row.questId }))
      } else {
        tasks.push(Promise.resolve({ data: { sonList: [] } }))
      }

      Promise.all(tasks)
        .then(([companyRes, statisticsRes]) => {
          this.companyList = companyRes.data || []
          this.surveyData = statisticsRes.data || { sonList: [] }
          this.restoreSelection()
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    openStatistics() {
      this.selectedSurveyId = this.currentVote ? this.currentVote.questId : null
      if (!this.selectedSurveyId) {
        this.$modal.msgWarning('当前投票没有可查看的问卷统计。')
        return
      }
      this.statisticsVisible = true
    },
    saveSelection() {
      const selected = this.selectionOptions.find((item) => item.key === this.selectedSupplierKey)
      if (!selected) {
        this.$modal.msgWarning('请先选择最终供应商。')
        return
      }
      if (!this.currentVote || !this.currentVote.id) {
        this.$modal.msgWarning('请先选择投票记录。')
        return
      }

      const storageData = this.readStorage()
      storageData[this.currentVote.id] = {
        supplierKey: selected.key,
        supplierId: selected.id,
        supplierName: selected.name,
        savedAt: new Date().toISOString()
      }
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(storageData))
      this.$modal.msgSuccess('最终选择已保存在当前浏览器。')
    },
    restoreSelection() {
      const saved = this.getSavedSelection()
      this.selectedSupplierKey = saved && saved.supplierKey ? saved.supplierKey : ''
    },
    getSavedSelection() {
      if (!this.currentVote || !this.currentVote.id) {
        return null
      }
      const storageData = this.readStorage()
      return storageData[this.currentVote.id] || null
    },
    readStorage() {
      try {
        const value = window.localStorage.getItem(STORAGE_KEY)
        return value ? JSON.parse(value) : {}
      } catch (error) {
        return {}
      }
    },
    getSurveyOptionEntries() {
      const resultMap = {}
      const surveys = (this.surveyData && this.surveyData.sonList) || []
      surveys.forEach((survey) => {
        const results = survey.xfptQuestionnaireResults || []
        results.forEach((item) => {
          const optionName = String(item.questionnaireResult || '').trim()
          if (!optionName) {
            return
          }
          const key = this.normalizeText(optionName)
          if (!resultMap[key]) {
            resultMap[key] = {
              name: optionName,
              voteCount: 0
            }
          }
          resultMap[key].voteCount += Number(item.amount || 0)
        })
      })
      return Object.values(resultMap)
    },
    findVoteCountBySupplier(supplierName, optionEntries) {
      const normalizedSupplierName = this.normalizeText(supplierName)
      let matchedEntry = optionEntries.find((item) => this.normalizeText(item.name) === normalizedSupplierName)
      if (!matchedEntry) {
        matchedEntry = optionEntries.find((item) => {
          const normalizedOption = this.normalizeText(item.name)
          return normalizedOption.includes(normalizedSupplierName) || normalizedSupplierName.includes(normalizedOption)
        })
      }
      return matchedEntry ? Number(matchedEntry.voteCount || 0) : 0
    },
    normalizeText(value) {
      return String(value || '')
        .replace(/\s+/g, '')
        .replace(/[()（）]/g, '')
        .trim()
        .toLowerCase()
    },
    buildSupplierKey(item) {
      if (item && item.id !== null && item.id !== undefined) {
        return 'supplier-' + item.id
      }
      return 'supplier-name-' + this.normalizeText(item && item.name)
    },
    formatRate(value) {
      if (value === null || value === undefined || value === '') {
        return '0%'
      }
      return Number(value).toFixed(2) + '%'
    },
    formatSavedTime(value) {
      if (!value) {
        return '未记录时间'
      }
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return '未记录时间'
      }
      return this.parseTime(date, '{y}-{m}-{d} {h}:{i}')
    }
  }
}
</script>

<style scoped>
.vote-result-page {
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.panel-card {
  border-radius: 18px;
  border: 1px solid #e6ebf5;
}

.panel-header,
.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.panel-title,
.detail-title {
  color: #1f2a44;
  font-size: 22px;
  font-weight: 600;
  line-height: 1.3;
}

.panel-subtitle,
.detail-subtitle {
  color: #7a869a;
  font-size: 13px;
  margin-top: 6px;
}

.detail-card {
  min-height: 760px;
}

.hint-alert {
  margin-top: 14px;
  margin-bottom: 18px;
}

.overview-cards {
  margin-bottom: 18px;
}

.overview-card {
  background: linear-gradient(135deg, #f9fbff 0%, #eef4ff 100%);
  border: 1px solid #dbe6ff;
  border-radius: 16px;
  padding: 18px 20px;
}

.overview-label {
  color: #6b7280;
  font-size: 13px;
}

.overview-value {
  color: #153e75;
  font-size: 28px;
  font-weight: 700;
  margin-top: 10px;
}

.summary-descriptions {
  margin-bottom: 20px;
}

.section-title {
  color: #1f2a44;
  font-size: 16px;
  font-weight: 600;
  margin: 22px 0 14px;
}

.vote-table {
  margin-bottom: 10px;
}

.progress-line {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-track {
  flex: 1;
  height: 10px;
  background: #edf2f7;
  border-radius: 999px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #3b82f6 0%, #14b8a6 100%);
}

.progress-text {
  color: #52637a;
  font-size: 12px;
  min-width: 48px;
  text-align: right;
}

.final-form {
  background: #fbfcfe;
  border: 1px solid #e6ebf5;
  border-radius: 16px;
  padding: 20px 20px 4px;
}

.supplier-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.supplier-radio {
  margin-right: 0;
  border: 1px solid #dbe6ff;
  border-radius: 14px;
  padding: 12px 14px;
  background: #fff;
}

.supplier-radio-name {
  color: #1f2a44;
  font-weight: 600;
  margin-right: 10px;
}

.supplier-radio-meta {
  color: #7a869a;
  font-size: 12px;
}

@media (max-width: 991px) {
  .detail-card {
    margin-top: 20px;
    min-height: auto;
  }
}
</style>
