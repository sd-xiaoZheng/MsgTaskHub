import request from '@/utils/request'

// 查询项目设置列表
export function listProjectSet(query) {
  return request({
    url: '/chat/ProjectSet/list',
    method: 'get',
    params: query
  })
}
// 查询项目设置详细
export function getProjectSetByProjectId(projectId) {
  return request({
    url: '/chat/ProjectSet' ,
    method: 'get',
    params:{projectId}
  })
} 
// 查询项目设置详细
export function getProjectSet(setId) {
  return request({
    url: '/chat/ProjectSet/' + setId,
    method: 'get'
  })
} 
// 新增项目设置
export function addProjectSet(data) {
  return request({
    url: '/chat/ProjectSet',
    method: 'post',
    data: data
  })
}

// 修改项目设置
export function updateProjectSet(data) {
  return request({
    url: '/chat/ProjectSet',
    method: 'put',
    data: data
  })
}

// 删除项目设置
export function delProjectSet(setId) {
  return request({
    url: '/chat/ProjectSet/' + setId,
    method: 'delete'
  })
}
