package com.bol.lirong.mancala.integration;

import com.bol.lirong.mancala.data.response.GameResponse;
import com.bol.lirong.mancala.data.response.GameStatusResponse;
import com.bol.lirong.mancala.settings.MancalaDefault;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Integration test for Mancala game, please make sure the backend server is running before run the test
 *
 * @author linlirong
 * @created 27/02/2022
 * @project mancala
 */
@Slf4j
@SpringBootTest()
public class MancalaGameIntegrationTest {

    @Value("${integration.test.host}")
    private String host;
    private String url = "";

    private static final String SEND_GAME_JOIN = MancalaDefault.APP_DEST_PREFIX + "/join";
    private static final String SEND_GAME_SOW = MancalaDefault.APP_DEST_PREFIX + "/sow";
    private static final String SUBSCRIBE_GAME_PREFIX = MancalaDefault.USER_DEST_PREFIX + MancalaDefault.GAME_INFO_SUB;

    private final BlockingQueue<GameResponse> gameResponseQueueForP1 = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<GameResponse> gameResponseQueueForP2 = new ArrayBlockingQueue<>(10);

    @BeforeEach
    public void setup() {

        this.url = this.host + MancalaDefault.WEBSOCKET_ENDPOINT;
        this.gameResponseQueueForP1.clear();
        this.gameResponseQueueForP2.clear();
    }

    /**
     * handler for the callback of stomp subscription
     */
    private static class MancalaGameStompFrameHandler implements StompFrameHandler {

        Queue<GameResponse> gameResponseQueue;

        MancalaGameStompFrameHandler(Queue<GameResponse> gameResponseQueue) {
            this.gameResponseQueue = gameResponseQueue;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GameResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            log.info(" add to response queue = {}", payload);
            gameResponseQueue.add((GameResponse) payload);
        }
    }

    private List<Transport> createTransportClient() {

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    /**
     * action for new player connect to the server
     */
    private StompSession newPlayerConnect() throws ExecutionException, InterruptedException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(this.createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient.connect(this.url, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
    }

    /**
     * action for first player subscribe game info
     */
    private void firstPlayerSubscribeGameInfo(StompSession playerSession) {
        playerSession.subscribe(SUBSCRIBE_GAME_PREFIX, new MancalaGameStompFrameHandler(this.gameResponseQueueForP1));
    }

    /**
     * action for second player subscribe game info
     */
    private void secondPlayerSubscribeGameInfo(StompSession playerSession) {
        playerSession.subscribe(SUBSCRIBE_GAME_PREFIX, new MancalaGameStompFrameHandler(this.gameResponseQueueForP2));
    }

    /**
     * action for player join the game
     */
    private void playerJoin(StompSession playerSession) {
        playerSession.send(SEND_GAME_JOIN, "");
    }

    /**
     * action for player sow
     */
    private void playerSow(StompSession playerSession, int pitIndex) {
        playerSession.send(SEND_GAME_SOW, pitIndex);
    }

    @Test
    public void testConnectToServer() throws ExecutionException, InterruptedException, TimeoutException {

        //given
        StompSession stompSession = this.newPlayerConnect();

        //then
        Assertions.assertNotNull(stompSession);
        Assertions.assertTrue(stompSession.isConnected());
    }

    @Test
    public void testTwoPlayersJoinAndSubscribeGameAndReceiveGameInfoSuccess() throws ExecutionException,
            InterruptedException, TimeoutException {

        //firstPlayer connect, subscribe and join
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);

        int[] initialPits = new int[MancalaDefault.PITS_NUMBER];
        Arrays.fill(initialPits, 6);
        // first player get game response once join
        GameResponse firstPlayerGameResponse = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get firstPlayerGameResponse = {}", firstPlayerGameResponse);

        Assertions.assertNotNull(firstPlayerGameResponse);
        //opponent is null
        Assertions.assertNull(firstPlayerGameResponse.getOpponent());
        Assertions.assertNotNull(firstPlayerGameResponse.getMe());
        Assertions.assertEquals(GameStatusResponse.WAITING_FOR_OPPONENT, firstPlayerGameResponse.getGameStatus());
        Assertions.assertNotNull(firstPlayerGameResponse.getGameBoard());
        Assertions.assertEquals(0, firstPlayerGameResponse.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, firstPlayerGameResponse.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(initialPits, firstPlayerGameResponse.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(initialPits, firstPlayerGameResponse.getGameBoard().getOpponentPits());

        //player 2 join
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        // first and second player both get game response once join
        GameResponse playerGameResponse1 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A = {}", playerGameResponse1);
        GameResponse playerGameResponse2 = this.gameResponseQueueForP2.poll(5, SECONDS);
        log.info("get playerGameResponse B = {}", playerGameResponse2);

        Assertions.assertNotNull(playerGameResponse1);
        Assertions.assertNotNull(playerGameResponse2);
        //opponent is me, me is opponent to each other
        Assertions.assertEquals(playerGameResponse1.getMe(), playerGameResponse2.getOpponent());
        Assertions.assertEquals(playerGameResponse1.getOpponent(), playerGameResponse2.getMe());
        //not same status, MY_TURN and OPPONENT_TURN
        Assertions.assertNotEquals(playerGameResponse1.getGameStatus(), playerGameResponse2.getGameStatus());
        Assertions.assertEquals(playerGameResponse1.getGameStatus(), GameStatusResponse.MY_TURN);
        Assertions.assertEquals(playerGameResponse2.getGameStatus(), GameStatusResponse.OPPONENT_TURN);

        // check gameboard response
        Assertions.assertNotNull(playerGameResponse1.getGameBoard());
        Assertions.assertNotNull(playerGameResponse2.getGameBoard());
        Assertions.assertEquals(0, playerGameResponse1.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, playerGameResponse2.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, playerGameResponse1.getGameBoard().getOpponentBigPit());
        Assertions.assertEquals(0, playerGameResponse2.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(initialPits, playerGameResponse1.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(initialPits, playerGameResponse1.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(initialPits, playerGameResponse2.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(initialPits, playerGameResponse2.getGameBoard().getOpponentPits());
    }

    @Test
    public void testTwoPlayerJoinAndFirstPlayerQuit() throws ExecutionException, InterruptedException, TimeoutException {

        // given two player connected, both join and sub the game
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        //ignore first 3 responses(2 for first player, 1 for second player)
        // because this case was tested in previous test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // when player1 quit
        playerSession1.disconnect();

        // then player2 will get gameInfo, status is OPPONENT_QUIT
        GameResponse gameResponse = this.gameResponseQueueForP2.poll(5, SECONDS);
        log.info("get playerGameResponse after opponent quit = {}", gameResponse);

        int[] initialPits = new int[MancalaDefault.PITS_NUMBER];
        Arrays.fill(initialPits, 6);

        Assertions.assertNotNull(gameResponse);
        Assertions.assertEquals(GameStatusResponse.OPPONENT_QUIT, gameResponse.getGameStatus());
        // check gameboard response
        Assertions.assertNotNull(gameResponse.getGameBoard());
        Assertions.assertEquals(0, gameResponse.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, gameResponse.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(initialPits, gameResponse.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(initialPits, gameResponse.getGameBoard().getOpponentPits());
    }

    @Test
    public void testTwoPlayerJoinAndDoValidSowOneRoundForEach() throws ExecutionException, InterruptedException,
            TimeoutException {

        // given two player connected, both join and sub the game
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        //ignore first 3 responses(2 for first player, 1 for second player) because this case was tested in previous
        // test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // when player1 sow
        this.playerSow(playerSession1, 3);

        // first and second player both get game response after player1 sow
        GameResponse playerGameResponse1 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A = {}", playerGameResponse1);
        GameResponse playerGameResponse2 = this.gameResponseQueueForP2.poll(5, SECONDS);
        log.info("get playerGameResponse B = {}", playerGameResponse2);

        Assertions.assertNotNull(playerGameResponse1);
        Assertions.assertNotNull(playerGameResponse2);
        //opponent is me, me is opponent to each other
        Assertions.assertEquals(playerGameResponse1.getMe(), playerGameResponse2.getOpponent());
        Assertions.assertEquals(playerGameResponse1.getOpponent(), playerGameResponse2.getMe());
        //check status, MY_TURN or OPPONENT_TURN
        Assertions.assertNotEquals(playerGameResponse1.getGameStatus(), playerGameResponse2.getGameStatus());
        Assertions.assertEquals(playerGameResponse1.getGameStatus(), GameStatusResponse.OPPONENT_TURN);
        Assertions.assertEquals(playerGameResponse2.getGameStatus(), GameStatusResponse.MY_TURN);

        // check game board response
        Assertions.assertNotNull(playerGameResponse1.getGameBoard());
        Assertions.assertNotNull(playerGameResponse2.getGameBoard());
        Assertions.assertEquals(1, playerGameResponse1.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, playerGameResponse2.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, playerGameResponse1.getGameBoard().getOpponentBigPit());
        Assertions.assertEquals(1, playerGameResponse2.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(playerGameResponse1.getGameBoard().getMyPits(),
                playerGameResponse2.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(playerGameResponse2.getGameBoard().getMyPits(),
                playerGameResponse1.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(new int[]{6, 6, 6, 0, 7, 7}, playerGameResponse1.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(new int[]{7, 7, 7, 6, 6, 6}, playerGameResponse2.getGameBoard().getMyPits());

        //player2 sow index 4
        this.playerSow(playerSession2, 4);

        // first and second player both get game response after player2 sow
        GameResponse playerGameResponse3 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A2 = {}", playerGameResponse1);
        GameResponse playerGameResponse4 = this.gameResponseQueueForP2.poll(5, SECONDS);
        log.info("get playerGameResponse B2 = {}", playerGameResponse2);

        Assertions.assertNotNull(playerGameResponse3);
        Assertions.assertNotNull(playerGameResponse4);
        //opponent is me, me is opponent to each other
        Assertions.assertEquals(playerGameResponse3.getMe(), playerGameResponse4.getOpponent());
        Assertions.assertEquals(playerGameResponse3.getOpponent(), playerGameResponse4.getMe());
        //check status, MY_TURN or OPPONENT_TURN
        Assertions.assertNotEquals(playerGameResponse3.getGameStatus(), playerGameResponse4.getGameStatus());
        Assertions.assertEquals(playerGameResponse3.getGameStatus(), GameStatusResponse.MY_TURN);
        Assertions.assertEquals(playerGameResponse4.getGameStatus(), GameStatusResponse.OPPONENT_TURN);

        // check game board response
        Assertions.assertNotNull(playerGameResponse3.getGameBoard());
        Assertions.assertNotNull(playerGameResponse4.getGameBoard());
        Assertions.assertEquals(1, playerGameResponse3.getGameBoard().getMyBigPit());
        Assertions.assertEquals(1, playerGameResponse4.getGameBoard().getMyBigPit());
        Assertions.assertEquals(1, playerGameResponse3.getGameBoard().getOpponentBigPit());
        Assertions.assertEquals(1, playerGameResponse4.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(playerGameResponse3.getGameBoard().getMyPits(),
                playerGameResponse4.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(playerGameResponse4.getGameBoard().getMyPits(),
                playerGameResponse3.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(new int[]{7, 7, 7, 1, 7, 7}, playerGameResponse3.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(new int[]{7, 7, 7, 6, 0, 7}, playerGameResponse4.getGameBoard().getMyPits());
    }

    @Test
    public void testFirstPlayerSowWhenItIsNotHisTurn() throws ExecutionException, InterruptedException,
            TimeoutException {

        // given two player connected, both join and sub the game
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        //ignore first 4 responses(2 for first player, 2 for second player) because this case was tested in previous
        // test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // when player1 sow
        this.playerSow(playerSession1, 3);
        // ignore two messages, case tested in other test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        //player1 sow again
        this.playerSow(playerSession1, 4);

        // then
        // first player both get game response with error after player1 sow
        GameResponse playerGameResponse1 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A = {}", playerGameResponse1);

        Assertions.assertNotNull(playerGameResponse1);
        //opponent is me, me is opponent to each other
        Assertions.assertEquals(playerGameResponse1.getGameStatus(), GameStatusResponse.OPPONENT_TURN);

        //there is an error message for first player
        Assertions.assertNotNull(playerGameResponse1.getMe().getErrorMsg());
        log.info("errorMsg for first player = {}", playerGameResponse1.getMe().getErrorMsg());

        // check game board response, same as the previous sow
        Assertions.assertNotNull(playerGameResponse1.getGameBoard());
        Assertions.assertEquals(1, playerGameResponse1.getGameBoard().getMyBigPit());
        Assertions.assertEquals(0, playerGameResponse1.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(new int[]{6, 6, 6, 0, 7, 7}, playerGameResponse1.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(new int[]{7, 7, 7, 6, 6, 6}, playerGameResponse1.getGameBoard().getOpponentPits());
    }

    @Test
    public void testFirstPlayerSowEmptyPitGetErrorMsg() throws InterruptedException, TimeoutException,
            ExecutionException {

        // given two player connected, both join and sub the game
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        //ignore first 3 responses(2 for first player, 1 for second player) because this case was tested in previous
        // test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // when player1 sow-> P1:[6,6,6,0,7,7], P2: [7,7,7,6,6,6]
        this.playerSow(playerSession1, 3);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        //player2 sow next -> P1: {7,6,6,0,7,7}, P2: {0,8,8,7,7,7}
        this.playerSow(playerSession2, 0);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // then P1 sow index 3 which has 0 stones, will get error
        this.playerSow(playerSession1, 3);

        // first player get game response with error after sow
        GameResponse playerGameResponse1 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A = {}", playerGameResponse1);

        Assertions.assertNotNull(playerGameResponse1);
        //still first player's turn
        Assertions.assertEquals(GameStatusResponse.MY_TURN, playerGameResponse1.getGameStatus());

        //there is an error message for first player
        Assertions.assertNotNull(playerGameResponse1.getMe().getErrorMsg());
        log.info("errorMsg for first player = {}", playerGameResponse1.getMe().getErrorMsg());

        // check game board response, same as the previous sow
        Assertions.assertNotNull(playerGameResponse1.getGameBoard());
        Assertions.assertEquals(1, playerGameResponse1.getGameBoard().getMyBigPit());
        Assertions.assertEquals(1, playerGameResponse1.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(new int[]{7, 6, 6, 0, 7, 7}, playerGameResponse1.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(new int[]{0, 8, 8, 7, 7, 7}, playerGameResponse1.getGameBoard().getOpponentPits());
    }

    @Test
    public void testPlayerGetOneMoreTurnByHitTheLastStoneInBIgPit() throws ExecutionException, InterruptedException,
            TimeoutException {

        // given two player connected, both join and sub the game
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        //ignore first 3 responses(2 for first player, 1 for second player) because this case was tested in previous
        // test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // after player1 sow-> P1:[6,6,6,6,6,0], P2: [7,7,7,7,7,6]
        this.playerSow(playerSession1, 5);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // after player2 sow-> P1:[7,7,7,7,7,0], P2: [7,7,7,7,7,0]
        this.playerSow(playerSession2, 5);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // after player1 sow-> P1:[7,7,7,7,0,1], P2: [8,8,8,8,8,0]
        this.playerSow(playerSession1, 4);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // after player2 sow-> P1:[8,8,7,7,0,1], P2: [0,9,9,9,9,1]
        this.playerSow(playerSession2, 0);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // player1 sow and hit the last stone in big pit
        this.playerSow(playerSession1, 5);
        GameResponse playerGameResponse1 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A = {}", playerGameResponse1);
        GameResponse playerGameResponse2 = this.gameResponseQueueForP2.poll(5, SECONDS);
        log.info("get playerGameResponse B = {}", playerGameResponse2);

        Assertions.assertNotNull(playerGameResponse1);
        Assertions.assertNotNull(playerGameResponse2);
        //opponent is me, me is opponent to each other
        Assertions.assertEquals(playerGameResponse1.getMe().getPlayerId(),
                playerGameResponse2.getOpponent().getPlayerId());
        Assertions.assertEquals(playerGameResponse1.getOpponent().getPlayerId(),
                playerGameResponse2.getMe().getPlayerId());
        //check status, MY_TURN or OPPONENT_TURN
        Assertions.assertNotEquals(playerGameResponse1.getGameStatus(), playerGameResponse2.getGameStatus());
        //still first player's turn
        Assertions.assertEquals(playerGameResponse1.getGameStatus(), GameStatusResponse.MY_TURN);
        Assertions.assertEquals(playerGameResponse2.getGameStatus(), GameStatusResponse.OPPONENT_TURN);

        // check game board response, same as the previous sow
        Assertions.assertNotNull(playerGameResponse1.getGameBoard());
        Assertions.assertNotNull(playerGameResponse2.getGameBoard());
        Assertions.assertEquals(3, playerGameResponse1.getGameBoard().getMyBigPit());
        Assertions.assertEquals(2, playerGameResponse2.getGameBoard().getMyBigPit());
        Assertions.assertEquals(2, playerGameResponse1.getGameBoard().getOpponentBigPit());
        Assertions.assertEquals(3, playerGameResponse2.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(playerGameResponse1.getGameBoard().getMyPits(),
                playerGameResponse2.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(playerGameResponse2.getGameBoard().getMyPits(),
                playerGameResponse1.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(new int[]{8, 8, 7, 7, 0, 0}, playerGameResponse1.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(new int[]{0, 9, 9, 9, 9, 1}, playerGameResponse2.getGameBoard().getMyPits());
    }

    @Test
    public void testPlayerCaptureStonesByHitTheLastStoneOnEmptyPit() throws ExecutionException, InterruptedException,
            TimeoutException {

        // given two player connected, both join and sub the game
        StompSession playerSession1 = this.newPlayerConnect();
        this.firstPlayerSubscribeGameInfo(playerSession1);
        this.playerJoin(playerSession1);
        StompSession playerSession2 = this.newPlayerConnect();
        this.secondPlayerSubscribeGameInfo(playerSession2);
        this.playerJoin(playerSession2);

        //ignore first 3 responses(2 for first player, 1 for second player) because this case was tested in previous
        // test case
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // after player1 sow-> P1:[0,7,7,7,7,7], P2: [6,6,6,6,6,6]
        this.playerSow(playerSession1, 0);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // player1 one more turn -> P1:[0,0,8,8,8,8], P2: [7,7,6,6,6,6]
        this.playerSow(playerSession1, 1);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // p2
        this.playerSow(playerSession2, 0);
        //ignore the coming 2 messages
        this.gameResponseQueueForP1.poll(5, SECONDS);
        this.gameResponseQueueForP2.poll(5, SECONDS);

        // p1, capture stones of the opponent's
        this.playerSow(playerSession1, 0);

        GameResponse playerGameResponse1 = this.gameResponseQueueForP1.poll(5, SECONDS);
        log.info("get playerGameResponse A = {}", playerGameResponse1);
        GameResponse playerGameResponse2 = this.gameResponseQueueForP2.poll(5, SECONDS);
        log.info("get playerGameResponse B = {}", playerGameResponse2);

        Assertions.assertNotNull(playerGameResponse1);
        Assertions.assertNotNull(playerGameResponse2);
        //opponent is me, me is opponent to each other
        Assertions.assertEquals(playerGameResponse1.getMe().getPlayerId(), playerGameResponse2.getOpponent().getPlayerId());
        Assertions.assertEquals(playerGameResponse1.getOpponent().getPlayerId(), playerGameResponse2.getMe().getPlayerId());
        //check status, MY_TURN or OPPONENT_TURN
        Assertions.assertNotEquals(playerGameResponse1.getGameStatus(), playerGameResponse2.getGameStatus());
        //still first player's turn
        Assertions.assertEquals(playerGameResponse1.getGameStatus(), GameStatusResponse.OPPONENT_TURN);
        Assertions.assertEquals(playerGameResponse2.getGameStatus(), GameStatusResponse.MY_TURN);

        // check game board response, same as the previous sow
        Assertions.assertNotNull(playerGameResponse1.getGameBoard());
        Assertions.assertNotNull(playerGameResponse2.getGameBoard());
        Assertions.assertEquals(10, playerGameResponse1.getGameBoard().getMyBigPit());
        Assertions.assertEquals(1, playerGameResponse2.getGameBoard().getMyBigPit());
        Assertions.assertEquals(1, playerGameResponse1.getGameBoard().getOpponentBigPit());
        Assertions.assertEquals(10, playerGameResponse2.getGameBoard().getOpponentBigPit());
        Assertions.assertArrayEquals(playerGameResponse1.getGameBoard().getMyPits(),
                playerGameResponse2.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(playerGameResponse2.getGameBoard().getMyPits(),
                playerGameResponse1.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(new int[]{0, 0, 8, 8, 8, 8}, playerGameResponse1.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(new int[]{0, 8, 7, 7, 0, 7}, playerGameResponse2.getGameBoard().getMyPits());
    }
}
