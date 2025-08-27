import request from '@/utils/request'

// 查询分配给的客服账号列表
export function listAccounts(query) {
  return request({
    url: '/chat/Accounts/list',
    method: 'get',
    params: query
  })
}

// 批量重置账号
export function resetAccount(accountIds) {
  return request({
    url: '/chat/Accounts/resetAccount/'+accountIds,
    method: 'post',
  })
}

// 批量补号
export function supplementFillAccount(userIds, num = 0) {
  return request({
    url: '/chat/Accounts/supplement/' + userIds,
    method: 'post',
    params: { num }
  })
}

// 真删除账号
export function deleteAccountsByAccountIdsReal(ids) {
  return request({
    url: '/chat/Accounts/deleteAccountsByAccountIdsReal/' + ids,
    method: 'delete',
  })
}

// 离职逻辑
export function dimission(userId) {
  return request({
    url: '/chat/Accounts/dimission',
    method: 'post',
    params: { userId }
  })
}

// 根据项目回收账号
export function recycleAccount(projectId) {
  return request({
    url: '/chat/Accounts/recycleAccount',
    method: 'post',
    params: { projectId }
  })
}

// 按项目id查看帐号状态
export function accountStatus(projectId) {
  return request({
    url: '/chat/Accounts/accountStatus',
    method: 'post',
    params: { projectId }
  })
}

// 清空账号
export function emptyAccount(params) {
  return request({
    url: '/chat/Accounts/emptyAccount',
    method: 'post',
    params
  })
}

// 批量回收账号
export function recycleAccountBatch(accountIds) {
  return request({
    url: '/chat/Accounts/recycleAccountBatch/' + accountIds,
    method: 'get',
  })
}

export function listRecycleAccounts(query) {
  return request({
    url: '/chat/Accounts/recycleList',
    method: 'get',
    params: query
  })
}

// 查询分配给的客服账号详细
export function getAccounts(accountId) {
  return request({
    url: '/chat/Accounts/' + accountId,
    method: 'get'
  })
}

// 新增分配给的客服账号
export function addAccounts(data) {
  return request({
    url: '/chat/Accounts',
    method: 'post',
    data: data
  })
}

// 修改分配给的客服账号
export function updateAccounts(data) {
  return request({
    url: '/chat/Accounts',
    method: 'put',
    data: data
  })
}

// 删除分配给的客服账号
export function delAccounts(accountId) {
  return request({
    url: '/chat/Accounts/' + accountId,
    method: 'delete'
  })
}

// 导出账号的数据
export function exportAccounts(data) {
  return request({
    url: '/chat/Accounts/export',
    method: 'post',
    data
  })
}
