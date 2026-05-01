<!-- @format -->

<template>
  <div class="app-container" style="position: relative; padding-bottom: 50px">
    <div class="content" :class="{ noRequried: isDisabled }">
      <el-form
        :scroll-to-error="true"
        :scroll-into-view-options="{
          behavior: 'smooth',
          block: 'end',
          inline: 'nearest',
        }"
        ref="personFormRef"
        :model="personDataForm"
        :rules="personDataFormRules"
      >
        <div
          style="
            display: flex;
            justify-content: space-between;
            align-items: self-end;
          "
        >
          <div class="subheading">个人基本信息</div>
        </div>

        <div class="avater"></div>

        <div style="display: flex" class="person-baseInfo">
          <el-descriptions
            class="margin-top"
            style="margin-bottom: 0; margin-top: 0px; width: 100%"
            :column="3"
            size="large"
            border
          >
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">中文名</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="chineseName">
                <span v-if="isDisabled">
                  {{ personDataForm.chineseName }}
                </span>
                <el-input
                  :disabled="disabledSpbg"
                  style="width: 100%"
                  v-else
                  placeholder="请输入中文名"
                  v-model="personDataForm.chineseName"
                ></el-input>
              </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item>
              <template #label>
                <div class="cell-item">英文名</div>
              </template>
              <el-form-item>
                <el-input
                  :disabled="disabledSpbg"
                  v-if="!isDisabled"
                  placeholder="请输入英文名"
                  v-model="personDataForm.englishName"
                ></el-input>
                <span v-else>
                  {{ personDataForm.englishName || "无" }}
                </span>
              </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item>
              <template #label>
                <div class="cell-item">性别</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="gender">
                <span v-if="isDisabled">
                  {{ personDataForm.gender === "0" ? "男" : "女" }}
                </span>
                <el-select
                  :disabled="disabledSpbg"
                  v-else
                  style="width: 100%"
                  placeholder="请选择性别"
                  v-model="personDataForm.gender"
                >
                  <el-option label="男" value="0"></el-option>
                  <el-option label="女" value="1"></el-option>
                </el-select>
              </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item>
              <template #label>
                <div class="cell-item">政治面貌</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="politicsStatus">
                <span v-if="isDisabled">
                  <dict-tag
                    :options="politics_status"
                    :value="personDataForm.politicsStatus"
                  ></dict-tag>
                </span>
                <el-select
                  v-else
                  :disabled="disabledSpbg"
                  style="width: 100%"
                  v-model="personDataForm.politicsStatus"
                  placeholder="请选择您的政治面貌"
                >
                  <el-option
                    v-for="item of politics_status"
                    :key="item.value"
                    :value="item.value"
                    :label="item.label"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item>
              <template #label>
                <div class="cell-item">民族</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="minZu">
                <span v-if="isDisabled">
                  {{ personDataForm.minZu }}
                </span>
                <el-select
                  :disabled="disabledSpbg"
                  v-else
                  style="width: 100%"
                  v-model="personDataForm.minZu"
                  placeholder="请选择民族民族"
                >
                  <el-option
                    v-for="item of mz_data"
                    :key="item.name"
                    :label="item.name"
                    :value="item.name"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item>
              <template #label>
                <div class="cell-item">出生日期</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="chuShengDate">
                <span v-if="isDisabled">
                  {{ personDataForm.chuShengDate }}
                </span>
                <el-date-picker
                  v-else
                  :disabled="disabledSpbg"
                  style="width: 100%"
                  v-model="personDataForm.chuShengDate"
                  type="date"
                  placeholder="请选择出生日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item>
              <template #label>
                <div class="cell-item">国籍</div>
              </template>
              <el-form-item>
                <span v-if="isDisabled">
                  {{ personDataForm.nationality || "无" }}
                </span>
                <el-input
                  :disabled="disabledSpbg"
                  v-else
                  v-model="personDataForm.nationality"
                  placeholder="请输入国籍"
                ></el-input>
              </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item :span="3">
              <template #label>
                <div class="cell-item">籍贯</div>
              </template>
              <el-form-item>
                <span v-if="isDisabled">
                  {{ personDataForm.jiGuan || "无" }}
                </span>
                <el-input
                  :disabled="disabledSpbg"
                  v-else
                  v-model="personDataForm.jiGuan"
                  placeholder="请输入籍贯"
                ></el-input>
              </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">现工作单位职务</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="yjrcXianDanWeiZhiWu">
                <span v-if="isDisabled">
                  {{ personDataForm.yjrcXianDanWeiZhiWu || "无" }}
                </span>
                <el-input
                  v-else
                  v-model="personDataForm.yjrcXianDanWeiZhiWu"
                  placeholder="请输入现工作单位职务"
                ></el-input>
              </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item :span="2">
              <template #label>
                <div class="cell-item">本市居住地</div>
                <span class="required">*</span>
              </template>
              <el-form-item prop="juZhuDi">
                <div style="display: flex; align-items: center; width: 100%">
                  <div style="width: 273px">
                    <span v-if="isDisabled">
                      <dict-tag
                        style="display: inline-block"
                        :options="rclk_dzqx"
                        :value="personDataForm.juZhuDiQu"
                      ></dict-tag
                      >{{ personDataForm.juZhuDi }}
                    </span>

                    <el-select
                      v-else
                      style="width: 248px"
                      v-model="personDataForm.juZhuDiQu"
                      placeholder="请选择区县"
                    >
                      <el-option
                        v-for="item in rclk_dzqx"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      >
                      </el-option>
                    </el-select>
                  </div>

                  <div
                    v-if="!isDisabled"
                    style="width: 350px; margin-left: -15px"
                  >
                    <el-input
                      v-model="personDataForm.juZhuDi"
                      placeholder="请输入本市居住地"
                    ></el-input>
                  </div>
                </div>
              </el-form-item>
            </el-descriptions-item>
          </el-descriptions>
          <div class="photo-box">
            <el-form-item style="margin-left: 20px" prop="photoPath">
              <el-tooltip
                :disabled="isDisabled"
                class="box-item"
                effect="light"
                placement="top-start"
              >
                <template #content>
                  <div class="avater-modal">
                    <span>照片要求：</span>
                    <span>尺寸：295px*413px</span>
                    <span>大小：大于1M</span>
                    <span>底色：纯白</span>
                    <span>格式：jpg,png,jpeg,bmp</span>
                  </div>
                </template>
                <el-upload
                  :disabled="isDisabled || disabledSpbg"
                  :class="{ isUpload: fileList.length, isDisabled: isDisabled }"
                  :action="uploadFileUrl"
                  v-model:file-list="fileList"
                  list-type="picture-card"
                  :headers="headers"
                  :on-success="handleUploadSuccess"
                  :on-remove="handleRemove"
                >
                  <el-icon style="font-size: 50px"><Avatar /></el-icon>
                </el-upload>
              </el-tooltip>
            </el-form-item>
          </div>
        </div>

        <el-descriptions
          title="人才类别，证件类型"
          class="margin-top"
          :column="3"
          size="large"
          border
          direction="horizontal"
        >
          <el-descriptions-item>
            <template #label>
              <div
                class="cell-item"
                style="
                  display: flex;
                  align-items: center;
                  justify-content: center;
                "
              >
                <span>人才类别</span>
                <el-tooltip
                  class="box-item"
                  raw-content
                  effect="light"
                  placement="top-end"
                >
                  <template #content>
                    <div
                      style="font-size: 16px; padding-top: 20px"
                      v-html="applyContent.content"
                    ></div>
                  </template>

                  <el-icon
                    style="
                      margin-right: -15px;
                      margin-left: 5px;
                      font-size: 17px;
                      cursor: pointer;
                    "
                    ><QuestionFilled
                  /></el-icon>
                </el-tooltip>
              </div>

              <span class="required">*</span>
            </template>
            <el-form-item prop="renCaiType">
              <span v-if="isDisabled">
                {{
                  listByCardType.find(
                    (item) => item.id == personDataForm.renCaiType
                  )?.name || "无"
                }}
              </span>
              <el-select
                v-else
                :disabled="disabledSpbg"
                style="width: 100%"
                @change="handleChangeCardType"
                v-model="personDataForm.renCaiType"
                placeholder="请选择人才类别"
              >
                <el-option
                  v-for="item of listByCardType"
                  :key="item.id"
                  :value="String(item.id)"
                  :label="item.name"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">人才称号</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="renCaiChengHao">
              <span v-if="isDisabled">
                {{
                  talentTitle.find(
                    (item) => item.id == personDataForm.renCaiChengHao
                  )?.name
                }}
              </span>
              <el-select
                :disabled="disabledSpbg"
                v-else
                style="width: 100%"
                v-model="personDataForm.renCaiChengHao"
                placeholder="请选择人才称号"
              >
                <el-option
                  v-for="item of talentTitle"
                  :key="item.id"
                  :value="String(item.id)"
                  :label="item.name"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">取得日期</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="getDate">
              <span v-if="isDisabled">
                {{ personDataForm.getDate }}
              </span>
              <el-date-picker
                :disabled="disabledSpbg"
                v-else
                v-model="personDataForm.getDate"
                style="width: 100%"
                type="date"
                placeholder="请选择取得日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">证件类型</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="zhengJianType">
              <span v-if="isDisabled">
                <dict-tag
                  :options="zheng_jian_type"
                  :value="personDataForm.zhengJianType"
                ></dict-tag>
              </span>
              <el-select
                :disabled="disabledSpbg"
                v-else
                style="width: 100%"
                v-model="personDataForm.zhengJianType"
                placeholder="请选择证件类型"
              >
                <el-option
                  v-for="item of zheng_jian_type"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">证件号码</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="idNum" :rules="idNumRules">
              <span v-if="isDisabled">
                {{ personDataForm.idNum }}
              </span>
              <el-input
                :disabled="disabledSpbg"
                v-else
                style="width: 288px"
                v-model="personDataForm.idNum"
                placeholder="请输入证件号码"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions
          title="毕业院校及学历专业"
          class=""
          :column="3"
          size="large"
          border
        >
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">学历学位</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="xueLi">
              <span v-if="isDisabled">
                <dict-tag
                  :options="xue_li"
                  :value="personDataForm.xueLi"
                ></dict-tag>
              </span>
              <el-select
                v-else
                style="width: 100%"
                v-model="personDataForm.xueLi"
                placeholder="请输入学历学位"
              >
                <el-option
                  v-for="item of xue_li"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">全日制教育</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="school">
              <span v-if="isDisabled">
                {{ personDataForm.school }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.school"
                placeholder="请输入全日制教育专业"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">全日制教育专业</div>
              <span class="required">*</span>
            </template>
            <div style="display: flex">
              <el-form-item prop="specialty">
                <span v-if="isDisabled">
                  {{ personDataForm.specialty }}
                </span>
                <el-input
                  style="width: 190px"
                  v-else
                  v-model="personDataForm.specialty"
                  placeholder="专业"
                ></el-input>
                <span v-if="isDisabled">
                  （ 海外： {{ personDataForm.isHaiwai == 1 ? "是" : "否" }}）
                </span>
                <el-checkbox
                  style="margin-left: 10px"
                  v-else
                  true-label="1"
                  false-label="0"
                  v-model="personDataForm.isHaiwai"
                  label="是否毕业"
                >
                  是否海外
                </el-checkbox>
              </el-form-item>
            </div>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">毕业证书编号</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="zhengShuNum">
              <span v-if="isDisabled">
                {{ personDataForm.zhengShuNum }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.zhengShuNum"
                placeholder="请输入毕业证书编号"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">在职教育</div>
            </template>
            <el-form-item>
              <span v-if="isDisabled">
                {{ personDataForm.zaiZhiJiaoYu || "无" }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.zaiZhiJiaoYu"
                placeholder="请输入在职教育学校"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">在职教育专业</div>
            </template>
            <div style="display: flex">
              <el-form-item>
                <span v-if="isDisabled">
                  {{ personDataForm.zaiZhiZhuanYe || "无" }}
                </span>
                <el-input
                  v-else
                  v-model="personDataForm.zaiZhiZhuanYe"
                  placeholder="请输入在职教育专业"
                ></el-input>
              </el-form-item>
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions
          title="联系方式"
          class="margin-top"
          :column="3"
          size="large"
          border
        >
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">固定电话</div>
            </template>
            <el-form-item>
              <span v-if="isDisabled">
                {{ personDataForm.phoneNum }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.phoneNum"
                placeholder="请输入固定电话号码"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">手机号码</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="shouJiNum">
              <span v-if="isDisabled">
                {{ personDataForm.shouJiNum }}
              </span>
              <el-input
                v-else
                v-model.number="personDataForm.shouJiNum"
                placeholder="请输入手机号码"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">电子邮箱</div>
            </template>
            <el-form-item>
              <span v-if="isDisabled">
                {{ personDataForm.eMail }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.eMail"
                placeholder="请输入电子邮箱"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions
          title="引进人才"
          class="margin-top"
          :column="3"
          size="large"
          border
          direction="horizontal"
        >
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">原工作单位职务、职称</div>
            </template>
            <el-form-item>
              <span v-if="isDisabled">
                {{ personDataForm.yjrcYuanDanWeiZhiWu || "无" }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.yjrcYuanDanWeiZhiWu"
                placeholder="请输入原工作单位职务、职称"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">引进类型</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yjrcType">
              <span v-if="isDisabled">
                <dict-tag
                  :options="yjrc_type"
                  :value="personDataForm.yjrcType"
                ></dict-tag>
              </span>
              <el-select
                v-else
                style="width: 100%"
                @change="handelChangeyjrcTypeRules"
                placeholder="请选择引进人才类型"
                v-model="personDataForm.yjrcType"
              >
                <el-option
                  v-for="item of yjrc_type"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item
            v-if="
              personDataForm.yjrcType != 'gxyj' &&
              personDataForm.yjrcType == 'rxyj'
            "
          >
            <template #label>
              <div class="cell-item">签约期限</div>
              <span v-if="personDataForm.yjrcType == 'rxyj'" class="required"
                >*{{
              }}</span>
            </template>
            <el-form-item prop="yjrcQianyueDate" :rules="qyRules">
              <span v-if="isDisabled">
                <dict-tag
                  :options="yjrc_qianyue_date"
                  :value="personDataForm.yjrcQianyueDate"
                ></dict-tag>
              </span>

              <el-select
                v-else
                style="width: 288px"
                v-model="personDataForm.yjrcQianyueDate"
                placeholder="请输入签约期限"
              >
                <el-option
                  v-for="item of yjrc_qianyue_date"
                  :key="item.id"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>

          <!-- <el-descriptions-item
            :span="1"
            v-if="
              personDataForm.yjrcType != 'gxyj' &&
              personDataForm.yjrcType == 'zzpy'
            "
          >
            <template #label>
              <div class="cell-item">单位职务职称</div>
              <span v-if="personDataForm.yjrcType == 'zzpy'" class="required"
                >*</span
              >
            </template>
            <el-form-item prop="zzpyZhiWu" :rules="zwRules">
              <span v-if="isDisabled">
                {{ personDataForm.zzpyZhiWu }}
              </span>
              <el-input
                v-else
                style="width: 288px"
                placeholder="请输入单位职务职称"
                v-model="personDataForm.zzpyZhiWu"
              ></el-input>
            </el-form-item>
          </el-descriptions-item> -->
        </el-descriptions>

        <el-descriptions
          title="用人单位情况"
          class="margin-top"
          :column="3"
          size="large"
          border
          direction="horizontal"
        >
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">单位名称</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwName">
              <span v-if="isDisabled">
                {{ personDataForm.yrdwName }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.yrdwName"
                placeholder="请输入用人单位名称"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">单位性质</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwXingZhi">
              <span v-if="isDisabled">
                <dict-tag
                  :options="rclk_dwxz"
                  :value="personDataForm.yrdwXingZhi"
                ></dict-tag>
              </span>
              <el-select
                v-else
                style="width: 100%"
                v-model="personDataForm.yrdwXingZhi"
                placeholder="请选择单位性质"
              >
                <el-option
                  v-for="item of rclk_dwxz"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">所属行业</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwIndustryInvolved">
              <span v-if="isDisabled">
                <dict-tag
                  :options="yrdw_industry_involved"
                  :value="personDataForm.yrdwIndustryInvolved"
                ></dict-tag>
              </span>
              <el-select
                v-else
                style="width: 100%"
                v-model="personDataForm.yrdwIndustryInvolved"
                placeholder="请输入所属行业"
              >
                <el-option
                  v-for="item of yrdw_industry_involved"
                  :key="item.id"
                  :value="item.value"
                  :label="item.label"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">法定代表人</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwFaRen">
              <span v-if="isDisabled">
                {{ personDataForm.yrdwFaRen }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.yrdwFaRen"
                placeholder="请输入单位法定代表人"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">联系人</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwLianxiRen">
              <span v-if="isDisabled">
                {{ personDataForm.yrdwLianxiRen }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.yrdwLianxiRen"
                placeholder="请输入单位联系人"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>

          <el-descriptions-item :span="0">
            <template #label>
              <div class="cell-item">联系电话</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwPhone">
              <span v-if="isDisabled">
                {{ personDataForm.yrdwPhone }}
              </span>
              <el-input
                v-else
                v-model="personDataForm.yrdwPhone"
                placeholder="请输入单位联系电话"
              ></el-input>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">地址</div>
              <span class="required">*</span>
            </template>
            <el-form-item prop="yrdwAddress">
              <div style="display: flex; align-items: center">
                <span style="display: inline-block" v-if="isDisabled">
                  <dict-tag
                    style="display: inline-block"
                    :options="rclk_dzqx"
                    :value="personDataForm.yrdwAddressArea"
                  ></dict-tag>
                </span>
                <span
                  style="display: inline-block; margin-left: 10px"
                  v-if="isDisabled"
                  >{{ personDataForm.yrdwAddress }}
                </span>
                <el-select
                  v-else
                  v-model="personDataForm.yrdwAddressArea"
                  placeholder="请选择区县"
                  style="width: 288px"
                >
                  <el-option
                    v-for="item in rclk_dzqx"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>

                <el-input
                  v-if="!isDisabled"
                  style="width: 70%; margin-left: 10px"
                  v-model="personDataForm.yrdwAddress"
                  placeholder="请输入用人单位地址"
                ></el-input>
              </div>
            </el-form-item>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions
          title="家庭主要成员及重要社会关系"
          size="large"
          border
          direction="horizontal"
        ></el-descriptions>

        <el-table
          style="margin-bottom: 20px"
          :data="personDataForm.rclkMemberFamilyList"
          border
        >
          <el-table-column type="index" align="center" label="序号" width="55">
          </el-table-column>
          <el-table-column prop="userName" align="center" label="姓名">
            <template #default="scope">
              <el-input
                :disabled="disabledSpbg"
                v-if="!isDisabled"
                placeholder="请输入姓名"
                v-model="scope.row.userName"
              ></el-input>
              <span v-else>{{ scope.row.userName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="gender" align="center" label="性别">
            <template #default="scope">
              <el-select
                :disabled="disabledSpbg"
                v-if="!isDisabled"
                style="width: 100%"
                placeholder="请选择性别"
                v-model="scope.row.gender"
              >
                <el-option label="男" value="0"></el-option>
                <el-option label="女" value="1"></el-option>
              </el-select>
              <span v-else>{{
                scope.row.gender
                  ? scope.row.gender == "0"
                    ? "男"
                    : "女"
                  : null
              }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="relation" align="center" label="关系">
            <template #default="scope">
              <el-input
                :disabled="disabledSpbg"
                v-if="!isDisabled"
                placeholder="请输入关系"
                v-model="scope.row.relation"
              ></el-input>
              <span v-else>{{ scope.row.relation }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="nation" align="center" label="民族">
            <template #default="scope">
              <el-select
                :disabled="disabledSpbg"
                v-if="!isDisabled"
                style="width: 100%"
                v-model="scope.row.nation"
                placeholder="请选择民族"
              >
                <el-option
                  v-for="item of mz_data"
                  :key="item.name"
                  :label="item.name"
                  :value="item.name"
                ></el-option>
              </el-select>
              <span v-else>{{ scope.row.nation }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="dateBirth"
            width="220"
            align="center"
            label="出生日期"
          >
            <template #default="scope">
              <el-date-picker
                :disabled="disabledSpbg"
                v-if="!isDisabled"
                v-model="scope.row.dateBirth"
                type="date"
                style="width: 100%"
                value-format="YYYY-MM-DD"
                placeholder="选择出生日期"
              ></el-date-picker>
              <span v-else>{{ scope.row.dateBirth }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="position" align="center" label="现在何处何职">
            <template #default="scope">
              <el-input
                :disabled="disabledSpbg"
                v-if="!isDisabled"
                placeholder="请输入现在何处何职"
                v-model="scope.row.position"
              ></el-input>
              <span v-else>{{ scope.row.position }}</span>
            </template>
          </el-table-column>
          <el-table-column
            v-if="!isDisabled && !disabledSpbg"
            prop="address"
            width="100"
            align="center"
            label="操作"
          >
            <template #default="scope">
              <el-button
                v-if="scope.$index == 0"
                type="primary"
                @click="handleEdit(scope.row)"
                >添加</el-button
              >
              <el-button
                v-if="scope.$index != 0"
                type="danger"
                @click="handleDelete(scope.row, scope.$index)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>

        <div style="display: flex; align-items: center; margin-top: 30px">
          <el-descriptions
            title="国内外学习工作经历"
            size="large"
            border
            direction="horizontal"
          ></el-descriptions>
          <el-tooltip
            class="box-item"
            raw-content
            effect="light"
            placement="top-end"
          >
            <template #content>
              <div class="content-box-tooltip">
                <div>
                  从参加工作时填起，大、中专院校学习毕业后参加工作的，从大、中专院校学习时填起，一段经历先填起止时间，后填学习所在院校系及专业或工作单位及职务。<br />
                </div>
                <div>
                  起止时间填写到年月（年份用4位数字表示，月份用2位数字表示），起止之间用“--”连接，格式显示为：“1991.01--2000.01”。<br />
                </div>
                <div>
                  起止时间输入完毕后输入两个空格，之后输入汉字部分。最近一段简历若无结束时间，则结束时间可用7个空格代替，与起止时间后的两空格共连续输入9个空格，格式显示为：“1991.01--
                  ”，之后输入汉字部分。<br />
                </div>
                <div>
                  各段经历时间要前后衔接，上一段经历的结束时间即为下一段经历的开始时间，不得空断，因病休学、休养、待分配等都要如实填写。<br />
                </div>
                <div>工作单位和职务一律使用规范的简称。<br /></div>
              </div>
            </template>
            <el-icon
              style="
                margin-left: 15px;
                font-size: 17px;
                cursor: pointer;
                margin-bottom: 20px;
              "
              ><QuestionFilled
            /></el-icon>
          </el-tooltip>
          <div
            v-if="!isDisabled"
            style="width: 500px; margin-top: -20px; margin-left: 10px"
          >
            <el-alert
              style="height: 30px"
              title="（请点击问号查看说明，请严格按照格式说明填写学习及工作经历）"
              type="error"
            />
          </div>
        </div>
        <editor
          v-if="!isDisabled"
          :readOnly="isDisabled"
          v-model="personDataForm.jingLi"
          :min-height="192"
        />
        <div v-else class="contentRichText-box">
          <div
            style="white-space: pre-wrap"
            v-html="personDataForm.jingLi"
          ></div>
        </div>

        <el-descriptions
          style="margin-top: 30px"
          title="成果和奖励情况"
          size="large"
          border
          direction="horizontal"
        ></el-descriptions>
        <editor
          v-if="!isDisabled"
          :readOnly="isDisabled"
          v-model="personDataForm.huojiangCondition"
          :min-height="192"
        />
        <div v-else class="contentRichText-box">
          <div
            style="white-space: pre-wrap"
            v-html="personDataForm.huojiangCondition"
          ></div>
        </div>
      </el-form>

      <div></div>
    </div>
    <div
      style="display: flex; justify-content: center; margin-left: 50px"
      class="button-group"
      v-if="!isDisabled"
    >
      <el-button
        v-if="
          route.query.auditState != 'clbz' && route.query.auditState != 'sqbg'
        "
        style="width: 100px; height: 40px; font-size: 16px"
        :loading="draftBoxLoading"
        @click="saveDraftBox"
        type="success"
        >保存草稿</el-button
      >
      <el-button
        style="width: 100px; height: 40px; font-size: 16px"
        :loading="personFormLoading"
        @click="onsubmit(personFormRef)"
        type="primary"
        >下一步</el-button
      >
    </div>
  </div>
</template>

<script setup name="AmatterClass">
import { getMessageContent } from "@/api/talentApplication/index";

import { QuestionFilled } from "@element-plus/icons-vue";
import { useRoute } from "vue-router";
import { getToken } from "@/utils/auth";
import { computed, getCurrentInstance, watch } from "vue";
import mz_data from "@/mzData";
import {
  getlistByCardType,
  getListByParentId,
  addCG,
  getMaterialformData,
  postDraftBox,
  getMaterialsData,
  submitChangeAuthorization,
  getChangeData,
} from "@/api/matterClass";
import { ElMessage } from "element-plus";

const { proxy } = getCurrentInstance();
const route = useRoute();
const baseImgUrl = import.meta.env.VITE_APP_BASE_API;
const disabledSpbg = ref(route.query.auditState == "sqbg");
//申请条件
const applyContent = ref({});
const {
  yjrc_type,
  politics_status,
  zheng_jian_type,
  xue_li,
  yrdw_industry_involved,
  yjrc_qianyue_date,
  rclk_dwxz,
  rclk_dzqx,
} = proxy.useDict(
  "yjrc_type",
  "politics_status",
  "zheng_jian_type",
  "xue_li",
  "yrdw_industry_involved",
  "yjrc_qianyue_date",
  "rclk_dwxz",
  "rclk_dzqx"
);
const personFormLoading = ref(false);
const personFormRef = ref(null);
const listByCardType = ref([]);
const talentTitle = ref([]);
const personDataForm = reactive({
  photoPath: null, //头像
  chineseName: null, //中文名称
  englishName: null, //英文名称
  politicsStatus: null, //政治面貌
  gender: null, //性别
  minZu: null, //民族
  chuShengDate: null, //出生日期
  jiGuan: null, //籍贯
  juZhuDi: null, //本市居住地
  xueLi: null, //学历学位
  school: null, //学校
  zaiZhiJiaoYu: null, //在职教育
  renCaiType: null,
  renCaiChengHao: null, //人才称号
  zhengJianType: null, //证件类型
  idNum: null, //证件号
  phoneNum: null, //固定电话
  shouJiNum: null, //手机号码
  eMail: null, //电子邮箱
  yjrcYuanDanWeiZhiWu: null, //原工作单位职务、职称
  yjrcXianDanWeiZhiWu: null, //现工作单位职务、职称
  yjrcType: null, //引进人才类型
  yjrcQianyueDate: null, //签约期限
  yrdwName: null, //用人单位名称
  yrdwAddress: null, //用人单位地址
  yrdwXingZhi: null, //yrdwXingZhi
  yrdwIndustryInvolved: null, //用人单位所属行业
  yrdwFaRen: null, //用人单位法人
  yrdwLianxiRen: null, //用人单位联系人
  yrdwPhone: null, //用人单位联系电话
  jingLi: null, //国外学习工作经历
  nationality: null, //国籍
  huojiangCondition: `<p style="
    font-size: 15px;
"><strong>获奖情况：</strong></p><p>	<span style="color: rgb(255, 0, 0);">
注：请在此输入获奖情况。（输入前请删除此行注释,黑色字体请勿删除）</span></p><p style="
    font-size: 15px;
"><strong>获荣誉称号情况：</strong></p><p>	<span style="color: rgb(255, 0, 0);">
注：请在此输入获荣誉称号情况。（输入前请删除此行注释,黑色字体请勿删除）</span></p><p style="
    font-size: 15px;
"><strong>发表论文、著作情况：</strong></p><p style="
    /* font-size: 15px; */
">	<span style="color: rgb(255, 0, 0);">
注：请在此输入发表论文、著作情况。（输入前请删除此行注释,黑色字体请勿删除）</span></p><p style="
    font-size: 15px;
"><strong>重要工作业绩和实际贡献：</strong></p><p>	<span style="color: rgb(255, 0, 0);">
注：请在此输入重要工作业绩和实际贡献。（输入前请删除此行注释,黑色字体请勿删除）</span></p>
  `,
  //工作奖励和成果
  zzpyZhiWu: null, //单位职务职称
  getDate: null, //取得日期
  zhengShuNum: null, //毕业证书编号
  juZhuDiQu: null,
  specialty: null,
  isHaiwai: null,
  zaiZhiZhuanYe: null,
  yrdwAddressArea: null,
  rclkMemberFamilyList: [
    {
      userName: null,
      gender: null,
      relation: null,
      nation: null,
      dateBirth: null,
      position: null,
      address: null,
    },
  ], //家庭成员
});

//家庭成员处理逻辑
const handleEdit = (row) => {
  personDataForm.rclkMemberFamilyList.push({});
};
const handleDelete = (row, index) => {
  personDataForm.rclkMemberFamilyList.splice(index, 1);
};

const props = defineProps({
  materialCalssId: {
    type: String,
    required: true,
  },
  isDisabled: {},
  isReviewed: {
    type: Boolean,
    default: false,
  },
  isSqbg: {
    type: String,
    default: "",
  },
});

const emits = defineEmits("update:materialCalssId");
const handleNextStep = inject("handleNextStep");

const personDataFormRules = {
  zhengJianType: [
    {
      required: true,
      message: "证件类型不能为空",
      trigger: "blur",
    },
  ],
  renCaiType: [
    {
      required: true,
      message: "人才类别不能为空",
      trigger: "blur",
    },
  ],
  getDate: [
    {
      required: true,
      message: "取得日期不能为空",
      trigger: "blur",
    },
  ],
  zhengShuNum: [
    {
      required: true,
      message: "毕业证书编号不能为空",
      trigger: "blur",
    },
  ],
  school: [
    {
      required: true,
      message: "全日制教育学校不能为空",
      trigger: "blur",
    },
  ],
  specialty: [
    {
      required: true,
      message: "全日制教育专业不能为空",
      trigger: "blur",
    },
  ],
  photoPath: [
    {
      required: true,
      message: "请上传证件照",
      trigger: "blur",
    },
  ],
  xueLi: [
    {
      required: true,
      message: "学历学位不能为空",
      trigger: "blur",
    },
  ],
  politicsStatus: [
    {
      required: true,
      message: "政治面貌不能为空",
      trigger: "blur",
    },
  ],
  minZu: [
    {
      required: true,
      message: "民族不能为空",
      trigger: "blur",
    },
  ],
  renCaiChengHao: [
    {
      required: true,
      message: "人才称号不能为空",
      trigger: "blur",
    },
  ],
  chineseName: [
    {
      required: true,
      message: "中文名不能为空",
      trigger: "blur",
    },
  ],
  yjrcXianDanWeiZhiWu: [
    {
      required: true,
      message: "现工作单位职务不能为空",
      trigger: "blur",
    },
  ],
  gender: [
    {
      required: true,
      message: "性别不能为空",
      trigger: "blur",
    },
  ],
  chuShengDate: [
    {
      required: true,
      message: "出生日期不能为空",
      trigger: "blur",
    },
  ],
  juZhuDi: [
    {
      required: true,
      message: "本市居住地不能为空",
      trigger: "blur",
    },
  ],
  idNum: [
    {
      required: true,
      message: "证件号码不能为空",
      trigger: "blur",
    },
  ],
  shouJiNum: [
    {
      required: true,
      message: "手机号码不能为空",
      trigger: "blur",
    },
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
  yjrcType: [
    {
      required: true,
      message: "人才类型不能为空",
      trigger: "blur",
    },
  ],
  yrdwName: [
    {
      required: true,
      message: "单位名称不能为空",
      trigger: "blur",
    },
  ],
  yrdwAddress: [
    {
      required: true,
      message: "地址不能为空",
      trigger: "blur",
    },
  ],
  eMail: [
    {
      pattern: /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/,
      message: "请输入正确的邮箱",
      trigger: "blur",
    },
  ],
  yrdwIndustryInvolved: [
    {
      required: true,
      message: "所属行业不能为空",
      trigger: "blur",
    },
  ],
  yrdwFaRen: [
    {
      required: true,
      message: "单位法定代表人不能为空",
      trigger: "blur",
    },
  ],
  yrdwLianxiRen: [
    {
      required: true,
      message: "单位联系人不能为空",
      trigger: "blur",
    },
  ],
  yrdwPhone: [
    {
      required: true,
      message: "单位联系电话不能为空",
      trigger: "blur",
    },
  ],
  yrdwXingZhi: [
    {
      required: true,
      message: "单位性质不能为空",
      trigger: "blur",
    },
  ],
};
const fileList = ref([]);
const handleUploadSuccess = (file) => {
  personDataForm.photoPath = file.fileNames;
};
const handleRemove = () => {
  personDataForm.photoPath = null;
};
const headers = ref({ Authorization: "Bearer " + getToken() });
const uploadFileUrl = ref(
  import.meta.env.VITE_APP_BASE_API + "/application/form/uploads"
); // 上传文件服务器地址

//保存草稿
const saveDraftBox = () => {
  if (!personDataForm.photoPath) {
    ElMessage({
      type: "error",
      message: "请上传证件照",
    });
    return;
  }
  if (!personDataForm.chineseName) {
    ElMessage({
      type: "error",
      message: "中文名不能为空",
    });
    return;
  }
  if (!personDataForm.idNum) {
    ElMessage({
      type: "error",
      message: "身份证号不能为空",
    });
    return;
  }
  if (checkFamilyMember()) {
    personDataForm.rclkMemberFamilyList = [];
  }
  postDraftBox({
    ...personDataForm,
    cardType: route.query.type,
    shenqingState: "cg",
    id: props.materialCalssId ?? null,
  }).then((res) => {
    ElMessage({
      type: "success",
      message: "保存成功",
    });
    emits("update:materialCalssId", res.data.id);
  });
};

//签约期限的动态校验
const qyRules = computed(() =>
  personDataForm.yjrcType == "rxyj"
    ? [
        {
          required: true,
          message: "签约类型不能为空",
          trigger: "change",
        },
      ]
    : []
);

//职务职称的动态校验
const zwRules = computed(() =>
  personDataForm.yjrcType == "zzpy"
    ? [
        {
          required: true,
          message: "单位职务职称不能为空",
          trigger: "blur",
        },
      ]
    : []
);

//证件类型的动态校验
const idNumRules = computed(() =>
  personDataForm.zhengJianType == "sfz"
    ? [
        {
          validator: (rule, value, callback) => {
            if (proxy.checkPsidno(value)) {
              callback();
            } else {
              callback(new Error("请填合法身份证号"));
            }
          },
          trigger: "blur",
        },
      ]
    : []
);

//获取人才类别
const handleGetlistByCardType = () => {
  getlistByCardType({
    cardType: "A",
    matterType: "F",
    status: "1",
  }).then((res) => {
    listByCardType.value = res.data;
  });
};
//查询类别称号
const handleChangeCardType = (id) => {
  personDataForm.renCaiChengHao = null;
};

//提交信息
const onsubmit = (formRef) => {
  formRef.validate((vaild) => {
    if (vaild) {
      if (checkFamilyMember()) {
        personDataForm.rclkMemberFamilyList = [];
      }
      personFormLoading.value = true;
      if (route.query.auditState == "sqbg") {
        saveChangeAuthorization();
      } else {
        saveApplication();
      }
    }
  });
};

//授权变更保存接口
const saveChangeAuthorization = () => {
  submitChangeAuthorization({
    ...personDataForm,
    cardType: route.query.type,

    id: props.materialCalssId ?? null,
  })
    .then((res) => {
      handleNextStep();
      ElMessage({
        type: "success",
        message: "保存成功",
      });
      emits("update:materialCalssId", res.data.id);
    })
    .finally((res) => {
      personFormLoading.value = false;
    });
};

//申请保存接口
const saveApplication = () => {
  addCG({
    ...personDataForm,
    cardType: route.query.type,
    shenqingState: route.query.auditState == "clbz" ? null : "cg",
    id: props.materialCalssId ?? null,
  })
    .then((res) => {
      handleNextStep();
      ElMessage({
        type: "success",
        message:
          route.query.auditState == "clbz" ? "保存成功" : "已保存至草稿箱中",
      });
      emits("update:materialCalssId", res.data.id);
    })
    .finally((res) => {
      personFormLoading.value = false;
    });
};

//如果id有的话就需要回显提交的材料
if (props.materialCalssId) {
  if (props.isReviewed || route.query.auditState == "sqbg") {
    if (props.isSqbg == "sqbg") {
      getChangeData({ id: props.materialCalssId }).then(({ data }) => {
        handleParseData(data);
      });
    } else {
      getMaterialsData(props.materialCalssId).then(({ data }) => {
        handleParseData(data);
      });
    }
  } else {
    //判断是否是授权修改后的内容

    getMaterialformData(props.materialCalssId).then(({ data }) => {
      handleParseData(data);
    });
  }
}

//回显传来的数据
const handleParseData = (data) => {
  Object.keys(personDataForm).forEach((item) => {
    if (item == "huojiangCondition" || item == "jingLi") {
      personDataForm[item] = proxy.escapeHTML(data[item]);
    } else {
      personDataForm[item] = data[item];
    }
  });
  if (
    personDataForm.rclkMemberFamilyList.length == 0 &&
    props.isDisabled == false
  ) {
    personDataForm.rclkMemberFamilyList = [
      {
        userName: null,
        gender: null,
        relation: null,
        nation: null,
        dateBirth: null,
        position: null,
        address: null,
      },
    ];
  }
};

//检测家庭成员是否存在内容
const checkFamilyMember = () => {
  const obj = [
    "userName",
    "gender",
    "relation",
    "nation",
    "dateBirth",
    "position",
  ];

  if (personDataForm.rclkMemberFamilyList.length == 1) {
    console.log(personDataForm.rclkMemberFamilyList[0]);
    const values = Object.values(personDataForm.rclkMemberFamilyList[0]).filter(
      (item) => item
    );
    const isEmpty = values.every((item) => item == null || item == "");
    return isEmpty;
  }
};

//获取申请条件内容
const handleGetMessageContent = () => {
  getMessageContent({ cardType: "A" }).then((res) => {
    applyContent.value = res.data[0];
  });
};

//监听头像地址有变化更新图像列表回显
watch(
  () => personDataForm.photoPath,
  (newVal) => {
    fileList.value = newVal
      ? [
          {
            url: baseImgUrl + newVal,
            name: "newVal",
          },
        ]
      : [];
  }
);

//监听人才类别
watch(
  () => personDataForm.renCaiType,
  (newValue) => {
    getListByParentId({ parentId: newValue, status: 1 }).then((res) => {
      talentTitle.value = res.data;
    });
  }
);

handleGetlistByCardType();
handleGetMessageContent();
</script>
<style lang="scss" scoped>
.app-container {
  height: 100%;
}
.contentRichText-box {
  border: 2px solid #ddd;
  padding: 10px;
  height: 400px;
  overflow: auto;
  border-radius: 3px;
}
.button-group {
  position: fixed;
  background: #fff;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 20px;
  border-radius: 10px;
  z-index: 999;
}
::v-deep .person-baseInfo .is-bordered-label {
  width: 160px;
}
::v-deep .photo-box .el-form-item__error {
  width: 100% !important;
  text-align: center !important;
}
::v-deep .el-upload-list__item {
  height: 174px;
}
::v-deep .el-upload--picture-card {
  height: 174px;
}
.cell-item {
  text-align: center;
}
.noRequried .required {
  display: none;
}
.content-box-tooltip {
  & > div {
    font-size: 15px;
    width: 700px;
    margin: 10px 0;
  }
}
.subheading {
  color: #303133;
  font-size: 16px;
  font-weight: 700;
  margin: 0px 0 15px 0;
}
::v-deep .el-descriptions__title,
.subheading {
  position: relative;
}
::v-deep .avater .el-descriptions__label {
  width: 135px;
}
::v-deep .el-descriptions__title::before,
.subheading::after {
  content: " ";
  display: inline-block;
  position: absolute;
  height: 100%;
  left: -9px;
  top: 50%;
  transform: translateY(-58%);
  background: #0e62fe;
  height: 16px;
  width: 3px;
}
.required {
  color: red;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 15%;
}
::v-deep .el-descriptions__label {
  position: relative;
}
.content {
  width: 95%;
  margin: 20px auto;
}
::v-deep .isUpload .el-upload--picture-card {
  display: none !important;
}
::v-deep .isDisabled .el-upload-list__item-actions {
  display: none !important;
}
::v-deep .isDisabled .el-upload-list__item-status-label {
  display: none !important;
}
::v-deep .el-form-item {
  margin-bottom: 0;
}
.margin-top {
  margin: 30px 0;
}
::v-deep .el-descriptions__label {
  width: 130px;
}
::v-deep .el-descriptions__content {
  width: 230px;
}
.avater-bg {
  width: 208px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  * {
    cursor: pointer;
  }
}
::v-deep .avater .el-descriptions__cell {
  width: 133px;
}
</style>
