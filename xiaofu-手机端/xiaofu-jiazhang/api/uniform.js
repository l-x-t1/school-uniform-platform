import request from '@/utils/request'

function normalizeGoods(item = {}) {
  const id = item.id || item.goodsId
  const mainImage = item.mainImage || item.coverImage || item.image || ''

  return {
    ...item,
    id,
    goodsId: id,
    uniformId: id,
    name: item.goodsName || item.name || '校服',
    uniformName: item.goodsName || item.name || '校服',
    goodsName: item.goodsName || item.name || '校服',
    image: mainImage,
    mainImage,
    price: item.salePrice || item.price || item.minPrice || '',
    salePrice: item.salePrice || item.price || item.minPrice || '',
    salesCount: item.saleCount || item.salesCount || item.buyCount || '',
    noticeId: item.noticeId || null,
    purchaseStartTime: item.purchaseStartTime || '',
    purchaseEndTime: item.purchaseEndTime || '',
    categoryName: item.categoryName || '',
    goodsCode: item.goodsCode || '',
    supplierId: item.supplierId || '',
    supplierName: item.supplierName || '',
    brandName: item.brandName || '',
    saleType: item.saleType || '',
    deptId: item.deptId || item.schoolId || '',
    schoolId: item.schoolId || item.deptId || '',
    schoolName: item.schoolName || '',
    publishStatus: item.publishStatus || '',
    publishTime: item.publishTime || '',
    detailContent: item.detailContent || '',
    detailImagesJson: item.detailImagesJson || '',
    status: item.status || ''
  }
}

function resolveCurrentStudent(params = {}) {
  const selectedStudent = uni.getStorageSync('selectedStudent') || {}
  const studentList = uni.getStorageSync('studentList') || []
  const fallbackStudent = studentList[0] || {}
  const selectedStudentId = selectedStudent.id || selectedStudent.studentId || ''

  if (params.studentId) {
    const matchedStudent = studentList.find((item) =>
      String(item.id || item.studentId || '') === String(params.studentId)
    )
    if (matchedStudent) {
      return matchedStudent
    }
  }

  if (selectedStudentId) {
    const matchedSelectedStudent = studentList.find((item) =>
      String(item.id || item.studentId || '') === String(selectedStudentId)
    )
    if (matchedSelectedStudent) {
      return matchedSelectedStudent
    }
  }

  return selectedStudentId && studentList.length === 0 ? selectedStudent : fallbackStudent
}

function resolveCurrentSchoolId(params = {}) {
  const currentStudent = resolveCurrentStudent(params)
  const schoolId = Number(
    params.schoolId ||
    currentStudent.schoolId ||
    currentStudent.shcoolId ||
    currentStudent.deptId ||
    0
  )

  return Number.isNaN(schoolId) || schoolId <= 0 ? '' : schoolId
}

function resolveCurrentSimId(params = {}) {
  return `${params.simId || uni.getStorageSync('simId') || ''}`.trim()
}

function buildGoodsParams(params = {}) {
  const schoolId = resolveCurrentSchoolId(params)
  const simId = resolveCurrentSimId(params)
  const query = {
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    goodsName: params.keyword || params.goodsName || '',
    saleType: params.saleType || '',
    groupPurchasable: '1',
    status: '1'
  }

  if (simId) {
    query.simId = simId
  }
  if (schoolId) {
    query.schoolId = schoolId
  }

  return query
}

export function listUniforms(params = {}) {
  return request({
    url: '/school/goods/list',
    method: 'GET',
    params: buildGoodsParams(params)
  }).then((res) => ({
    ...res,
    rows: (res.rows || []).map(normalizeGoods)
  }))
}

export function getUniform(id) {
  const schoolId = resolveCurrentSchoolId()
  const simId = resolveCurrentSimId()
  return request({
    url: `/school/goods/${id}`,
    method: 'GET',
    params: {
      simId,
      schoolId
    }
  }).then((res) => ({
    ...res,
    data: normalizeGoods(res.data || res)
  }))
}

export function listUniformCategories() {
  return Promise.resolve({
    code: 200,
    rows: [
      { id: 'all', name: '全部' },
      { id: 'daily', name: '日常校服' },
      { id: 'sport', name: '运动校服' },
      { id: 'winter', name: '冬季校服' },
      { id: 'summer', name: '夏季校服' }
    ]
  })
}
