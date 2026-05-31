<template>
  <el-image
    :src="`${realSrc}`"
    fit="cover"
    :style="`width:${realWidth};height:${realHeight};`"
    :preview-src-list="realSrcList"
    :class="{ 'is-hover-zoom': hoverScale }"
  >
    <div slot="error" class="image-slot">
      <i class="el-icon-picture-outline" />
    </div>
  </el-image>
</template>

<script>
export default {
  name: 'ImagePreview',
  props: {
    src: {
      type: String,
      default: ''
    },
    width: {
      type: [Number, String],
      default: ''
    },
    height: {
      type: [Number, String],
      default: ''
    },
    baseUrl: {
      type: String,
      default: process.env.VUE_APP_IMAGE_BASE_API
    },
    hoverScale: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    realSrc() {
      if (!this.src) {
        return ''
      }
      const realSrc = this.normalizeSrc(this.src.split(',')[0])
      return realSrc
    },
    realSrcList() {
      if (!this.src) {
        return []
      }
      const realSrcList = this.src.split(',')
      const srcList = []
      realSrcList.forEach(item => {
        const realSrc = this.normalizeSrc(item)
        if (realSrc) {
          srcList.push(realSrc)
        }
      })
      return srcList
    },
    realWidth() {
      return typeof this.width === 'string' ? this.width : `${this.width}px`
    },
    realHeight() {
      return typeof this.height === 'string' ? this.height : `${this.height}px`
    }
  },
  methods: {
    normalizeSrc(src) {
      const realSrc = String(src || '').trim()
      if (!realSrc) {
        return ''
      }
      if (/^(https?:)?\/\//.test(realSrc) || /^(data|blob):/.test(realSrc)) {
        return realSrc
      }
      if (!this.baseUrl) {
        return realSrc
      }
      return `${this.baseUrl.replace(/\/$/, '')}/${realSrc.replace(/^\//, '')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.el-image {
  display: inline-flex;
  border-radius: 5px;
  overflow: hidden;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 1px #ccc;

  :deep(.el-image__inner) {
    transition: all 0.3s;
    cursor: pointer;
  }

  :deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }

  &.is-hover-zoom :deep(.el-image__inner:hover) {
    transform: scale(1.2);
    transform-origin: center center;
  }
}
</style>
