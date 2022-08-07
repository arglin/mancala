import {Injectable} from '@angular/core';
import * as Stomp from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';
import {Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {GameInfo} from "./dto/GameInfo";

@Injectable({
    providedIn: 'root'
})
export class GameService {

    private config: any;
    private endpoint!: string;
    private gameInfoBroker!: string;
    private sowDestPrefix!: string;
    private joinDestPrefix!: string;

    private webSocket!: WebSocket;
    private stompClient!: Stomp.CompatClient;
    public isConnected: boolean = false;

    private $onReceiveGameInfo: Subject<GameInfo> = new Subject();
    private $onConnectionStatus: Subject<boolean> = new Subject<boolean>();

    constructor(private http: HttpClient) {
        this.loadConfig();
    }

    init() {
        this.endpoint = `${this.config.endpoint}`;
        this.gameInfoBroker = `${this.config.gameInfoBroker}`;
        this.sowDestPrefix = `${this.config.sowDestPrefix}`;
        this.joinDestPrefix = `${this.config.joinDestPrefix}`;
    }

    private loadConfig() {
        return this.http.get<any>('/assets/config.json').subscribe(
            data => {
                this.config = data;
                this.init();
            }
        )
    }

    public onConnectionStatus(): Subject<boolean> {
        return this.$onConnectionStatus;
    }

    public onReceiveGameInfo(): Subject<GameInfo> {
        return this.$onReceiveGameInfo;
    }

    public connect() {
        if (this.isConnected) {
            return;
        }
        this.webSocket = new SockJS(this.endpoint);
        this.stompClient = Stomp.Stomp.over(this.webSocket);
        this.stompClient.connect({}, (frame: any) => {
            console.log("connected with server");
            if (!this.isConnected) {
                this.isConnected = true;
                this.$onConnectionStatus.next(true);
            }
        }, () => {
        }, () => {
            console.log("no connection with server");
            if (this.isConnected) {
                this.$onConnectionStatus.next(false);
            }
            this.isConnected = false;
        });
    }

    public disconnect() {

        if (!this.isConnected) {
            console.log("already disconnected.")
            return;
        }
        this.stompClient.disconnect(() => {
            this.isConnected = false;
            this.$onConnectionStatus.next(this.isConnected);
        });
    }

    public subscribeGameInfo() {

        if (!this.isConnected) {
            console.log("not connected.")
            return;
        }
        this.stompClient.subscribe(this.gameInfoBroker, (response: any) => {
            console.log("receive game info : ", response.body);
            this.$onReceiveGameInfo.next(JSON.parse(response.body));
        });
    }

    public sow(pitIndex: string): boolean {

        if (!this.isConnected) {
            console.log("no connection.")
            return false;
        }
        this.stompClient.send(
            this.sowDestPrefix,
            {},
            pitIndex
        )

        return true;
    }

    public join(): boolean {

        if (!this.isConnected) {
            alert("not connected with server! Please try again later.");
            this.connect();
            return false;
        }
        this.stompClient.send(
            this.joinDestPrefix
        )
        return true;
    }
}
