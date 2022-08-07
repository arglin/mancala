import {Component, OnInit} from '@angular/core';
import {GameService} from "./game.service";
import {GameInfo} from "./dto/GameInfo";
import {PlayerInfo} from "./dto/PlayerInfo";
import {GameStatus} from "./dto/GameStatus";

@Component({
    selector: 'app-mancala',
    templateUrl: './mancala.component.html',
    styleUrls: ['./mancala.component.scss']
})
export class MancalaComponent implements OnInit {

    selectedPit: any;
    gameInfo!: GameInfo;
    myInfo!: PlayerInfo;

    isConnected: boolean = false;
    isConnecting: boolean = false;
    isFinished: any = true;

    maxReconnectTime: number = 15;

    constructor(public gameService: GameService) {
    }

    ngOnInit(): void {
        this.gameService.onConnectionStatus().subscribe(
            (isConnected) => {
                if (isConnected) {
                    this.isConnected = true;
                    this.gameService.subscribeGameInfo();
                } else {
                    //handle disconnected
                    alert("lost connection to game serve");
                    window.location.reload();
                    this.isConnected = false;
                }
            }
        )

        this.gameService.onReceiveGameInfo().subscribe(
            (gameInfo: GameInfo) => {
                console.log("gameInfo = ", gameInfo);
                this.gameInfo = gameInfo;
                this.myInfo = this.gameInfo.me;

                // @ts-ignore
                this.isFinished = gameInfo.gameStatus == GameStatus[GameStatus.OPPONENT_QUIT] ||
                    // @ts-ignore
                    this.gameInfo.gameStatus == GameStatus[GameStatus.TIE] ||
                    // @ts-ignore
                    this.gameInfo.gameStatus == GameStatus[GameStatus.WIN] ||
                    // @ts-ignore
                    this.gameInfo.gameStatus == GameStatus[GameStatus.LOSE] ||
                    // @ts-ignore
                    this.gameInfo.gameStatus == GameStatus[GameStatus.CANCEL] ||
                    // @ts-ignore
                    this.gameInfo.gameStatus == GameStatus[GameStatus.I_QUIT];
            }
        )
    }

    ngAfterViewInit() {
        this.connect();
    }

    connect() {
        if (!this.isConnected) {
            this.isConnecting = true;
            let connectInterval = setInterval(() => {
                if (this.isConnected || this.maxReconnectTime <= 0) {
                    clearInterval(connectInterval);
                    this.isConnecting = false;
                }
                this.maxReconnectTime--;
                this.gameService.connect();
            }, 1000);
        }
    }

    disconnect() {
        this.gameService.disconnect();
    }

    joinGame() {
       this.gameService.join();
    }

    sow(pitIndex: number) {
        this.gameService.sow(pitIndex + "");
    }
}
