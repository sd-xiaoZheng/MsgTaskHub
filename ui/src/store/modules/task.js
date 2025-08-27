// 会话消息  
const task = {
    state: { 
      pace:0,
      taskId:null,
    },
    mutations: { 
      SET_PACE(state,pace){
          state.pace = pace
      },
      SET_TASKID(state,taskId){
          state.taskId = taskId
      }
    },
    actions: { 
      
    }
  }
   
  export default task
  