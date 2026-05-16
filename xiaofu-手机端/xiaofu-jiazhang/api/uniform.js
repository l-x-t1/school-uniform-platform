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

function buildGoodsParams(params = {}) {
  return {
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    goodsName: params.keyword || params.goodsName || '',
    saleType: params.saleType || '',
    status: '1'
  }
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
  return request({
    url: `/school/goods/${id}`,
    method: 'GET'
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
