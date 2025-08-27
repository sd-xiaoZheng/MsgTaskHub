import request from '@/utils/request'

// 查询会话记录，用于存储会话记录相关信息列表
export function listSessionRecord(query) {
  return request({
    url: '/chat/SessionRecord/list',
    method: 'get',
    params: query
  })
}

// 查看每个会话类型的所有数量
export function sessionUnread(userId) {
  return request({
    url: '/chat/SessionRecord/sessionUnread',
    method: 'get',
    params: {userId}
  })
}

// 已读会话
export function readSession(sessionId) {
  return request({
    url: '/chat/SessionRecord/read/'+sessionId,
    method: 'post'
  })
}


// 查询收藏会话
export function myFavoriteSession(query) {
  return request({
    url: '/chat/SessionRecord/myFavoriteSession',
    method: 'get',
    params: query
  })
}

// 查询群发消息记录
export function myMassMessage(query) {
  return request({
    url: '/chat/SessionRecord/myMassMessage',
    method: 'get',
    params: query
  })
}

// 收藏
export function favoriteSession(sessionId) {
  return request({
    url: '/chat/SessionRecord/favoriteSession/' + sessionId,
    method: 'post',
  })
}

// 置顶
export function topSession(sessionId) {
  return request({
    url: '/chat/SessionRecord/topSession/'+sessionId,
    method: 'post',
  })
}


// 查询我的会话
export function mySession(query) {
  return request({
    url: '/chat/SessionRecord/mySession',
    method: 'get',
    params: query
  })
}


// 查询会话记录，用于存储会话记录相关信息详细
export function getSessionRecord(sessionId) {
  return request({
    url: '/chat/SessionRecord/' + sessionId,
    method: 'get'
  })
}

// 新增会话记录，用于存储会话记录相关信息
export function addSessionRecord(data) {
  return request({
    url: '/chat/SessionRecord',
    method: 'post',
    data: data
  })
}

// 修改会话记录，用于存储会话记录相关信息
export function updateSessionRecord(data) {
  return request({
    url: '/chat/SessionRecord',
    method: 'put',
    data: data
  })
}

// 删除会话记录，用于存储会话记录相关信息
export function delSessionRecord(sessionId) {
  return request({
    url: '/chat/SessionRecord/' + sessionId,
    method: 'delete'
  })
}
