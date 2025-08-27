import request from '@/utils/request'

// 查询客户账号，用于存储客户账号相关信息列表
export function listCustomerAccount(query) {
  return request({
    url: '/chat/CustomerAccount/list',
    method: 'get',
    params: query
  })
}

// 查询客户账号，用于存储客户账号相关信息详细
export function getCustomerAccount(accountId) {
  return request({
    url: '/chat/CustomerAccount/' + accountId,
    method: 'get'
  })
}

// 新增客户账号，用于存储客户账号相关信息
export function addCustomerAccount(data) {
  return request({
    url: '/chat/CustomerAccount',
    method: 'post',
    data: data
  })
}

// 修改客户账号，用于存储客户账号相关信息
export function updateCustomerAccount(data) {
  return request({
    url: '/chat/CustomerAccount',
    method: 'put',
    data: data
  })
}

// 删除客户账号，用于存储客户账号相关信息
export function delCustomerAccount(accountId) {
  return request({
    url: '/chat/CustomerAccount/' + accountId,
    method: 'delete'
  })
}
