import request from '@/utils/request'

// 查询聊天记录，用于存储聊天记录相关信息列表
export function listChatRecord(query) {
  return request({
    url: '/chat/ChatRecord/list',
    method: 'get',
    params: query
  })
}

// 查询聊天记录，用于存储聊天记录相关信息详细
export function getChatRecord(chatId) {
  return request({
    url: '/chat/ChatRecord/' + chatId,
    method: 'get'
  })
}

// 新增聊天记录，用于存储聊天记录相关信息
export function addChatRecord(data) {
  return request({
    url: '/chat/ChatRecord',
    method: 'post',
    data: data
  })
}

// 修改聊天记录，用于存储聊天记录相关信息
export function updateChatRecord(data) {
  return request({
    url: '/chat/ChatRecord',
    method: 'put',
    data: data
  })
}

// 删除聊天记录，用于存储聊天记录相关信息
export function delChatRecord(chatId) {
  return request({
    url: '/chat/ChatRecord/' + chatId,
    method: 'delete'
  })
}
