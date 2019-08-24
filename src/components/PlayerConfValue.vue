<template>
    <div class="player-conf-value" :class="{reverse: reverse}">
        <font-awesome-icon :icon="icon" :pull="reverse ? 'right' : 'left'"/>
        {{value}}
    </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator';

  @Component
  export default class PlayerConfValue extends Vue {
    @Prop() private icon!: string;
    @Prop() private value!: string;
    @Prop() private reverse!: boolean;
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    .player-conf-value {
        display: flex;
        justify-content: space-between;
        align-items: center;
        &.reverse {
            flex-direction: row-reverse;
        }
    }
</style>
