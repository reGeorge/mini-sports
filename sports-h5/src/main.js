import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { 
  Button, 
  Form, 
  Field, 
  CellGroup, 
  NavBar, 
  Toast,
  Image as VanImage,
  Icon,
  Cell,
  Tabbar,
  TabbarItem,
  Radio,
  RadioGroup,
  Search,
  Empty,
  Popup,
  Checkbox,
  CheckboxGroup,
  Tag
} from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)

// 注册 Vant 组件
app.use(Button)
   .use(Form)
   .use(Field)
   .use(CellGroup)
   .use(NavBar)
   .use(Toast)
   .use(VanImage)
   .use(Icon)
   .use(Cell)
   .use(Tabbar)
   .use(TabbarItem)
   .use(Radio)
   .use(RadioGroup)
   .use(Search)
   .use(Empty)
   .use(Popup)
   .use(Checkbox)
   .use(CheckboxGroup)
   .use(Tag)

app.use(router)
app.mount('#app')