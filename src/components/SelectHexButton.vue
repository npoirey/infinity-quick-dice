<template>
    <div class="select-hex-buttons">
        <hex-button v-for="(option, i) in this.options" :key="option.label" @click="onOptionSelect(option)"
                    :class="{active: value === option.value, 'select-hex-button': true, ['select-hex-button-'+(i+1)]: true}"
                    :label="option.label ? option.label : option.value"></hex-button>
    </div>
</template>

<script lang="ts">
  import HexButton from '@/components/HexButton.vue';
  import {Component, Prop, Vue} from 'vue-property-decorator';

  @Component({
    components: {HexButton},
  })
  export default class SelectHexButton extends Vue {
    @Prop({default: null})
    value: any;

    @Prop({default: []})
    private options!: Option[];

    onOptionSelect(option: Option) {
      this.$emit('input', option.value);
    };
  }

  export interface Option {
    label: string;
    value: any;
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">

</style>
