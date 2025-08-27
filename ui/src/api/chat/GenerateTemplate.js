import request from '@/utils/request'

// 查询GenerateTemplate列表
export function listGenerateTemplate(query) {
  return request({
    url: '/chat/GenerateTemplate/list',
    method: 'get',
    params: query
  })
}

// 查询GenerateTemplate详细
export function getGenerateTemplate(id) {
  return request({
    url: '/chat/GenerateTemplate/' + id,
    method: 'get'
  })
}

// 新增GenerateTemplate
export function addGenerateTemplate(data) {
  return request({
    url: '/chat/GenerateTemplate',
    method: 'post',
    data: data
  })
}

// 修改GenerateTemplate
export function updateGenerateTemplate(data) {
  return request({
    url: '/chat/GenerateTemplate',
    method: 'put',
    data: data
  })
}

// 删除GenerateTemplate
export function delGenerateTemplate(id) {
  return request({
    url: '/chat/GenerateTemplate/' + id,
    method: 'delete'
  })
}
