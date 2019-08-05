<template>
    <div class="roll-result">
        <h5>{{input.playerA.burst}} dice at {{input.playerA.attribute}} VS {{input.playerB.burst}} dice at {{input.playerB.attribute}}</h5>
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
    </div>
</template>

<script lang="ts">
  import RollResultInput from '@/definitions/RollResultInput';
  import {Component, Prop, Vue, Watch} from 'vue-property-decorator';

  @Component
  export default class HexButton extends Vue {
    @Prop({default: null}) private input!: RollResultInput;
    data: any = null;
    percentData: any = null;
    total: number = 0;

    @Watch('input', {immediate: true, deep: true})
    onInputChange(newVal: RollResultInput) {
      if (newVal && newVal.playerA && newVal.playerB && newVal.playerA.burst && newVal.playerB.burst) {
        const invert = newVal.playerA.burst > newVal.playerB.burst;
        const filename = invert ? newVal.playerB.burst + 'v' + newVal.playerA.burst + 'Final.json' : newVal.playerA.burst + 'v' + newVal.playerB.burst + 'Final.json';
        const json = require('@/assets/rolls/' + filename);
        const key = invert ?
                    newVal.playerB.burst + 'dicesat' + newVal.playerB.attribute + 'vs' + newVal.playerA.burst + 'dicesat' + newVal.playerA.attribute :
                    newVal.playerA.burst + 'dicesat' + newVal.playerA.attribute + 'vs' + newVal.playerB.burst + 'dicesat' + newVal.playerB.attribute;
        console.log('loading data from' + key + ', invert=' + invert);
        this.loadData(json[key], invert);
      }
    }

    loadData(rawData: { [s: string]: number }, invert: boolean) {
      console.log(rawData);
      let ftFResult = {
        playerACrit: 0,
        playerAHit: 0,
        nothing: 0,
        playerBCrit: 0,
        playerBHit: 0,
      };
      this.total = 0;
      for (let key of Object.keys(rawData)) {
        let strings = key.replace(/c/g, '').split('v');
        let firstStringSplitted = strings[0].split('h');
        let secondStringSplitted = strings[1].split('h');
        let firstPlayerCritNumber = parseInt(firstStringSplitted[0]);
        let firstPlayerHitNumber = parseInt(firstStringSplitted[1]);
        let secondPlayerCritNumber = parseInt(secondStringSplitted[0]);
        let secondPlayerHitNumber = parseInt(secondStringSplitted[1]);
        if (invert) {
          if (firstPlayerCritNumber > 0) {
            ftFResult.playerBCrit += rawData[key];
          } else if (firstPlayerHitNumber > 0) {
            ftFResult.playerBHit += rawData[key];
          } else if (secondPlayerCritNumber > 0) {
            ftFResult.playerACrit += rawData[key];
          } else if (secondPlayerHitNumber > 0) {
            ftFResult.playerAHit += rawData[key];
          } else {
            ftFResult.nothing += rawData[key];
          }
        } else {
          if (firstPlayerCritNumber > 0) {
            ftFResult.playerACrit += rawData[key];
          } else if (firstPlayerHitNumber > 0) {
            ftFResult.playerAHit += rawData[key];
          } else if (secondPlayerCritNumber > 0) {
            ftFResult.playerBCrit += rawData[key];
          } else if (secondPlayerHitNumber > 0) {
            ftFResult.playerBHit += rawData[key];
          } else {
            ftFResult.nothing += rawData[key];
          }
        }
        this.total += rawData[key];
      }
      this.data = ftFResult;
      this.percentData = {
        playerACrit: parseFloat((ftFResult.playerACrit / this.total * 100).toFixed(2)),
        playerAHit: parseFloat((ftFResult.playerAHit / this.total * 100).toFixed(2)),
        nothing: parseFloat((ftFResult.nothing / this.total * 100).toFixed(2)),
        playerBCrit: parseFloat((ftFResult.playerBCrit / this.total * 100).toFixed(2)),
        playerBHit: parseFloat((ftFResult.playerBHit / this.total * 100).toFixed(2)),
      };
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    .roll-result {
        padding: 0.5em;
        background: $panel-neutral-background-color;
        h5{
            font-family: Montalban;
            margin: 0;
        }
        .ftf-grid {
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

