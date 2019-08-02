<template>
    <div class="roll-result">
        <h5>{{input.playerA.burst}} dice at {{input.playerA.attribute}} VS {{input.playerB.burst}} dice at {{input.playerB.attribute}}</h5>
        <div class="bars">
            <div class="player-a crit" :style="{width:percentData.playerACrit+'%'}">{{percentData.playerACrit}}</div>
            <div class="player-a hit" :style="{width:percentData.playerAHit+'%'}">{{percentData.playerAHit}}</div>
            <div class="nothing" :style="{width:percentData.nothing+'%'}">{{percentData.nothing}}</div>
            <div class="player-b hit" :style="{width:percentData.playerBHit+'%'}">{{percentData.playerBHit}}</div>
            <div class="player-b crit" :style="{width:percentData.playerBCrit+'%'}">{{percentData.playerBCrit}}</div>
        </div>
        <div class="details">
            <span>{{percentData.playerACrit + percentData.playerAHit}}%</span><span>FtF</span><span>{{percentData.playerBCrit +
            percentData.playerBHit}}%</span>
            <span>{{percentData.playerACrit}}%</span><span>Crits</span><span>{{percentData.playerBCrit}}%</span>
            <span>{{percentData.playerAHit}}%</span><span>Hits</span><span>{{percentData.playerBHit}}%</span>
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
        margin: 0.5em;
        background: #925016;

        .bars {
            width: 100%;
            height: 3em;
            display: flex;

            > div {
                height: 100%;
                overflow: hidden;
            }

            .player-a.crit {
                background: blue;
            }

            .player-a.hit {
                background: lightblue;
            }

            .nothing {
                background: grey;
            }

            .player-b.hit {
                background: orange;
            }

            .player-b.crit {
                background: red;
            }
        }

        .details {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
        }
    }
</style>

