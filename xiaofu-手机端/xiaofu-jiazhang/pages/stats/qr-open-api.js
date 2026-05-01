/**
 * 家长端统计流程页接口：与 PC（xiaofu-xx `src/api`）同一批 `/school/...` 地址，不再使用 `/school/qr/open/...`。
 */

function baseUrl() {
  const u = getApp().globalData.baseUrl || "";
  return u.replace(/\/$/, "");
}

/**
 * 持久化班子会议列表记录的 id（与 `/school/jzbuy/opinions/list` 的 teamMeetingId 一致）
 * 由流程页同步首条、或由班子会议列表页在拉取列表后写入。
 */
export function setStatsTeamMeetingId(id) {
  const s =
    id != null && String(id).trim() !== "" ? String(id).trim() : "";
  try {
    const app = getApp();
    if (app && app.globalData) {
      app.globalData.statsTeamMeetingId = s;
    }
  } catch (e) {
    /* ignore */
  }
  return s;
}

export function getStatsTeamMeetingId() {
  try {
    const app = getApp();
    const g = app && app.globalData && app.globalData.statsTeamMeetingId;
    if (g != null && String(g).trim() !== "") {
      return String(g).trim();
    }
  } catch (e) {
    /* ignore */
  }
  return "";
}

/**
 * 与 PC `getProcessMenu` / 子 Tab `formId` 一致：应为 **班子会议列表首条记录主键 id**（流程页 `syncTeamMeetingIdFromList` 写入）。
 */
export function setStatsFormTeamId(id) {
  const s =
    id != null && String(id).trim() !== "" ? String(id).trim() : "";
  try {
    const app = getApp();
    if (app && app.globalData) {
      app.globalData.statsFormTeamId = s;
    }
  } catch (e) {
    /* ignore */
  }
  return s;
}

export function getStatsFormTeamId() {
  try {
    const app = getApp();
    const g = app && app.globalData && app.globalData.statsFormTeamId;
    if (g != null && String(g).trim() !== "") {
      return String(g).trim();
    }
  } catch (e) {
    /* ignore */
  }
  return "";
}

function resolveDiscloseTeamMeetingIdForRequest(explicit) {
  if (explicit != null && String(explicit).trim() !== "") {
    return String(explicit).trim();
  }
  const meetingId = getStatsTeamMeetingId();
  if (meetingId !== "") {
    return meetingId;
  }
  return getStatsFormTeamId();
}

/**
 * 流程子页 list 的 teamId：与班子会议 teamMeetingId 同值（第四步 repmode、第五步 notice 等）
 */
function resolveRepmodeTeamIdForRequest(explicit) {
  if (explicit != null && String(explicit).trim() !== "") {
    return String(explicit).trim();
  }
  const meetingId = getStatsTeamMeetingId();
  if (meetingId !== "") {
    return meetingId;
  }
  return getStatsFormTeamId();
}

/**
 * 家长意见征集等接口的 teamMeetingId：入参优先，否则用已持久化的班子会议 id
 * @param {string|number|undefined|null} explicit query 或调用方传入
 */
function resolveTeamMeetingIdForRequest(explicit) {
  if (explicit != null && String(explicit).trim() !== "") {
    return String(explicit).trim();
  }
  return getStatsTeamMeetingId();
}

/** 统计流程各页默认部门/学校 id（路由未带 `deptId` 时使用，与班子会议 list 缺省一致） */
export const DEFAULT_STATS_DEPT_ID = "100";

/** 班子会议 list/get 默认参数（可被路由或 query 覆盖） */
export const DEFAULT_TEAM_MEETING_DEPT_ID = DEFAULT_STATS_DEPT_ID;
export const DEFAULT_TEAM_MEETING_TEAM_ID = "1102";

/**
 * 与 PC `cg/_component/MeetingInfo.vue` 中 dicts 一致：代表类别 sc_dble、校服品类 xfpl
 * GET `/system/dict/data/type/{dictType}`（同 xiaofu-xx `getDicts`）
 */
const TEAM_MEETING_DICT_DBLB = "sc_dble";
const TEAM_MEETING_DICT_XFPL = "xfpl";

const dictValueLabelCache = new Map();

function extractDictRows(payload) {
  if (!payload || typeof payload !== "object") return [];
  if (Array.isArray(payload.data)) return payload.data;
  if (Array.isArray(payload.content)) return payload.content;
  if (Array.isArray(payload.rows)) return payload.rows;
  if (Array.isArray(payload)) return payload;
  return [];
}

/**
 * @param {string} dictType
 * @returns {Promise<Record<string, string>>} dictValue -> dictLabel
 */
function fetchDictValueLabelMap(dictType) {
  const b = baseUrl();
  const key = `${b}|${dictType}`;
  if (dictValueLabelCache.has(key)) {
    return dictValueLabelCache.get(key);
  }
  const p = new Promise((resolve) => {
    uni.request({
      url: `${b}/system/dict/data/type/${encodeURIComponent(dictType)}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const list = extractDictRows(res.data);
          const map = Object.create(null);
          list.forEach((row) => {
            if (!row || typeof row !== "object") return;
            const v =
              row.dictValue != null
                ? String(row.dictValue).trim()
                : row.value != null
                  ? String(row.value).trim()
                  : "";
            if (!v) return;
            const lab =
              row.dictLabel != null
                ? String(row.dictLabel)
                : row.label != null
                  ? String(row.label)
                  : v;
            map[v] = lab;
          });
          resolve(map);
        } else {
          resolve(Object.create(null));
        }
      },
      fail: () => resolve(Object.create(null)),
    });
  });
  dictValueLabelCache.set(key, p);
  return p;
}

function dictLabelFromMap(map, raw) {
  if (raw == null || raw === "") return "—";
  const key = String(raw).trim();
  if (!key) return "—";
  if (!map || typeof map !== "object") return key;
  const label = map[key];
  return label != null && String(label).trim() !== ""
    ? String(label)
    : key;
}

/** 班子会议详情展示用字典（并行请求，失败时退回显示原值） */
export function fetchTeamMeetingDetailDictMaps() {
  return Promise.all([
    fetchDictValueLabelMap(TEAM_MEETING_DICT_DBLB),
    fetchDictValueLabelMap(TEAM_MEETING_DICT_XFPL),
  ]).then(([dblb, xfpl]) => ({ dblb, xfpl }));
}

/** 与 PC `OrganizationList.vue` dicts 一致：职务 sc_zw、代表类别 sc_dble */
const ORG_DISCLOSE_DICT_ZW = "sc_zw";
const ORG_DISCLOSE_DICT_DBLB = "sc_dble";

/** 选用组织名单详情成员行展示用字典 */
export function fetchOrgDiscloseDisplayDictMaps() {
  return Promise.all([
    fetchDictValueLabelMap(ORG_DISCLOSE_DICT_ZW),
    fetchDictValueLabelMap(ORG_DISCLOSE_DICT_DBLB),
  ]).then(([zw, dblb]) => ({ zw, dblb }));
}

/** PC `PurchaseMode.vue`、`PurchaseResult.vue` 选购模式 */
const DICT_CGMSLX = "cgmslx";
/** PC 公告 / 选购结果公示 发布状态 isRelease */
const DICT_NOTICE_STATUS = "notice_status";
/** PC `SatisfactionReport.vue` 问卷状态 state */
const DICT_SC_WJZT = "sc_wjzt";
/** PC `QualityReport.vue` 质量检验报告类型 inspectionReportType（与 dict-tag 一致） */
const DICT_SC_ZLJY_BGLX = "sc_zljy_bglx";

/** 第四步 选用组织确定选购模式 — 选购模式字典 */
export function fetchCgmslxDictMaps() {
  return fetchDictValueLabelMap(DICT_CGMSLX).then((cgmslx) => ({ cgmslx }));
}

/** 第五步 发布公告 — 是否发布 / 状态 */
export function fetchNoticeReleaseDictMaps() {
  return fetchDictValueLabelMap(DICT_NOTICE_STATUS).then((notice_status) => ({
    notice_status,
  }));
}

/** 第六步 选购结果公示列表 — 选购模式 + 发布状态 */
export function fetchCgmslxNoticeStatusDictMaps() {
  return Promise.all([
    fetchDictValueLabelMap(DICT_CGMSLX),
    fetchDictValueLabelMap(DICT_NOTICE_STATUS),
  ]).then(([cgmslx, notice_status]) => ({ cgmslx, notice_status }));
}

/** 选购结果公示详情 — 含明细行校服品类 xfpl */
export function fetchProcResultDisplayDictMaps() {
  return Promise.all([
    fetchDictValueLabelMap(DICT_CGMSLX),
    fetchDictValueLabelMap(DICT_NOTICE_STATUS),
    fetchDictValueLabelMap(TEAM_MEETING_DICT_XFPL),
  ]).then(([cgmslx, notice_status, xfpl]) => ({
    cgmslx,
    notice_status,
    xfpl,
  }));
}

/** 满意度总结 — 问卷状态 sc_wjzt */
export function fetchSatisfactionSurveyDictMaps() {
  return fetchDictValueLabelMap(DICT_SC_WJZT).then((sc_wjzt) => ({
    sc_wjzt,
  }));
}

/** 产品质量检验报告 — 报告类型 sc_zljy_bglx（与 PC QualityReport dicts 一致） */
export function fetchQualityInspectionDictMaps() {
  return fetchDictValueLabelMap(DICT_SC_ZLJY_BGLX).then((sc_zljy_bglx) => ({
    sc_zljy_bglx,
  }));
}

/** 与 PC `ParentPurchase.vue` dicts 一致：意见征集类型 sc_yjzhlx、意见征集情况 sc_yjzjqk */
const OPINION_DICT_ZJLX = "sc_yjzhlx";
const OPINION_DICT_YJZJQK = "sc_yjzjqk";

/** 家长购买意见征集列表/详情展示用字典 */
export function fetchOpinionDisplayDictMaps() {
  return Promise.all([
    fetchDictValueLabelMap(OPINION_DICT_ZJLX),
    fetchDictValueLabelMap(OPINION_DICT_YJZJQK),
  ]).then(([zjlx, yjzjqk]) => ({ zjlx, yjzjqk }));
}

/** 组建评定小组投票确定厂商 — 投票类型字典（与 PC `sc_yjzhlx` 一致） */
export function fetchPdxzVoteTypeDictMaps() {
  return fetchDictValueLabelMap(OPINION_DICT_ZJLX).then((zjlx) => ({ zjlx }));
}

/**
 * 班子会议列表/详情与 PC 端一致（xiaofu-xx `src/api/cg/index.js`：listMeeting / getMeeting）
 */
export function fetchStatsTeamMeetingList(query) {
  return fetchTeamMeetingList(query);
}

/** @param {string} id @param {object} [query] deptId、teamId，缺省用默认常量 */
export function fetchStatsTeamMeetingDetail(id, query) {
  return fetchTeamMeetingDetail(id, query);
}

export function formatDateTime(val) {
  if (val == null || val === "") return "—";
  if (typeof val === "string") {
    const s = val.trim();
    if (!s) return "—";
    return s.length > 19 ? s.slice(0, 19).replace("T", " ") : s.replace("T", " ");
  }
  return String(val);
}

export function formatDate(val) {
  const s = formatDateTime(val);
  if (s === "—") return s;
  const i = s.indexOf(" ");
  return i > 0 ? s.slice(0, i) : s;
}

function isLikelyFileUrl(str) {
  if (!str || typeof str !== "string") return false;
  const s = str.trim();
  if (/^https?:\/\//i.test(s)) return true;
  if (s.startsWith("/")) {
    if (s.includes("/profile/") || s.includes("/download") || s.includes("/upload")) return true;
    if (/\.(pdf|doc|docx|rtf|png|jpe?g)$/i.test(s.split("?")[0])) return true;
  }
  return false;
}

export function resolveFileUrl(path) {
  if (!path || typeof path !== "string") return "";
  const t = path.trim();
  if (!t) return "";
  if (/^https?:\/\//i.test(t)) return t;
  return baseUrl() + (t.startsWith("/") ? t : "/" + t);
}

/**
 * 校服采购流程列表 GET /school/cglcprocess/list（PC `api/cg/cglcprocess.js` listCglcprocess）
 *
 * @param {{ deptId: string|number, pageNum?: number, pageSize?: number, [key: string]: any }} query
 */
export function fetchCglcList(query) {
  const data = {
    pageNum: query.pageNum != null ? query.pageNum : 1,
    pageSize: query.pageSize != null ? query.pageSize : 10,
  };
  if (query.deptId != null && query.deptId !== "") {
    data.deptId = query.deptId;
  }
  const skip = new Set(["pageNum", "pageSize", "deptId"]);
  Object.keys(query || {}).forEach((k) => {
    if (skip.has(k)) return;
    const v = query[k];
    if (v != null && v !== "") data[k] = v;
  });
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/cglcprocess/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "流程列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 采购类型 GET /school/team/meeting/getStatus（PC `api/cg/index.js` getCgType）
 * @param {string|number|null|undefined} teamId 与 PC 采购流程 formId 一致
 * @returns {Promise<number|string|null>} `res.msg`，常见 0 自主确定、2 招投标
 */
export function fetchCgType(teamId) {
  const id =
    teamId != null && String(teamId).trim() !== ""
      ? String(teamId).trim()
      : DEFAULT_TEAM_MEETING_TEAM_ID;
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/team/meeting/getStatus`,
      method: "GET",
      data: { teamId: id },
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const m = res.data.msg;
          if (typeof m === "number" && !Number.isNaN(m)) {
            resolve(m);
            return;
          }
          if (m != null && String(m).trim() !== "") {
            const n = Number(String(m).trim());
            resolve(Number.isNaN(n) ? m : n);
            return;
          }
          resolve(null);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "采购类型加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 流程各节点状态 GET /school/team/meeting/getProcessMenu（PC `getProcessMenu`）
 * 返回与 PC 侧栏顺序一致的数组：1 绿（完成）、0 红（未完成）
 * @param {string|number|null|undefined} teamId
 * @returns {Promise<(number|string)[]>}
 */
export function fetchProcessMenu(teamId) {
  const id =
    teamId != null && String(teamId).trim() !== ""
      ? String(teamId).trim()
      : DEFAULT_TEAM_MEETING_TEAM_ID;
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/team/meeting/getProcessMenu`,
      method: "GET",
      data: { teamId: id },
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const d = res.data.data;
          resolve(Array.isArray(d) ? d : []);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "流程状态加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 班子会议列表 GET /school/team/meeting/list（与 PC listMeeting 一致）
 * @param {{ deptId?: string|number, teamId?: string|number, pageNum?: number, pageSize?: number, [key: string]: any }} query
 */
export function fetchTeamMeetingList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  if (q.deptId != null && q.deptId !== "") {
    data.deptId = q.deptId;
  }
  if (q.teamId != null && q.teamId !== "") {
    data.teamId = q.teamId;
  }
  const skip = new Set([
    "pageNum",
    "pageSize",
    "deptId",
    "teamId",
    "omitDefaultTeamId",
  ]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  if (data.deptId == null || String(data.deptId).trim() === "") {
    data.deptId = DEFAULT_TEAM_MEETING_DEPT_ID;
  }
  /** 第一步班子会议：teamId 优先路由；未传且 omitDefaultTeamId 时不写死默认 1102 */
  const omitDefaultTeamId = q.omitDefaultTeamId === true;
  if (
    !omitDefaultTeamId &&
    (data.teamId == null || String(data.teamId).trim() === "")
  ) {
    data.teamId = DEFAULT_TEAM_MEETING_TEAM_ID;
  }
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/team/meeting/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 班子会议详情 GET /school/team/meeting/{id}（与 PC getMeeting 一致）
 * @param {object} [query] 附带的 deptId、teamId（GET query），缺省用默认常量
 */
export function fetchTeamMeetingDetail(id, query) {
  const q = query || {};
  const deptId =
    q.deptId != null && String(q.deptId).trim() !== ""
      ? String(q.deptId).trim()
      : DEFAULT_TEAM_MEETING_DEPT_ID;
  const omitDefaultTeamId = q.omitDefaultTeamId === true;
  const teamIdRaw =
    q.teamId != null && String(q.teamId).trim() !== ""
      ? String(q.teamId).trim()
      : "";
  const teamId =
    teamIdRaw !== ""
      ? teamIdRaw
      : omitDefaultTeamId
        ? ""
        : DEFAULT_TEAM_MEETING_TEAM_ID;
  const reqData = { deptId };
  if (teamId !== "") {
    reqData.teamId = teamId;
  }
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/team/meeting/${id}`,
      method: "GET",
      data: reqData,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve(res.data.data || {});
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * TeamMeeting + teamMeetingSonList -> 详情页展示结构
 * TeamMeetingSon: cgnj 采购年级, xfpl 校服品类, gmts 套数, jgqj 价格区间, bz 备注
 */
function normalizeMeetingSonList(apiData) {
  let sons = apiData.teamMeetingSonList;
  if (Array.isArray(sons) && sons.length) {
    return sons;
  }
  const raw = apiData.sonJsonData;
  if (raw == null || raw === "") {
    return [];
  }
  try {
    const parsed = typeof raw === "string" ? JSON.parse(raw) : raw;
    return Array.isArray(parsed) ? parsed : [];
  } catch (e) {
    return [];
  }
}

/**
 * PC FileUpload / 厂商资质：businessLicense 常为 JSON 字符串数组 [{ name, url }]
 * @returns {string} 首个附件的 url/路径，或原始字符串（供 mapFjToFileAndPlain）
 */
function firstUrlFromPcFileUploadField(v) {
  if (v == null || v === "") {
    return "";
  }
  if (typeof v === "object") {
    if (Array.isArray(v)) {
      const f = v[0];
      if (f && typeof f === "object") {
        const u = f.url || f.fileUrl || f.path || f.file;
        if (u != null && String(u).trim()) {
          return String(u).trim();
        }
      }
    } else {
      const u = v.url || v.fileUrl || v.path || v.file;
      if (u != null && String(u).trim()) {
        return String(u).trim();
      }
    }
    return "";
  }
  const s = String(v).trim();
  if (!s) {
    return "";
  }
  if (s.startsWith("[")) {
    try {
      const arr = JSON.parse(s);
      if (Array.isArray(arr) && arr.length) {
        const f = arr[0];
        if (f && typeof f === "object") {
          const u = f.url || f.fileUrl || f.path || f.file;
          if (u != null && String(u).trim()) {
            return String(u).trim();
          }
        }
      }
    } catch (e) {
      /* ignore */
    }
  }
  return s;
}

function pickFirstQualificationUpload(apiData, keys) {
  for (let i = 0; i < keys.length; i += 1) {
    const k = keys[i];
    const v = apiData[k];
    if (v == null || v === "") {
      continue;
    }
    const urlOrPath = firstUrlFromPcFileUploadField(v);
    if (urlOrPath) {
      return urlOrPath;
    }
  }
  return "";
}

/** PC FileUpload 常见存法：hyjy 为 JSON 字符串数组，元素含 name/url */
function normalizeHyjyForDisplay(apiData) {
  const hyjy = apiData.hyjy;
  if (hyjy == null || hyjy === "") {
    return "";
  }
  const s = String(hyjy).trim();
  if (!s) {
    return "";
  }
  if (s.startsWith("[")) {
    try {
      const arr = JSON.parse(s);
      if (Array.isArray(arr) && arr.length) {
        const f = arr[0];
        if (f && typeof f === "object") {
          if (f.url != null && String(f.url).trim()) {
            return String(f.url).trim();
          }
        }
      }
    } catch (e) {
      /* ignore */
    }
  }
  return s;
}

/**
 * @param {object} apiData
 * @param {{ dblb?: Record<string, string>, xfpl?: Record<string, string> }} [dictMaps] 与 PC 字典一致时传入，用于展示标签
 */
export function mapTeamMeetingToDetailView(apiData, dictMaps) {
  const dblbMap = dictMaps && dictMaps.dblb;
  const xfplMap = dictMaps && dictMaps.xfpl;
  const sons = normalizeMeetingSonList(apiData);
  const selectionRequirements = sons.map((s) => ({
    grade: s.cgnj != null && s.cgnj !== "" ? String(s.cgnj) : "—",
    uniformCategory:
      s.xfpl != null && s.xfpl !== ""
        ? dictLabelFromMap(xfplMap, s.xfpl)
        : "—",
    setsPerStudent:
      s.gmts != null && s.gmts !== "" ? String(s.gmts) : "—",
    priceRange:
      s.jgqj != null && s.jgqj !== "" ? String(s.jgqj) : "—",
    remark: s.bz != null ? String(s.bz) : "",
  }));

  const hyjyRaw = normalizeHyjyForDisplay(apiData);

  let minutesFile = null;
  let hyjyPlainText = "";

  if (hyjyRaw) {
    if (isLikelyFileUrl(hyjyRaw)) {
      const url = resolveFileUrl(hyjyRaw);
      const seg = hyjyRaw.split(/[/\\]/).pop() || "会议纪要.pdf";
      const name = decodeURIComponent(seg.split("?")[0]);
      minutesFile = { name, url };
    } else {
      hyjyPlainText = hyjyRaw;
    }
  }

  const hasLeader =
    (apiData.zw && String(apiData.zw).trim()) ||
    (apiData.dblb && String(apiData.dblb).trim()) ||
    (apiData.zzName && String(apiData.zzName).trim()) ||
    (apiData.xm && String(apiData.xm).trim());

  const proposedLeader = hasLeader
    ? {
      position: apiData.zw != null ? String(apiData.zw) : "—",
      category:
        apiData.dblb != null && String(apiData.dblb).trim() !== ""
          ? dictLabelFromMap(dblbMap, apiData.dblb)
          : "—",
      name:
        (apiData.zzName != null && String(apiData.zzName).trim()) ||
        (apiData.xm != null ? String(apiData.xm) : "—"),
    }
    : null;

  return {
    id: apiData.id,
    title: apiData.title != null ? String(apiData.title) : "—",
    meetingTime: formatDate(apiData.meetingTime),
    meetingDate: formatDate(apiData.meetingTime),
    createTime: formatDateTime(apiData.createTime),
    participants:
      apiData.meetingUser != null && String(apiData.meetingUser).trim()
        ? String(apiData.meetingUser)
        : "—",
    proposedLeader,
    selectionRequirements,
    minutesFile,
    hyjyPlainText,
  };
}

/**
 * 列表行 -> 卡片展示字段
 */
export function mapTeamMeetingListRow(row) {
  return {
    id: row.id,
    title: row.title != null ? String(row.title) : "—",
    meetingDate: formatDate(row.meetingTime),
    createTime: formatDateTime(row.createTime),
  };
}

export function formatVotingRate(val) {
  if (val == null || val === "") return "—";
  if (typeof val === "number") {
    if (val >= 0 && val <= 1) {
      const p = Math.round(val * 10000) / 100;
      return (Number.isInteger(p) ? String(p) : p.toFixed(2)) + "%";
    }
    if (val > 1 && val <= 100) {
      return (Number.isInteger(val) ? String(val) : val.toFixed(2)) + "%";
    }
    return String(val);
  }
  const s = String(val).trim();
  return s.includes("%") ? s : s + "%";
}

/**
 * 家长购买意见征集列表 GET /school/jzbuy/opinions/list（与 PC `api/cg/opinions.js` listOpinions 一致）
 * PC 侧通过 teamMeetingId 筛选；手机端该 id 为班子会议列表 `/school/team/meeting/list` 首条记录的 id（流程页已写入 globalData.statsTeamMeetingId）
 */
export function fetchGmyjzjList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamMeetingId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamMeetingId = resolveTeamMeetingIdForRequest(q.teamMeetingId);
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/jzbuy/opinions/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "意见征集列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 家长购买意见征集详情 GET /school/jzbuy/opinions/{id}（与 PC getOpinions 一致）
 */
export function fetchGmyjzjDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/jzbuy/opinions/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve(res.data.data || {});
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 评定小组确定厂商列表 GET /school/pdxzSupplier/list（PC `api/cg/pdxzSupplier.js` listPdxzSupplier）
 * teamId 与流程一致：班子会议 teamMeetingId 优先（同 resolveRepmodeTeamIdForRequest）
 */
export function fetchPdxzSupplierList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamId = resolveRepmodeTeamIdForRequest(q.teamId);
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/pdxzSupplier/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "投票确定厂商列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 评定小组确定厂商详情 GET /school/pdxzSupplier/{id}（PC `api/cg/pdxzSupplier.js` getPdxzSupplier）
 */
export function fetchPdxzSupplierDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/pdxzSupplier/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve(res.data.data || {});
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * @param {{ zjlx?: Record<string, string> }} [dictMaps]
 */
export function mapPdxzSupplierListRow(row, dictMaps) {
  const typeRaw = row.type;
  const typeNum =
    typeRaw != null && String(typeRaw).trim() !== ""
      ? Number(String(typeRaw).trim())
      : Number.NaN;
  const isOnline = !Number.isNaN(typeNum) && typeNum === 0;
  const zjlxMap = dictMaps && dictMaps.zjlx;
  return {
    id: row.id,
    title:
      row.title != null && String(row.title).trim() !== ""
        ? String(row.title).trim()
        : "—",
    typeRaw,
    typeText:
      typeRaw != null && String(typeRaw).trim() !== ""
        ? dictLabelFromMap(zjlxMap, typeRaw)
        : "—",
    votedCountDisplay:
      isOnline && row.votedCount != null && row.votedCount !== ""
        ? String(row.votedCount)
        : "—",
    totalCountDisplay:
      isOnline && row.count != null && row.count !== "" ? String(row.count) : "—",
    votingRateDisplay: isOnline ? formatVotingRate(row.votingRate) : "—",
    createTime: formatDateTime(row.createTime),
  };
}

/**
 * 组建评定小组投票确定厂商详情（区分线上/线下）
 * 线上：展示投票名单（questionnaire heading + signatureList）
 * 线下：展示投票结果文件
 * @param {{ zjlx?: Record<string, string> }} [dictMaps]
 */
export function mapPdxzSupplierToDetailView(apiData, dictMaps) {
  const s =
    apiData && apiData.xfptPdxzSureSupplier
      ? apiData.xfptPdxzSureSupplier
      : apiData || {};
  const typeRaw = s.type != null ? s.type : apiData.type;
  const typeNum =
    typeRaw != null && String(typeRaw).trim() !== ""
      ? Number(String(typeRaw).trim())
      : Number.NaN;
  const isOnline = !Number.isNaN(typeNum) && typeNum === 0;
  const zjlxMap = dictMaps && dictMaps.zjlx;
  const offlineFile =
    mapPcUploadFieldToFile(s.otherFile, "投票结果文件.pdf") ||
    mapFjToFileAndPlain(s.otherFile).resultFile;
  return {
    id: s.id != null ? s.id : apiData.id,
    title:
      s.title != null && String(s.title).trim() !== "" ? String(s.title).trim() : "—",
    createTime: formatDateTime(s.createTime != null ? s.createTime : apiData.createTime),
    typeRaw,
    typeText:
      typeRaw != null && String(typeRaw).trim() !== ""
        ? dictLabelFromMap(zjlxMap, typeRaw)
        : "—",
    isOnline,
    isOffline: !isOnline,
    questId:
      s.questId != null && String(s.questId).trim() !== ""
        ? String(s.questId).trim()
        : s.questionBusinessId != null && String(s.questionBusinessId).trim() !== ""
          ? String(s.questionBusinessId).trim()
          : "",
    offlineResultFile: offlineFile,
  };
}

/**
 * 问卷表头（投票名单列）GET `/school/questionnaireSurvey/headingList`（PC `api/cg/index.js` getZiduanList）
 */
export function fetchQuestionnaireHeadingList(questionId) {
  const qid =
    questionId != null && String(questionId).trim() !== ""
      ? String(questionId).trim()
      : "";
  if (!qid) {
    return Promise.resolve([]);
  }
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/questionnaireSurvey/headingList?questionId=${encodeURIComponent(
        qid
      )}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const d = res.data.data;
          if (Array.isArray(d)) {
            resolve(d);
            return;
          }
          if (d && typeof d === "object" && Array.isArray(d.rows)) {
            resolve(d.rows);
            return;
          }
          resolve([]);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "表头加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 投票/问卷签名名单 GET `/school/questionnaireResult/signatureList`（PC `api/cg/opinions.js` getVoteSignatureList）
 * @param {{ questId: string|number, pageNum?: number, pageSize?: number, name?: string, phone?: string }} query
 */
export function fetchVoteSignatureList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 100,
  };
  const questId = q.questId != null ? q.questId : q.questionId;
  if (questId == null || questId === "") {
    return Promise.resolve({ total: 0, rows: [] });
  }
  data.questId = questId;
  if (q.name != null && q.name !== "") {
    data.name = q.name;
  }
  if (q.phone != null && q.phone !== "") {
    data.phone = q.phone;
  }
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/questionnaireResult/signatureList`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const body = res.data;
          resolve({
            total: body.total != null ? body.total : 0,
            rows: Array.isArray(body.rows) ? body.rows : [],
          });
        } else {
          // const msg =
          //   (res.data && (res.data.msg || res.data.message)) ||
          //   "投票名单加载失败";
          reject();
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

function mapFjToFileAndPlain(fjRaw) {
  if (fjRaw == null || fjRaw === "") {
    return { resultFile: null, fjPlainText: "" };
  }
  const raw = String(fjRaw).trim();
  if (!raw) return { resultFile: null, fjPlainText: "" };
  if (isLikelyFileUrl(raw)) {
    const url = resolveFileUrl(raw);
    const seg = raw.split(/[/\\]/).pop() || "征集结果.pdf";
    const name = decodeURIComponent(seg.split("?")[0]);
    return { resultFile: { name, url }, fjPlainText: "" };
  }
  return { resultFile: null, fjPlainText: raw };
}

/**
 * 家长购买意见列表行 -> 卡片字段（与 PC ParentPurchase 表格一致：zjlx==0 时展示投票人数/总人数/投票率）
 * @param {object} row
 * @param {{ zjlx?: Record<string, string>, yjzjqk?: Record<string, string> }} [dictMaps]
 */
export function mapJzBuyOpinionsListRow(row, dictMaps) {
  const zjlxRaw = row.zjlx;
  const zjlxNum =
    zjlxRaw != null && zjlxRaw !== "" ? Number(zjlxRaw) : Number.NaN;
  const isVoteType = !Number.isNaN(zjlxNum) && zjlxNum === 0;
  const voted =
    row.votedCount != null && row.votedCount !== ""
      ? String(row.votedCount)
      : "—";
  const total =
    row.count != null && row.count !== "" ? String(row.count) : "—";
  const zjlxMap = dictMaps && dictMaps.zjlx;
  const yjzjqkMap = dictMaps && dictMaps.yjzjqk;
  return {
    id: row.id,
    title: row.title != null ? String(row.title) : "—",
    zjlx:
      zjlxRaw != null && String(zjlxRaw).trim() !== ""
        ? dictLabelFromMap(zjlxMap, zjlxRaw)
        : "—",
    votedCountDisplay: isVoteType ? voted : "—",
    totalCountDisplay: isVoteType ? total : "—",
    votingRateDisplay: isVoteType ? formatVotingRate(row.votingRate) : "—",
    createTime: formatDateTime(row.createTime),
    yjzjqk: dictLabelFromMap(yjzjqkMap, row.yjzjqk),
  };
}

/**
 * 意见征集详情 -> 页面展示结构
 * - PC `ParentPurchase.vue`：`zjlx == 0` 为线上投票 → 展示 `configContent`、`fj`(人员名单)、投票名单表
 * - 其它类型：`fj` 为征集结果附件
 * @param {{ zjlx?: Record<string, string> }} [dictMaps]
 */
export function mapJzBuyOpinionsToDetailView(apiData, dictMaps) {
  const zjlxMap = dictMaps && dictMaps.zjlx;
  const zjlxRaw = apiData.zjlx;
  const zjlxNum =
    zjlxRaw != null && zjlxRaw !== "" ? Number(zjlxRaw) : Number.NaN;
  const isOnlineVote = !Number.isNaN(zjlxNum) && zjlxNum === 0;

  const title = apiData.title != null ? String(apiData.title) : "—";
  const zjlxLabel = dictLabelFromMap(zjlxMap, zjlxRaw);

  if (isOnlineVote) {
    const rymd = mapProcurementAttachField(apiData, [
      "fj",
      "ryfile",
      "namesFile",
      "rymdFile",
    ]);
    const qbid = apiData.questionBusinessId;
    return {
      mode: "online",
      id: apiData.id,
      title,
      zjlx: zjlxLabel,
      zjlxRaw,
      configContent:
        apiData.configContent != null ? String(apiData.configContent) : "",
      questionBusinessId:
        qbid != null && qbid !== "" ? String(qbid) : "",
      rymdFile: rymd.resultFile,
      rymdPlain: rymd.fjPlainText,
    };
  }

  const { resultFile, fjPlainText } = mapFjToFileAndPlain(apiData.fj);
  return {
    mode: "offline",
    id: apiData.id,
    title,
    zjlx: zjlxLabel,
    zjlxRaw,
    resultFile,
    fjPlainText,
  };
}

/**
 * 选用组织名单报备与公开列表 GET /school/org/report/disclose/list（PC `api/cg/OrganizationList.js` listDisclose）
 * query.teamMeetingId：与第二步意见征集一致，为班子会议列表首条 id（globalData.statsTeamMeetingId）；无则回退 statsFormTeamId（cglc 行 teamId）
 */
export function fetchXyzzList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamMeetingId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamMeetingId = resolveDiscloseTeamMeetingIdForRequest(q.teamMeetingId);
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/org/report/disclose/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "选用组织名单加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 选用组织名单详情 GET /school/org/report/disclose/{id}（PC getDisclose）
 */
export function fetchXyzzDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/org/report/disclose/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve(res.data.data || {});
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

export function mapXyzzListRow(row) {
  const total =
    row.total != null && String(row.total).trim() !== ""
      ? String(row.total)
      : "—";
  return {
    id: row.id,
    title: row.title != null ? String(row.title) : "—",
    totalDisplay: total,
    createTime: formatDateTime(row.createTime),
  };
}

/** 与 PC `OrganizationList.vue` representTotal：家长委员会/家长/学生代表 dblb */
const ORG_DISCLOSE_REPRESENTATIVE_DBLB = new Set(["0", "5", "4"]);

/**
 * 与 PC 查看对话框 el-alert 文案一致（按 sonDataList 原始 dblb 统计）
 * @returns {{ text: string, repPct: number }}
 */
function computeOrgDiscloseSummaryFromSonList(sonList) {
  if (!Array.isArray(sonList) || sonList.length === 0) {
    return { text: "", repPct: 0 };
  }
  const total = sonList.length;
  let repCount = 0;
  sonList.forEach((m) => {
    if (!m || typeof m !== "object") return;
    const d = m.dblb != null ? String(m.dblb).trim() : "";
    if (ORG_DISCLOSE_REPRESENTATIVE_DBLB.has(d)) {
      repCount += 1;
    }
  });
  const repPct = total > 0 ? Math.round((repCount / total) * 100) : 0;
  const text =
    `本次选用组织共${total}人，其中家长学生代表（含家长委员会代表、家长代表、学生代表）共${repCount}人，占选用组织总人数的${repPct}%`;
  return { text, repPct };
}

/**
 * XfptOrgReportDisclose + sonDataList -> 详情展示
 * @param {{ zw?: Record<string, string>, dblb?: Record<string, string> }} [dictMaps] 与 PC OrganizationList dicts 一致
 */
export function mapXyzzToDetailView(apiData, dictMaps) {
  const sons = apiData.sonDataList || [];
  const zwMap = dictMaps && dictMaps.zw;
  const dblbMap = dictMaps && dictMaps.dblb;
  const members = sons.map((s) => ({
    zw:
      s.zw != null && String(s.zw).trim()
        ? dictLabelFromMap(zwMap, s.zw)
        : "—",
    dblb:
      s.dblb != null && String(s.dblb).trim()
        ? dictLabelFromMap(dblbMap, s.dblb)
        : "—",
    xm: s.xm != null && String(s.xm).trim() ? String(s.xm) : "—",
    phone: s.phone != null && String(s.phone).trim() ? String(s.phone) : "—",
    njZy: s.njZy != null && String(s.njZy).trim() ? String(s.njZy) : "",
    bj: s.bj != null && String(s.bj).trim() ? String(s.bj) : "",
  }));
  const fileVal =
    apiData.file != null && apiData.file !== "" ? apiData.file : "";
  const { resultFile, fjPlainText } = mapFjToFileAndPlain(fileVal);
  const totalDisp =
    apiData.total != null && String(apiData.total).trim() !== ""
      ? String(apiData.total)
      : "—";
  const fromApi =
    (apiData.listSummary != null && String(apiData.listSummary).trim()) ||
    (apiData.remark != null && String(apiData.remark).trim()) ||
    "";
  const computed = computeOrgDiscloseSummaryFromSonList(sons);
  const detailBanner = fromApi || computed.text;
  const orgSummaryLowRepresentative =
    sons.length > 0 && computed.repPct < 80;
  return {
    id: apiData.id,
    title: apiData.title != null ? String(apiData.title) : "—",
    totalDisplay: totalDisp,
    jzxsDb:
      apiData.jzxsDb != null && String(apiData.jzxsDb).trim()
        ? String(apiData.jzxsDb)
        : "",
    jzxsZb:
      apiData.jzxsZb != null && String(apiData.jzxsZb).trim()
        ? String(apiData.jzxsZb)
        : "",
    detailBanner,
    orgSummaryLowRepresentative,
    members,
    resultFile,
    fjPlainText,
  };
}

/**
 * 选用组织确定选购模式 — 列表 GET /school/repmode/list（PC `api/cg/repmode.js` listRepmode）
 * teamId 与流程里班子会议 teamMeetingId 同值（PurchaseMode queryParams.teamId = formId）
 */
export function fetchQdxgmsList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamId = resolveRepmodeTeamIdForRequest(q.teamId);
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/repmode/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "选购模式列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 详情 GET /school/repmode/{id}（PC getRepmode，data 含 xfptReportMode、operList）
 */
export function fetchQdxgmsDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/repmode/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const d = res.data.data || {};
          const raw =
            d.xfptReportMode != null
              ? Object.assign({}, d.xfptReportMode, {
                operList: d.operList,
              })
              : d;
          resolve(raw);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * @param {{ cgmslx?: Record<string, string> }} [dictMaps] PC dict cgmslx
 */
export function mapQdxgmsListRow(row, dictMaps) {
  const cgmslxMap = dictMaps && dictMaps.cgmslx;
  const modeRaw = row.purchaseMode;
  return {
    id: row.id,
    title: row.title != null ? String(row.title) : "—",
    purchaseMode:
      modeRaw != null && String(modeRaw).trim()
        ? dictLabelFromMap(cgmslxMap, modeRaw)
        : "—",
    createTime: formatDateTime(row.createTime),
  };
}

/**
 * XfptReportMode：报备表文件优先 reportForm，其次 file / fj
 * @param {{ cgmslx?: Record<string, string> }} [dictMaps]
 */
export function mapQdxgmsToDetailView(apiData, dictMaps) {
  const fileVal =
    (apiData.reportForm != null && String(apiData.reportForm).trim() !== ""
      ? String(apiData.reportForm).trim()
      : "") ||
    (apiData.file != null && String(apiData.file).trim() !== ""
      ? String(apiData.file).trim()
      : "") ||
    (apiData.fj != null && String(apiData.fj).trim() !== ""
      ? String(apiData.fj).trim()
      : "");
  const { resultFile, fjPlainText } = mapFjToFileAndPlain(fileVal);
  const cgmslxMap = dictMaps && dictMaps.cgmslx;
  const modeRaw = apiData.purchaseMode;
  return {
    id: apiData.id,
    title: apiData.title != null ? String(apiData.title) : "—",
    purchaseMode:
      modeRaw != null && String(modeRaw).trim()
        ? dictLabelFromMap(cgmslxMap, modeRaw)
        : "—",
    resultFile,
    fjPlainText,
  };
}

function isPublishedRelease(val) {
  if (val == null || val === "") return false;
  if (val === true) return true;
  const s = String(val).trim().toLowerCase();
  return (
    s === "1" ||
    s === "y" ||
    s === "yes" ||
    s === "true" ||
    s === "是" ||
    s === "已发布"
  );
}

/**
 * 备选公告列表 GET /school/rwggzs/list（PC `api/cg/rwggzs.js` listRwggzs）
 */
export function fetchNoticeOpenList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamId = resolveRepmodeTeamIdForRequest(q.teamId);
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/rwggzs/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "备选公告列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 备选公告详情 GET /school/rwggzs/{id}（PC `api/cg/rwggzs.js` getRwggzs）
 */
export function fetchNoticeOpenDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/rwggzs/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve(res.data.data || {});
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * @param {{ notice_status?: Record<string, string> }} [dictMaps] PC notice_status
 */
function mapPcUploadFieldToFile(value, fallbackName) {
  const raw = firstUrlFromPcFileUploadField(value);
  if (!raw) return null;
  if (!isLikelyFileUrl(raw)) return null;
  const url = resolveFileUrl(raw);
  const seg = raw.split(/[/\\]/).pop() || fallbackName || "附件.pdf";
  const name = decodeURIComponent(seg.split("?")[0] || fallbackName || "附件.pdf");
  return { name, url };
}

function mapRwggzsSupplierRows(list) {
  if (!Array.isArray(list)) return [];
  return list.map((row, index) => ({
    name:
      row && row.name != null && String(row.name).trim() !== ""
        ? String(row.name).trim()
        : "—",
    introUrl:
      row && row.url != null && String(row.url).trim() !== ""
        ? String(row.url).trim()
        : "",
    videoUrl:
      row && row.videoUrl != null && String(row.videoUrl).trim() !== ""
        ? resolveFileUrl(String(row.videoUrl).trim())
        : "",
    introFile: mapPcUploadFieldToFile(
      row ? row.file : "",
      `企业介绍文件${index + 1}.pdf`
    ),
  }));
}

export function mapNoticeOpenListRow(row, dictMaps) {
  const published = isPublishedRelease(row.isRelease);
  const stMap = dictMaps && dictMaps.notice_status;
  const statusText = stMap
    ? dictLabelFromMap(stMap, row.isRelease)
    : published
      ? "已发布"
      : "未发布";
  return {
    id: row.id,
    title:
      row.name != null && String(row.name).trim() !== ""
        ? String(row.name).trim()
        : row.title != null && String(row.title).trim() !== ""
          ? String(row.title).trim()
          : "—",
    statusText,
    statusPublished: published,
    createTime: formatDateTime(row.createTime),
  };
}

/**
 * 企业备选公告详情：主公告文件 + 企业列表（名称、链接、视频、文件）
 * @param {{ notice_status?: Record<string, string> }} [dictMaps]
 */
export function mapNoticeOpenToDetailView(apiData, dictMaps) {
  const resultFile =
    mapPcUploadFieldToFile(apiData.file, "备选公告.pdf") ||
    mapPcUploadFieldToFile(apiData.noticeFile, "备选公告.pdf") ||
    mapPcUploadFieldToFile(apiData.otherFile, "备选公告.pdf");
  const fjPlainText = "";
  const published = isPublishedRelease(apiData.isRelease);
  const stMap = dictMaps && dictMaps.notice_status;
  const releaseText = stMap
    ? dictLabelFromMap(stMap, apiData.isRelease)
    : published
      ? "是"
      : "否";
  return {
    id: apiData.id,
    title:
      apiData.name != null && String(apiData.name).trim() !== ""
        ? String(apiData.name).trim()
        : apiData.title != null && String(apiData.title).trim() !== ""
          ? String(apiData.title).trim()
          : "—",
    releaseText,
    releaseYes: published,
    resultFile,
    fjPlainText,
    supplierList: mapRwggzsSupplierRows(apiData.supplierList),
  };
}

function pickFirstFileField(apiData, keys) {
  for (let i = 0; i < keys.length; i += 1) {
    const k = keys[i];
    const v = apiData[k];
    if (v == null || v === "") continue;
    const s = String(v).trim();
    if (s) return s;
  }
  return "";
}

function mapProcResultSonToRows(apiData, xfplMap) {
  const sons =
    apiData.sonDataList ||
    apiData.detailList ||
    apiData.purchaseDetailList ||
    apiData.teamMeetingSonList ||
    apiData.resultSonList ||
    [];
  if (!Array.isArray(sons)) return [];
  function cell(v) {
    if (v == null || v === "") return "—";
    return String(v);
  }
  return sons.map((s) => {
    const rawXfpl = s.xfpl ?? s.uniformCategory ?? s.category;
    const uniformCategory =
      rawXfpl != null && rawXfpl !== ""
        ? dictLabelFromMap(xfplMap, rawXfpl)
        : "—";
    return {
      grade: cell(s.cgnj ?? s.purchaseGrade ?? s.grade ?? s.nj),
      uniformCategory,
      setsPerStudent: cell(s.gmts ?? s.setsPerStudent ?? s.studentSets),
      priceRange: cell(s.jgqj ?? s.priceRange),
      remark: s.bz != null ? String(s.bz) : s.remark != null ? String(s.remark) : "",
    };
  });
}

/**
 * 选用组织选购结果公示 — 列表 GET /school/results/list（PC `api/school/results.js` listResults）
 * teamId 与流程一致：班子会议 teamMeetingId 优先（同 resolveRepmodeTeamIdForRequest）
 */
export function fetchProcResultDiscloseList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamId = resolveRepmodeTeamIdForRequest(q.teamId);
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/results/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "选购结果公示列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 详情 GET /school/results/{id}（PC getResults）
 */
export function fetchProcResultDiscloseDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/results/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const d = res.data.data || {};
          const nested =
            d.xfptPurchaseResultDisclose ||
            d.xfptProcurementResult ||
            d.xfptDealResult ||
            d.purchaseResult ||
            null;
          const raw =
            nested != null
              ? Object.assign({}, nested, { operList: d.operList })
              : d;
          resolve(raw);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * @param {{ cgmslx?: Record<string, string>, notice_status?: Record<string, string> }} [dictMaps]
 */
export function mapProcResultDiscloseListRow(row, dictMaps) {
  const releaseRaw =
    row.isRelease != null
      ? row.isRelease
      : row.releaseStatus != null
        ? row.releaseStatus
        : row.fbzt;
  const published = isPublishedRelease(releaseRaw);
  const stMap = dictMaps && dictMaps.notice_status;
  const statusText = stMap
    ? dictLabelFromMap(stMap, releaseRaw)
    : published
      ? "已发布"
      : "未发布";
  const modeRaw =
    row.reportMode != null && String(row.reportMode).trim() !== ""
      ? row.reportMode
      : row.purchaseMode;
  const cgmslxMap = dictMaps && dictMaps.cgmslx;
  const titleRaw =
    row.noticeTitle || row.title || row.publicityTitle || row.gsmc;
  return {
    id: row.id,
    title:
      titleRaw != null && String(titleRaw).trim()
        ? String(titleRaw).trim()
        : "—",
    purchaseMode:
      modeRaw != null && String(modeRaw).trim()
        ? dictLabelFromMap(cgmslxMap, modeRaw)
        : "—",
    createTime: formatDateTime(row.createTime),
    statusText,
    statusPublished: published,
  };
}

/**
 * 选购结果公示详情 -> 页面（成交公告、企业、采购日期、明细行、其他档案）
 * @param {{ cgmslx?: Record<string, string>, notice_status?: Record<string, string>, xfpl?: Record<string, string> }} [dictMaps]
 */
export function mapProcResultDiscloseToDetailView(apiData, dictMaps) {
  const titleRaw =
    apiData.noticeTitle ||
    apiData.title ||
    apiData.cjggmc ||
    apiData.announcementName ||
    apiData.publicityTitle;
  const title =
    titleRaw != null && String(titleRaw).trim()
      ? String(titleRaw).trim()
      : "—";

  const enterpriseRaw =
    apiData.transactionCompanyName ||
    apiData.dealEnterpriseName ||
    apiData.supplierName ||
    apiData.cjqymc ||
    apiData.transactionVendor ||
    apiData.winningVendor;
  const dealEnterpriseName =
    enterpriseRaw != null && String(enterpriseRaw).trim()
      ? String(enterpriseRaw).trim()
      : "—";

  const dateRaw =
    apiData.cgsj ||
    apiData.purchaseDate ||
    apiData.cgrq ||
    apiData.procurementDate ||
    apiData.cgTime;
  const purchaseDate =
    dateRaw != null && String(dateRaw).trim()
      ? formatDate(dateRaw)
      : "—";

  const createTime = formatDateTime(apiData.createTime);

  const dealFileVal = pickFirstFileField(apiData, [
    "noticeFile",
    "cjgg",
    "dealNotice",
    "dealAnnouncementFile",
    "transactionNotice",
    "cjggFile",
    "fj",
  ]);
  const { resultFile: dealNoticeFile, fjPlainText: dealNoticePlain } =
    mapFjToFileAndPlain(dealFileVal);

  const otherFileVal = pickFirstFileField(apiData, [
    "otherFile",
    "qtFj",
    "qtda",
    "archiveFile",
    "otherArchive",
  ]);
  const { resultFile: otherArchiveFile, fjPlainText: otherArchivePlain } =
    mapFjToFileAndPlain(otherFileVal);

  const xfplMap = dictMaps && dictMaps.xfpl;
  const purchaseDetails = mapProcResultSonToRows(apiData, xfplMap);

  return {
    id: apiData.id,
    title,
    dealEnterpriseName,
    purchaseDate,
    createTime,
    dealNoticeFile,
    dealNoticePlain,
    otherArchiveFile,
    otherArchivePlain,
    purchaseDetails,
  };
}

/**
 * 合作厂商列表 GET /school/suppliceContract/list（PC schoolSupplierBindList，参数 teamMeetingId）
 */
export function fetchContractVendorList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamMeetingId", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamMeetingId = resolveRepmodeTeamIdForRequest(
    q.teamMeetingId != null && q.teamMeetingId !== ""
      ? q.teamMeetingId
      : q.teamId
  );
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/suppliceContract/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "合作厂商列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 厂商详情 GET /school/supplierMessage/{id}（PC getSupplierInfo，id 为 supplierId）
 */
export function fetchContractVendorDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/supplierMessage/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const body = res.data;
          let d =
            body.data != null && typeof body.data === "object"
              ? body.data
              : body;
          if (
            d &&
            typeof d === "object" &&
            d.data != null &&
            typeof d.data === "object" &&
            d.supplierName == null &&
            d.supplier_name == null
          ) {
            d = d.data;
          }
          const nested =
            d.xfptVendor ||
            d.xfptManufacturer ||
            d.vendor ||
            d.manufacturer ||
            null;
          resolve(nested != null ? Object.assign({}, nested) : d);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 某合作绑定下的采购合同子表列表 GET /school/suppliceContractSon/list（PC suppliceContractSon）
 * @param {{ vendorId?: string|number, suppliceContractId?: string|number, deptId?: string, pageNum?: number, pageSize?: number }} query
 * vendorId 与 PC 侧一致时表示 school_supplice_contract 主表 id（合作绑定 id）
 */
export function fetchProcurementContractList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const bindId =
    q.suppliceContractId != null && q.suppliceContractId !== ""
      ? q.suppliceContractId
      : q.vendorId;
  if (bindId != null && bindId !== "") {
    data.suppliceContractId = bindId;
  }
  if (q.deptId != null && q.deptId !== "") {
    data.deptId = q.deptId;
  }
  const skip = new Set([
    "pageNum",
    "pageSize",
    "vendorId",
    "suppliceContractId",
    "deptId",
  ]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/suppliceContractSon/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "采购合同列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

export function mapContractVendorListRow(row) {
  const bindId =
    row.id != null ? row.id : row.suppliceContractId != null ? row.suppliceContractId : row.vendorId;
  const supplierId =
    row.supplierId != null && String(row.supplierId).trim() !== ""
      ? row.supplierId
      : row.supplier_id != null && String(row.supplier_id).trim() !== ""
        ? row.supplier_id
        : null;
  const name =
    row.supplierName ||
    row.vendorName ||
    row.csmc ||
    row.manufacturerName ||
    row.companyName ||
    row.name;
  const legal =
    row.legalRepresentative ||
    row.fddbr ||
    row.frdb ||
    row.legalPerson;
  const phone =
    row.supplierPhone ||
    row.contactPhone ||
    row.lxdh ||
    row.phone ||
    row.tel ||
    row.mobile;
  const deptId =
    row.deptId != null && String(row.deptId).trim() !== ""
      ? String(row.deptId).trim()
      : "";
  return {
    id: bindId,
    supplierId,
    deptId,
    vendorName:
      name != null && String(name).trim() ? String(name).trim() : "—",
    legalRepresentative:
      legal != null && String(legal).trim() ? String(legal).trim() : "—",
    contactPhone:
      phone != null && String(phone).trim() ? String(phone).trim() : "—",
  };
}

export function mapContractVendorToDetailView(apiData) {
  const nameRaw =
    apiData.supplierName ||
    apiData.supplier_name ||
    apiData.vendorName ||
    apiData.csmc ||
    apiData.manufacturerName ||
    apiData.companyName ||
    apiData.name;
  const vendorName =
    nameRaw != null && String(nameRaw).trim()
      ? String(nameRaw).trim()
      : "—";

  const legal =
    apiData.legalRepresentative ||
    apiData.legal_representative ||
    apiData.fddbr ||
    apiData.frdb ||
    apiData.legalPerson;
  const legalRepresentative =
    legal != null && String(legal).trim() ? String(legal).trim() : "—";

  const cap =
    apiData.registeredCapital != null
      ? apiData.registeredCapital
      : apiData.registered_capital != null
        ? apiData.registered_capital
        : apiData.zczj != null
          ? apiData.zczj
          : apiData.regCapital;
  const registeredCapital =
    cap != null && String(cap).trim() !== "" ? String(cap).trim() : "—";

  const emp =
    apiData.staffNumber != null
      ? apiData.staffNumber
      : apiData.staff_number != null
        ? apiData.staff_number
        : apiData.employeeCount != null
          ? apiData.employeeCount
          : apiData.zgrs != null
            ? apiData.zgrs
            : apiData.staffNum;
  const employeeCount =
    emp != null && String(emp).trim() !== "" ? String(emp).trim() : "—";

  const type =
    apiData.supplierType ||
    apiData.supplier_type ||
    apiData.enterpriseType ||
    apiData.qylx ||
    apiData.companyType;
  const enterpriseType =
    type != null && String(type).trim() ? String(type).trim() : "—";

  const est =
    apiData.establishedTime ||
    apiData.established_time ||
    apiData.establishDate ||
    apiData.clrq ||
    apiData.foundDate ||
    apiData.setupDate;
  const establishDate =
    est != null && String(est).trim() ? formatDate(est) : "—";

  const addr =
    apiData.supplierAddress ||
    apiData.supplier_address ||
    apiData.address ||
    apiData.csdz ||
    apiData.factoryAddress ||
    apiData.vendorAddress;
  const address =
    addr != null && String(addr).trim() ? String(addr).trim() : "—";

  const introRaw =
    apiData.supplierIntroduce ||
    apiData.supplier_introduce ||
    apiData.introduction ||
    apiData.csjj ||
    apiData.vendorProfile ||
    apiData.profile ||
    apiData.remark;
  const introduction =
    introRaw != null && String(introRaw).trim()
      ? String(introRaw).trim()
      : "";
  const introductionIsHtml =
    introduction.length > 0 && /<[^>]+>/.test(introduction);

  const qualVal = pickFirstQualificationUpload(apiData, [
    "businessLicense",
    "business_license",
    "qualificationFile",
    "qualification_file",
    "zzfj",
    "licenseFile",
    "yyzz",
    "zzFile",
    "fj",
  ]);
  const { resultFile: qualificationFile, fjPlainText: qualificationPlain } =
    mapFjToFileAndPlain(qualVal);

  return {
    id: apiData.id != null ? apiData.id : apiData.vendorId,
    vendorName,
    legalRepresentative,
    registeredCapital,
    employeeCount,
    enterpriseType,
    establishDate,
    address,
    introduction,
    introductionIsHtml,
    qualificationFile,
    qualificationPlain,
  };
}

function formatContractServicePeriod(row) {
  const single =
    row.contractDeadlineStr ||
    row.contractDeadline ||
    row.servicePeriod ||
    row.fwqx ||
    row.contractPeriod ||
    row.htqx ||
    row.term;
  if (single != null && String(single).trim()) {
    return String(single).trim();
  }
  const s =
    row.serviceStart ||
    row.htksrq ||
    row.startDate ||
    row.beginDate ||
    "";
  const e =
    row.serviceEnd || row.htjsrq || row.endDate || row.finishDate || "";
  const ss = s != null && String(s).trim() ? formatDate(s) : "";
  const ee = e != null && String(e).trim() ? formatDate(e) : "";
  if (ss && ee) return ss + " 至 " + ee;
  if (ss) return ss;
  if (ee) return ee;
  return "—";
}

export function mapProcurementContractListRow(row) {
  const name =
    row.contractName ||
    row.htmc ||
    row.name ||
    row.title;
  return {
    id: row.id,
    contractName:
      name != null && String(name).trim() ? String(name).trim() : "—",
    servicePeriod: formatContractServicePeriod(row),
  };
}

function mapEnterpriseQualificationFiles(apiData) {
  const rawList =
    apiData.zzList ||
    apiData.qualificationList ||
    apiData.enterpriseQualificationList ||
    apiData.qyzzList ||
    apiData.qualificationFileList;
  const out = [];
  if (Array.isArray(rawList)) {
    rawList.forEach((row) => {
      if (!row) return;
      const path =
        row.file ||
        row.url ||
        row.fj ||
        row.path ||
        row.attachment ||
        "";
      const t = String(path).trim();
      if (!t) return;
      const { resultFile } = mapFjToFileAndPlain(t);
      if (resultFile) {
        const n = row.fileName || row.name || row.title;
        if (n != null && String(n).trim()) {
          resultFile.name = String(n).trim();
        }
        out.push(resultFile);
      }
    });
  }
  return out;
}

/**
 * 产品质量检验 — 合作厂商列表 GET /school/suppliceInspectionReport/list（PC suppliceInspectionReportList，teamMeetingId）
 */
export function fetchQualityVendorList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamMeetingId", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamMeetingId = resolveRepmodeTeamIdForRequest(
    q.teamMeetingId != null && q.teamMeetingId !== ""
      ? q.teamMeetingId
      : q.teamId
  );
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/suppliceInspectionReport/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "质检厂商列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 某厂商绑定下的质检报告子表列表 GET /school/suppliceInspectionReportSon/list（PC getReportList，参数 reportId 为父表 id）
 */
export function fetchQualityReportSonList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  if (q.reportId != null && q.reportId !== "") {
    data.reportId = q.reportId;
  }
  const skip = new Set(["pageNum", "pageSize", "reportId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/suppliceInspectionReportSon/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "质检报告列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 质检报告子表详情 GET /school/suppliceInspectionReportSon/{id}
 */
export function fetchQualityReportDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/suppliceInspectionReportSon/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const body = res.data;
          let d =
            body.data != null && typeof body.data === "object"
              ? body.data
              : body;
          if (
            d &&
            typeof d === "object" &&
            d.data != null &&
            typeof d.data === "object" &&
            d.inspectionReportTitle == null &&
            d.inspection_report_title == null
          ) {
            d = d.data;
          }
          const nested =
            d.xfptQualityReport ||
            d.qualityReport ||
            d.report ||
            null;
          resolve(nested != null ? Object.assign({}, nested) : d);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 厂商详情 GET /school/supplierMessage/{id}（与采购合同模块一致，id 为 supplierId）
 */
export function fetchQualityVendorDetail(id) {
  return fetchContractVendorDetail(id);
}

export function mapQualityVendorListRow(row) {
  const base = mapContractVendorListRow(row);
  const bindId =
    row.id != null ? row.id : row.reportId != null ? row.reportId : base.id;
  return {
    ...base,
    /** 父表 id，用于 /suppliceInspectionReportSon/list?reportId= */
    reportId: bindId,
  };
}

/**
 * 报告类型展示：优先字典 sc_zljy_bglx；无值时与 PC 弹窗一致按行序一次/二次
 * @param {{ sc_zljy_bglx?: Record<string, string> }} [dictMaps]
 */
function mapInspectionReportTypeLabel(typeRaw, index, dictMaps) {
  const lxMap = dictMaps && dictMaps.sc_zljy_bglx;
  const trimmed =
    typeRaw != null && String(typeRaw).trim() !== ""
      ? String(typeRaw).trim()
      : "";
  if (trimmed) {
    return dictLabelFromMap(lxMap, trimmed);
  }
  if (index == null || index === "" || Number.isNaN(Number(index))) {
    return "质量检测报告";
  }
  const idx = Number(index);
  if (idx === 0) {
    return "一次质量检测报告";
  }
  if (idx === 1) {
    return "二次质量检测报告";
  }
  return "质量检测报告";
}

/**
 * 质检报告子表行 -> 列表卡片（与 PC 弹窗列 + dict 一致）
 * @param {number} [index] 行下标 0-based
 * @param {{ sc_zljy_bglx?: Record<string, string> }} [dictMaps]
 */
export function mapQualityReportListRow(row, index, dictMaps) {
  const idx = index != null && !Number.isNaN(Number(index)) ? Number(index) : 0;
  const titleRaw = row.inspectionReportTitle || row.reportTitle || row.title;
  const reportTitle =
    titleRaw != null && String(titleRaw).trim()
      ? String(titleRaw).trim()
      : "—";
  const typeRaw =
    row.inspectionReportType != null
      ? String(row.inspectionReportType).trim()
      : "";
  const reportType = mapInspectionReportTypeLabel(typeRaw, idx, dictMaps);
  const uploadTime = formatDateTime(row.createTime);
  return {
    id: row.id,
    reportTitle,
    reportType,
    uploadTime,
  };
}

/**
 * @param {{ sc_zljy_bglx?: Record<string, string> }} [dictMaps]
 */
export function mapQualityReportToDetailView(apiData, dictMaps) {
  const titleRaw =
    apiData.inspectionReportTitle ||
    apiData.inspection_report_title ||
    apiData.reportTitle ||
    apiData.bgbt ||
    apiData.title ||
    apiData.name;
  const reportTitle =
    titleRaw != null && String(titleRaw).trim()
      ? String(titleRaw).trim()
      : "—";

  const typeRaw =
    apiData.inspectionReportType ||
    apiData.inspection_report_type ||
    apiData.reportType ||
    apiData.bglx ||
    apiData.jclx ||
    apiData.type;
  const reportType = mapInspectionReportTypeLabel(
    typeRaw != null && String(typeRaw).trim() !== "" ? typeRaw : "",
    null,
    dictMaps
  );

  const uploadRaw =
    apiData.createTime ||
    apiData.uploadTime ||
    apiData.scsj ||
    apiData.updateTime;
  const uploadTime = formatDateTime(uploadRaw);

  const fileVal =
    pickFirstQualificationUpload(apiData, [
      "url",
      "reportFile",
      "fj",
      "file",
      "attachment",
      "zjbgFile",
    ]) || pickFirstFileField(apiData, ["url", "reportFile", "fj", "file", "attachment", "zjbgFile"]);
  let { resultFile: reportFile, fjPlainText: reportPlain } =
    mapFjToFileAndPlain(fileVal);
  const fn = apiData.fileName || apiData.file_name;
  if (reportFile && fn != null && String(fn).trim()) {
    reportFile.name = String(fn).trim();
  }

  return {
    id: apiData.id,
    reportTitle,
    reportType,
    uploadTime,
    reportFile,
    reportPlain,
  };
}

export function mapQualityVendorToDetailView(apiData) {
  const base = mapContractVendorToDetailView(apiData);
  const updateRaw =
    apiData.updateTime || apiData.gxsj || apiData.modifyTime;
  const updateTime = formatDateTime(updateRaw);

  let qualificationFiles = mapEnterpriseQualificationFiles(apiData);
  if (!qualificationFiles.length && base.qualificationFile) {
    qualificationFiles = [base.qualificationFile];
  }

  return {
    ...base,
    updateTime,
    qualificationFiles,
  };
}

function formatSurveyPeriod(row) {
  const single =
    row.surveyPeriod ||
    row.wjdcQzsj ||
    row.dcqj ||
    row.questionnaireRange;
  if (single != null && String(single).trim()) {
    return String(single).trim();
  }
  const s =
    row.surveyStart ||
    row.wjdcKssj ||
    row.startTime ||
    row.kssj ||
    "";
  const e =
    row.surveyEnd || row.wjdcJssj || row.endTime || row.jssj || "";
  const ss = s != null && String(s).trim() ? formatDateTime(s) : "";
  const ee = e != null && String(e).trim() ? formatDateTime(e) : "";
  if (ss !== "—" && ee !== "—") return ss + " 至 " + ee;
  if (ss !== "—") return ss;
  if (ee !== "—") return ee;
  return "—";
}

function satisfactionDetailNavTitle(surveyTypeRaw) {
  const s =
    surveyTypeRaw != null ? String(surveyTypeRaw).trim() : "";
  if (!s) return "详情";
  if (s.includes("线下")) return "线下调查详情";
  if (s.includes("线上")) return "线上调查详情";
  return "详情";
}

/**
 * 家长满意度总结报告 — 列表 GET /school/satisfactionSummarize/list（PC summarizeReportList，参数 teamMeetingId）
 */
export function fetchSatisfactionSummaryList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamMeetingId", "teamId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamMeetingId = resolveRepmodeTeamIdForRequest(
    q.teamMeetingId != null && q.teamMeetingId !== ""
      ? q.teamMeetingId
      : q.teamId
  );
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/satisfactionSummarize/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "满意度总结列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 详情 GET /school/satisfactionSummarize/{id}（PC getSummarizeReportDetail）
 */
export function fetchSatisfactionSummaryDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/satisfactionSummarize/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const body = res.data;
          let d =
            body.data != null && typeof body.data === "object"
              ? body.data
              : body;
          if (
            d &&
            typeof d === "object" &&
            d.data != null &&
            typeof d.data === "object" &&
            d.summarizeReportName == null &&
            d.summarize_report_name == null
          ) {
            d = d.data;
          }
          const nested =
            d.xfptSatisfactionSummary ||
            d.satisfactionReport ||
            d.summary ||
            null;
          resolve(nested != null ? Object.assign({}, nested) : d);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * PC `SatisfactionReport.vue` 表格：summarizeReportName / summarizeReportType / state(sc_wjzt)；
 * 仅「线上调查」展示人数、投票率、起止时间、状态；「线下调查」状态为「无」，人数等为空。
 * @param {{ sc_wjzt?: Record<string, string> }} [dictMaps]
 */
export function mapSatisfactionSummaryListRow(row, dictMaps) {
  const titleRaw =
    row.summarizeReportName ||
    row.title ||
    row.reportName ||
    row.zjbgmc ||
    row.summaryTitle;
  const title =
    titleRaw != null && String(titleRaw).trim()
      ? String(titleRaw).trim()
      : "—";

  const typeRaw =
    row.summarizeReportType ||
    row.wjdcLx ||
    row.questionnaireType ||
    row.surveyType ||
    row.zjlx ||
    row.dcLx;
  const surveyType =
    typeRaw != null && String(typeRaw).trim()
      ? String(typeRaw).trim()
      : "—";

  const isOnline = surveyType === "线上调查";
  const isOffline = surveyType === "线下调查";

  const wjztMap = dictMaps && dictMaps.sc_wjzt;

  let votedCountDisplay = "—";
  let totalCountDisplay = "—";
  let votingRateDisplay = "—";
  let surveyPeriod = "—";
  let surveyStatus = "—";

  if (isOnline) {
    votedCountDisplay =
      row.votedCount != null && row.votedCount !== ""
        ? String(row.votedCount)
        : "—";
    totalCountDisplay =
      row.count != null && row.count !== "" ? String(row.count) : "—";
    votingRateDisplay = formatVotingRate(row.votingRate);
    surveyPeriod = formatSurveyPeriod(row);
    const statusRaw =
      row.state ||
      row.wjdcZt ||
      row.surveyStatus ||
      row.dczt ||
      row.status;
    surveyStatus =
      statusRaw != null && String(statusRaw).trim() !== ""
        ? dictLabelFromMap(wjztMap, statusRaw)
        : "—";
  } else if (isOffline) {
    surveyStatus = "无";
  } else {
    const statusRaw =
      row.state ||
      row.wjdcZt ||
      row.surveyStatus ||
      row.dczt ||
      row.status;
    if (statusRaw != null && String(statusRaw).trim() !== "") {
      surveyStatus = dictLabelFromMap(wjztMap, statusRaw);
    }
  }

  return {
    id: row.id,
    title,
    surveyType,
    votedCountDisplay,
    totalCountDisplay,
    votingRateDisplay,
    surveyPeriod,
    surveyStatus,
    /** 手机端支持线上/线下都可进详情 */
    showDetailButton: true,
  };
}

/**
 * PC 线下调查详情弹窗：summarizeReportName、createTime、surveyFileUrl；线上另有状态等。
 * @param {{ sc_wjzt?: Record<string, string> }} [dictMaps]
 */
export function mapSatisfactionSummaryToDetailView(apiData, dictMaps) {
  const titleRaw =
    apiData.summarizeReportName ||
    apiData.title ||
    apiData.reportName ||
    apiData.zjbgmc ||
    apiData.summaryTitle;
  const title =
    titleRaw != null && String(titleRaw).trim()
      ? String(titleRaw).trim()
      : "—";

  const createTime = formatDateTime(apiData.createTime);

  const typeRaw =
    apiData.summarizeReportType ||
    apiData.wjdcLx ||
    apiData.questionnaireType ||
    apiData.surveyType ||
    apiData.zjlx;
  const surveyType =
    typeRaw != null && String(typeRaw).trim()
      ? String(typeRaw).trim()
      : "";

  const isOnline = surveyType === "线上调查";

  const statusRaw =
    apiData.wjdcZt ||
    apiData.surveyStatus ||
    apiData.dczt ||
    apiData.status ||
    apiData.state;
  const wjztMap = dictMaps && dictMaps.sc_wjzt;
  let surveyStatus = "";
  if (isOnline && statusRaw != null && String(statusRaw).trim() !== "") {
    surveyStatus = dictLabelFromMap(wjztMap, statusRaw);
  } else if (!isOnline && surveyType === "线下调查") {
    surveyStatus = "";
  } else if (statusRaw != null && String(statusRaw).trim() !== "") {
    surveyStatus = dictLabelFromMap(wjztMap, statusRaw);
  }

  const fileVal =
    pickFirstQualificationUpload(apiData, [
      "surveyFileUrl",
      "survey_file_url",
      "surveyResultFile",
      "dcjgFj",
      "dcjgFile",
      "resultFile",
    ]) ||
    pickFirstFileField(apiData, [
      "surveyFileUrl",
      "survey_file_url",
      "surveyResultFile",
      "dcjgFj",
      "dcjgFile",
      "resultFile",
      "fj",
      "attachment",
    ]);
  let { resultFile, fjPlainText } = mapFjToFileAndPlain(fileVal);

  return {
    id: apiData.id,
    title,
    createTime,
    surveyType,
    surveyStatus,
    detailNavTitle: satisfactionDetailNavTitle(surveyType),
    resultFile,
    fjPlainText,
    questId:
      apiData.questId != null && String(apiData.questId).trim() !== ""
        ? String(apiData.questId).trim()
        : apiData.questionBusinessId != null &&
            String(apiData.questionBusinessId).trim() !== ""
          ? String(apiData.questionBusinessId).trim()
          : apiData.questionId != null && String(apiData.questionId).trim() !== ""
            ? String(apiData.questionId).trim()
            : "",
  };
}

/**
 * 采购信息报备 — 列表 GET /school/summary/list（PC `api/school/summary.js` listSummary，通常带 teamId）
 */
export function fetchProcInfoList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamId", "teamMeetingId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamId = resolveRepmodeTeamIdForRequest(
    q.teamId != null && q.teamId !== "" ? q.teamId : q.teamMeetingId
  );
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/summary/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "采购信息报备列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 采购信息报备详情 GET `/school/summary/{id}`（与 PC `src/api/school/summary.js` getSummary 完全一致）
 * RuoYi 结构：`{ code, msg, data }`，业务实体在 `data`；若同时存在嵌套对象，与 PC 一致合并顶层 + 嵌套字段。
 */
export function fetchProcInfoDetail(id) {
  const sid = id != null ? String(id).trim() : "";
  return new Promise((resolve, reject) => {
    if (!sid) {
      reject(new Error("缺少记录 id"));
      return;
    }
    uni.request({
      url: `${baseUrl()}/school/summary/${encodeURIComponent(sid)}`,
      method: "GET",
      success: (res) => {
        const body = res.data;
        if (res.statusCode === 200 && body && body.code === 200) {
          let d = body.data;
          if (typeof d === "string") {
            try {
              d = JSON.parse(d);
            } catch (e) {
              d = {};
            }
          }
          if (!d || typeof d !== "object") {
            d = {};
          }
          const nested =
            d.xfptProcurementInfo ||
            d.procurementInfo ||
            d.cgxx ||
            null;
          const merged =
            nested != null && typeof nested === "object"
              ? Object.assign({}, d, nested)
              : d;
          resolve(merged);
        } else {
          const msg =
            (body && (body.msg || body.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

function fmtMoneyOrInt(val) {
  if (val == null || val === "") return "—";
  const s = String(val).trim();
  return s || "—";
}

/**
 * PC FileUpload 字段解析：JSON 数组、对象、纯路径或逗号分隔多文件（与 `Caigoubaobei` inviteFile、`procurementWork` file 等一致）
 * @param {string[]} keys 按优先级尝试的字段名
 */
function pickPcFileUploadStylePath(apiData, keys) {
  let v =
    pickFirstQualificationUpload(apiData, keys) ||
    pickFirstFileField(apiData, keys);
  if (v == null || v === "") {
    return "";
  }
  if (typeof v !== "string") {
    const u = firstUrlFromPcFileUploadField(v);
    return u && String(u).trim() ? String(u).trim() : "";
  }
  const t = String(v).trim();
  if (!t) {
    return "";
  }
  if (t.includes(",")) {
    return t.split(",")[0].trim();
  }
  return t;
}

/**
 * 采购公告/成交公告附件：与 PC `Caigoubaobei` FileUpload 一致，优先用 JSON 里的 `name`，再退回路径截取
 */
function mapProcurementAttachField(apiData, keys) {
  for (let i = 0; i < keys.length; i += 1) {
    const k = keys[i];
    const v = apiData[k];
    if (v == null || v === "") continue;
    let pathStr = "";
    let jsonName = "";
    if (typeof v === "string") {
      const s = v.trim();
      if (s.startsWith("[")) {
        try {
          const arr = JSON.parse(s);
          if (Array.isArray(arr) && arr.length && arr[0] && typeof arr[0] === "object") {
            const f = arr[0];
            const u = f.url || f.fileUrl || f.path || f.file;
            if (u != null && String(u).trim()) {
              pathStr = String(u).trim();
              const jn = f.name || f.fileName || f.originalFilename;
              if (jn != null && String(jn).trim()) {
                jsonName = String(jn).trim();
              }
            }
          }
        } catch (e) {
          /* fall through */
        }
      }
    } else if (Array.isArray(v) && v[0] && typeof v[0] === "object") {
      const f = v[0];
      const u = f.url || f.fileUrl || f.path || f.file;
      if (u != null && String(u).trim()) {
        pathStr = String(u).trim();
        const jn = f.name || f.fileName || f.originalFilename;
        if (jn != null && String(jn).trim()) {
          jsonName = String(jn).trim();
        }
      }
    } else if (typeof v === "object" && v !== null) {
      const u = v.url || v.fileUrl || v.path || v.file;
      if (u != null && String(u).trim()) {
        pathStr = String(u).trim();
        const jn = v.name || v.fileName || v.originalFilename;
        if (jn != null && String(jn).trim()) {
          jsonName = String(jn).trim();
        }
      }
    }
    if (pathStr) {
      if (pathStr.includes(",")) {
        pathStr = pathStr.split(",")[0].trim();
      }
      const { resultFile, fjPlainText } = mapFjToFileAndPlain(pathStr);
      if (resultFile && jsonName) {
        resultFile.name = jsonName;
      }
      return { resultFile, fjPlainText };
    }
    const fallbackPath = pickPcFileUploadStylePath(apiData, [k]);
    if (fallbackPath) {
      return mapFjToFileAndPlain(fallbackPath);
    }
  }
  const pathOnly = pickPcFileUploadStylePath(apiData, keys);
  return mapFjToFileAndPlain(pathOnly);
}

export function mapProcInfoListRow(row) {
  const school =
    row.schoolName ||
    row.title ||
    row.xxmc ||
    row.deptName ||
    row.school ||
    row.jgmc;
  const schoolName =
    school != null && String(school).trim()
      ? String(school).trim()
      : "—";
  const zje =
    row.totalAmount != null && row.totalAmount !== ""
      ? String(row.totalAmount)
      : row.money != null && row.money !== ""
        ? String(row.money)
        : row.zje != null && row.zje !== ""
          ? String(row.zje)
          : "—";
  return {
    id: row.id,
    schoolName,
    purchaseDate: formatDate(
      row.buyTime || row.purchaseDate || row.cgrq || row.cgTime
    ),
    totalAmountDisplay: zje,
  };
}

/**
 * 采购信息报备详情展示（PC `Caigoubaobei.vue` 字段：schoolName、inviteFile、winBidFile、enterprise、dj、count、money、buyTime）
 */
export function mapProcInfoToDetailView(apiData) {
  const school =
    apiData.schoolName ||
    apiData.title ||
    apiData.xxmc ||
    apiData.deptName ||
    apiData.school ||
    apiData.jgmc;
  const schoolName =
    school != null && String(school).trim()
      ? String(school).trim()
      : "—";

  const inviteKeys = [
    "inviteFile",
    "cggg",
    "cgNotice",
    "procurementNotice",
    "zbcggg",
    "cgggFile",
    "noticeFile",
    "cgGg",
  ];
  const winKeys = [
    "winBidFile",
    "cjgg",
    "dealNotice",
    "transactionNotice",
    "cjggFile",
    "zbtzs",
    "winningNotice",
    "cjGg",
  ];
  const { resultFile: procurementNoticeFile, fjPlainText: cgNoticePlain } =
    mapProcurementAttachField(apiData, inviteKeys);
  const { resultFile: dealNoticeFile, fjPlainText: dealNoticePlain } =
    mapProcurementAttachField(apiData, winKeys);

  const ent =
    apiData.enterprise ||
    apiData.winningEnterprise ||
    apiData.cjgy ||
    apiData.dealEnterprise ||
    apiData.supplierName ||
    apiData.cjqymc;
  const winningEnterprise =
    ent != null && String(ent).trim() ? String(ent).trim() : "—";

  const dj =
    apiData.unitPrice != null
      ? apiData.unitPrice
      : apiData.dj != null
        ? apiData.dj
        : apiData.price;
  const unitPrice = fmtMoneyOrInt(dj);

  const dgrs =
    apiData.orderCount != null
      ? apiData.orderCount
      : apiData.count != null
        ? apiData.count
        : apiData.dgrs != null
          ? apiData.dgrs
          : apiData.subscriberCount;
  const orderCount = fmtMoneyOrInt(dgrs);

  const zje =
    apiData.totalAmount != null
      ? apiData.totalAmount
      : apiData.money != null
        ? apiData.money
        : apiData.zje != null
          ? apiData.zje
          : apiData.totalMoney;
  const totalAmount = fmtMoneyOrInt(zje);

  const dateRaw =
    apiData.buyTime ||
    apiData.purchaseDate ||
    apiData.cgrq ||
    apiData.cgTime ||
    apiData.cgDate;
  const purchaseDate =
    dateRaw != null && String(dateRaw).trim()
      ? formatDate(dateRaw)
      : "—";

  return {
    id: apiData.id,
    schoolName,
    procurementNoticeFile,
    cgNoticePlain,
    dealNoticeFile,
    dealNoticePlain,
    winningEnterprise,
    unitPrice,
    orderCount,
    totalAmount,
    purchaseDate,
  };
}

/**
 * 学校校服采购工作总结 — 列表 GET /school/cglcgzzj/list（PC `api/cg/cglcgzzj.js` listCglcgzzj）
 */
export function fetchProcWorkSummaryList(query) {
  const q = query || {};
  const data = {
    pageNum: q.pageNum != null ? q.pageNum : 1,
    pageSize: q.pageSize != null ? q.pageSize : 20,
  };
  const skip = new Set(["pageNum", "pageSize", "teamId", "teamMeetingId"]);
  Object.keys(q).forEach((k) => {
    if (skip.has(k)) return;
    const v = q[k];
    if (v != null && v !== "") data[k] = v;
  });
  data.teamId = resolveRepmodeTeamIdForRequest(
    q.teamId != null && q.teamId !== "" ? q.teamId : q.teamMeetingId
  );
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/cglcgzzj/list`,
      method: "GET",
      data,
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve({
            total: res.data.total != null ? res.data.total : 0,
            rows: Array.isArray(res.data.rows) ? res.data.rows : [],
          });
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) ||
            "校服采购工作总结列表加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

/**
 * 工作总结详情 GET /school/cglcgzzj/{id}（PC getCglcgzzj）
 */
export function fetchProcWorkSummaryDetail(id) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${baseUrl()}/school/cglcgzzj/${id}`,
      method: "GET",
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          let d = res.data.data;
          if (typeof d === "string") {
            try {
              d = JSON.parse(d);
            } catch (e) {
              d = {};
            }
          }
          if (!d || typeof d !== "object") {
            d = {};
          }
          const nested =
            d.xfptProcWorkSummary ||
            d.workSummary ||
            d.summary ||
            null;
          resolve(nested != null ? Object.assign({}, nested) : d);
        } else {
          const msg =
            (res.data && (res.data.msg || res.data.message)) || "详情加载失败";
          reject(new Error(msg));
        }
      },
      fail: (e) => reject(e || new Error("网络错误")),
    });
  });
}

export function mapProcWorkSummaryListRow(row) {
  /** PC `procurementWork.vue` 表格列 prop="title" */
  const nameRaw =
    row.title || row.name || row.mc || row.summaryName || row.zjmc;
  const name =
    nameRaw != null && String(nameRaw).trim()
      ? String(nameRaw).trim()
      : "—";
  return {
    id: row.id,
    name,
    createTime: formatDateTime(row.createTime),
  };
}

export function mapProcWorkSummaryToDetailView(apiData) {
  /** PC `procurementWork.vue`：名称 title，附件 file（FileUpload） */
  const nameRaw =
    apiData.title || apiData.name || apiData.mc || apiData.summaryName;
  const name =
    nameRaw != null && String(nameRaw).trim()
      ? String(nameRaw).trim()
      : "—";

  const fileVal = pickPcFileUploadStylePath(apiData, [
    "file",
    "attachment",
    "fj",
    "summaryFile",
    "zjwd",
    "document",
  ]);
  const { resultFile, fjPlainText } = mapFjToFileAndPlain(fileVal);

  return {
    id: apiData.id,
    name,
    resultFile,
    fjPlainText,
  };
}
