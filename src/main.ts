import Vue from 'vue';
import Toasted from 'vue-toasted';

import App from './App.vue';
import './registerServiceWorker';

Vue.use(Toasted, {
  position: 'top-center',
  duration: 5000,
  fullWidth: false,
  fitToScreen: false,
  action : {
    text : 'Close',
    onClick : (e, toastObject) => {
      toastObject.goAway(0);
    }
  },
});

Vue.config.productionTip = false;

new Vue({
  render: (h) => h(App),
}).$mount('#app');
