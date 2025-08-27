// 会话消息  
const message = {
  state: { 
    type:'',
    data:{
        sessionId:null,//会话id
        flagId:null,//假id
        isFavorite:0,
        text:'',
        realId:null,//消息id
        time:null,//消息时间
    }
  },
  mutations: { 
    SET_TYPE(state,type){
        state.type = type
    },
    SET_DATA(state,data){
        state.data = data
    }
  },
  actions: { 
    
  }
}
 
export default message
