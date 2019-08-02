<template>
    <div id="app" style="height: 500px">
        <div class="headers">
            <h1>Select both players data and click Roll !</h1>
        </div>
        <div class="main">
            <player-conf-panel :player-name="'A'" class="player-conf-panel-a" v-model="value.playerA"></player-conf-panel>
            <player-conf-panel :player-name="'B'" class="player-conf-panel-b" v-model="value.playerB"></player-conf-panel>
        </div>
        <div class="actions">
            <div>
                <button @click="clear()">clear all results</button>
                <button @click="reset()">reset selections</button>
                <button @click="roll()">roll the dice !</button>
            </div>
            <div v-if="error">
                {{error}}
            </div>
        </div>
        <div class="results">
            <roll-result v-for="(result, i) in results" :input="result" :key="i"></roll-result>
        </div>
    </div>
</template>


<script lang="ts">
  import HexButton from '@/components/HexButton.vue';
  import PlayerConfPanel from '@/components/PlayerConfPanel.vue';
  import RollResult from '@/components/RollResult.vue';
  import SelectHexButton from '@/components/SelectHexButton.vue';
  import RollResultInput from '@/definitions/RollResultInput';
  import {Component, Vue, Watch} from 'vue-property-decorator';

  @Component({
    components: {
      RollResult,
      PlayerConfPanel,
      SelectHexButton,
      HexButton,
    },
  })
  export default class App extends Vue {
    results: RollResultInput[] = [];
    error: string | null = null;
    value: RollResultInput = {
      playerA: {},
      playerB: {},
    };

    @Watch('value', {deep: true})
    onValueChange(){
      this.error = null;
    }

    checkForm(): boolean {
      let invalid: boolean = !this.value.playerA || !this.value.playerA.burst || !this.value.playerA.attribute || !this.value.playerB || !this.value.playerB.burst || !this.value.playerB.attribute;
      if (invalid) {
        this.error = 'Please select burst and attribute for both players';
      } else {
        this.error = null;
      }
      return !invalid;
    }

    roll() {
      this.error = null;
      if (this.checkForm()) {
        this.results.unshift({
          playerA: {
            ...this.value.playerA,
          },
          playerB: {
            ...this.value.playerB,
          },
        });
      }
    }

    reset() {
      this.value = {
        playerA: {},
        playerB: {},
      };
    }

    clear() {
      this.results = [];
    }
  }
</script>

<style lang="scss">
    #app {
        font-family: "Roboto Condensed", "Helvetica Neue", Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        max-width: 800px;
        margin: auto;
        display: grid;
        grid-template-columns: 100%;
        grid-template-rows: 3em auto 3em auto;
        grid-template-areas: "header" "main" "actions" "results";

        .main {
            grid-area: main;
            display: grid;
            grid-template-columns: 1fr 1fr;
            grid-template-rows: 1fr;
        }

        .actions {
            grid-area: actions;
        }

        .results {
            grid-area: results;
        }

    }

</style>
