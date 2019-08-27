<template>
    <div class="player-hit-conf-panel">
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
  import PlayerHitInput from '@/definitions/PlayerHitInput';
  import {Component, Prop, Vue} from 'vue-property-decorator';

  @Component({
      components: {
        SelectHexButton,
      },
    },
  )
  export default class PlayerHitConfPanel extends Vue {
    @Prop({default: ''}) private playerName!: string;
    @Prop({default: null}) private value!: PlayerHitInput;

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
    .player-hit-conf-panel {
        display: flex;
        flex-flow: column;
        padding: 0.5em;
    }
</style>
