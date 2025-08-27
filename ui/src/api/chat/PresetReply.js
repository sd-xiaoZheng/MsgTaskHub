import request from '@/utils/request'

// 查询预设回复，用于存储预设回复相关信息列表
export function listPresetReply(query) {
  return request({
    url: '/chat/PresetReply/list',
    method: 'get',
    params: query
  })
}

// 查询六个回复类型的列表
export function listCountPreset(query) {
  return request({
    url: '/chat/PresetReply/listCount',
    method: 'get',
    params: query
  })
}

// 根据类型删除全部数据
export function deleteByWeight(query) {
  return request({
    url: '/chat/PresetReply/deleteByWeight',
    method: 'post',
    params: query
  })
}
 

// 清空预设回复的内容
export function emptyPresetReply(projectId) {
  return request({
    url: '/chat/PresetReply/empty',
    method: 'post',
    params: {projectId}
  })
}

// 随机返回回复信息
export function randomText(query) {
  return request({
    url: '/chat/PresetReply/randomText',
    method: 'get',
    params: query
  })
} 

// 查询预设回复，用于存储预设回复相关信息详细
export function getPresetReply(replyId) {
  return request({
    url: '/chat/PresetReply/' + replyId,
    method: 'get'
  })
}

// 新增预设回复，用于存储预设回复相关信息
export function addPresetReply(data) {
  return request({
    url: '/chat/PresetReply',
    method: 'post',
    data: data
  })
}

// 修改预设回复，用于存储预设回复相关信息
export function updatePresetReply(data) {
  return request({
    url: '/chat/PresetReply',
    method: 'put',
    data: data
  })
}

// 删除预设回复，用于存储预设回复相关信息
export function delPresetReply(replyId) {
  return request({
    url: '/chat/PresetReply/' + replyId,
    method: 'delete'
  })
}
