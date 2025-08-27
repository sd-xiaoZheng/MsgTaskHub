import request from '@/utils/request'

// 清空项目
export function deleteSys() {
  return request({
    url: '/warn/deleteSys',
    method: 'post',
  })
}

// 查询项目管理列表
export function listProject(query) {
  return request({
    url: '/chat/Project/list',
    method: 'get',
    params: query
  })
}
export function listProjectAll(query) {
  return request({
    url: '/chat/Project/listAll',
    method: 'get',
    params: query
  })
}
// 查询项目管理详细
export function getProject(projectId) {
  return request({
    url: '/chat/Project/' + projectId,
    method: 'get'
  })
}

// 新增项目管理
export function addProject(data) {
  return request({
    url: '/chat/Project',
    method: 'post',
    data: data
  })
}

// 修改项目管理
export function updateProject(data) {
  return request({
    url: '/chat/Project',
    method: 'put',
    data: data
  })
}

// 删除项目管理
export function delProject(projectId) {
  return request({
    url: '/chat/Project/' + projectId,
    method: 'delete'
  })
}
