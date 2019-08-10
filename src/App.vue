<template>
    <div id="app">
        <div class="headers">
            <transition>
                <div class="loader" v-if="loadingState.state !== 'done'">
                    <div class="loading-bar" :style="{width: (loadingState.download ? loadingState.download : 0) + '%'}"></div>
                    <div class="loading-title">
                        <div class="loading-title-main">Loading data...</div>
                        <div class="loading-title-sub">
                            <span v-if="loadingState.state === 'downloading'">downloading ({{loadingState.download.toFixed(2)}}%)</span>
                            <span v-else>extracting data</span>
                        </div>
                    </div>
                </div>
            </transition>
        </div>
        <div class="main" id="main">
            <player-conf-panel :player-name="'A'" id="player-conf-panel-a" class="player-conf-panel-a" v-model="playerA"
                               @becomeValid="scrollMainPanel(1)"></player-conf-panel>
            <player-conf-panel :player-name="'B'" id="player-conf-panel-b" class="player-conf-panel-b" v-model="playerB"></player-conf-panel>
        </div>
        <div class="actions">
            <action-button @click="clear()" :label="'clear all results'"></action-button>
            <action-button @click="reset()" :label="'reset selections'"></action-button>
            <action-button @click="roll()" :label="'roll the dice !'"></action-button>
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
  import PlayerInput from '@/definitions/PlayerInput';
  import RollResultInput from '@/definitions/RollResultInput';
  import {Component, Vue} from 'vue-property-decorator';
  import {LoadingState, loadingStateObservable, loadRolls} from './services/RollService';

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
    playerA: PlayerInput = {};
    playerB: PlayerInput = {};
    loadingState: LoadingState = {
      state: 'done',
      download: 0,
    };

    constructor() {
      super();
      loadRolls();
      loadingStateObservable.subscribe(this.setLoading);
    }

    setLoading(value: LoadingState) {
      this.loadingState = value;
    }

    checkForm(): boolean {
      let canProceed = true;
      if (this.loadingState.state !== 'done') {
        Vue.toasted.show('Please wait the end of data loading', {
          type: 'error',
        });
        canProceed = false;
      } else if (!this.playerA || !this.playerA.burst || !this.playerA.attribute || !this.playerB || !this.playerB.burst || !this.playerB.attribute) {
        Vue.toasted.show('at least burst and roll are needed for both players', {
          type: 'error',
        });
        canProceed = false;
      }
      return canProceed;
    }

    roll() {
      if (this.checkForm()) {
        this.results.unshift({
          key: this.results.length,
          playerA: {
            ...this.playerA,
          },
          playerB: {
            ...this.playerB,
          },
        });
      }
    }

    reset() {
      this.playerA = {};
      this.playerB = {};
      this.scrollMainPanel(0);
    }

    scrollMainPanel(index: number) {
      let firstPanel = document && document.getElementById('main');
      if (firstPanel != null) {
        firstPanel.scroll({
          behavior: 'smooth',
          left: index * firstPanel.offsetWidth,
          top: 0,
        });
      }
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

        .headers {

            .loader {
                $loader-height: 3em;
                height: $loader-height;
                transition: height 0.5s ease-in-out;
                background: $hover-background-color;

                .loading-bar {
                    background: $active-background-color;
                    height: 100%;
                    transition: width 0.5s ease-out;
                }

                .loading-title {
                    font-family: Montalban;
                    opacity: 1;
                    height: $loader-height;
                    overflow: hidden;
                    transition: all 0.5s ease-in-out;
                    text-shadow: black 1px 0 0 0;
                    position: absolute;
                    top: 0.25em;
                    width: 100%;
                    display: inline-block;
                    transform: translateX(-50%);
                    .loading-title-sub{
                        font-size: x-small;
                    }
                }

                &.v-enter, &.v-leave-to {
                    height: 0;

                    .loading-title {
                        opacity: 0;
                        height: 0;
                    }
                }

            }

        }

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

        @media only screen and (max-width: 600px) {
            .actions, .results {
                padding-left: 0.5em;
                padding-right: 0.5em;
            }
        }

        .actions {
            margin: 0.5em 0;
            grid-area: actions;
            display: flex;
            justify-content: space-between;

            .action-button {
                flex-basis: 0;
                flex-grow: 1;
                margin-right: 0.5em;

                &:last-of-type {
                    margin-right: 0;
                }
            }
        }

        .results {
            .roll-result {
                display: block;
                transition: all 0.5s;
                max-height: 6em;
                transform: scale(1);
                margin: 0 0 0.5em;
                overflow: hidden;
            }

            .results-enter, .results-leave-to {
                opacity: 0;
                padding: 0;
                max-height: 0;
                margin: 0;
                transform: scale(0);
            }

            .results-move {
                transition: transform 0.5s;
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
