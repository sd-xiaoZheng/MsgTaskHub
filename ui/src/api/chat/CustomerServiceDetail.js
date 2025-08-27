import request from '@/utils/request'

// 查询客服号详情，用于存储客服号详细信息列表
export function listCustomerServiceDetail(query) {
  return request({
    url: '/chat/CustomerServiceDetail/list',
    method: 'get',
    params: query
  })
}

// 批量删除用户数据
export function removeUser(userIds) {
  return request({
    url: '/chat/CustomerServiceDetail/removeUser/'+userIds,
    method: 'delete', 
  })
}

export function addBatch(data) {
  return request({
    url: '/chat/CustomerServiceDetail/addBatch',
    method: 'post',
   data
  })
} 

export function leaderAllList(query) {
  return request({
    url: '/chat/CustomerServiceDetail/leaderAllList',
    method: 'get',
    params: query
  })
} 


// 获取自己旗下的分配客服的列表或者自己的列表
export function taskMyStatusList(query) {
  return request({
    url: '/chat/CustomerServiceDetail/taskStatusByUserId',
    method: 'get',
    params: query
  })
}


// 查询客服号详情，用于存储客服号详细信息详细
export function getCustomerServiceDetail(customerId) {
  return request({
    url: '/chat/CustomerServiceDetail/' + customerId,
    method: 'get'
  })
}

// 新增客服号详情，用于存储客服号详细信息
export function addCustomerServiceDetail(data) {
  return request({
    url: '/chat/CustomerServiceDetail',
    method: 'post',
    data: data
  })
}

// 修改客服号详情，用于存储客服号详细信息
export function updateCustomerServiceDetail(data) {
  return request({
    url: '/chat/CustomerServiceDetail',
    method: 'put',
    data: data
  })
}

// 删除客服号详情，用于存储客服号详细信息
export function delCustomerServiceDetail(customerId) {
  return request({
    url: '/chat/CustomerServiceDetail/' + customerId,
    method: 'delete'
  })
}
