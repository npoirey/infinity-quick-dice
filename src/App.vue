<template>
    <div id="app">
        <div class="headers">
            <vue-snotify></vue-snotify>
        </div>
        <div class="main" id="main">
            <player-conf-panel :player-name="'A'" id="player-conf-panel-a" class="player-conf-panel-a" v-model="value.playerA"></player-conf-panel>
            <player-conf-panel :player-name="'B'" id="player-conf-panel-b" class="player-conf-panel-b" v-model="value.playerB"></player-conf-panel>
        </div>
        <div class="actions">
            <div>
                <action-button @click="clear()" :label="'clear all results'"></action-button>
                <action-button @click="reset()" :label="'reset selections'"></action-button>
                <action-button @click="roll()" :label="'roll the dice !'"></action-button>
            </div>
        </div>
        <div class="results">
            <transition-group name="results" tag="div">
                <roll-result v-for="result in results" :input="result" :key="result.key"></roll-result>
            </transition-group>
        </div>
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
        });
      }
      return !invalid;
    }

    roll() {
      if (this.checkForm()) {
        this.results.unshift({
          key: new Date().toString(),
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
      let firstPanel = document && document.getElementById('main');
      setTimeout(() => {
        if (firstPanel != null) {
          return firstPanel.scroll({
            behavior: 'smooth',
            left: 0,
            top: 0,
          });
        }
      });

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
        height: 100%;
        margin: auto;
        display: grid;
        grid-template-columns: 100%;
        grid-template-rows: auto auto auto auto;
        grid-template-areas: "header" "main" "actions" "results";

        .main {
            grid-area: main;
            display: grid;
            @media only screen and (max-width: 600px) {
                // smartphone
                overflow-x: auto;
                overflow-y: visible;
                grid-template-columns: 100% 100%;
                grid-template-rows: 1fr;
                scroll-snap-coordinate: 0 0;
                scroll-snap-points-x: repeat(100%);
                scroll-snap-type: x mandatory;
                -webkit-overflow-scrolling: touch;
            }
            @media only screen and (min-width: 601px) {
                // PC
                grid-template-columns: 1fr 1fr;
                grid-template-rows: 1fr;
            }


            .player-conf-panel-a {
                background-color: $panel-a-background-color;
                scroll-snap-align: start;
            }

            .player-conf-panel-b {
                background-color: $panel-b-background-color;
                scroll-snap-align: start;
            }
        }

        .actions {
            margin: 0.5em 0;

            > div {
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
        }

        .results {
            .roll-result {
                display: block;
                transition: all 1s;
                max-height: 6em;
                transform: scale(1);
                margin: 0 0 0.5em;
                overflow: hidden;
            }

            .results-enter, .results-leave-to /* .list-leave-active below version 2.1.8 */
            {
                opacity: 0;

                padding: 0;
                max-height: 0;
                margin: 0;
                transform: scale(0);
            }

            .results-move {
                transition: transform 1s;
            }

            grid-area: results;
            overflow: auto;
        }

        h2 {
            margin-top: 0.25em;
            margin-bottom: .25em;
        }

    }

</style>
