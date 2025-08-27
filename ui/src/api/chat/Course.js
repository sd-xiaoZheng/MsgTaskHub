import request from '@/utils/request'

// 查询新手教程列表
export function listCourse(query) {
  return request({
    url: '/chat/Course/list',
    method: 'get',
    params: query
  })
}

// 查询新手教程详细
export function getCourse(courseId) {
  return request({
    url: '/chat/Course/' + courseId,
    method: 'get'
  })
}

// 新增新手教程
export function addCourse(data) {
  return request({
    url: '/chat/Course',
    method: 'post',
    data: data
  })
}

// 修改新手教程
export function updateCourse(data) {
  return request({
    url: '/chat/Course',
    method: 'put',
    data: data
  })
}

// 删除新手教程
export function delCourse(courseId) {
  return request({
    url: '/chat/Course/' + courseId,
    method: 'delete'
  })
}
