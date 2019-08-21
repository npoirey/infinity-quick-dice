<template>
    <svg viewBox="0 0 175 200">
        <a @click="onClick" tabindex="0">
            <polyline id="hexagon" points="87,2 173,50 173,150 87,198 2,150 2,50 87,2" />
            <text x="90"
                  y="95"
                  text-anchor="middle"
                  font-size="70"
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

    @Emit('click')
    onClick() {

    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">

    svg {
        display: inline-block;
        max-height: $hex-button-height;
        width: 100%;
        clip-path: polygon(0 25%,0 75%, 50% 100%, 100% 75%,100% 25%, 50% 0)
    }

    a {
        font-family: Montalban;

        -webkit-user-select: none;
        -khtml-user-select: none;
        -moz-user-select: none;
        -o-user-select: none;
        user-select: none;

        polyline {
            fill: $default-background-color;
            stroke: $default-border-color;
            stroke-width: 4px;
            stroke-linecap: square;
        }

        text {
            fill: $default-text-color;
        }

        &:hover {
            cursor: pointer;
        }

        &:hover, &:focus {
            polyline {
                fill: $hover-background-color;
                stroke: $hover-border-color;
            }
        }
    }

    .active a, a:active {
        polyline {
            fill: $active-background-color;
            stroke: $active-border-color;
        }

        text {
            fill: $active-text-color
        }
    }

</style>
