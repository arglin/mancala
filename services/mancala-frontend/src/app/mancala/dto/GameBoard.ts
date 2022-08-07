export class GameBoard {
    /**
     * pits of the first player, each value represents the number of the stones on each pit
     */
    myPits!: any;

    /**
     * the number of stones on the big pit of the first player
     */
    myBigPit!: number;

    /**
     * pits of the second player, each value represents the number of the stones on each pit
     */
    opponentPits!: any;

    /**
     * the number of stones on the big pit of the second player
     */
    opponentBigPit!: number;
}
