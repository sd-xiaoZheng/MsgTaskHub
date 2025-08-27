export default {
  navbar: {
    profile: '个人中心',
    layoutSettings: '布局设置',
    logout: '退出登录',
    notice: '消息',
    project: '项目',
    switchLannguage: '切换语言',
    switchProject: '切换项目',
    role: {
      admin: '超管',
      serviceStaff: '客服',
      teamLeader: '管理',
      operation:"运维管理",
    }
  },
  noticeDialogTitle:"消息列表",
  notice: {
    title: '消息列表',
    detail: '公告详情',
    publishTime: '发布时间'
  },
  task: {
    upload: {
      title: "任务上传",
      remainingCount: "剩余可上传数量",
      taskName: "任务名称",
      inputTaskName: "请输入任务名称，可不输入。",
      template: "群发模板",
      selectTemplate: "请选择模板",
      remaining: "剩余",
      times: "次",
      customize: "自定义",
      close: "关闭",
      templateFile: "模板文件",
      uploadButton: "点击上传",
      taskUploadTipOne: "上传任务号码，只支持xlsx、xls格式",
      taskUploadTipTwo: " 上传任务号码 + 话术文件，只支持xlsx、xls格式",
      back: "返回",
      selectTemplateButton: "选择模板",
      taskFile: "任务文件",
      dragTip:"将文件拖到此处，或<em>点击上传</em>"
    },
    status: {
      start: "启动",
      stop: "停止",
      assign: "分配",
      details: "详情",
      delete: "删除"
    },
    list: {
      taskName: "任务名称",
      taskStatus: "任务状态",
      startTime: "开始时间",
      endTime: "结束时间",
      createTime: "创建时间",
      operations: "操作"
    },
    customerService: {
      title: "客服分配列表",
      singleTaskData: "单任务数据",
      clear: "清空",
      customerCount: "客服",
      maxLoad: "分配数量",
      uploadCount: "上传数量",
      status: "状态",
      successCount: "总数/分配/成功",
      replyCount: "回复数量",
      replyRate: "回复率",
      view: "查看",
      dataDetails: "数据详情"
    }
  },
  login: {
    welcome:"欢迎登录",
    login: '登录',
    loginLoading: '登 录 中...',
    username: '请输入用户名',
    password: '请输入密码',
    code: '请输入验证码',
    loginSuccess: '登录成功',
    rember: '记住密码'
  },
  importDialog: {
    title: '导入模板',
    templateTitle: '标题',
    inputTemplateTitle: '请输入模板标题',
    file: '文件',
    selectFile: '选取文件',
    fileTypeLimit: '只能上传xlsx/xls文件',
    upload: '上 传',
    cancel: '取 消'
  }
}
