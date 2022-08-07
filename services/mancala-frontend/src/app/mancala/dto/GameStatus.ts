export enum GameStatus {

    /**
     * waiting for opponent
     */
    WAITING_FOR_OPPONENT,

    /**
     * my turn
     */
    MY_TURN,

    /**
     * opponent's turn
     */
    OPPONENT_TURN,

    /**
     * I win
     */
    WIN,

    /**
     * opponent win
     */
    LOSE,

    /**
     * game is a tie
     */
    TIE,

    /**
     * I quit
     */
    I_QUIT,

    /**
     * opponent quit
     */
    OPPONENT_QUIT,

    /**
     * game is canceled
     */
    CANCEL
}