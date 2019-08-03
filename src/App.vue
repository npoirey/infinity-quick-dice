<template>
    <div id="app" style="height: 500px">
        <div class="headers">
        </div>
        <div class="main">
            <player-conf-panel :player-name="'A'" class="player-conf-panel-a" v-model="value.playerA"></player-conf-panel>
            <player-conf-panel :player-name="'B'" class="player-conf-panel-b" v-model="value.playerB"></player-conf-panel>
        </div>
        <div class="actions">
            <div>
                <action-button @click="clear()" :label="'clear all results'"></action-button>
                <action-button @click="reset()" :label="'reset selections'"></action-button>
                <action-button @click="roll()" :label="'roll the dice !'"></action-button>
            </div>
        </div>
        <div class="results">
            <roll-result v-for="(result, i) in results" :input="result" :key="i"></roll-result>
        </div>
        <vue-snotify></vue-snotify>
    </div>
</template>


<script lang="ts">
  import ActionButton from '@/components/ActionButton.vue';
  import HexButton from '@/components/HexButton.vue';
  import PlayerConfPanel from '@/components/PlayerConfPanel.vue';
  import RollResult from '@/components/RollResult.vue';
  import SelectHexButton from '@/components/SelectHexButton.vue';
  import RollResultInput from '@/definitions/RollResultInput';
  import {Component, Vue} from 'vue-property-decorator';

  @Component({
    components: {
      ActionButton,
      RollResult,
      PlayerConfPanel,
      SelectHexButton,
      HexButton,
    },
  })
  export default class App extends Vue {
    results: RollResultInput[] = [];
    value: RollResultInput = {
      playerA: {},
      playerB: {},
    };

    checkForm(): boolean {
      let invalid: boolean = !this.value.playerA || !this.value.playerA.burst || !this.value.playerA.attribute || !this.value.playerB || !this.value.playerB.burst || !this.value.playerB.attribute;
      if (invalid) {
        this.$snotify.error('Please select burst and attribute for both players', 'Error', {
          timeout: 3000,
          showProgressBar: true,
        })
      }
      return !invalid;
    }

    roll() {
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

            .player-conf-panel-a {
                background-color: $panel-a-background-color;
            }
            .player-conf-panel-b {
                background-color: $panel-b-background-color;
            }
        }

        .actions > div {
            grid-area: actions;
            display: flex;
            justify-content: space-between;
            .action-button {
                margin-right: 0.5em;
                &:last-of-type {
                    margin-right: 0;
                }
            }
        }

        .results {
            grid-area: results;
        }

        h2{
            margin-top: 0.25em;
            margin-bottom: .25em;
        }

    }

</style>
