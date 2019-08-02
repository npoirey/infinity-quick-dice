<template>
    <div>
        <h5>{{input.playerA.burst}} dice at {{input.playerA.attribute}} VS {{input.playerB.burst}} dice at {{input.playerB.attribute}}</h5>
    </div>
</template>

<script lang="ts">
  import RollResultInput from '@/definitions/RollResultInput';
  import {Component, Prop, Vue} from 'vue-property-decorator';

  @Component
  export default class HexButton extends Vue {
    @Prop({default: null}) private input!: RollResultInput;
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">

</style>

