import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { Button, Form, Field, CellGroup, NavBar, Toast } from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)

// 注册 Vant 组件
app.use(Button)
   .use(Form)
   .use(Field)
   .use(CellGroup)
   .use(NavBar)
   .use(Toast)

app.use(router)
app.mount('#app') 