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
    newServerPort: '28015'
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
    this.setData({ loading: true })

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

      const res = await request(`${API_BASE}/servers`, 'GET', params)

      if (res.code === 200 && res.data) {
        this.setData({
          serverList: res.data.list || []
        })
      }
    } catch (error) {
      console.error('加载服务器列表失败:', error)
      // 使用mock数据
      this.setData({
        serverList: this.getMockData()
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 获取Mock数据
   */
  getMockData() {
    return [
      {
        id: '1001',
        name: 'Rusticated.com - Main',
        ip: '47.115.230.101',
        port: '28015',
        region: 'asia',
        mapName: 'Procedural Map',
        mapSize: '4000',
        players: '187',
        maxPlayers: '300',
        isOfficial: '0',
        isModded: '0',
        gatherRate: '1',
        status: 'online',
        ping: '35'
      },
      {
        id: '1002',
        name: 'Facepunch Singapore',
        ip: '103.62.49.103',
        port: '28015',
        region: 'asia',
        mapName: 'Procedural Map',
        mapSize: '4500',
        players: '245',
        maxPlayers: '400',
        isOfficial: '1',
        isModded: '0',
        gatherRate: '1',
        status: 'online',
        ping: '28'
      },
      {
        id: '1003',
        name: 'Rustoria.co - 3x Solo/Duo/Trio',
        ip: '45.88.228.91',
        port: '28015',
        region: 'eu',
        mapName: 'Procedural Map',
        mapSize: '3500',
        players: '156',
        maxPlayers: '250',
        isOfficial: '0',
        isModded: '1',
        gatherRate: '3',
        status: 'online',
        ping: '120'
      },
      {
        id: '1004',
        name: 'GGEZ.RIP - 5x MAX 4',
        ip: '185.217.59.43',
        port: '28015',
        region: 'eu',
        mapName: 'Procedural Map',
        mapSize: '3000',
        players: '98',
        maxPlayers: '200',
        isOfficial: '0',
        isModded: '1',
        gatherRate: '5',
        status: 'online',
        ping: '145'
      },
      {
        id: '1005',
        name: 'Facepunch US West',
        ip: '208.103.5.182',
        port: '28015',
        region: 'us',
        mapName: 'Procedural Map',
        mapSize: '4250',
        players: '312',
        maxPlayers: '350',
        isOfficial: '1',
        isModded: '0',
        gatherRate: '1',
        status: 'online',
        ping: '89'
      }
    ]
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
      // 跳转到服务器详情页（待实现）
      wx.showModal({
        title: server.name,
        content: `IP: ${server.ip}:${server.port}\n玩家: ${server.players}/${server.maxPlayers}\n地图: ${server.mapName}`,
        showCancel: false,
        confirmText: '知道了'
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

    wx.showLoading({ title: '查询中...' })

    try {
      // 先查询服务器
      const queryRes = await request(`${API_BASE}/servers/query`, 'POST', {
        ip: newServerIp,
        port: newServerPort || '28015'
      })

      if (queryRes.code === 200) {
        // 查询成功，添加到数据库
        const addRes = await request(`${API_BASE}/servers`, 'POST', {
          ip: newServerIp,
          port: newServerPort || '28015'
        })

        wx.hideLoading()

        if (addRes.code === 200) {
          wx.showToast({
            title: '添加成功',
            icon: 'success'
          })
          this.closeModal()
          this.loadServerList()
        } else {
          wx.showToast({
            title: addRes.message || '添加失败',
            icon: 'none'
          })
        }
      } else {
        wx.hideLoading()
        wx.showToast({
          title: queryRes.message || '无法连接服务器',
          icon: 'none'
        })
      }
    } catch (error) {
      wx.hideLoading()
      console.error('查询服务器失败:', error)
      wx.showToast({
        title: '查询失败，请检查网络',
        icon: 'none'
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
   * 复制服务器地址
   */
  copyAddress(e) {
    const server = e.currentTarget.dataset.server
    if (server) {
      wx.setClipboardData({
        data: `client.connect ${server.ip}:${server.port}`,
        success: () => {
          wx.showToast({
            title: '已复制连接命令',
            icon: 'success'
          })
        }
      })
    }
  }
})
