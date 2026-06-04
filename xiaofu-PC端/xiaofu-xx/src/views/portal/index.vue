<template>
  <div class="portal-container">
    <div class="header-container" :class="{ 'no-banner': activeNavId !== 'home' }">
      <header>
        <div class="header-top">
          <div class="title">石家庄市校服管理平台</div>
          <div class="nav">
            <div
              class="nav-item"
              :class="{ active: activeNavId === 'home' }"
              @click="setActiveNav('home')"
            >
              首页
            </div>
            <div
              class="nav-item"
              v-for="item in navMenuList"
              :key="item.id"
              :class="{ active: activeNavId === item.bs }"
              @click="setActiveNav(item.bs)"
            >
              {{ item.name }}
            </div>

            <!-- <div
              class="nav-item"
              :class="{ active: activeNavId === 'XXXX' }"
              @click="setActiveNav('XXXX')"
            >
              学校信息
            </div> -->
            <!-- <div
              class="nav-item"
              :class="{ active: activeNavId === 'YJFK' }"
              @click="setActiveNav('YJFK')"
            >
              意见反馈
            </div> -->
          </div>
        </div>
        <div class="tab-bar">
          <span
            v-if="!isLogin"
            style="
              font-size: 14px;
              font-weight: bold;
              color: #fff;
              cursor: pointer;
              border: 1px solid #fff;
              padding: 5px 10px;
              border-radius: 20px;
            "
            @click="goLogin"
          >
            请登录
          </span>
          <span
            v-else
            style="
              font-size: 14px;
              font-weight: bold;
              color: #fff;
              cursor: pointer;
            "
            @click="handleIndex"
          >
            欢迎您，{{ userInfo.nickName }}
          </span>
        </div>
      </header>

      <div class="banner" ref="banner" v-if="activeNavId === 'home'">
        <div class="search-container">
          <div class="search-box">
            <!-- <img
              src="@/assets/images/deep.png"
              class="search-logo"
              alt="logo"
            /> -->

            <input
              @input="handleSearch"
              @focus="handleSearchFocus"
              @blur="handleSearchBlur"
              @keydown="handleSearchKeydown"
              ref="searchInput"
              class="search-input"
              type="text"
              placeholder="请输入要搜索的内容"
            />
            <button @click="performSearch" class="search-button">
              <i class="el-icon-search"></i>
              <span>搜索</span>
            </button>
          </div>
          <div v-if="showSearchResults" class="search-results">
            <div v-if="searchResults.length > 0">
              <div
                class="result-item"
                v-for="(item, index) in searchResults"
                :key="index"
                @click="handleResultClick(item)"
              >
                <span class="category-tag"
                  >[{{ getCategoryName(item.catagory) }}]</span
                >
                <span class="title">{{ item.title }}</span>
              </div>
            </div>
            <div v-else-if="$refs.searchInput && $refs.searchInput.value" class="no-results">
              <i class="el-icon-search"></i>
              <span>暂无搜索结果</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 吸顶导航条 -->
    <div class="sticky-nav" :class="{ 'sticky-active': isSticky }">
      <div class="sticky-nav-content">
        <div class="sticky-title">石家庄市校服管理平台</div>
        <div class="sticky-nav-menu">
          <div
            class="sticky-nav-item"
            :class="{ active: activeNavId === 'home' }"
            @click="setActiveNav('home')"
          >
            首页
          </div>
          <div
            class="sticky-nav-item"
            v-for="item in navMenuList"
            :key="item.id"
            :class="{ active: activeNavId === item.bs }"
            @click="setActiveNav(item.bs)"
          >
            {{ item.name }}
          </div>
          <!-- <div
            class="sticky-nav-item"
            :class="{ active: activeNavId === 'XXXX' }"
            @click="setActiveNav('XXXX')"
          >
            学校信息
          </div> -->
          <!-- <div
            class="sticky-nav-item"
            :class="{ active: activeNavId === 'YJFK' }"
            @click="setActiveNav('YJFK')"
          >
            意见反馈
          </div> -->
        </div>
        <div class="sticky-user-info">
          <span
            v-if="!isLogin"
            class="sticky-login-btn"
            @click="goLogin"
          >
            请登录
          </span>
          <span
            v-else
            class="sticky-user-name"
            @click="handleIndex"
          >
            欢迎您，{{ userInfo.nickName }}
          </span>
        </div>
      </div>
    </div>
    <main class="main-content" :class="{ 'no-banner': activeNavId !== 'home' }">
      <div v-if="activeNavId === 'home'">
        <!-- 公告信息区域 -->
        <div class="announcements-section">
          <div class="section-header">
            <h2 class="section-title">最新公告</h2>
            <div class="section-tabs">
              <span
                class="tab-item"
                :class="{ active: activeTab === 'purchase' }"
                @click="activeTab = 'purchase'"
              >
                采购公告
              </span>
              <!-- <span
                class="tab-item"
                :class="{ active: activeTab === 'qualified' }"
                @click="activeTab = 'qualified'"
              >
                备选公告
              </span> -->
              <span
                class="tab-item"
                :class="{ active: activeTab === 'CJGG' }"
                @click="activeTab = 'CJGG'"
              >
                采购结果公告
              </span>
            </div>
          </div>

          <div class="announcements-content">
            <div v-if="activeTab === 'purchase'" class="announcements-list">
              <div
                v-for="item in articleList.slice(0, 6)"
                :key="item.id"
                class="announcement-card"
                @click="handleAnnouncementDetails(item)"
                :class="{ 'empty-item': item.isEmpty }"
              >
                <div class="announcement-header">
                  <h3 class="announcement-title">{{item.isBc == 1? item.title +'（补充）':item.title}}</h3>
                </div>
                <div class="announcement-meta">
                  <span class="meta-item">地区：{{ item.areaname || '未知' }}</span>
                  <span class="meta-item">发布人：{{ item.createBy || '未知' }}</span>
                </div>
              </div>
            </div>
            <div v-if="activeTab === 'CJGG'" class="announcements-list">
              <div
                v-for="item in cjggList.slice(0, 6)"
                :key="item.id"
                class="announcement-card"
                @click="handleAnnouncementDetails(item)"
                :class="{ 'empty-item': item.isEmpty }"
              >
                <div class="announcement-header">
                  <h3 class="announcement-title">{{item.isBc == 1? item.title +'（补充）':item.title}}</h3>
                </div>
                <div class="announcement-meta">
                  <span class="meta-item">地区：{{ item.areaname || '未知' }}</span>
                  <span class="meta-item">发布人：{{ item.createBy || '未知' }}</span>
                </div>
              </div>
            <!-- <div v-if="activeTab === 'qualified'" class="announcements-list">
              <div
                v-for="item in qualifiedList.slice(0, 6)"
                :key="item.id"
                class="announcement-card"
                @click="handleAnnouncementDetails(item)"
                :class="{ 'empty-item': item.isEmpty }"
              >
                <div class="announcement-header">
                  <h3 class="announcement-title">{{ item.title }}</h3>
                </div>
                <div class="announcement-meta">
                  <span class="meta-item">地区：{{ item.areaname || '未知' }}</span>
                  <span class="meta-item">发布人：{{ item.createBy || '未知' }}</span>
                </div>
              </div> -->
            </div>
          </div>
        </div>

        <!-- 资讯与知识区域 -->
        <div class="info-knowledge-section">
          <div class="info-column">
            <div class="column-header" @click="setActiveNav('ZCFG')" style="cursor: pointer;">
              <div class="header-icon">
                <svg-icon icon-class="documentation" />
              </div>
              <div class="header-content">
                <h3>政策法规</h3>
                <p>最新政策文件发布</p>
              </div>
            </div>
            <div class="info-list">
              <div
                v-for="(item) in policyList.slice(0, 5)"
                :key="item.id"
                class="info-item"
                @click="showPolicyDetails(item)"
              >
                <div class="info-number"></div>
                <div class="info-content">
                  <h4 class="info-title">{{ item.title }}</h4>
                  <div class="info-meta">
                    <span class="info-date">{{ item.createTime ? item.createTime.split(" ")[0] : '' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="info-column">
            <div class="column-header" @click="setActiveNav('JYZX')" style="cursor: pointer;">
              <div class="header-icon">
                <svg-icon icon-class="education" />
              </div>
              <div class="header-content">
                <h3>教育资讯</h3>
                <p>教育行业最新动态</p>
              </div>
            </div>
            <div class="info-list">
              <div
                v-for="(item) in educationList.slice(0, 5)"
                :key="item.id"
                class="info-item"
                @click="showEducationDetails(item)"
              >
                <div class="info-number"></div>
                <div class="info-content">
                  <h4 class="info-title">{{ item.title }}</h4>
                  <div class="info-meta">
                    <span class="info-date">{{ item.createTime ? item.createTime.split(" ")[0] : '' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="info-column">
            <div class="column-header" @click="setActiveNav('ZSKT')" style="cursor: pointer;">
              <div class="header-icon">
                <svg-icon icon-class="star" />
              </div>
              <div class="header-content">
                <h3>知识课堂</h3>
                <p>校服知识科普</p>
              </div>
            </div>
            <div class="info-list" >
              <!-- <div
                v-for="(item) in knowledgeCards.slice(0, 5)"
                :key="item.id"
                class="info-item"
                @click="showKnowledgeDetails(item)"
              >
                <div class="info-number"></div>
                <div class="info-content">
                  <h4 class="info-title">{{ item.title }}</h4>
                  <div class="info-meta">
                    <span class="info-date">{{ item.createTime ? item.createTime.split(" ")[0] : '' }}</span>
                  </div>
                </div>
              </div> -->
              <div class="video-grid" style="display: flex;flex-direction: column;">
                <div class="video-item" @click="playVideo('http://127.0.0.1:9300/filestatics/voide/校服讲座.mp4')">
                  <div class="video-thumbnail">
                    <!-- <video
                      :src="'http://127.0.0.1:9300/filestatics/voide/校服讲座.mp4'"
                      @loadeddata="setVideoThumbnail"
                      preload="metadata"
                      muted
                    ></video> -->
                    <img src="@/assets/images/video_1.png" alt="基于标准的中小学生校服质量研究" style="width: 100%;height: 100%;" />
                    <div class="video-overlay">
                        <i class="el-icon-video-play" style="font-size: 40px;color: #fff;"></i>
                    </div>
                  </div>
                  <div class="video-content">
                    <h3 class="video-description">基于标准的中小学生校服质量研究</h3>
                  </div>
                </div>

                <div class="video-item" @click="playVideo('http://127.0.0.1:9300/filestatics/voide/质量把控.mp4')">
                  <div class="video-thumbnail">
                    <!-- <video
                      :src="'http://127.0.0.1:9300/filestatics/voide/质量把控.mp4'"
                      @loadeddata="setVideoThumbnail"
                      preload="metadata"
                      muted
                    ></video> -->
                    <img src="@/assets/images/video_2.png" alt="GB/T31888-2015标准解读与校服质量把控" style="width: 100%;height: 100%;" />
                    <div class="video-overlay">
                        <i class="el-icon-video-play" style="font-size: 40px;color: #fff;"></i>
                    </div>
                  </div>
                  <div class="video-content">
                    <h3 class="video-description">GB/T31888-2015标准解读与校服质量把控</h3>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 校园风采区域 -->
        <div class="campus-section">
          <div class="section-header">
            <div class="header-left" @click="setActiveNav('XYFC')" style="cursor: pointer;">
              <h2 class="section-title">校园风采</h2>
              <span class="section-subtitle">展示校服文化与校园风采</span>
            </div>
            <div class="header-right">

            </div>
          </div>
          <div class="campus-grid">
            <div class="campus-main">
              <div
                v-for="item in campusStyleList"
                :key="item.id"
                class="campus-main-item"
                @click="showCampusStyleDetails(item)"
              >
                <div class="campus-image-wrapper">
                  <img :src="baseUrl + item.fmImg" :alt="item.title" />
                  <div class="campus-overlay">
                    <div class="overlay-content">
                      <h3>{{ item.title }}</h3>
                      <p>点击查看详情</p>
                    </div>
                  </div>
                </div>
                <div class="campus-info">
                  <h4>{{ item.title }}</h4>
                  <div class="campus-meta">
                    <span class="campus-date">{{ item.createTime ? item.createTime.split(" ")[0] : '' }}</span>
                  </div>
                </div>
              </div>
            </div>
            <!-- 隐藏右侧边栏，只显示三个水平卡片 -->
          </div>
        </div>

        <!-- 学校信息区域 -->
        <div class="schools-section">
          <div class="section-header" @click="setActiveNav('XXXX')" style="cursor: pointer;">
            <h2 class="section-title" >学校信息</h2>
            <div class="section-more">
            </div>
          </div>
          <div class="schools-grid">
            <div
              v-for="school in schoolList.slice(0, 10)"
              :key="school.deptId"
              class="school-card"
              @click="showSchoolDetails(school)"
            >
              <div class="school-logo">
                <img
                  v-if="school.schoolImage"
                  :src="baseUrl + school.schoolImage"
                  :alt="school.deptName"
                />
                <img
                  v-else
                  src="@/assets/images/xuexiao.png"
                  :alt="school.deptName"
                />
              </div>
              <div class="school-info">
                <div style="font-size: 14px;font-weight: 500;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;" :title="school.deptName">{{ school.deptName }}</div>
                <!-- <p class="school-type">
                  {{ school.schoolType == 0 ? "高级中学" :
                     school.schoolType == 1 ? "初级中学" :
                     school.schoolType == 2 ? "小学" :
                     school.schoolType == 3 ? "幼儿园" : "其他" }}
                </p> -->
              </div>
            </div>
          </div>
        </div>
      </div>

      <shortlisted v-if="activeNavId === 'QYRWGG'" />
      <purchase v-if="activeNavId === 'CGGG'" />
      <cjgg v-if="activeNavId === 'CJGG'" />
      <policy v-if="activeNavId === 'ZCFG'" />
      <consulting v-if="activeNavId === 'JYZX'" />
      <classroom v-if="activeNavId === 'ZSKT'" />
      <schoolInformation v-if="activeNavId === 'XXXX'" />
      <opinion v-if="activeNavId === 'YJFK'" />
      <campusStyle v-if="activeNavId === 'XYFC'" />
      <supplierSettle v-if="activeNavId === 'GYSRZGG'" />
    </main>
    <footer class="footer">
      <div class="footer-main">
        <div class="footer-content-wrapper">
          <div class="link-section">
            <div class="link-column">
              <a href="http://localhost/" class="link-item" target="_blank">教育部</a>
              <a href="http://localhost/" class="link-item" target="_blank">河北省教育厅</a>
              <a href="http://localhost/" class="link-item" target="_blank">石家庄市教育局</a>
            </div>
            <div class="copyright-section">
              <div class="copyright-item">版权所有: 石家庄市教育局</div>
              <div class="copyright-item">技术支持: 石家庄开发区亿网科技发展有限公司</div>
              <div class="copyright-item">客服电话：0311-66568606</div>
            </div>
          </div>
                      <div class="right-section">
                          <div class="feedback-section">
              <a href="#" class="feedback-link" @click="showFeedbackDialog = true">
                <i class="el-icon-chat-dot-round"></i>
                <span>意见反馈</span>
              </a>
            </div>
              <div class="qr-section">
                <div class="qr-item">
                  <img src="@/assets/images/sjzbszn.png" style="height: 160px;width: 140px;border-radius: 10px;"  alt="校服平台家长端" />
                </div>
                <div class="qr-item" style="background-color: #fff;padding: 10px;border-radius: 10px;">
                  <img src="@/assets/images/sjzjyjgzh.jpg" alt="微信公众号" />
                  <p style="color: #000;margin: 0;width: 120px;">石家庄教育发布微信公众号</p>
                </div>
              </div>
            </div>
        </div>
      </div>
    </footer>
    <el-dialog
      :visible.sync="schoolDialogVisible"
      width="600px"
      title="学校详情"
      append-to-body
    >
      <div v-if="selectedSchool" class="school-details-content">
        <div class="school-logo-wrapper">
          <img
            v-if="selectedSchool.schoolImage"
            :src="baseUrl + selectedSchool.schoolImage"
            alt="logo"
            class="school-logo"
          />
          <img
            v-else
            src="@/assets/images/xuexiao.png"
            alt="logo"
            class="school-logo"
          />
        </div>
        <h3 class="school-dialog-name">{{ selectedSchool.deptName }}</h3>
        <div class="school-info-list">
          <p>
            <strong>学校类别：</strong>
            {{
              selectedSchool.schoolType == 0
                ? "高级中学"
                : selectedSchool.schoolType == 1
                ? "初级中学"
                : selectedSchool.schoolType == 2
                ? "小学"
                : selectedSchool.schoolType == 3
                ? "幼儿园"
                : "其他"
            }}
          </p>
          <p>
            <strong>学校区域：</strong>
            {{ selectedSchool.schoolArea == 1 ? "乡镇学校" : "城市学校" }}
          </p>
          <p>
            <strong>学校地区：</strong>
            {{
              getSchoolArea(
                selectedSchool.regionProvince,
                selectedSchool.regionCity,
                selectedSchool.regionCounty
              ) || "未知"
            }}
          </p>
          <p>
            <strong>详细地址：</strong>
            {{ selectedSchool.detailedAddress || "未提供" }}
          </p>
          <p>
            <strong>学校简介：</strong>
            {{ selectedSchool.remark || "未提供" }}
          </p>
        </div>
      </div>
    </el-dialog>
    <!-- 知识课堂详情对话框 -->
    <el-dialog
      :visible.sync="knowledgeDialogVisible"
      width="1200px"
      append-to-body
      custom-class="knowledge-dialog"
    >
      <div class="dialog-container" v-if="knowledgeDialogVisible">
        <div class="dialog-header">
          <h2 class="knowledge-dialog-title">{{ selectedKnowledge.title }}</h2>
          <div class="knowledge-dialog-meta">
            <span>发布时间：{{ selectedKnowledge.createTime }}</span>
            <span v-if="selectedKnowledge.endTime">截止时间：{{ selectedKnowledge.endTime }}</span>
            <span v-if="selectedKnowledge.createBy"
              >发布人：{{ selectedKnowledge.createBy }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div
            class="knowledge-dialog-content"
            v-html="selectedKnowledge.content"
          ></div>
          <div v-if="selectedKnowledge.file != '' && selectedKnowledge.file != null">
            <file-upload :value="selectedKnowledge.file" :disabled="true" />
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="knowledgeDialogVisible = false">关 闭</el-button> -->
      </div>
    </el-dialog>

    <!-- 采购公告 -->
    <el-dialog
      :visible.sync="announcementDialogVisible"
      width="1200px"
      append-to-body
      custom-class="announcement-dialog"
    >
      <div class="dialog-container" v-if="announcementDialogVisible">
        <div class="dialog-header">
          <h2 class="policy-dialog-title">{{ selectedAnnouncement.isBc == 1? selectedAnnouncement.title +'（补充）':selectedAnnouncement.title }}</h2>
          <div class="policy-dialog-meta">
            <!-- <span>发布时间：{{ selectedAnnouncement.createTime }}</span> -->
            <span v-if="selectedAnnouncement.endTime"
              >截止时间：{{ selectedAnnouncement.endTime }}</span
            >
            <span v-if="selectedAnnouncement.createBy"
              >发布人：{{ selectedAnnouncement.createBy }}</span
            >
            <span v-if="selectedAnnouncement.readCount !== undefined"
              >阅读量：{{ selectedAnnouncement.readCount }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <!-- <div
            class="policy-dialog-content"
            v-html="selectedAnnouncement.content"
          ></div> -->
          <iframe
            v-if="previewUrl"
            :src="previewUrl"
            width="100%"
            height="100%"
            frameborder="0"
            style="border: none"
          ></iframe>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="announcementDialogVisible = false">关 闭</el-button> -->
      </div>
    </el-dialog>

    <!-- 政策法规详情对话框 -->
    <el-dialog
      :visible.sync="policyDialogVisible"
      width="1200px"
      append-to-body
      custom-class="policy-dialog"
    >
      <div class="dialog-container" v-if="policyDialogVisible">
        <div class="dialog-header">
          <h2 class="policy-dialog-title">{{ selectedPolicy.title }}</h2>
          <div class="policy-dialog-meta">
            <span>发布时间：{{ selectedPolicy.createTime }}</span>
            <span v-if="selectedPolicy.endTime">截止时间：{{ selectedPolicy.endTime }}</span>
            <span v-if="selectedPolicy.createBy"
              >发布人：{{ selectedPolicy.createBy }}</span
            >
            <span v-if="selectedPolicy.readCount !== undefined"
              >阅读量：{{ selectedPolicy.readCount }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div
            class="policy-dialog-content"
            v-html="selectedPolicy.content"
          ></div>
          <div v-if="selectedPolicy.file != '' && selectedPolicy.file != null">
            <file-upload :value="selectedPolicy.file" :disabled="true" />
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="policyDialogVisible = false">关 闭</el-button> -->
      </div>
    </el-dialog>

    <!-- 教育咨询详情对话框 -->
    <el-dialog
      :visible.sync="educationDialogVisible"
      width="1200px"
      append-to-body
      custom-class="education-dialog"
    >
      <div class="dialog-container" v-if="educationDialogVisible">
        <div class="dialog-header">
          <h2 class="education-dialog-title">{{ selectedEducation.title }}</h2>
          <div class="education-dialog-meta">
            <span>发布时间：{{ selectedEducation.createTime }}</span>
            <span v-if="selectedEducation.endTime">截止时间：{{ selectedEducation.endTime }}</span>
            <span v-if="selectedEducation.createBy"
              >发布人：{{ selectedEducation.createBy }}</span
            >
            <span v-if="selectedEducation.readCount !== undefined"
              >阅读量：{{ selectedEducation.readCount }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div
            class="education-dialog-content"
            v-html="selectedEducation.content"
          ></div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="educationDialogVisible = false">关 闭</el-button> -->
      </div>
    </el-dialog>

    <!-- 校园风采详情对话框 -->
    <el-dialog
      :visible.sync="campusStyleDialogVisible"
      width="1200px"
      append-to-body
      custom-class="campus-style-dialog"
    >
      <div class="dialog-container" v-if="campusStyleDialogVisible">
        <div class="dialog-header">
          <h2 class="campus-style-dialog-title">
            {{ selectedCampusStyle.title }}
          </h2>
          <div class="campus-style-dialog-meta">
            <span>发布时间：{{ selectedCampusStyle.createTime }}</span>
            <span v-if="selectedCampusStyle.endTime">截止时间：{{ selectedCampusStyle.endTime }}</span>
            <span v-if="selectedCampusStyle.createBy"
              >发布人：{{ selectedCampusStyle.createBy }}</span
            >
            <span v-if="selectedCampusStyle.readCount !== undefined"
              >阅读量：{{ selectedCampusStyle.readCount }}</span
            >
          </div>
        </div>
        <div class="dialog-divider"></div>
        <div class="dialog-body">
          <div
            class="campus-style-dialog-content"
            v-html="selectedCampusStyle.content"
          ></div>
          <div v-if="selectedCampusStyle.file != '' && selectedCampusStyle.file != null">
          <file-upload :value="selectedCampusStyle.file" :disabled="true" />
        </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button @click="campusStyleDialogVisible = false">关 闭</el-button> -->
      </div>
    </el-dialog>

    <!-- 意见反馈弹框 -->
    <el-dialog
      title="意见反馈"
      :visible.sync="showFeedbackDialog"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="feedbackForm"
        :rules="feedbackRules"
        ref="feedbackForm"
        label-width="100px"
        class="feedback-form"
      >
        <el-form-item label="区县" prop="region">
          <el-select
            style="width: 100%"
            v-model="feedbackForm.region"
            placeholder="请选择区县"
          >
            <el-option
              v-for="item of shijiazhuangDistricts"
              :value="item.name"
              :label="item.name"
              :key="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="姓名" prop="userName">
          <el-input
            placeholder="请输入姓名"
            v-model="feedbackForm.userName"
          ></el-input>
        </el-form-item>

        <el-form-item label="联系方式" prop="lxfs">
          <el-input
            v-model="feedbackForm.lxfs"
            placeholder="请输入您的手机号"
          ></el-input>
        </el-form-item>

        <el-form-item label="反馈内容" prop="feedbackContent">
          <el-input
            type="textarea"
            v-model="feedbackForm.feedbackContent"
            placeholder="请输入反馈内容"
            :rows="6"
            show-word-limit
            maxlength="500"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFeedback" :loading="submitting">
          提 交
        </el-button>
        <el-button @click="closeFeedbackDialog">关 闭</el-button>

      </span>
    </el-dialog>

    <!-- 回到顶部按钮 -->
    <div
      class="back-to-top"
      :class="{ 'show': showBackToTop }"
      @click="scrollToTop"
      v-show="showBackToTop"
    >
      <i class="el-icon-top" style="font-size: 20px; color: #fff;"></i>
    </div>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

import purchase from "./_component/purchase.vue";
import policy from "./_component/policy.vue";
import consulting from "./_component/consulting.vue";
import classroom from "./_component/classroom.vue";
import schoolInformation from "./_component/schoolInformation.vue";
import campusStyle from "./_component/campusStyle.vue";
import opinion from "./_component/opinion.vue";
import shortlisted from "./_component/shortlisted.vue";
import supplierSettle from "./_component/supplierSettle.vue";
import cjgg from "./_component/cjgg.vue";
import {
  getNoticeColumnList,
  getArticleList,
  getSchoolList,
  searchContent,
  feedback,
  getArticleDetail,
  addReadCount,
} from "@/api/publicIndex";
import { shijiazhuangDistricts } from "@/utils/region";

export default {
  components: {
    purchase,
    policy,
    consulting,
    classroom,
    schoolInformation,
    opinion,
    campusStyle,
    shortlisted,
    cjgg,
    supplierSettle,
  },
  name: "Portal",
  data() {
    return {
      cjggList: [],
      leftCjggList: [],
      rightCjggList: [],
      previewUrl: "",
      campusStyleDialogVisible: false,
      selectedCampusStyle: {},
      baseUrl: process.env.VUE_APP_IMAGE_BASE_API,
      isLogin: false,
      userInfo: {},
      navMenuList: [],
      activeNavId: "home", // 默认选中首页
      title: "门户首页",
      description: "这是一个Vue2组件骨架示例",
      loading: false,
      articleList: [],
      leftArticleList: [],
      rightArticleList: [],
      qualifiedList: [],
      leftQualifiedList: [],
      rightQualifiedList: [],
      policyList: [],
      educationList: [],
      knowledgeCards: [],
      schoolList: [],
      schoolDialogVisible: false,
      selectedSchool: null,
      campusStyleList: [], // For left side big images
      sideArticleList: [], // For right side small images with text
      knowledgeDialogVisible: false,
      selectedKnowledge: null,
      announcementDialogVisible: false,
      selectedAnnouncement: null,
      policyDialogVisible: false,
      selectedPolicy: null,
      educationDialogVisible: false,
      selectedEducation: null,
      searchResults: [],
      searchTimeout: null,
      showSearchResults: false,
      activeTab: 'purchase',
      isSticky: false,
      showBackToTop: false,
      // 意见反馈相关
      shijiazhuangDistricts: shijiazhuangDistricts,
      showFeedbackDialog: false,
      submitting: false,
      feedbackForm: {
        userName: "",
        lxfs: "",
        feedbackContent: "",
        region: "",
      },
      feedbackRules: {
        userName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
        lxfs: [{ required: true, message: "请输入联系方式", trigger: "blur" },{pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }],
        feedbackContent: [
          { required: true, message: "请输入反馈内容", trigger: "blur" },
        ],
        region: [{ required: true, message: "请选择区县", trigger: "blur" }],
      },
    };
  },
  computed: {
    // 计算属性
    getCategoryName() {
      const categoryMap = {
        CGGG: "采购公告",
        ZCFG: "政策法规",
        JYZX: "教育资讯",
        XXXX: "学校信息",
        YJFK: "意见反馈",
        XYZS: "校园展示",
        XYFC: "校园风采",
        ZSKT: "知识课堂",
        QYRWGG:'备选公告',
        CJGG:'成交公告',
      };

      return (category) => {
        return categoryMap[category];
      };
    },
    formattedSearchResults() {
      if (!this.searchResults || this.searchResults.length === 0) {
        return [];
      }

      return this.searchResults.map((item) => {
        return {
          ...item,
          catagory: this.getCategoryName(item.catagory),
        };
      });
    },
  },
  watch: {
    // 监听属性
    activeNavId(newVal) {
      // 当导航切换时，重新计算滚动状态
      this.$nextTick(() => {
        this.handleScroll();
      });
    }
  },
  created() {
    this.getNoticeColumnList();
    this.getArticleList();
    this.getCjggList();
    this.getQualifiedList();
    this.getPolicyAndEducationData();
    this.getKnowledgeCards();
    this.getSchoolList();
    this.getCampusStyleData();
    if (getToken()) {
      this.isLogin = true;
      this.$store.dispatch("GetInfo").then((res) => {
        this.$store.dispatch("GenerateRoutes").then((accessRoutes) => {
          this.$router.addRoutes(accessRoutes);
        });
        this.userInfo = this.$store.state.user;
      });
    }
    // 组件创建时的钩子
  },
  handleResize() {
    const screenHeight = window.innerHeight;
    const screenWidth = window.innerWidth;

    // 这里可以改成你想要的相对位置
    const top = screenHeight * 0.4; // 例如 70% 视口高度
    const right = screenWidth * 0.2; // 20% 视口宽度
    this.$refs.banner.style.top = top + "px";
    this.$refs.banner.style.right = right + "px";
  },
  mounted() {
    window.addEventListener("resize", this.handleResize);
    // 添加滚动监听
    window.addEventListener("scroll", this.handleScroll);
    // 组件挂载时的钩子
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleResize);
    // 移除滚动监听
    window.removeEventListener("scroll", this.handleScroll);
  },
  methods: {
    handleScroll() {
      // 获取滚动距离
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
      // 根据当前页面状态设置吸顶阈值
      const threshold = this.activeNavId === 'home' ? 600 : 120;
      this.isSticky = scrollTop > threshold;

      // 控制回到顶部按钮的显示
      this.showBackToTop = scrollTop > 300;
    },
    handleResultClick(item) {
      switch (item.catagory) {
        case "QYRWGG":
          this.handleAnnouncementDetails(item);
          break;
        case "CGGG":
          this.handleAnnouncementDetails(item);
          break;
        case "ZCFG":
          this.showPolicyDetails(item);
          break;
        case "JYZX":
          this.showEducationDetails(item);
          break;
        case "ZSKT":
          this.showKnowledgeDetails(item);
          break;
        case "XYFC":
          this.showCampusStyleDetails(item);
          break;
        case "XXXX":
          this.showSchoolDetails(item);
          break;
        default:
          console.warn("未知的分类:", item.catagory);
          return;
      }
      this.searchResults = [];
      this.showSearchResults = false;
      this.$refs.searchInput.value = "";
    },
    handleSearch() {
      clearTimeout(this.searchTimeout);
      this.searchTimeout = setTimeout(() => {
        this.performSearch();
      }, 1000);
    },
    handleSearchFocus() {
      // 当搜索框获得焦点且有搜索结果时，显示结果列表
      if (this.searchResults.length > 0 || (this.$refs.searchInput && this.$refs.searchInput.value)) {
        this.showSearchResults = true;
      }
    },
    handleSearchBlur() {
      // 当搜索框失去焦点时，延迟隐藏结果列表，给用户点击结果的时间
      setTimeout(() => {
        this.showSearchResults = false;
      }, 200);
    },
    handleSearchKeydown(event) {
      // 当用户按ESC键时隐藏搜索结果
      if (event.key === 'Escape') {
        this.showSearchResults = false;
        this.$refs.searchInput.blur();
      }
    },
    performSearch() {
      const searchTerm = this.$refs.searchInput.value;
      if (!searchTerm) {
        this.searchResults = [];
        this.showSearchResults = false;
        return;
      }
      searchContent({
        name: searchTerm,
      }).then((res) => {
        this.searchResults = res.data;
        console.log(this.searchResults, "搜索结果");
        // 如果搜索框有焦点且有搜索结果，显示结果列表
        if (this.$refs.searchInput === document.activeElement && this.searchResults.length > 0) {
          this.showSearchResults = true;
        }
      });
    },
    async showCampusStyleDetails(item) {
      try {
        // 先调用增加阅读数量接口
        await addReadCount(item.id);

        // 然后获取详情
        const res = await getArticleDetail(item.id);
        const detailData = res.data;

        // 设置详情数据，包含阅读数量
        this.selectedCampusStyle = {
          ...detailData,
          readCount: detailData.readCount || 0
        };
        this.campusStyleDialogVisible = true;
      } catch (error) {
        console.error('获取校园风采详情失败:', error);
        // 如果接口调用失败，使用原有逻辑
        this.selectedCampusStyle = item;
        this.campusStyleDialogVisible = true;
      }
    },
    getSchoolArea(province, city, county) {
      let area = province == null ? "" : province;
      let area2 = city == null ? "" : city;
      let area3 = county == null ? "" : county;
      return area + area2 + area3;
    },
    //阅览查看
    async handleAnnouncementDetails(item) {
      if (item.isEmpty) return;

      try {
        // 先调用增加阅读数量接口
        await addReadCount(item.id);

        // 然后获取详情
        const res = await getArticleDetail(item.id);
        const detailData = res.data;

        // 设置预览URL
        if (detailData.ggFile) {
          this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + detailData.ggFile;
        } else if (detailData.file) {
          this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + detailData.file;
        }

        // 设置详情数据，包含阅读数量
        this.selectedAnnouncement = {
          ...detailData,
          readCount: detailData.readCount || 0
        };
        this.announcementDialogVisible = true;
      } catch (error) {
        console.error('获取公告详情失败:', error);
        // 如果接口调用失败，使用原有逻辑
        if (item.ggFile) {
          this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + item.ggFile;
        } else if (item.file) {
          this.previewUrl = process.env.VUE_APP_IMAGE_BASE_API + item.file;
        }
        this.selectedAnnouncement = item;
        this.announcementDialogVisible = true;
      }
    },
    showKnowledgeDetails(card) {
      this.selectedKnowledge = card;
      this.knowledgeDialogVisible = true;
    },
    async showPolicyDetails(item) {
      try {
        // 先调用增加阅读数量接口
        await addReadCount(item.id);

        // 然后获取详情
        const res = await getArticleDetail(item.id);
        const detailData = res.data;

        // 设置详情数据，包含阅读数量
        this.selectedPolicy = {
          ...detailData,
          readCount: detailData.readCount || 0
        };
        this.policyDialogVisible = true;
      } catch (error) {
        console.error('获取政策详情失败:', error);
        // 如果接口调用失败，使用原有逻辑
        this.selectedPolicy = item;
        this.policyDialogVisible = true;
      }
    },
    async showEducationDetails(item) {
      try {
        // 先调用增加阅读数量接口
        await addReadCount(item.id);

        // 然后获取详情
        const res = await getArticleDetail(item.id);
        const detailData = res.data;

        // 设置详情数据，包含阅读数量
        this.selectedEducation = {
          ...detailData,
          readCount: detailData.readCount || 0
        };
        this.educationDialogVisible = true;
      } catch (error) {
        console.error('获取教育资讯详情失败:', error);
        // 如果接口调用失败，使用原有逻辑
        this.selectedEducation = item;
        this.educationDialogVisible = true;
      }
    },
    getCampusStyleData() {
      getArticleList({
        bs: "XYFC",
      }).then((res) => {
        this.campusStyleList = res.rows.slice(0,4);
      });
    },
    showSchoolDetails(school) {
      this.selectedSchool = school;

      this.schoolDialogVisible = true;
    },
    handleIndex() {
      if (this.userInfo.roles && this.userInfo.roles.includes("xuexiao")) {
        this.$router.push({
          path: "/schoolIndex",
        });
      } else {
        this.$router.push({
          path: "/index",
        });
      }
    },
    goLogin() {
      this.$router.push("/login");
    },
    //
    //获取栏目列表
    async getNoticeColumnList() {
      const res = await getNoticeColumnList();
      this.navMenuList = res.data;
    },
    // 获取成交公告列表
    async getCjggList() {
      try {
        const res = await getArticleList({
          bs: "CGGG", // 采购公告栏目标识
          type: 3,
          pageNum: 1,
          pageSize: 6, // 获取6条数据，左右各显示3条
        });
        this.cjggList = res.rows || [];

        // 确保至少有6条数据用于显示，如果不足则填充空数据
        const minItems = 6;
        while (this.cjggList.length < minItems) {
          this.cjggList.push({
            id: `empty-${this.cjggList.length}`,
            title: "暂无更多公告",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          });
        }

        // 将文章列表分为左右两列，每列最多3条
        this.leftCjggList = this.cjggList
          .filter((_, index) => index % 2 === 0)
          .slice(0, 3);
        this.rightCjggList = this.cjggList
          .filter((_, index) => index % 2 === 1)
          .slice(0, 3);
      } catch (error) {
        console.error("获取文章列表失败:", error);
        // 如果API调用失败，创建默认的空数据
        this.leftCjggList = Array(3)
          .fill()
          .map((_, index) => ({
            id: `default-left-${index}`,
            title: "暂无公告数据",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          }));
        this.rightCjggList = Array(3)
          .fill()
          .map((_, index) => ({
            id: `default-right-${index}`,
            title: "暂无公告数据",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          }));
      }
    },
    // 获取文章列表
    async getArticleList() {
      try {
        const res = await getArticleList({
          bs: "CGGG", // 采购公告栏目标识
          type: 0,
          pageNum: 1,
          pageSize: 6, // 获取6条数据，左右各显示3条
        });
        this.articleList = res.rows || [];

        // 确保至少有6条数据用于显示，如果不足则填充空数据
        const minItems = 6;
        while (this.articleList.length < minItems) {
          this.articleList.push({
            id: `empty-${this.articleList.length}`,
            title: "暂无更多公告",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          });
        }

        // 将文章列表分为左右两列，每列最多3条
        this.leftArticleList = this.articleList
          .filter((_, index) => index % 2 === 0)
          .slice(0, 3);
        this.rightArticleList = this.articleList
          .filter((_, index) => index % 2 === 1)
          .slice(0, 3);
      } catch (error) {
        console.error("获取文章列表失败:", error);
        // 如果API调用失败，创建默认的空数据
        this.leftArticleList = Array(3)
          .fill()
          .map((_, index) => ({
            id: `default-left-${index}`,
            title: "暂无公告数据",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          }));
        this.rightArticleList = Array(3)
          .fill()
          .map((_, index) => ({
            id: `default-right-${index}`,
            title: "暂无公告数据",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          }));
      }
    },
    // 获取备选公告列表
    async getQualifiedList() {
      try {
        const res = await getArticleList({
          bs: "QYRWGG", // 备选公告栏目标识
          type: 0,
          pageNum: 1,
          pageSize: 6, // 获取6条数据，左右各显示3条
        });
        this.qualifiedList = res.rows || [];

        // 确保至少有6条数据用于显示，如果不足则填充空数据
        const minItems = 6;
        while (this.qualifiedList.length < minItems) {
          this.qualifiedList.push({
            id: `empty-qualified-${this.qualifiedList.length}`,
            title: "暂无更多备选公告",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          });
        }

        // 将备选公告列表分为左右两列，每列最多3条
        this.leftQualifiedList = this.qualifiedList
          .filter((_, index) => index % 2 === 0)
          .slice(0, 3);
        this.rightQualifiedList = this.qualifiedList
          .filter((_, index) => index % 2 === 1)
          .slice(0, 3);
      } catch (error) {
        console.error("获取备选公告列表失败:", error);
        // 如果API调用失败，创建默认的空数据
        this.leftQualifiedList = Array(3)
          .fill()
          .map((_, index) => ({
            id: `default-qualified-left-${index}`,
            title: "暂无备选公告数据",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          }));
        this.rightQualifiedList = Array(3)
          .fill()
          .map((_, index) => ({
            id: `default-qualified-right-${index}`,
            title: "暂无备选公告数据",
            publishDate: "",
            district: "",
            school: "",
            isEmpty: true,
          }));
      }
    },
    getPolicyAndEducationData() {
      getArticleList({
        bs: "ZCFG",
      }).then((res) => {
        this.policyList = res.rows;
      });

      getArticleList({
        bs: "JYZX",
      }).then((res) => {
        this.educationList = res.rows;
      });
    },
    getKnowledgeCards() {
      getArticleList({
        bs: "ZSKT",
      }).then((res) => {
        this.knowledgeCards = res.rows.slice(0, 3);
      });
    },
    async getSchoolList() {
      const res = await getSchoolList();
      this.schoolList = res.rows;
    },
    // 设置选中的导航项
    setActiveNav(navId) {
      this.activeNavId = navId;

      // 清空搜索结果
      this.searchResults = [];
      this.showSearchResults = false;

      // 清空搜索输入框
      if (this.$refs.searchInput) {
        this.$refs.searchInput.value = "";
      }
    },

    // 回到顶部
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    },

    // 意见反馈相关方法
    // 提交意见反馈
    submitFeedback() {
      this.$refs.feedbackForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            await feedback(this.feedbackForm);
            this.$message.success("反馈提交成功！");
            this.closeFeedbackDialog();
          } catch (error) {
            console.error(error);
          } finally {
            this.submitting = false;
          }
        } else {

          return false;
        }
      });
    },

    // 关闭提交弹框
    closeFeedbackDialog() {
      this.showFeedbackDialog = false;
      this.resetFeedbackForm();
    },

    // 重置表单
    resetFeedbackForm() {
      this.$refs.feedbackForm && this.$refs.feedbackForm.resetFields();
      this.feedbackForm = {
        userName: "",
        lxfs: "",
        feedbackContent: "",
        region: "",
      };
    },

    // 播放视频
    playVideo(videoUrl) {
      // 创建视频播放弹窗
      // this.$confirm('', '视频播放', {
      //   confirmButtonText: '确定',
      //   cancelButtonText: '取消',
      //   customClass: 'video-dialog',
      //   dangerouslyUseHTMLString: true,
      //   message: `
      //     <div class="video-player-container" style="width: 800px; height: 400px;">
      //       <video controls autoplay style="width: 100%; width: 800px; height: auto;">
      //         <source src="${videoUrl}" type="video/mp4">
      //         您的浏览器不支持视频播放。
      //       </video>
      //     </div>
      //   `
      // }).then(() => {
      //   // 用户点击确定
      // }).catch(() => {
      //   // 用户点击取消
      // });
      window.open(videoUrl, '_blank');
    },

    // 设置视频缩略图
    setVideoThumbnail(event) {
      const video = event.target;
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

      // 将canvas转换为背景图片
      const thumbnailUrl = canvas.toDataURL('image/jpeg');
      video.style.backgroundImage = `url(${thumbnailUrl})`;
      video.style.backgroundSize = 'cover';
      video.style.backgroundPosition = 'center';
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-message-box{
  width: 800px !important;
}
header {
  justify-content: center;
  display: flex;
  align-items: center;
}
.erji-title {
  cursor: pointer;
}
.tab-bar {
  padding-top: 20px;
  display: flex;
  gap: 20px;
  justify-content: flex-end;
}
.portal-container {
  background: rgb(250, 250, 250);
  overflow-x: hidden;
  overflow-y: auto;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header-container {
  // background-image: url("../../assets/images/bgIndex.png");
  background-image: url("../../assets/images/bg1.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  height: 600px;
  width: 100%;
  max-width: 100vw;
  overflow: hidden;

  &.no-banner {
    height: 80px;
    background-image: none;
    background-color: #337dff;
  }
}
.header-top {
  max-width: 1300px;
  width: 100%;
  // margin: 0 auto;
  display: flex;
  padding-top: 20px;

  .title {
    letter-spacing: 3px;
    font-style: italic;
    color: #fff;
    font-weight: bold;
    font-size: 33px;
    font-family: "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial,
      sans-serif;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }
  .nav {
    width: 400px;
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    .nav-item {
      color: #fff;
      padding: 8px 16px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-size: 18px;

      &:hover {
        background-color: rgba(29, 165, 255, 0.8);
      }

      &.active {
        background-color: rgb(29, 165, 255);
        font-weight: bold;
      }
    }
  }
}

.main-content {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 20px;
  background: #f8fafc;
  flex: 1;
  overflow-x: hidden;

  &.no-banner {
    padding-top: 20px;
  }
}

/* 快速入口区域 */
.quick-access-section {
  margin-bottom: 50px;
}

.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.quick-access-item {
  background: white;
  border-radius: 16px;
  padding: 30px 25px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
    border-color: #3b82f6;
  }

  .quick-access-icon {
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #3b82f6, #1d4ed8);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;

    .svg-icon {
      font-size: 28px;
      color: white;
    }
  }

  .quick-access-content {
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 8px 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    p {
      font-size: 14px;
      color: #64748b;
      margin: 0;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

/* 公告信息区域 */
.announcements-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
}

.section-header {
  display: flex;
  //justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;

  .section-title {
    font-size: 24px;
    font-weight: 600;
    color: #1e293b;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .section-subtitle {
    font-size: 14px;
    color: #64748b;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .section-tabs {
    display: flex;
    gap: 30px;
    margin-left: 40px;
    .tab-item {
      padding: 8px 0;
      cursor: pointer;
      font-size: 18px;
      font-weight: 500;
      transition: all 0.3s ease;
      color: #666;
      opacity: 0.6;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      position: relative;
      border: none;
      background: none;

      &:hover {
        color: #3b82f6;
      }

      &.active {
        font-size: 18px;
        color: #3b82f6;
        background: none;
        border: none;
        font-weight: bold;
        opacity: 1;
        border-bottom: 2px solid #3b82f6;
        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          right: 0;
          height: 2px;
          background-color: #3b82f6;
        }
      }
    }
  }
}

.announcements-content {
  .announcements-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .announcement-card {
    background: #f8fafc;
    border-radius: 12px;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #e2e8f0;

    &:hover {
      background: white;
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: #3b82f6;
      transform: translateY(-2px);
    }

    &.empty-item {
      opacity: 0.6;
      cursor: not-allowed;

      &:hover {
        transform: none;
        box-shadow: none;
        border-color: #e2e8f0;
      }
    }

    .announcement-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 15px;

      .announcement-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
        line-height: 1.4;
        flex: 1;
        margin-right: 15px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .announcement-date {
        font-size: 12px;
        color: #64748b;
        background: #e2e8f0;
        padding: 4px 8px;
        border-radius: 6px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .announcement-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;

      .meta-item {
        font-size: 12px;
        color: #64748b;
        background: white;
        padding: 4px 8px;
        border-radius: 6px;
        border: 1px solid #e2e8f0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}

/* 资讯与知识区域 */
.info-knowledge-section {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 30px;
  margin-bottom: 40px;
}

/* 视频课堂区域 */
.video-classroom-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
}

.video-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 30px;
}

.video-item {
  background: #f8fafc;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
    border-color: #3b82f6;

    .video-overlay {
      background: rgba(0, 0, 0, 0.7);
    }

    .play-button {
      transform: scale(1.1);
    }
  }

  .video-thumbnail {
    position: relative;
    width: 100%;
    height: 160px;
    overflow: hidden;

    video {
      width: 100%;
      height: 100%;
      object-fit: cover;
      background: #e2e8f0;
    }

    .video-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      transition: background 0.3s ease;

      .play-button {
        width: 40px;
        height: 40px;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 15px;
        transition: all 0.3s ease;

        i {
          font-size: 24px;
          color: #3b82f6;
        }
      }

      .video-info {
        .video-duration {
          color: white;
          font-size: 14px;
          font-weight: 500;
          background: rgba(0, 0, 0, 0.7);
          padding: 4px 12px;
          border-radius: 20px;
        }
      }
    }
  }

  .video-content {
    padding: 10px;

    .video-description {
      font-size: 14px;
      color: #64748b;
      margin: 0;
      line-height: 1.5;
      text-align: center;
    }
  }
}

/* 视频播放弹窗样式 */
::v-deep .video-dialog {
  .el-message-box__content {
    padding: 20px;
  }

  .video-player-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
  }
}

.info-column {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;

  .column-header {
    display: flex;
    align-items: flex-start;
    margin-bottom: 25px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f1f5f9;
    transition: all 0.3s ease;

    &:hover {
      // transform: translateY(-2px);

      .header-icon {
        transform: scale(1.05);
      }

      .header-content h3 {
        color: #3b82f6;
      }
    }

    .header-icon {
      width: 50px;
      height: 50px;
      background: linear-gradient(135deg, #3b82f6, #1d4ed8);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      flex-shrink: 0;

      .svg-icon {
        font-size: 24px;
        color: white;
      }
    }

    .header-content {
      flex: 1;

      h3 {
        font-size: 18px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 5px 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      p {
        font-size: 14px;
        color: #64748b;
        margin: 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }

  .info-list {
    .info-item {
      display: flex;
      align-items: flex-start;
      padding: 10px;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 1px solid transparent;
      margin-bottom: 10px;

      &:last-child {
        margin-bottom: 0;
      }

      &:hover {
        background: #f8fafc;
        border-color: #3b82f6;
        transform: translateX(5px);

        .info-title {
          color: #3b82f6;
        }

        .info-arrow .svg-icon {
          color: #3b82f6;
          transform: translateX(3px);
        }
      }

      .info-number {
        width: 10px;
        height: 10px;
        background: linear-gradient(135deg, #3b82f6, #1d4ed8);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 14px;
        font-weight: 600;
        margin-right: 15px;
        flex-shrink: 0;
        margin-top: 4px;
      }

      .info-content {
        flex: 1;

        .info-title {
          font-size: 16px;
          color: #374151;
          font-weight: 500;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          margin: 0 0 8px 0;
        }

        .info-meta {
          display: flex;
          gap: 10px;
          align-items: center;

          .info-date {
            font-size: 14px;
            color: #9ca3af;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .info-tag {
            font-size: 11px;
            color: #3b82f6;
            background: rgba(59, 130, 246, 0.1);
            padding: 2px 8px;
            border-radius: 12px;
            font-weight: 500;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }

      .info-arrow {
        margin-left: 15px;

        .svg-icon {
          font-size: 18px;
          color: #9ca3af;
          transition: all 0.3s ease;
        }
      }
    }
  }
}



/* 校园风采区域 */
.campus-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
}

.campus-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;

  .header-left {
    transition: all 0.3s ease;

    &:hover {
      // transform: translateY(-2px);

      .section-title {
        color: #3b82f6;
      }

      .section-subtitle {
        color: #3b82f6;
      }
    }

    .section-title {
      font-size: 24px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 5px 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      transition: color 0.3s ease;
    }

    .section-subtitle {
      font-size: 14px;
      color: #64748b;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      transition: color 0.3s ease;
    }
  }

  .header-right {
    .view-more-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      background: linear-gradient(135deg, #3b82f6, #1d4ed8);
      color: white;
      border: none;
      border-radius: 25px;
      padding: 12px 20px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.3s ease;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(59, 130, 246, 0.3);
      }

      .svg-icon {
        font-size: 16px;
      }
    }
  }
}

/* 学校信息更多按钮样式 */
.section-more {
  .more-button {
    position: relative;
    display: flex;
    align-items: center;
    gap: 8px;
    background: linear-gradient(135deg, #3b82f6, #1d4ed8);
    color: white;
    border: none;
    border-radius: 25px;
    padding: 12px 20px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    overflow: hidden;
    white-space: nowrap;
    box-shadow: 0 4px 15px rgba(59, 130, 246, 0.2);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
      background: linear-gradient(135deg, #2563eb, #1e40af);

      .more-icon {
        transform: translateX(3px);
      }

      .more-ripple {
        animation: ripple 0.6s ease-out;
      }
    }

    &:active {
      transform: translateY(0);
      box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
    }

    .more-text {
      font-weight: 500;
      letter-spacing: 0.5px;
    }

    .more-icon {
      font-size: 14px;
      transition: transform 0.3s ease;
    }

    .more-ripple {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 0;
      height: 0;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      transform: translate(-50%, -50%);
      pointer-events: none;
    }
  }
}

@keyframes ripple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
  }
  100% {
    width: 300px;
    height: 300px;
    opacity: 0;
  }
}

.campus-grid {
  display: flex;
  gap: 20px;
  align-items: start;
}

.campus-main {
  display: flex;
  gap: 20px;
  flex: 1;

  .campus-main-item {
    cursor: pointer;
    transition: all 0.3s ease;
    background: white;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      border-color: #3b82f6;

      .campus-image-wrapper img {
        transform: scale(1.05);
      }
    }

    .campus-image-wrapper {
      position: relative;
      width: 100%;
      height: 200px;
      overflow: hidden;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }
    }

    .campus-info {
      padding: 15px;
      background: white;
      display: flex;
      flex-direction: column;
      gap: 8px;

      h4 {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin: 0;
        line-height: 1.4;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .campus-meta {
        display: flex;

        .campus-date {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}

/* 移除campus-side样式，因为不再使用 */

/* 学校信息区域 */
.schools-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;

  .section-header {
    transition: all 0.3s ease;

    &:hover {
      // transform: translateY(-2px);

      .section-title {
        color: #3b82f6;
      }
    }

    .section-title {
      transition: color 0.3s ease;
    }
  }
}

.schools-grid {
  display: grid;
  grid-template-columns: repeat(5,minmax(0, 1fr));
  gap: 20px;
}

.school-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;

  &:hover {
    background: white;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    border-color: #3b82f6;
    transform: translateY(-3px);
  }

  .school-logo {
    width: 50px;
    height: 50px;
    margin: 0 auto 15px;

    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
      border-radius: 8px;
    }
  }

  .school-info {
    .school-name {
      font-size: 14px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 8px 0;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .school-type {
      font-size: 12px;
      color: #64748b;
      margin: 0;
      background: white;
      padding: 4px 8px;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
      display: inline-block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .quick-access-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-knowledge-section {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .video-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .campus-grid {
    grid-template-columns: 1fr;
  }

  .schools-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .sticky-nav .sticky-nav-content {
    max-width: 100%;
    padding: 15px;

    .sticky-nav-menu {
      gap: 20px;

      .sticky-nav-item {
        font-size: 14px;
        padding: 6px 12px;
      }
    }
  }
}

@media (max-width: 768px) {
  .header-top {
    flex-direction: column;
    align-items: center;
    gap: 20px;

    .title {
      font-size: 24px;
    }

    .nav {
      width: 100%;
      flex-wrap: wrap;
      justify-content: center;

      .nav-item {
        font-size: 14px;
        padding: 6px 12px;
      }
    }
  }

  .footer-content-wrapper {
    flex-direction: column;
    gap: 30px;
    text-align: center;
  }

  .link-section {
    gap: 40px;
  }

  .sticky-nav .sticky-nav-content {
    flex-direction: column;
    gap: 15px;
    padding: 10px 15px;

    .sticky-title {
      font-size: 18px;
    }

    .sticky-nav-menu {
      gap: 15px;
      flex-wrap: wrap;
      justify-content: center;

      .sticky-nav-item {
        font-size: 13px;
        padding: 5px 10px;
      }
    }

    .sticky-user-info {
      gap: 15px;

      .sticky-user-text,
      .sticky-login-btn,
      .sticky-user-name {
        font-size: 13px;
        padding: 6px 12px;
      }
    }
  }
}

@media (max-width: 480px) {
  .header-container {
    height: 500px;
  }

  .header-top .title {
    font-size: 20px;
  }

  .search-box {
    width: 90%;
    max-width: 400px;
  }

  .sticky-nav .sticky-nav-content {
    padding: 8px 10px;

    .sticky-title {
      font-size: 16px;
    }

    .sticky-nav-menu {
      gap: 10px;

      .sticky-nav-item {
        font-size: 12px;
        padding: 4px 8px;
      }
    }

    .sticky-user-info {
      gap: 10px;

      .sticky-user-text,
      .sticky-login-btn,
      .sticky-user-name {
        font-size: 12px;
        padding: 4px 8px;
      }
    }
  }
}

.footer {
  margin-top: auto;

  .footer-main {
    background-color: #337dff;
    padding: 30px 0 10px 0;
    color: white;

    .footer-content-wrapper {
      max-width: 1200px;
      width: 100%;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 0 20px;
      margin-bottom: 20px;
    }

    .link-section {
      display: flex;
      flex-direction: column;
      gap: 10px;
      min-width: 300px;
    }

    .link-column {
      display: flex;
      flex-direction: column;
      a {
        color: #fff;
        font-size: 16px;
      }
      .link-title {
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 24px;
        color: rgba(255, 255, 255, 1);
      }

      .link-item {
        color: white;
        text-decoration: none;
        margin-bottom: 6px;
        font-size: 16px;
        &:hover {
          text-decoration: underline;
        }
      }

      .sub-link {
        margin-bottom: 12px;
      }
    }

    .copyright-section {
      padding-top: 15px;
      border-top: 1px solid rgba(255, 255, 255, 0.3);

      .copyright-item {
        color: rgba(255, 255, 255, 0.9);
        font-size: 14px;
        margin-bottom: 6px;
        line-height: 1.5;
        font-weight: 400;
      }
    }

    .right-section {
      display: flex;
      align-items: flex-start;
      gap: 40px;

      @media (max-width: 768px) {
        flex-direction: column;
        gap: 20px;
        align-items: center;
      }
    }

    .feedback-section {
      display: flex;
      align-items: flex-start;
      padding-top: 10px;
      animation: subtle-pulse 3s ease-in-out infinite;

      @keyframes subtle-pulse {
        0%, 100% {
          transform: scale(1);
        }
        50% {
          transform: scale(1.01);
        }
      }

      .feedback-link {
        color: white;
        text-decoration: none;
        font-size: 16px;
        font-weight: 500;
        padding: 12px 20px;
        border: 2px solid rgba(255, 255, 255, 0.3);
        border-radius: 25px;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
        backdrop-filter: blur(10px);
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
        display: flex;
        align-items: center;
        gap: 8px;

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transition: left 0.5s ease;
        }

        &:hover {
          background: rgba(255, 255, 255, 0.2);
          border-color: rgba(255, 255, 255, 0.6);
          transform: translateY(-2px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

          &::before {
            left: 100%;
          }
        }

        &:active {
          transform: translateY(0);
          box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        i {
          font-size: 18px;
          transition: transform 0.3s ease;
        }

        &:hover i {
          transform: scale(1.1);
        }
      }
    }

    .qr-section {
      display: flex;
      gap: 50px;
      .qr-item {
        text-align: center;
        img {
          width: 100px;
          height: 100px;
          background-color: #fff;
          border-radius: 4px;
        }
        p {
          font-size: 14px;

        }
      }
    }
  }


}
.school-details-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.school-logo-wrapper {
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.school-logo {
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.school-dialog-name {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.school-info-list {
  align-self: flex-start;
  padding-left: 50px;
  p {
    font-size: 16px;
    color: #555;
    margin: 8px 0;
    strong {
      color: #333;
    }
  }
}
::v-deep .knowledge-dialog {
  border-radius: 12px;
  // .el-dialog__header {
  //   display: none;
  // }
  .el-dialog__body {
    padding: 0;
  }
}

.dialog-container {
  height: 80vh;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  padding: 30px 40px 20px 40px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.dialog-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  margin: 0 40px;
}

.dialog-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 40px 0px 40px;
}

.knowledge-dialog-title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  margin-top: 0;
}

.knowledge-dialog-meta {
  text-align: center;
  font-size: 14px;
  color: #888;
  margin-bottom: 0px;
  span {
    margin: 0 15px;
  }
}

.knowledge-dialog-content {
  line-height: 1.8;
  color: #333;
  img {
    max-width: 100%;
  }
  margin-top: 0;
}

.knowledge-dialog-footer {
  text-align: center;
  margin-top: 30px;
  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

::v-deep .policy-dialog {
  border-radius: 12px;
  // .el-dialog__header {
  //   display: none;
  // }
  .el-dialog__body {
    padding: 0;
  }
}

.policy-dialog-title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  margin-top: 0;
}

.policy-dialog-meta {
  text-align: center;
  font-size: 14px;
  color: #888;
  span {
    margin: 0 15px;
  }
}

.policy-dialog-content {
  line-height: 1.8;
  color: #333;
  img {
    max-width: 100%;
  }
  margin-top: 0;
}

.policy-dialog-footer {
  text-align: center;
  margin-top: 30px;
  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

::v-deep .education-dialog {
  border-radius: 12px;
  // .el-dialog__header {
  //   display: none;
  // }
  .el-dialog__body {
    padding: 0;
  }
}

.education-dialog-title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  margin-top: 0;
}

.education-dialog-meta {
  text-align: center;
  font-size: 14px;
  color: #888;
  span {
    margin: 0 15px;
  }
}

.education-dialog-content {
  line-height: 1.8;
  color: #333;
  img {
    max-width: 100%;
  }
  margin-top: 0;
}

.education-dialog-footer {
  text-align: center;
  margin-top: 30px;
  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

::v-deep .campus-style-dialog {
  border-radius: 12px;
  // .el-dialog__header {
  //   display: none;
  // }
  .el-dialog__body {
    padding: 0;
  }
}

::v-deep .announcement-dialog {
  border-radius: 12px;
  // .el-dialog__header {
  //   display: none;
  // }
  .el-dialog__body {
    padding: 0;
  }
}

.campus-style-dialog-title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  margin-top: 0;
}

.campus-style-dialog-meta {
  text-align: center;
  font-size: 14px;
  color: #888;
  span {
    margin: 0 15px;
  }
}

.campus-style-dialog-content {
  line-height: 1.8;
  color: #333;
  img {
    max-width: 100%;
  }
  margin-top: 0;
}

.campus-style-dialog-footer {
  text-align: center;
  margin-top: 30px;
  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

.banner {
  position: absolute;
  top: 416px;
  width: 100%;
  max-width: 100vw;
  // overflow: hidden;
}

.search-container {
  position: relative;
  max-width: 1000px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  justify-content: flex-end;
  // overflow: hidden;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 440px;
  right: 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-top: 10px;
  padding: 10px;
  max-height: 400px;
  overflow-y: auto;
  z-index: 100;
  animation: fadeInDown 0.3s ease-out;
  width: 560px;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.no-results {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #999;
  font-size: 14px;

  i {
    margin-right: 8px;
    font-size: 16px;
  }
}

.result-item {
  padding: 12px 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.result-item:hover {
  background-color: #f0f7ff;
  transform: translateX(5px);
}

.category-tag {
  color: #4a7dff;
  font-weight: 500;
  margin-right: 10px;
  flex-shrink: 0;
}

.title {
  color: #333;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.search-box {
  display: flex;
  align-items: center;
  width: 550px;
  height: 55px;
  background-color: #fff;
  border-radius: 999px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  padding: 0 5px 0 20px;

  .search-logo {
    height: 28px;
    margin-right: 10px;
  }

  .logo-text {
    font-size: 22px;
    color: #4a7dff; // or some other color
    font-weight: bold;
    margin-right: 15px;
  }

  .search-input {
    flex-grow: 1;
    border: none;
    outline: none;
    font-size: 16px;
    color: #333;
    background: transparent;
    &::placeholder {
      color: #999;
    }
  }

  .search-button {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 45px;
    border: none;
    border-radius: 999px;
    background: #4a7dff;
    color: white;
    font-size: 16px;
    padding: 0 25px;
    cursor: pointer;
    transition: background-color 0.3s;

    i {
      margin-right: 6px;
    }

    &:hover {
      background: #608dff;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-top {
    flex-direction: column;
    align-items: center;
    gap: 20px;

    .title {
      font-size: 24px;
    }

    .nav {
      width: 100%;
      flex-wrap: wrap;
      justify-content: center;

      .nav-item {
        font-size: 14px;
        padding: 6px 12px;
      }
    }
  }

  .footer-content-wrapper {
    flex-direction: column;
    gap: 30px;
    text-align: center;
  }

  .link-section {
    gap: 40px;
  }
}

@media (max-width: 480px) {
  .header-container {
    height: 500px;
  }

  .header-top .title {
    font-size: 20px;
  }

  .search-box {
    width: 90%;
    max-width: 400px;
  }
}

.sticky-nav {
  position: fixed;
  top: -100px; /* 初始状态隐藏 */
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 1000;
  transition: all 0.3s ease;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #e2e8f0;

  &.sticky-active {
    top: 0; /* 显示状态 */
  }

  .sticky-nav-content {
    max-width: 1400px;
    width: 100%;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;

    .sticky-title {
      font-size: 20px;
      font-weight: 600;
      color: #1e293b;
      letter-spacing: 1px;
      // white-space: nowrap;
      // overflow: hidden;
      // text-overflow: ellipsis;
      // max-width: 200px;
    }

    .sticky-nav-menu {
      display: flex;
      gap: 15px;
      align-items: center;

      .sticky-nav-item {
        font-size: 16px;
        font-weight: 500;
        color: #64748b;
        cursor: pointer;
        transition: all 0.3s ease;
        padding: 8px 16px;
        border-radius: 8px;
        position: relative;
        white-space: nowrap;
        // overflow: hidden;
        text-overflow: ellipsis;
        max-width: 120px;

        &:hover {
          color: #3b82f6;
          background: rgba(59, 130, 246, 0.1);
        }

        &.active {
          color: #3b82f6;
          background: rgba(59, 130, 246, 0.1);
          font-weight: 600;
        }
      }
    }

    .sticky-user-info {
      display: flex;
      align-items: center;
      gap: 20px;

      .sticky-user-text {
        font-size: 14px;
        font-weight: 500;
        color: #64748b;
        cursor: pointer;
        transition: color 0.3s ease;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 100px;

        &:hover {
          color: #3b82f6;
        }
      }

      .sticky-login-btn,
      .sticky-user-name {
        font-size: 14px;
        font-weight: 500;
        color: #3b82f6;
        cursor: pointer;
        transition: all 0.3s ease;
        padding: 8px 16px;
        border-radius: 20px;
        background: rgba(59, 130, 246, 0.1);
        border: 1px solid rgba(59, 130, 246, 0.2);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 150px;

        &:hover {
          background: #3b82f6;
          color: white;
          transform: translateY(-1px);
        }
      }
    }
  }
}

/* 全局样式 - 防止水平滚动 */
html, body {
  overflow-x: hidden;
  max-width: 100%;
}

/* 回到顶部按钮 */
.back-to-top {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 999;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);

  &:hover {
    background: linear-gradient(135deg, #2563eb, #1e40af);
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
  }

  &.show {
    opacity: 1;
    transform: translateY(0);
  }

  .svg-icon {
    font-size: 20px;
    color: white;
  }
}

/* 意见反馈弹框样式 */
.feedback-form {
  .el-form-item {
    margin-bottom: 20px;

    .el-form-item__label {
      font-weight: 500;
      color: #374151;
    }

    .el-input__inner,
    .el-textarea__inner,
    .el-select .el-input__inner {
      border-radius: 8px;
      border: 1px solid #e2e8f0;

      &:focus {
        border-color: #3b82f6;
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
      }
    }
  }
}

.dialog-footer {
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 20px;

  .el-button--primary {
    background: linear-gradient(135deg, #3b82f6, #1d4ed8);
    border: none;
    border-radius: 8px;
    font-weight: 500;

    &:hover {
      background: linear-gradient(135deg, #2563eb, #1e40af);
      transform: translateY(-1px);
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .back-to-top {
    right: 20px;
    bottom: 20px;
    width: 45px;
    height: 45px;

    .svg-icon {
      font-size: 18px;
    }
  }
}
</style>
