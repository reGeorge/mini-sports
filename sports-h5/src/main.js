import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 导入 Vant 样式
import 'vant/lib/index.css'

// 导入 Vant 组件
import {
  Button,
  NavBar,
  Form,
  Field,
  CellGroup,
  Cell,
  Icon,
  Tag,
  Card,
  Search,
  DropdownMenu,
  DropdownItem,
  PullRefresh,
  List,
  FloatingBubble,
  Popup,
  DatePicker,
  Picker,
  Dialog,
  Toast,
  Tabbar,
  TabbarItem,
  Empty,
  Checkbox,
  CheckboxGroup,
  TimePicker,
  PickerGroup,
  Radio,
  RadioGroup,
  Tabs,
  Tab,
  Collapse,
  CollapseItem
} from 'vant'

const app = createApp(App)

// 注册 Vant 组件
const vantComponents = [
  Button,
  NavBar,
  Form,
  Field,
  CellGroup,
  Cell,
  Icon,
  Tag,
  Card,
  Search,
  DropdownMenu,
  DropdownItem,
  PullRefresh,
  List,
  FloatingBubble,
  Popup,
  DatePicker,
  Picker,
  Dialog,
  Toast,
  Tabbar,
  TabbarItem,
  Empty,
  Checkbox,
  CheckboxGroup,
  TimePicker,
  PickerGroup,
  Radio,
  RadioGroup,
  Tabs,
  Tab,
  Collapse,
  CollapseItem
]

vantComponents.forEach(component => {
  app.use(component)
})

app.use(router)
app.mount('#app')