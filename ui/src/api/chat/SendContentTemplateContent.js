import request from '@/utils/request'

// 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息列表
export function listSendContentTemplateContent(query) {
  return request({
    url: '/chat/SendContentTemplateContent/list',
    method: 'get',
    params: query
  })
}

// 查询发送内容模板内容，用于存储发送内容模板的具体内容相关信息详细
export function getSendContentTemplateContent(contentId) {
  return request({
    url: '/chat/SendContentTemplateContent/' + contentId,
    method: 'get'
  })
}

// 新增发送内容模板内容，用于存储发送内容模板的具体内容相关信息
export function addSendContentTemplateContent(data) {
  return request({
    url: '/chat/SendContentTemplateContent',
    method: 'post',
    data: data
  })
}

// 修改发送内容模板内容，用于存储发送内容模板的具体内容相关信息
export function updateSendContentTemplateContent(data) {
  return request({
    url: '/chat/SendContentTemplateContent',
    method: 'put',
    data: data
  })
}

// 删除发送内容模板内容，用于存储发送内容模板的具体内容相关信息
export function delSendContentTemplateContent(contentId) {
  return request({
    url: '/chat/SendContentTemplateContent/' + contentId,
    method: 'delete'
  })
}
