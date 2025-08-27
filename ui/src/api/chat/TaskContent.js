import request from '@/utils/request'

// 查询任务内容，用于存储任务具体内容相关信息列表
export function listTaskContent(query) {
  return request({
    url: '/chat/TaskContent/list',
    method: 'get',
    params: query
  })
}

export function taskContentByUserId(query) {
  return request({
    url: '/chat/TaskContent/taskContentByUserId',
    method: 'get',
    params: query
  })
}

// 查询任务内容，用于存储任务具体内容相关信息详细
export function getTaskContent(taskContentId) {
  return request({
    url: '/chat/TaskContent/' + taskContentId,
    method: 'get'
  })
}

// 新增任务内容，用于存储任务具体内容相关信息
export function addTaskContent(data) {
  return request({
    url: '/chat/TaskContent',
    method: 'post',
    data: data
  })
}

// 修改任务内容，用于存储任务具体内容相关信息
export function updateTaskContent(data) {
  return request({
    url: '/chat/TaskContent',
    method: 'put',
    data: data
  })
}

// 删除任务内容，用于存储任务具体内容相关信息
export function delTaskContent(taskContentId) {
  return request({
    url: '/chat/TaskContent/' + taskContentId,
    method: 'delete'
  })
}

// 删除客服手中未进行的任务
export function removeByUserId(taskId) {
  return request({
    url: '/chat/TaskContent/removeByUserId' ,
    method: 'post',
    data: {taskId}
  })
}
