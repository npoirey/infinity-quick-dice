import Vue from 'vue';
import Snotify, {SnotifyPosition} from 'vue-snotify';

import {SnotifyService} from 'vue-snotify/SnotifyService';
import App from './App.vue';
import './registerServiceWorker';

declare module 'vue/types/vue' {
  interface Vue {
    $snotify: SnotifyService;
  }
}

Vue.config.productionTip = false;

const options = {
  toast: {
    position: SnotifyPosition.centerCenter,
  },
};

Vue.use(Snotify, options);

new Vue({
  render: (h) => h(App),
}).$mount('#app');
