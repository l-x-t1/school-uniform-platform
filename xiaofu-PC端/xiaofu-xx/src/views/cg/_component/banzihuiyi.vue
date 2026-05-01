<template>
    <div class="detail-panel">
      <div class="header" style="display: flex; align-items: center">
        <h3>班子会议纪要报备与公开</h3>
        <div style="margin-left: 20px">
          <el-button
            icon="el-icon-upload"
            type="primary"
            v-hasPermi="['xfpt:xzbzhy:add']"
            @click="handleAdd"
            v-if="meetingList.length == 0"
            >添加班子会议</el-button
          >
        </div>
      </div> 
  
      <el-table
        v-loading="loading"
        :data="meetingList" 
      >  
        <el-table-column label="序号" align="center" type="index" />
        <el-table-column label="会议名称" align="center" prop="title" />
        <el-table-column label="会议日期" align="center" prop="meetingTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.meetingTime, "{y}-{m}-{d}") }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime">
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope"> 
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['xfpt:xzbzhy:edit']"
              >编辑</el-button
            >
            <el-button
              type="text"
              icon="el-icon-view"
              @click="handleDetail(scope.row)" 
              >详情</el-button
            >
          </template>
        </el-table-column>
      </el-table> 
  
      <!-- 添加或修改班子会议对话框 -->
      <el-dialog
        top="5vh"
        :title="title"
        :visible.sync="open"
        width="1100px"
        append-to-body
      >
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-row :gutter="20">
            <el-col span="12">
              <el-form-item label="会议名称" prop="title">
                <el-input v-model="form.title" placeholder="请输入会议名称" />
              </el-form-item>
            </el-col>
            <el-col span="12">
              <el-form-item label="会议日期" prop="meetingTime">
                <el-date-picker
                  style="width: 100%"
                  clearable
                  v-model="form.meetingTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择会议日期"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-form-item label="参会人员" prop="meetingUser">
            <el-input
              :rows="2"
              type="textarea"
              v-model="form.meetingUser"
              placeholder="请输入参会人员"
            />
          </el-form-item>
          <div>
            <span class="form-label">拟担任选用组织组长</span>
            <el-form-item label-width="0">
              <el-table border :data="form.Proposed">
                <el-table-column
                  label="职务"
                  width="100"
                  align="center"
                  prop="zw"
                >
                  <template>
                    <span>组长</span>
                  </template>
                </el-table-column>
                <el-table-column label="代表类别" align="center" prop="dblb">
                  <template slot="header">
                    <span class="required-field">代表类别</span>
                  </template>
                  <template slot-scope="scope">
                    <el-form
                      :ref="'dblbForm' + scope.$index"
                      label-width="0px"
                      :model="scope.row"
                    >
                      <el-form-item
                        prop="dblb"
                        :rules="[
                          {
                            required: true,
                            message: '请选择代表类别',
                            trigger: 'change',
                          },
                        ]"
                      >
                        <el-select
                          placeholder="请选择代表类别"
                          v-model="scope.row.dblb"
                          disabled
                        >
                          <el-option
                            v-for="item in dict.type.sc_dble"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column label="姓名" align="center" prop="zzName">
                  <template slot="header">
                    <span class="required-field">姓名</span>
                  </template>
                  <template slot-scope="scope">
                    <el-form
                      :ref="'zzNameForm' + scope.$index"
                      label-width="0px"
                      :model="scope.row"
                    >
                      <el-form-item
                        prop="zzName"
                        :rules="[
                          {
                            required: true,
                            message: '请输入姓名',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-input
                          placeholder="请输入姓名"
                          v-model="scope.row.zzName"
                        ></el-input>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </div>
  
          <div>
            <span class="form-label">选购需求</span>
            <el-form-item label-width="0">
              <el-table border :data="form.sonJsonData">
                <el-table-column label="采购年级（级）" align="center" prop="cgnj">
                  <template slot="header">
                    <span class="required-field">采购年级（级）</span>
                  </template>
                  <template slot-scope="scope">
                    <el-form
                      :ref="'cgnjForm' + scope.$index"
                      label-width="0px"
                      :model="scope.row"
                    >
                      <el-form-item
                        prop="cgnj"
                        :rules="[
                          {
                            required: true,
                            message: '请选择采购年级（级）',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-date-picker
                          @change="handleChangeCgnj(scope.row, scope.$index)"
                          style="width: 100%"
                          v-model="scope.row.cgnj"
                          type="year"
                          format="yyyy"
                          value-format="yyyy"
                          placeholder="选择采购年级（级）"
                        >
                        </el-date-picker> 
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column label="校服品类" align="center" width="200" prop="xfpl">
                  <template slot="header">
                    <span class="required-field">校服品类</span>
                  </template>
                  <template slot-scope="scope">
                    <el-form
                      :ref="'xfplForm' + scope.$index"
                      label-width="0px"
                      :model="scope.row"
                    >
                      <el-form-item
                        prop="xfpl"
                        :rules="[
                          {
                            required: true,
                            message: '请输入校服品类',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-select
                          placeholder="请选择校服品类"
                          v-model="scope.row.xfpl"
                          clearable
                        >
                          <el-option
                            v-for="item in dict.type.xfpl"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column
                  label="每个学生拟购买套数"
                  align="center"
                  prop="gmts"
                >
                  <template slot="header">
                    <span class="required-field">每个学生拟购买套数</span>
                  </template>
                  <template slot-scope="scope">
                    <el-form
                      :ref="'gmtsForm' + scope.$index"
                      label-width="0px"
                      :model="scope.row"
                    >
                      <el-form-item
                        prop="gmts"
                        :rules="[
                          {
                            required: true,
                            message: '请输入学生拟购买套数',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-input 
                          placeholder="请输入学生拟购买套数"
                          type="number"
                          v-model="scope.row.gmts"
                        ></el-input>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column
                  label="每套价格区间(拟)"
                  align="center"
                  prop="jgqj"
                >
                  <template slot="header">
                    <span class="required-field">每套价格区间(拟)(元)</span>
                  </template>
                  <template slot-scope="scope">
                    <el-form
                      :ref="'jgqjForm' + scope.$index"
                      label-width="0px"
                      :model="scope.row"
                    >
                      <el-form-item
                        prop="jgqj"
                        :rules="[
                          {
                            required: true,
                            message: '请输入每套价格区间',
                            trigger: 'blur',
                          },
                        ]"
                      >
                        <el-input 
                          placeholder="请输入每套价格区间"
                          v-model="scope.row.jgqj"
                        ></el-input>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column label="备注" align="center" prop="bz" width="140">
                  <template slot-scope="scope">
                    <el-input
                      type="textarea"
                      placeholder="请输入备注"
                      v-model="scope.row.bz"
                    ></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="100">
                  <template slot-scope="scope">
                    <el-button
                      type="primary"
                      icon="el-icon-plus"
                      circle
                      size="mini"
                      v-if="scope.$index === form.sonJsonData.length - 1"
                      @click="addTableRow"
                    ></el-button>
                    <el-button
                      :disabled="scope.row.zw == '2'"
                      type="danger"
                      icon="el-icon-minus"
                      circle
                      size="mini"
                      @click="removeTableRow(scope.$index, scope.row)"
                      v-show="form.sonJsonData.length > 1"
                    ></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item> 
  
            <el-form-item label="会议纪要" prop="hyjy">
              <FileUpload
                :fileType="['pdf']"
                v-model="form.hyjy"
                :gaizhang="true"
              >
                <el-button slot="template" type="text" @click.stop="importTemplate"
                  >下载模板</el-button
                >
              </FileUpload>
            </el-form-item>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
      <el-dialog title="详情" :visible.sync="detailOpen" width="1100px" append-to-body>
        <MeetingInfo :formId="formId" />
      </el-dialog>
    </div>
  </template>
  
  <script> 
  import {
    listMeeting,
    getMeeting,
    addMeeting,
    updateMeeting,
    deleteSonDataById,
    getCurrSchoolGradeList,
  } from "@/api/cg/index";
  import MeetingInfo from "./MeetingInfo.vue";
  export default {
    dicts: ["sc_dble", "xfpl"],
    name: "Meeting", 
    props: {
      teamId: {
        required: true,
      },
    },
    components: {
      MeetingInfo,
    },
    data() {
      return {
        formId:null,
        isEdit: false,
        // 遮罩层
        loading: true,
        // 总条数
        total: 0,
        // 班子会议表格数据
        meetingList: [{}],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 班子会议选购需求
        teamXGCQ: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          title: null,
          meetingTime: null,
          meetingUser: null,
          zw: null,
          dblb: null,
          zzName: null,
          hyjy: null,
          gyfwnx: null,
          deptId: null,
          teamId:this.teamId
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          title: [
            { required: true, message: "会议名称不能为空", trigger: "blur" },
          ],
          meetingTime: [
            { required: true, message: "会议日期不能为空", trigger: "blur" },
          ],
          meetingUser: [
            { required: true, message: "参会人员不能为空", trigger: "blur" },
          ],
          zw: [{ required: true, message: "职务不能为空", trigger: "blur" }],
          dblb: [
            { required: true, message: "代表类别不能为空", trigger: "blur" },
          ],
          zzName: [
            { required: true, message: "组织组长姓名不能为空", trigger: "blur" },
          ],
          hyjy: [
            { required: true, message: "会议纪要不能为空", trigger: "blur" },
          ],
        },
        detailOpen:false,
      };
    },
    created() {
      if (this.$route.query.deptId != undefined) {
        this.queryParams.deptId = this.$route.query.deptId;
      } else {
        this.queryParams.deptId = this.$store.getters.deptId;
      }
      this.getList();
      this.handlegetCurrSchoolGradeList(1);
    },
    methods: {
      importTemplate() {
        const link = document.createElement("a");
        link.href =
          process.env.VUE_APP_IMAGE_BASE_API +
          "/filestatics/download/附件1xx学校三重一大事项会议记录（公开）.docx";
        link.download = "学校三重一大事项会议记录（公开）.docx";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      },
      handleChangeCgnj(row, index) {
        console.log(row, index);
        this.form.sonJsonData[index].cgnj = row.cgnj;
      },

      // 获取班子会议选购需求
      handlegetCurrSchoolGradeList() {
        getCurrSchoolGradeList().then((response) => {
          this.teamXGCQ = response.data;
        });
      },
      /** 查询班子会议列表 */
      getList() {
        this.loading = true;
        listMeeting(this.queryParams)
          .then((response) => {
            this.meetingList = response.rows;
            this.total = response.total; 
            this.loading = false;
          })
          .catch(() => {
            this.loading = false;
          });
      },
      //详情
      handleDetail(row) {
        this.formId = row.id;
        this.detailOpen = true;
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          title: null,
          meetingTime: null,
          meetingUser: null,
          zw: null,
          dblb: 2,
          zzName: null,
          hyjy: null,
          currOrder: null,
          gyfwnx: null,
          Proposed: [
            {
              zw: "组长",
              dblb: "2",
              zzName: null,
            },
          ],
          teamId:this.teamId,
          sonJsonData: [
            { cgnj: null, xfpl: null, gmts: null, jgqj: null, bz: null },
          ],
        };
        this.resetForm("form");
      },

      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.isEdit = false;
        this.title = "添加班子会议";
      },

      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id;
        getMeeting(id).then((response) => {
          this.form = response.data;
          // 处理嵌套数据
          if (this.form.teamMeetingSonList) {
            try {
              this.form.sonJsonData = this.form.teamMeetingSonList;
            } catch (error) {
              this.form.sonJsonData = [
                { cgnj: null, xfpl: null, gmts: null, jgqj: null, bz: null },
              ];
            }
          } else {
            this.form.sonJsonData = [
              { cgnj: null, xfpl: null, gmts: null, jgqj: null, bz: null },
            ];
          }
  
          this.isEdit = true;
  
          // 处理 Proposed 数据
          if (!this.form.Proposed) {
            this.form.Proposed = [
              {
                zw: "组长",
                dblb: this.form.dblb,
                zzName: this.form.zzName,
              },
            ];
          }
  
          this.open = true;
          this.title = "修改班子会议";
        });
      },
      parseForm() {
        this.form.zw = this.form.Proposed[0].zw;
        this.form.dblb = this.form.Proposed[0].dblb;
        this.form.zzName = this.form.Proposed[0].zzName;
        this.form.sonJsonData = JSON.stringify([...this.form.sonJsonData]);
      },
      // 表单验证方法
      formValidate() {
        // 将所有表单验证包装为Promise
        const validateFormRef = (ref) => {
          return new Promise((resolve) => {
            if (this.$refs[ref]) {
              this.$refs[ref].validate((valid) => {
                resolve(valid);
              });
            } else {
              resolve(true); // 如果引用不存在就视为验证通过
            }
          });
        };
  
        // 验证所有组织组长表单
        const validateProposed = () => {
          const promises = [];
          this.form.Proposed.forEach((_, index) => {
            promises.push(validateFormRef("dblbForm" + index));
            promises.push(validateFormRef("zzNameForm" + index));
          });
          return Promise.all(promises);
        };
  
        // 验证所有选购需求表单
        const validateSonJsonData = () => {
          const promises = [];
          this.form.sonJsonData.forEach((_, index) => {
            promises.push(validateFormRef("cgnjForm" + index));
            promises.push(validateFormRef("xfplForm" + index));
            promises.push(validateFormRef("gmtsForm" + index));
            promises.push(validateFormRef("jgqjForm" + index));
          });
          return Promise.all(promises);
        };
  
        // 执行验证并处理结果
        return new Promise((resolve) => {
          Promise.all([validateProposed(), validateSonJsonData()]).then(
            (results) => {
              // 将所有验证结果展平并检查是否都通过了验证
              const allResults = results.flat(2);
              const isValid = allResults.every((valid) => valid === true);
  
              if (!isValid) {
                this.$message.error(`请填写必要信息!`);
                resolve(false);
              } else {
                resolve(true);
              }
            }
          );
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate((valid) => {
          if (valid) {
            // 先验证表格内容
            this.formValidate().then((tableValid) => {
              if (!tableValid) {
                return;
              }
  
              this.parseForm();
  
              if (this.form.id != null) {
                updateMeeting(this.form).then((response) => {
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                  
                });
              } else {
                addMeeting(this.form).then((response) => {
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
                  this.formId = response.data;
                  this.$emit('success',response.data)
                });
              }
            });
          }
        });
      },

      // 添加表格行
      addTableRow() {
        this.form.sonJsonData.push({
          cgnj: null,
          xfpl: null,
          gmts: null,
          jgqj: null,
          bz: null,
        });
      },
  
      // 删除表格行
      removeTableRow(index, row) {
        if (this.form.sonJsonData.length > 1) {
          if (row.id) {
            deleteSonDataById(row.id).then((response) => {
              this.$modal.msgSuccess("删除成功");
              this.form.sonJsonData.splice(index, 1);
            });
          } else {
            this.form.sonJsonData.splice(index, 1);
          }
        } else {
          this.$message.warning("至少保留一行数据");
        }
      },

    },
  };
  </script>
  <style lang="scss" scoped>
  .header {
  margin-top: 0;
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}
.detail-panel {
  padding: 20px 0;
}

.detail-panel h3 {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
  ::v-deep .el-dialog__header {
    padding-bottom: 0px !important;
    padding: 15px 20px 10px;
  }
  ::v-deep .el-dialog__body {
    padding: 10px 20px;
  }
  .form-label {
    display: inline-block;
    margin-bottom: 10px;
    font-size: 14px;
    color: #606266;
    line-height: 40px;
    padding: 0 12px 0 0;
    box-sizing: border-box;
    text-align: right;
  }
  .el-form-item__error {
    color: #f56c6c;
    font-size: 12px;
    line-height: 1;
    padding-top: 4px;
    position: absolute;
    left: 0;
    right: 0;
    text-align: center;
  }
  .required-field:before {
    content: "*";
    color: #f56c6c;
    margin-right: 4px;
  }

  
  // 文本域字体样式
  ::v-deep .el-textarea__inner {
    font-family: "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
    font-weight: normal;
  }
  </style>
  