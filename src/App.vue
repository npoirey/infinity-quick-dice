<template>
  <div id="app">
    <HexButton @click="setVal" label="10"/>
    <HexButton label="1"/>
    <HexButton label="9"/>
    <HexButton label="29"/>
    {{val}}
  </div>
</template>

<script lang="ts">
  import HexButton from '@/components/HexButton.vue';
  import {Component, Vue} from 'vue-property-decorator';
  import HelloWorld from './components/HelloWorld.vue';

  @Component({
  components: {
    HelloWorld,
    HexButton,
  },
})
export default class App extends Vue {
  val: number = 0;
  setVal(){
    this.val = this.val+1;
  }
}
</script>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
