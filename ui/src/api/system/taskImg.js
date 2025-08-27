import request from '@/utils/request'

// 查询TaskImg列表
export function listTaskImg(query) {
  return request({
    url: '/system/taskImg/list',
    method: 'get',
    params: query
  })
}

// 查询TaskImg详细
export function getTaskImg(id) {
  return request({
    url: '/system/taskImg/' + id,
    method: 'get'
  })
}

// 新增TaskImg
export function addTaskImg(data) {
  return request({
    url: '/system/taskImg',
    method: 'post',
    data: data
  })
}

// 修改TaskImg
export function updateTaskImg(data) {
  return request({
    url: '/system/taskImg',
    method: 'put',
    data: data
  })
}

// 删除TaskImg
export function delTaskImg(id) {
  return request({
    url: '/system/taskImg/' + id,
    method: 'delete'
  })
}
