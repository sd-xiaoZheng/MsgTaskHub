import request from '@/utils/request'

// 查询群发消息，用于存储群发消息相关信息列表
export function listMassMessage(query) {
  return request({
    url: '/chat/MassMessage/list',
    method: 'get',
    params: query
  })
}


// 查询群发消息，用于存储群发消息相关信息详细
export function getMassMessage(messageId) {
  return request({
    url: '/chat/MassMessage/' + messageId,
    method: 'get'
  })
}

// 新增群发消息，用于存储群发消息相关信息
export function addMassMessage(data) {
  return request({
    url: '/chat/MassMessage',
    method: 'post',
    data: data
  })
}

// 修改群发消息，用于存储群发消息相关信息
export function updateMassMessage(data) {
  return request({
    url: '/chat/MassMessage',
    method: 'put',
    data: data
  })
}

// 删除群发消息，用于存储群发消息相关信息
export function delMassMessage(messageId) {
  return request({
    url: '/chat/MassMessage/' + messageId,
    method: 'delete'
  })
}
