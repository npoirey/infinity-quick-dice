<template>
    <div class="player-conf-panel">
        <h2>
            <font-awesome-icon icon="dice-d20" />
            Player {{playerName}} Burst
        </h2>
        <select-hex-button class="grid-6" :options="diceOptions" :value="value.burst" @input="updateBurst"></select-hex-button>
        <h2>
            <font-awesome-icon icon="crosshairs"/>
            Player {{playerName}} rolls on
        </h2>
        <select-hex-button class="grid-30" :options="attributesOptions" :value="value.attribute" @input="updateAttribute"></select-hex-button>
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

    updateBurst(value: number) {
      let wasInvalid = !this.value.attribute || !this.value.burst;
      let isInvalid = !this.value.attribute || !value;
      this.$emit('input', {
        ...this.value,
        burst: value,
      });
      if (wasInvalid && !isInvalid) {
        this.$emit('becomeValid');
      }
    }

    updateAttribute(value: number) {
      let wasInvalid = !this.value.attribute || !this.value.burst;
      let isInvalid = !this.value.burst || !value;
      this.$emit('input', {
        ...this.value,
        attribute: value,
      });
      if (wasInvalid && !isInvalid) {
        this.$emit('becomeValid');
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    .player-conf-panel {
        display: flex;
        flex-flow: column;
        padding: 0.5em;
    }

    @mixin hex-grid($hex-max-by-row, $total) {
        display: grid;
        grid-template-columns: repeat($hex-max-by-row * 2 + 1, $hex-button-width / 2);
        grid-template-rows: $hex-button-y1 repeat(ceil($total / $hex-max-by-row), $hex-button-y2 $hex-button-y1);
        justify-content: center;

        &::v-deep .select-hex-button {
            grid-column-end: span 2;
            grid-row-end: span 3;
        }

        @for $i from 0 to $total {
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

    .grid-6 {
        @include hex-grid(6, 6);
    }

    .grid-30 {
        @include hex-grid(6, 30);
    }
</style>
