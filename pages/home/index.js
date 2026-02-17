/**
 * Rust服务器查询首页
 * 篝火风格设计
 */
import request from '~/api/request';

const API_BASE = '/api';

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
    const systemInfo = wx.getSystemInfoSync()
    this.setData({
      statusBarHeight: systemInfo.statusBarHeight
    })
    this.loadServerList()
  },

  onShow() {
    this.loadServerList()
  },

  onPullDownRefresh() {
    this.loadServerList().then(() => {
      wx.stopPullDownRefresh()
    })
  },

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

      console.log('请求服务器列表:', API_BASE + '/servers', params)
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
        errorMsg: '请求失败: ' + (error.errMsg || '请检查后端是否启动')
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  onSearchChange(e) {
    this.setData({ searchValue: e.detail.value })
  },

  onSearch() {
    this.loadServerList()
  },

  onRegionFilter(e) {
    const region = e.currentTarget.dataset.region
    this.setData({ currentRegion: region })
    this.loadServerList()
  },

  onOfficialFilter() {
    this.setData({
      isOfficial: this.data.isOfficial === '1' ? '' : '1',
      isModded: ''
    })
    this.loadServerList()
  },

  onModdedFilter() {
    this.setData({
      isModded: this.data.isModded === '1' ? '' : '1',
      isOfficial: ''
    })
    this.loadServerList()
  },

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

  onAddServer() {
    this.setData({
      showAddModal: true,
      newServerIp: '',
      newServerPort: '28015'
    })
  },

  closeModal() {
    this.setData({ showAddModal: false })
  },

  preventClose() {},

  onIpInput(e) {
    this.setData({ newServerIp: e.detail.value })
  },

  onPortInput(e) {
    this.setData({ newServerPort: e.detail.value || '28015' })
  },

  async confirmAddServer() {
    const { newServerIp, newServerPort } = this.data

    if (!newServerIp) {
      wx.showToast({ title: '请输入IP地址', icon: 'none' })
      return
    }

    wx.showLoading({ title: '查询中...', mask: true })

    try {
      console.log('A2S查询:', newServerIp, newServerPort)
      const queryRes = await request(`${API_BASE}/servers/query`, 'POST', {
        ip: newServerIp,
        port: newServerPort || '28015'
      })
      console.log('A2S查询结果:', queryRes)

      if (queryRes.code === 200 && queryRes.data) {
        const info = queryRes.data
        wx.hideLoading()

        wx.showModal({
          title: '查询成功',
          content: `服务器: ${info.name}\n地图: ${info.map}\n玩家: ${info.players}/${info.maxPlayers}\n\n是否添加到列表?`,
          confirmText: '添加',
          success: async (res) => {
            if (res.confirm) {
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
                  wx.showToast({ title: addRes.message || '添加失败', icon: 'none' })
                }
              } catch (err) {
                wx.hideLoading()
                wx.showToast({ title: '添加失败', icon: 'none' })
              }
            }
          }
        })
      } else {
        wx.hideLoading()
        wx.showToast({ title: queryRes.message || '无法连接服务器', icon: 'none', duration: 2000 })
      }
    } catch (error) {
      wx.hideLoading()
      console.error('查询服务器失败:', error)
      wx.showToast({ title: '查询失败，请检查网络', icon: 'none', duration: 2000 })
    }
  },

  onRetry() {
    this.loadServerList()
  }
})
