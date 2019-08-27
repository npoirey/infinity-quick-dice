import {library} from '@fortawesome/fontawesome-svg-core';
import {faCrosshairs as falCrosshairs} from '@fortawesome/pro-light-svg-icons';
import {faCrosshairs as farCrosshairs} from '@fortawesome/pro-regular-svg-icons';
import {faCrosshairs as fasCrosshairs, faDiceD20, faShield, faSword} from '@fortawesome/pro-solid-svg-icons';
import {FontAwesomeIcon, FontAwesomeLayers, FontAwesomeLayersText} from '@fortawesome/vue-fontawesome';
import Vue from 'vue';
import Toasted from 'vue-toasted';

import App from './App.vue';
import './registerServiceWorker';

library.add(fasCrosshairs, farCrosshairs, falCrosshairs, faDiceD20, faSword, faShield);

Vue.component('font-awesome-icon', FontAwesomeIcon);
Vue.component('font-awesome-layers', FontAwesomeLayers);
Vue.component('font-awesome-layers-text', FontAwesomeLayersText);

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
