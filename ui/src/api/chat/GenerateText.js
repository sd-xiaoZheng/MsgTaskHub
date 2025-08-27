import request from '@/utils/request'

// 生成话术
export function genGenerateText(data){
  return request({
    url:"/chat/GenerateText/generate",
    method:"post",
    data
  })
}

// 查询GenerateText列表
export function listGenerateText(query) {
  return request({
    url: '/chat/GenerateText/list',
    method: 'get',
    params: query
  })
}

// 查询GenerateText详细
export function getGenerateText(id) {
  return request({
    url: '/chat/GenerateText/' + id,
    method: 'get'
  })
}

// 新增GenerateText
export function addGenerateText(data) {
  return request({
    url: '/chat/GenerateText',
    method: 'post',
    data: data
  })
}

// 修改GenerateText
export function updateGenerateText(data) {
  return request({
    url: '/chat/GenerateText',
    method: 'put',
    data: data
  })
}

// 删除GenerateText
export function delGenerateText(id) {
  return request({
    url: '/chat/GenerateText/' + id,
    method: 'delete'
  })
}
