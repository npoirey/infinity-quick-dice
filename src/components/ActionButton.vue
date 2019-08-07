<template>
    <button class="action-button" :disabled="disabled" @click.prevent="onClick">{{label}}</button>
</template>

<script lang="ts">
  import {Component, Emit, Prop, Vue} from 'vue-property-decorator';

  @Component
  export default class ActionButton extends Vue {
    @Prop() private label!: string;
    @Prop() private active!: boolean;
    @Prop() private disabled!: boolean;
    animation = false;

    @Emit('click')
    onClick() {
      this.animation = true;
      setTimeout(() => this.animation = false, 500);
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    button {
        border: 1px solid $default-border-color;
        color: $default-text-color;
        background: $default-background-color;
        font-family: Montalban;
        padding: 0.5em;
        text-transform: uppercase;

        &:hover {
            background-color: $hover-background-color;
            border-color: $hover-border-color;
            color: $hover-text-color;
            cursor: pointer;
        }
        &:active {
            background-color: $active-background-color;
            border-color: $active-border-color;
            color: $active-text-color;
        }
        &:focus{
            outline: none;
        }
        &::-moz-focus-inner {
            border: 0;
        }
    }
</style>
