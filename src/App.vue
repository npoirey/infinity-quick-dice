<template>
    <div id="app" style="height: 500px">
        <div>
            <h2>Player A Burst</h2>
            <select-hex-button :options="diceOptions" v-model="burstA"></select-hex-button>
            <h2>Player A rolls on</h2>
            <select-hex-button :options="attributesOptions" v-model="attributeA" class="grid"></select-hex-button>
        </div>
    </div>
</template>

<script lang="ts">
  import HexButton from '@/components/HexButton.vue';
  import SelectHexButton from '@/components/SelectHexButton.vue';
  import {Component, Vue} from 'vue-property-decorator';
  import HelloWorld from './components/HelloWorld.vue';

  @Component({
    components: {
      SelectHexButton,
      HelloWorld,
      HexButton,
    },
  })
  export default class App extends Vue {
    burstA: number = 0;
    attributeA: number = 0;
    diceOptions = [...Array(6).keys()].map(value => ({value: value + 1}));
    attributesOptions = [...Array(30).keys()].map(value => ({value: value + 1}));
  }
</script>

<style lang="scss">
    #app {
        font-family: "Roboto Condensed", "Helvetica Neue", Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        display: flex;
        flex-flow: column;
    }

    .grid {
        $hex-max-by-row: 6;
        display: grid;
        grid-template-columns: repeat($hex-max-by-row * 2 + 1, $hex-button-width / 2);
        grid-template-rows: $hex-button-y1 repeat(ceil(30 / $hex-max-by-row), $hex-button-y2 $hex-button-y1);
        justify-content: center;


        .select-hex-button {
            grid-column-end: span 2;
            grid-row-end: span 3;
        }


        @for $i from 0 to 30 {
            .select-hex-button.select-hex-button-#{($i+1)} {
                color: $i % ($hex-max-by-row*2);
                @if $i % ($hex-max-by-row*2) < $hex-max-by-row {
                    // odd rows
                    grid-column-start: ($i % $hex-max-by-row) * 2 + 1;
                } @else {
                    // even rows
                    grid-column-start: ($i % $hex-max-by-row) * 2 + 2;
                }
                grid-row-start: floor($i / $hex-max-by-row) * 2 + 1;
            }
        }
    }
</style>
