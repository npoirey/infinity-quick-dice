<template>
    <div>
        <svg width="175" height="200">
            <a @click="onClick" tabindex="0">
                <polyline id="hexagon" points="87,0 174,50 174,150 87,200 0,150 0,50 87,0" />
                <text x="90"
                      y="90"
                      fill="#FFFFFF"
                      text-anchor="middle"
                      font-size="90"
                      dominant-baseline="central"
                      alignment-baseline="central">
                    {{label}}
                </text>
            </a>
        </svg>
    </div>
</template>

<script lang="ts">
  import {Component, Emit, Prop, Vue} from 'vue-property-decorator';

  @Component
  export default class HexButton extends Vue {
    @Prop() private label!: string;

    @Emit('click')
    onClick(){

    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    div{
        display: inline-block;
    }
    a {
        font-family: Montalban;

        -webkit-user-select: none;
        -khtml-user-select: none;
        -moz-user-select: none;
        -o-user-select: none;
        user-select: none;


        &:hover, &:focus {
            polyline {
                fill: #42b983;
                stroke: black;
                stroke-width: -10px;
            }
        }
        &:active {
            polyline {
                fill: #b0b921;
                stroke: black;
                stroke-width: -10px;
            }
        }
    }
</style>
