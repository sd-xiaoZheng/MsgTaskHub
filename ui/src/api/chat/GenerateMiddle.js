import request from '@/utils/request'

// 查询GenerateMiddle列表
export function listGenerateMiddle(query) {
  return request({
    url: '/chat/GenerateMiddle/list',
    method: 'get',
    params: query
  })
}

// 查询GenerateMiddle详细
export function getGenerateMiddle(id) {
  return request({
    url: '/chat/GenerateMiddle/' + id,
    method: 'get'
  })
}

// 新增GenerateMiddle
export function addGenerateMiddle(data) {
  return request({
    url: '/chat/GenerateMiddle',
    method: 'post',
    data: data
  })
}

// 修改GenerateMiddle
export function updateGenerateMiddle(data) {
  return request({
    url: '/chat/GenerateMiddle',
    method: 'put',
    data: data
  })
}

// 删除GenerateMiddle
export function delGenerateMiddle(id) {
  return request({
    url: '/chat/GenerateMiddle/' + id,
    method: 'delete'
  })
}
