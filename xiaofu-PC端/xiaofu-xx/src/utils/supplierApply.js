const STATUS_TAG_MAP = {
  未报名: "info",
  待提交: "info",
  待审核: "warning",
  已通过: "success",
  已驳回: "danger",
  已撤回: ""
}

const MATERIAL_LABEL_MAP = {
  BUSINESS_LICENSE: "营业执照",
  PRODUCTION_LICENSE: "生产许可",
  QUALITY_REPORT: "质检报告",
  SAMPLE_IMAGE: "样衣图",
  ATTACHMENT: "补充附件",
  LEGACY_FILE: "历史附件"
}

export function getApplyDisplayStatus(row = {}) {
  if (row.statusText) {
    return row.statusText
  }
  if (String(row.applyStatus) === "2") {
    return "已撤回"
  }
  if (String(row.qualificationStatus) === "1") {
    return "已通过"
  }
  if (String(row.qualificationStatus) === "2") {
    return "已驳回"
  }
  if (String(row.applyStatus) === "1") {
    return "待审核"
  }
  if (String(row.applyStatus) === "0") {
    return "待提交"
  }
  return "未报名"
}

export function getApplyStatusTag(statusText) {
  return STATUS_TAG_MAP[statusText] || "info"
}

export function normalizeApplyMaterials(rawValue) {
  if (!rawValue) {
    return []
  }
  try {
    const parsed = typeof rawValue === "string" ? JSON.parse(rawValue) : rawValue
    if (!Array.isArray(parsed)) {
      return []
    }
    if (parsed.length && typeof parsed[0] === "string") {
      return parsed.map((item, index) => ({
        type: `LEGACY_FILE_${index + 1}`,
        label: MATERIAL_LABEL_MAP.LEGACY_FILE,
        required: false,
        source: "LEGACY",
        fileUrls: item ? [item] : [],
        snapshotTime: ""
      }))
    }
    return parsed.map((item, index) => {
      const fileUrls = Array.isArray(item.fileUrls)
        ? item.fileUrls.filter(Boolean)
        : item.fileUrls
          ? String(item.fileUrls).split(",").filter(Boolean)
          : []
      const type = item.type || `UNKNOWN_${index + 1}`
      return {
        type,
        label: item.label || MATERIAL_LABEL_MAP[type] || "附件",
        required: Boolean(item.required),
        source: item.source || "",
        fileUrls,
        snapshotTime: item.snapshotTime || ""
      }
    })
  } catch (error) {
    return String(rawValue)
      .split(",")
      .map((item) => item.trim())
      .filter(Boolean)
      .map((item, index) => ({
        type: `LEGACY_FILE_${index + 1}`,
        label: MATERIAL_LABEL_MAP.LEGACY_FILE,
        required: false,
        source: "LEGACY",
        fileUrls: [item],
        snapshotTime: ""
      }))
  }
}

export function getMaterialUrls(materials, type) {
  const material = normalizeApplyMaterials(materials).find((item) => item.type === type)
  return material ? material.fileUrls.join(",") : ""
}
