import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {GameInfo} from "../dto/GameInfo";
import {GameStatus} from "../dto/GameStatus";

@Component({
    selector: 'app-game-board',
    templateUrl: './game-board.component.html',
    styleUrls: ['./game-board.component.scss']
})
export class GameBoardComponent implements OnInit {

    @Input() gameInfo!: GameInfo;

    @Output() sowEvent = new EventEmitter<number>();

    gameStatusText: string = "";
    gameStatus!: GameStatus;

    opponentBigPit: number = 0;
    opponentPits: any;
    opponentName!: string;
    opponentTotalMatch: number = 0;
    opponentWinMatch: number = 0;
    opponentTieMatch: number = 0;

    myBigPit: number = 0;
    myPits: any;
    myName!: string;
    myTotalMatch: number = 0;
    myWinMatch: number = 0;
    myTieMatch: number = 0;

    isFinish: boolean = false;
    isWaiting: boolean = false;

    constructor() {
    }

    ngOnInit(): void {
    }

    ngOnChanges(changes: SimpleChanges) {
        let newGameInfo = changes['gameInfo'].currentValue;
        console.log("gameInfo updated!", newGameInfo);
        if (newGameInfo === undefined) return;

        let me = newGameInfo.me;
        let opponent = newGameInfo.opponent;
        let gameBoard = newGameInfo.gameBoard;
        let gameStatus = newGameInfo.gameStatus;

        // update my info
        if (me != undefined) {
            this.myName = me.playerName;
            this.myTotalMatch = me.totalMatch;
            this.myWinMatch = me.winMatch;
            this.myTieMatch = me.tieMatch;
        }

        //update opponent's info
        if (opponent != undefined) {
            this.opponentName = opponent.playerName;
            this.opponentTotalMatch = opponent.totalMatch;
            this.opponentWinMatch = opponent.winMatch;
            this.opponentTieMatch = opponent.tieMatch;
        }

        // update gameBoard
        if (gameBoard != undefined) {
            this.myPits = gameBoard.myPits;
            this.myBigPit = gameBoard.myBigPit;
            this.opponentPits = gameBoard.opponentPits;
            this.opponentBigPit = gameBoard.opponentBigPit;
        }

        this.gameStatusUpdated(gameStatus);
    }

    private gameStatusUpdated(gameStatus: string): void {

        this.isFinish = false;
        this.isWaiting = false;
        switch (gameStatus) {
            case GameStatus[GameStatus.CANCEL]:
                this.isFinish = true;
                this.gameStatusText = "game is canceled";
                break;
            case GameStatus[GameStatus.MY_TURN]:
                this.gameStatusText = "my turn";
                break;
            case GameStatus[GameStatus.OPPONENT_TURN]:
                this.gameStatusText = this.opponentName + " turn";
                break;
            case GameStatus[GameStatus.WAITING_FOR_OPPONENT]:
                this.isWaiting = true;
                this.gameStatusText = "waiting for opponent..."
                break;
            case GameStatus[GameStatus.LOSE]:
                this.isFinish = true;
                this.gameStatusText = "LOSE~~"
                break;
            case GameStatus[GameStatus.WIN]:
                this.isFinish = true;
                this.gameStatusText = "WIN!!!"
                break;
            case GameStatus[GameStatus.TIE]:
                this.isFinish = true;
                this.gameStatusText = "TIE"
                break;
            case GameStatus[GameStatus.OPPONENT_QUIT]:
                this.isFinish = true;
                this.gameStatusText = "opponent quit!"
                break;
            case GameStatus[GameStatus.I_QUIT]:
                this.isFinish = true;
                this.gameStatusText = "I quit"
                break;
            default:
                this.gameStatusText = "unrecognized game statue";
                break;
        }
    }

    sow(pitIndex: number) {
        this.sowEvent.emit(pitIndex);
    }
}
