<template>
    <div class="player-dmg-conf-panel">
        <h2>
            <font-awesome-icon icon="shield" />
            Player {{playerName}} Armor
        </h2>
        <select-hex-button class="grid-12" :options="armorOptions" :value="value.armor" @input="updateArmor"></select-hex-button>
        <h2>
            <font-awesome-icon icon="sword" />
            Player {{playerName}} Damage
        </h2>
        <select-hex-button class="grid-12" :options="damageOptions" :value="value.damage" @input="updateDamage"></select-hex-button>
    </div>
</template>

<script lang="ts">
  import SelectHexButton from '@/components/SelectHexButton.vue';
  import PlayerDmgInput from '@/definitions/PlayerDmgInput';
  import {Component, Prop, Vue} from 'vue-property-decorator';

  @Component({
      components: {
        SelectHexButton,
      },
    },
  )
  export default class PlayerDmgConfPanel extends Vue {
    @Prop({default: ''}) private playerName!: string;
    @Prop({default: null}) private value!: PlayerDmgInput;

    damageOptions = [...Array(12).keys()].map(value => ({value: value + 7}));
    armorOptions = [...Array(12).keys()].map(value => ({value: value}));
    isComplete: boolean = false;

    isValueComplete(value: PlayerDmgInput): boolean {
      this.isComplete = Boolean(value.damage && value.armor !== undefined && value.armor !== null && value.ammunition);
      return this.isComplete;
    }

    emitValue(newValue: PlayerDmgInput) {
      this.$emit('input', newValue);
      if (!this.isComplete && this.isValueComplete(newValue)) {
        this.$emit('becomeComplete');
      }
    }

    updateArmor(armor: number) {
      const newValue: PlayerDmgInput = {
        ...this.value,
        armor: armor,
      };
      this.emitValue(newValue);
    }

    updateDamage(damage: number) {
      const newValue: PlayerDmgInput = {
        ...this.value,
        damage: damage,
      };
      this.emitValue(newValue);
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    .player-dmg-conf-panel {
        display: flex;
        flex-flow: column;
        padding: 0.5em;
    }
</style>
