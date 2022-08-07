import {GameStatus} from "./GameStatus";
import {GameBoard} from "./GameBoard";
import {PlayerInfo} from "./PlayerInfo";

export class GameInfo {
    /**
     * myself
     */
    me!: PlayerInfo;

    /**
     * opponent player
     */
    opponent!: PlayerInfo;

    /**
     *  board of the game
     */
    gameBoard!: GameBoard

    /**
     * status of the game life cycle
     */
    gameStatus!: GameStatus;
}
