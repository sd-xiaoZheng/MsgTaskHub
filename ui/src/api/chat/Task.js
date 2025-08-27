import request from '@/utils/request'

// 查询任务，用于存储任务相关信息列表
export function listTask(query) {
  return request({
    url: '/chat/Task/list',
    method: 'get',
    params: query
  })
}
// 查询任务，用于存储任务相关信息列表
export function taskStatus(query) {
  return request({
    url: '/chat/Task/taskStatus',
    method: 'get',
    params: query
  })
}
// 暂停任务
export function suspendTask(taskId) {
  return request({
    url: '/chat/Task/suspendTask',
    method: 'get',
    params: {taskId}
  })
}
// 启动任务
export function StartTask(taskId) {
  return request({
    url: '/chat/Task/StartTask',
    method: 'get',
    params: {taskId}
  })
}
// 分配任务
export function assignedTask(taskId) {
  return request({
    url: '/chat/Task/assigned',
    method: 'get',
    params: {taskId}
  })
}
// 查询任务，用于存储任务相关信息详细
export function getTask(taskId) {
  return request({
    url: '/chat/Task/' + taskId,
    method: 'get'
  })
}

// 新增任务，用于存储任务相关信息
export function addTask(data) {
  return request({
    url: '/chat/Task',
    method: 'post',
    data: data
  })
}

// 修改任务，用于存储任务相关信息
export function updateTask(data) {
  return request({
    url: '/chat/Task',
    method: 'put',
    data: data
  })
}

// 删除任务，用于存储任务相关信息
export function delTask(taskId) {
  return request({
    url: '/chat/Task/' + taskId,
    method: 'delete'
  })
}
