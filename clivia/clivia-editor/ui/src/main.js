import { createApp } from 'vue';
import App from './App.vue';
import icon from './icon.vue';

import './assets/main.css';

createApp(App)
    .component('icon', icon)
    .mount('#app');
