export class PlayerInfo {

  /**
   * unique id of the player
   */
  playerId!: string;

  /**
   * name of the player in the game
   */
  playerName!: string;

  /**
   * number of total matches that player played
   */
  totalMatch!: number;

  /**
   * number of win matches that player played
   */
  winMatch!: number;

  /**
   * number of tie matches that player played
   */
  tieMatch!: number;

  /**
   * error message
   */
  errorMsg!: string;

  constructor() {
  }
}
