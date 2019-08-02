<template>
    <div class="player-conf-panel">
        <h2>Player {{playerName}} Burst</h2>
        <select-hex-button :options="diceOptions" v-model="value.burst" @input="updateValue()"></select-hex-button>
        <h2>Player {{playerName}} rolls on</h2>
        <select-hex-button class="grid" :options="attributesOptions" v-model="value.attribute" ></select-hex-button>
    </div>
</template>

<script lang="ts">
  import SelectHexButton from '@/components/SelectHexButton.vue';
  import PlayerInput from '@/definitions/PlayerInput';
  import {Component, Prop, Vue} from 'vue-property-decorator';

  @Component({
      components: {
        SelectHexButton,
      },
    },
  )
  export default class HexButton extends Vue {
    @Prop({default: ''}) private playerName!: string;
    @Prop({default: null}) private value!: PlayerInput;

    diceOptions = [...Array(6).keys()].map(value => ({value: value + 1}));
    attributesOptions = [...Array(30).keys()].map(value => ({value: value + 1}));

    updateValue() {
      this.$emit('input', {
        ...this.value
      })
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    .player-conf-panel {
        display: flex;
        flex-flow: column;
    }
    .grid {
        $hex-max-by-row: 6;
        display: grid;
        grid-template-columns: repeat($hex-max-by-row * 2 + 1, $hex-button-width / 2);
        grid-template-rows: $hex-button-y1 repeat(ceil(30 / $hex-max-by-row), $hex-button-y2 $hex-button-y1);
        justify-content: center;


        &::v-deep .select-hex-button {
            grid-column-end: span 2;
            grid-row-end: span 3;
        }


        @for $i from 0 to 30 {
            &::v-deep .select-hex-button.select-hex-button-#{($i+1)} {
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
