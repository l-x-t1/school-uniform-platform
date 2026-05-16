const isH5 = typeof window !== 'undefined' && window.location
const gatewayHost = isH5 ? window.location.hostname : '127.0.0.1'
const gatewayOrigin = `http://${gatewayHost}:8080`

const config = {
  baseUrl: gatewayOrigin,
  uploadUrl: `${gatewayOrigin}/common/upload`,
  tokenKey: 'token',
  tokenPrefix: 'Bearer ',
  enableMockFallback: true
}

export default config
