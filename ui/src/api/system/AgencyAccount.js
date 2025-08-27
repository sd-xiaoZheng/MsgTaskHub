import request from '@/utils/request'

// 查询代理列表
export function listAgencyAccount(query) {
  return request({
    url: '/system/AgencyAccount/list',
    method: 'get',
    params: query
  })
}

// 查询代理详细
export function getAgencyAccount(id) {
  return request({
    url: '/system/AgencyAccount/' + id,
    method: 'get'
  })
}

// 新增代理
export function addAgencyAccount(data) {
  return request({
    url: '/system/AgencyAccount',
    method: 'post',
    data: data
  })
}

// 修改代理
export function updateAgencyAccount(data) {
  return request({
    url: '/system/AgencyAccount',
    method: 'put',
    data: data
  })
}

// 删除代理
export function delAgencyAccount(id) {
  return request({
    url: '/system/AgencyAccount/' + id,
    method: 'delete'
  })
}
