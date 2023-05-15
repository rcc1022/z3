import { createApp } from 'vue'
import {
    Field,
    Switch,
    Button,
    Toast,
    Icon,
    Divider,
    Loading,
    Swipe,
    SwipeItem,
    Calendar,
    Cell,
    Picker,
    Popup,
    NoticeBar,
    List,
    DropdownMenu,
    DropdownItem,
    Badge
} from 'vant';
import 'vant/lib/index.css';
import App from './App.vue'

import './assets/main.css'

createApp(App)
    .use(Field)
    .use(Switch)
    .use(Button)
    .use(Toast)
    .use(Icon)
    .use(Divider)
    .use(Loading)
    .use(Swipe)
    .use(SwipeItem)
    .use(Calendar)
    .use(Cell)
    .use(Picker)
    .use(Popup)
    .use(NoticeBar)
    .use(List)
    .use(DropdownMenu)
    .use(DropdownItem)
    .use(Badge)
    .mount('#app')
