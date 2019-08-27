import PlayerDmgInput from '@/definitions/PlayerDmgInput';
import PlayerHitInput from '@/definitions/PlayerHitInput';

export default interface PlayerInput {
  dmgConf?: PlayerDmgInput;
  hitConf?: PlayerHitInput;
}