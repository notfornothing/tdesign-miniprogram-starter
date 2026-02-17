/**
 * Rust服务器查询页面
 * 篝火风格设计
 */
import request from '~/api/request';

const API_BASE = '/api';  // 相对路径，baseUrl在config.js中配置

Page({
  data: {
    statusBarHeight: 20,
    searchValue: '',
    currentRegion: '',
    isOfficial: '',
    isModded: '',
    loading: false,
    serverList: [],
    showAddModal: false,
    newServerIp: '',
    newServerPort: '28015',
    errorMsg: ''
  },

  onLoad() {
    // 获取状态栏高度
    const systemInfo = wx.getSystemInfoSync()
    this.setData({
      statusBarHeight: systemInfo.statusBarHeight
    })

    // 加载服务器列表
    this.loadServerList()
  },

  onShow() {
    // 每次显示页面时刷新数据
    this.loadServerList()
  },

  onPullDownRefresh() {
    this.loadServerList().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  /**
   * 加载服务器列表
   */
  async loadServerList() {
    this.setData({ loading: true, errorMsg: '' })

    try {
      const params = {
        pageNum: '1',
        pageSize: '50'
      }

      if (this.data.currentRegion) {
        params.region = this.data.currentRegion
      }
      if (this.data.isOfficial) {
        params.isOfficial = this.data.isOfficial
      }
      if (this.data.isModded) {
        params.isModded = this.data.isModded
      }
      if (this.data.searchValue) {
        params.keyword = this.data.searchValue
      }

      console.log('请求服务器列表:', `${API_BASE}/servers`, params)
      const res = await request(`${API_BASE}/servers`, 'GET', params)
      console.log('服务器列表响应:', res)

      if (res.code === 200 && res.data) {
        this.setData({
          serverList: res.data.list || []
        })
      } else {
        this.setData({
          errorMsg: res.message || '获取数据失败'
        })
      }
    } catch (error) {
      console.error('加载服务器列表失败:', error)
      this.setData({
        errorMsg: '请求失败: ' + (error.errMsg || error.message || '未知错误')
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 搜索输入变化
   */
  onSearchChange(e) {
    this.setData({
      searchValue: e.detail.value
    })
  },

  /**
   * 执行搜索
   */
  onSearch() {
    this.loadServerList()
  },

  /**
   * 地区筛选
   */
  onRegionFilter(e) {
    const region = e.currentTarget.dataset.region
    this.setData({
      currentRegion: region
    })
    this.loadServerList()
  },

  /**
   * 官方服筛选
   */
  onOfficialFilter() {
    this.setData({
      isOfficial: this.data.isOfficial === '1' ? '' : '1',
      isModded: ''
    })
    this.loadServerList()
  },

  /**
   * 模组服筛选
   */
  onModdedFilter() {
    this.setData({
      isModded: this.data.isModded === '1' ? '' : '1',
      isOfficial: ''
    })
    this.loadServerList()
  },

  /**
   * 点击服务器卡片
   */
  onServerTap(e) {
    const serverId = e.currentTarget.dataset.id
    const server = this.data.serverList.find(s => s.id === serverId)

    if (server) {
      wx.showModal({
        title: server.name,
        content: `IP: ${server.ip}:${server.port}\n玩家: ${server.players}/${server.maxPlayers}\n地图: ${server.mapName || '未知'}`,
        showCancel: true,
        cancelText: '复制地址',
        confirmText: '知道了',
        success: (res) => {
          if (res.cancel) {
            wx.setClipboardData({
              data: `client.connect ${server.ip}:${server.port}`,
              success: () => {
                wx.showToast({ title: '已复制', icon: 'success' })
              }
            })
          }
        }
      })
    }
  },

  /**
   * 显示添加服务器弹窗
   */
  onAddServer() {
    this.setData({
      showAddModal: true,
      newServerIp: '',
      newServerPort: '28015'
    })
  },

  /**
   * 关闭弹窗
   */
  closeModal() {
    this.setData({
      showAddModal: false
    })
  },

  /**
   * 阻止冒泡
   */
  preventClose() {},

  /**
   * IP输入
   */
  onIpInput(e) {
    this.setData({
      newServerIp: e.detail.value
    })
  },

  /**
   * 端口输入
   */
  onPortInput(e) {
    this.setData({
      newServerPort: e.detail.value || '28015'
    })
  },

  /**
   * 确认添加服务器
   */
  async confirmAddServer() {
    const { newServerIp, newServerPort } = this.data

    if (!newServerIp) {
      wx.showToast({
        title: '请输入IP地址',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '查询中...', mask: true })

    try {
      // 先查询服务器(A2S协议)
      console.log('A2S查询:', newServerIp, newServerPort)
      const queryRes = await request(`${API_BASE}/servers/query`, 'POST', {
        ip: newServerIp,
        port: newServerPort || '28015'
      })
      console.log('A2S查询结果:', queryRes)

      if (queryRes.code === 200 && queryRes.data) {
        // 查询成功，显示服务器信息
        const info = queryRes.data
        wx.hideLoading()

        wx.showModal({
          title: '查询成功',
          content: `服务器: ${info.name}\n地图: ${info.map}\n玩家: ${info.players}/${info.maxPlayers}\n\n是否添加到列表?`,
          confirmText: '添加',
          success: async (res) => {
            if (res.confirm) {
              // 添加到数据库
              wx.showLoading({ title: '添加中...', mask: true })
              try {
                const addRes = await request(`${API_BASE}/servers`, 'POST', {
                  ip: newServerIp,
                  port: newServerPort || '28015'
                })
                wx.hideLoading()

                if (addRes.code === 200) {
                  wx.showToast({ title: '添加成功', icon: 'success' })
                  this.closeModal()
                  this.loadServerList()
                } else {
                  wx.showToast({
                    title: addRes.message || '添加失败',
                    icon: 'none'
                  })
                }
              } catch (err) {
                wx.hideLoading()
                wx.showToast({
                  title: '添加失败',
                  icon: 'none'
                })
              }
            }
          }
        })
      } else {
        wx.hideLoading()
        wx.showToast({
          title: queryRes.message || '无法连接服务器',
          icon: 'none',
          duration: 2000
        })
      }
    } catch (error) {
      wx.hideLoading()
      console.error('查询服务器失败:', error)
      wx.showToast({
        title: '查询失败，请检查网络',
        icon: 'none',
        duration: 2000
      })
    }
  },

  /**
   * 返回上一页
   */
  goBack() {
    wx.navigateBack({
      fail: () => {
        wx.switchTab({
          url: '/pages/home/index'
        })
      }
    })
  },

  /**
   * 重试
   */
  onRetry() {
    this.loadServerList()
  }
})
