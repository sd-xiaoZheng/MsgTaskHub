import request from '@/utils/request'

// 查询发送内容模板，用于存储发送内容模板相关信息列表
export function listSendContentTemplate(query) {
  return request({
    url: '/chat/SendContentTemplate/list',
    method: 'get',
    params: query
  })
}
export function listSendContentTemplateAll(query) {
  return request({
    url: '/chat/SendContentTemplate/listAll',
    method: 'get',
    params: query
  })
}


// 清空群发的内容
export function emptySendContentTemplate(projectId) {
  return request({
    url: '/chat/SendContentTemplate/empty',
    method: 'post',
    params: {projectId}
  })
}


// 查询发送内容模板，用于存储发送内容模板相关信息详细
export function getSendContentTemplate(templateId) {
  return request({
    url: '/chat/SendContentTemplate/' + templateId,
    method: 'get'
  })
}

// 一键重置群发内容
export function resetByTemplateId(templateId) {
  return request({
    url: '/chat/SendContentTemplate/resetByTemplateId',
    method: 'get',
    params: { templateId }
  })
}

// 新增发送内容模板，用于存储发送内容模板相关信息
export function addSendContentTemplate(data) {
  return request({
    url: '/chat/SendContentTemplate',
    method: 'post',
    data: data
  })
}
// 根据模板id删除所有内容
export function deleteByTemplate(templateId) {
  return request({
    url: '/chat/SendContentTemplate/deleteByTemplate',
    method: 'post',
    params: {templateId}
  })
}
// 修改发送内容模板，用于存储发送内容模板相关信息
export function updateSendContentTemplate(data) {
  return request({
    url: '/chat/SendContentTemplate',
    method: 'put',
    data: data
  })
}

// 删除发送内容模板，用于存储发送内容模板相关信息
export function delSendContentTemplate(templateId) {
  return request({
    url: '/chat/SendContentTemplate/' + templateId,
    method: 'delete'
  })
}
