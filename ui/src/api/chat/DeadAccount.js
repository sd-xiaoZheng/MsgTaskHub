import request from '@/utils/request'

// 查询死号，用于存储死号相关信息列表
export function listDeadAccount(query) {
  return request({
    url: '/chat/DeadAccount/list',
    method: 'get',
    params: query
  })
}

// 查询死号，用于存储死号相关信息详细
export function getDeadAccount(deadId) {
  return request({
    url: '/chat/DeadAccount/' + deadId,
    method: 'get'
  })
}

// 新增死号，用于存储死号相关信息
export function addDeadAccount(data) {
  return request({
    url: '/chat/DeadAccount',
    method: 'post',
    data: data
  })
}

// 修改死号，用于存储死号相关信息
export function updateDeadAccount(data) {
  return request({
    url: '/chat/DeadAccount',
    method: 'put',
    data: data
  })
}

// 删除死号，用于存储死号相关信息
export function delDeadAccount(deadId) {
  return request({
    url: '/chat/DeadAccount/' + deadId,
    method: 'delete'
  })
}
