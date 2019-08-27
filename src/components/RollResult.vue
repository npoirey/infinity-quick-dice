<template>
    <div class="roll-result">
        <div class="playerA player-conf">
            <player-conf-value icon="dice-d20" :value="input.playerA.hitConf.burst"></player-conf-value>
            <player-conf-value icon="crosshairs" :value="input.playerA.hitConf.attribute"></player-conf-value>
            <player-conf-value v-if="input.playerA.dmgConf.armor" icon="shield" :value="input.playerA.dmgConf.armor"></player-conf-value>
            <player-conf-value v-if="input.playerA.dmgConf.damage" icon="sword" :value="input.playerA.dmgConf.damage"></player-conf-value>
        </div>
        <div class="ftf-grid"
             :style="{'grid-template-columns': `${percentData.playerACrit}% ${percentData.playerAHit}% auto ${percentData.playerBHit}% ${percentData.playerBCrit}%`}">
            <div class="ftf-grid-legend ftf-grid-legend-top">
                <span class="player-a">Player A hit ({{(percentData.playerACrit + percentData.playerAHit).toFixed(2)}}%)</span>
                <span class="player-b">Player B hit ({{(percentData.playerBCrit + percentData.playerBHit).toFixed(2)}}%)</span>
            </div>
            <div class="ftf-grid-legend-bar ftf-grid-legend-bar-top ftf-grid-legend-bar-player-a"></div>
            <div class="ftf-grid-legend-bar ftf-grid-legend-bar-top ftf-grid-legend-bar-player-b"></div>
            <div class="ftf-grid-bar ftf-grid-bar-player-a crit"></div>
            <div class="ftf-grid-bar ftf-grid-bar-player-a hit"></div>
            <div class="ftf-grid-bar ftf-grid-bar-nothing"></div>
            <div class="ftf-grid-bar ftf-grid-bar-player-b hit"></div>
            <div class="ftf-grid-bar ftf-grid-bar-player-b crit"></div>
            <div class="ftf-grid-legend-bar ftf-grid-legend-bar-bottom ftf-grid-legend-bar-player-a"></div>
            <div class="ftf-grid-legend-bar ftf-grid-legend-bar-bottom ftf-grid-legend-bar-player-b"></div>
            <tr class="ftf-grid-legend ftf-grid-legend-bottom">
                <span class="player-a">Player A Crit ({{percentData.playerACrit.toFixed(2)}}%)</span>
                <span class="player-b">Player B Crit ({{percentData.playerBCrit.toFixed(2)}}%)</span>
            </tr>
        </div>
        <div class="playerB player-conf">
            <player-conf-value icon="dice-d20" :value="input.playerB.hitConf.burst" :reverse="true"></player-conf-value>
            <player-conf-value icon="crosshairs" :value="input.playerB.hitConf.attribute" :reverse="true"></player-conf-value>
            <player-conf-value v-if="input.playerB.dmgConf.armor" icon="shield" :value="input.playerB.dmgConf.armor"
                               :reverse="true"></player-conf-value>
            <player-conf-value v-if="input.playerB.dmgConf.damage" icon="sword" :value="input.playerB.dmgConf.damage"
                               :reverse="true"></player-conf-value>
        </div>
        {{dmgResults}}
    </div>
</template>

<script lang="ts">
  import PlayerConfValue from '@/components/PlayerConfValue.vue';
  import RollResultInput from '@/definitions/RollResultInput';
  import {getRollResult} from '@/services/RollService';
  import {Component, Prop, Vue, Watch} from 'vue-property-decorator';

  interface HittingData {
    crits: number,
    hits: number,
  }

  @Component({
    components: {PlayerConfValue},
  })
  export default class HexButton extends Vue {
    @Prop({default: null}) private input!: RollResultInput;
    data: any = null;
    percentData: any = {
      playerACrit: 0,
      playerAHit: 0,
      nothing: 0,
      playerBCrit: 0,
      playerBHit: 0,
    };
    dmgResults: any = {};
    total: number = 0;

    @Watch('input', {immediate: true, deep: true})
    async onInputChange(newVal: RollResultInput) {
      if (newVal && newVal.playerA && newVal.playerB && newVal.playerA.hitConf && newVal.playerB.hitConf && newVal.playerA.hitConf.burst && newVal.playerB.hitConf.burst) {
        const invert = newVal.playerA.hitConf.burst > newVal.playerB.hitConf.burst;
        const key = invert ?
                    newVal.playerB.hitConf.burst + 'dicesat' + newVal.playerB.hitConf.attribute + 'vs' + newVal.playerA.hitConf.burst + 'dicesat' + newVal.playerA.hitConf.attribute :
                    newVal.playerA.hitConf.burst + 'dicesat' + newVal.playerA.hitConf.attribute + 'vs' + newVal.playerB.hitConf.burst + 'dicesat' + newVal.playerB.hitConf.attribute;
        console.log('loading data from' + key + ', invert=' + invert);
        const results = await getRollResult(key);
        this.loadData(results, invert);
      }
    }

    loadData(rawData: { [s: string]: number }, invert: boolean) {
      let ftFResult: { [key: string]: number } = {
        playerACrit: 0,
        playerAHit: 0,
        nothing: 0,
        playerBCrit: 0,
        playerBHit: 0,
      };
      let percentData = {...ftFResult};
      let woundsToCompute = 2;
      let dmgResults: any = {
        playerAWin: {},
        nothing: 0,
        playerBWin: {},
      };
      this.total = Object.keys(rawData).reduce((acc, currentKey) => acc + rawData[currentKey], 0);
      for (let key of Object.keys(rawData)) {
        let strings = key.replace(/c/g, '').split('v');
        let firstStringSplitted = strings[0].split('h');
        let secondStringSplitted = strings[1].split('h');
        let throwResult = {
          firstPlayer: {
            crits: parseInt(firstStringSplitted[0]),
            hits: parseInt(firstStringSplitted[1]),
          },
          secondPlayer: {
            crits: parseInt(secondStringSplitted[0]),
            hits: parseInt(secondStringSplitted[1]),
          },
        };
        let firstPlayer = invert ? 'B' : 'A';
        let secondPlayer = invert ? 'A' : 'B';
        let hittingPlayer: string | null;
        let hittingData: HittingData | null;
        let scenarioProbability = (rawData[key] / this.total * 100);
        // computing ftf
        if (throwResult.firstPlayer.crits > 0) {
          ftFResult[`player${firstPlayer}Crit`] += rawData[key];
          percentData[`player${firstPlayer}Crit`] += scenarioProbability;
          hittingPlayer = firstPlayer;
          hittingData = throwResult.firstPlayer;
        } else if (throwResult.firstPlayer.hits > 0) {
          ftFResult[`player${firstPlayer}Hit`] += rawData[key];
          percentData[`player${firstPlayer}Hit`] += scenarioProbability;
          hittingPlayer = firstPlayer;
          hittingData = throwResult.firstPlayer;
        } else if (throwResult.secondPlayer.crits > 0) {
          ftFResult[`player${secondPlayer}Crit`] += rawData[key];
          percentData[`player${secondPlayer}Crit`] += scenarioProbability;
          hittingPlayer = secondPlayer;
          hittingData = throwResult.secondPlayer;
        } else if (throwResult.secondPlayer.hits > 0) {
          ftFResult[`player${secondPlayer}Hit`] += rawData[key];
          percentData[`player${secondPlayer}Hit`] += scenarioProbability;
          hittingPlayer = secondPlayer;
          hittingData = throwResult.secondPlayer;
        } else {
          ftFResult['nothing'] += rawData[key];
          percentData[`nothing`] += scenarioProbability;
          hittingPlayer = null;
          hittingData = null;
        }
        // computing wounds
        if (hittingData) {
          let nbWoundsFromCrits = hittingData.crits;
          let nbSaveRolls = hittingData.hits;
          let subProbasOfWounds: {[key: string]: number} = {};
          for (let i = 0; i < woundsToCompute; i++) {
            // compute probability of exactly i wounds inflicted for the current scenario
            if (nbWoundsFromCrits > i) {
              subProbasOfWounds[`${i}`] = 0;
            } else {
              // todo 'proba de (i-nbWoundsFromCrits) jets de sauvegarde ratés'
              subProbasOfWounds[`${i}`] = (1 - 0.2) / woundsToCompute;
            }
          }
          // compute the rest (woundsToCompute or more wounds inflicted)
          subProbasOfWounds[`${woundsToCompute}OrMore`] = 1 - Object.keys(subProbasOfWounds).reduce((acc, key) => acc + subProbasOfWounds[key], 0);

          // from this sub probabilities, augment the aggregated values
          console.log(subProbasOfWounds);
          console.log(Object.keys(subProbasOfWounds));
          for(let key of Object.keys(subProbasOfWounds)){

            if(key === '0') {
              dmgResults.nothing += scenarioProbability * subProbasOfWounds[0];
            } else {
              if(!dmgResults[`player${hittingPlayer}Win`][key]){
                dmgResults[`player${hittingPlayer}Win`][key] = 0;
              }
              dmgResults[`player${hittingPlayer}Win`][key] += scenarioProbability * subProbasOfWounds[key];
            }
          }
        } else {
          dmgResults.nothing += scenarioProbability;
        }
      }
      this.data = ftFResult;
      this.percentData = percentData;
      this.dmgResults = dmgResults;
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    .roll-result {
        padding: 0.5em;
        background: $panel-neutral-background-color;
        display: flex;
        flex-flow: row;

        .player-conf {
            font-family: Montalban;
            display: flex;
            flex-flow: column;
            justify-content: center;
        }

        .ftf-grid {
            flex-grow: 1;
            margin: 0 0.5em;
            $legend-bar-size: 8px;
            display: grid;
            grid-template-rows: auto $legend-bar-size 1em $legend-bar-size auto;
            // @formatter:off
            grid-template-areas: "legend-top          legend-top          legend-top    legend-top       legend-top"
                                 "legend-bar-top-a    legend-bar-top-a    .             legend-bar-top-b legend-bar-top-b"
                                 "a-crits             a-hits              nothing       b-hits           b-crits"
                                 "legend-bar-bottom-a .                   .             .                legend-bar-bottom-b"
                                 "legend-bottom       legend-bottom       legend-bottom legend-bottom    legend-bottom";
            // @formatter:on

            .ftf-grid-legend {
                display: flex;
                justify-content: space-between;
                font-size: 0.75em;

                &.ftf-grid-legend-top {
                    grid-area: legend-top;
                }

                &.ftf-grid-legend-bottom {
                    grid-area: legend-bottom;
                }
            }

            .ftf-grid-bar {
                &.ftf-grid-bar-player-a {
                    &.hit {
                        grid-area: a-hits;
                    }

                    &.crit {
                        grid-area: a-crits;
                    }
                }

                &.ftf-grid-bar-player-b {
                    &.hit {
                        grid-area: b-hits;
                    }

                    &.crit {
                        grid-area: b-crits;
                    }
                }

                &.ftf-grid-bar-nothing {
                    grid-area: nothing;
                }
            }

            .ftf-grid-legend-bar {
                border: 1px solid white;
                margin: 2px 0;

                &.ftf-grid-legend-bar-top {
                    border-bottom: none;

                    &.ftf-grid-legend-bar-player-a {
                        grid-area: legend-bar-top-a;
                    }

                    &.ftf-grid-legend-bar-player-b {
                        grid-area: legend-bar-top-b;
                    }
                }

                &.ftf-grid-legend-bar-bottom {
                    border-top: none;

                    &.ftf-grid-legend-bar-player-a {
                        grid-area: legend-bar-bottom-a;
                    }

                    &.ftf-grid-legend-bar-player-b {
                        grid-area: legend-bar-bottom-b;
                    }
                }
            }

            .ftf-grid-bar-player-a {
                background-color: dodgerblue;

                &.crit {
                    background-color: blue;
                }
            }

            .ftf-grid-bar-player-b {
                background-color: orange;

                &.crit {
                    background-color: orangered;
                }
            }

            .ftf-grid-bar-nothing {
                background-color: beige;
            }
        }
    }
</style>

