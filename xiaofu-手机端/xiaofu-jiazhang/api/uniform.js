import request from '@/utils/request'

function normalizeGoods(item = {}) {
  return {
    ...item,
    id: item.id || item.goodsId,
    uniformId: item.id || item.goodsId,
    name: item.goodsName || item.name || '校服',
    uniformName: item.goodsName || item.name || '校服',
    image: item.mainImage || item.coverImage || item.image || '/static/qjxf.png',
    price: item.salePrice || item.price || item.minPrice || 0,
    salePrice: item.salePrice || item.price || item.minPrice || 0,
    salesCount: item.saleCount || item.salesCount || item.buyCount || 0
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
