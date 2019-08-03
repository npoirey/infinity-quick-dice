<template>
    <svg viewBox="0 0 250 50" class="action-button">
        <a @click="onClick" tabindex="0" :class="{animation: animation}">
            <polyline class="o1" points="0 0, 250 0, 250 50, 0 50, 0 0"></polyline>
            <polyline class="o2" points="0 0, 250 0, 250 50, 0 50, 0 0"></polyline>
            <text x="125"
                  y="25"
                  text-anchor="middle"
                  font-size="15"
                  dominant-baseline="central"
                  alignment-baseline="central">
                {{label}}
            </text>
        </a>
    </svg>
</template>

<script lang="ts">
  import {Component, Emit, Prop, Vue} from 'vue-property-decorator';

  @Component
  export default class HexButton extends Vue {
    @Prop() private label!: string;
    @Prop() private active!: boolean;
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
    $duration: 0.5s;
    $offset: 600;
    svg {
        display: inline-block;
        max-height: $hex-button-height;

        fill: transparent;
        polyline {
            stroke-width: 5px;
            vector-effect:non-scaling-stroke;
        }
    }

    a {
        font-family: Montalban;

        -webkit-user-select: none;
        -khtml-user-select: none;
        -moz-user-select: none;
        -o-user-select: none;
        user-select: none;

        .o1 {
            stroke: $active-background-color;
            transition: all $duration ease-in-out;
        }

        .o2 {
            stroke: white;
            stroke-dasharray: 20 $offset;
            stroke-dashoffset: 20;
            transition: all $duration ease-in-out;
        }

        text {
            fill: $default-text-color;
            text-transform: uppercase;
            margin: 1em;
        }


        &.animation {
            .o1{
                fill: $active-background-color;
                fill-opacity: .3;
            }
            .o2 {
                stroke-dashoffset: -$offset;
            }
        }
    }
</style>
