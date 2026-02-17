package com.rustserver.a2s;

import com.rustserver.a2s.model.A2SInfo;
import com.rustserver.a2s.model.A2SPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Component
public class A2SClient {

    private static final Logger log = LoggerFactory.getLogger(A2SClient.class);

    @Value("${a2s.timeout:5000}")
    private int timeout;

    @Value("${a2s.buffer-size:8192}")
    private int bufferSize;

    /**
     * Query server info using A2S protocol
     *
     * @param ip   Server IP address
     * @param port Server query port (game port + 1 for Rust)
     * @return A2SInfo containing server information
     */
    public A2SInfo queryInfo(String ip, int port) throws IOException {
        long startTime = System.currentTimeMillis();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);
            socket.setReceiveBufferSize(bufferSize);

            InetAddress address = InetAddress.getByName(ip);
            int queryPort = port + 1; // Rust query port is game port + 1

            // Send A2S_INFO request
            DatagramPacket request = new DatagramPacket(
                    A2SQuery.A2S_INFO_REQUEST,
                    A2SQuery.A2S_INFO_REQUEST.length,
                    address,
                    queryPort
            );
            socket.send(request);

            // Receive response
            byte[] receiveBuffer = new byte[bufferSize];
            DatagramPacket response = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(response);

            long ping = System.currentTimeMillis() - startTime;

            // Parse response
            return parseInfoResponse(response.getData(), response.getLength(), ip, port, ping);
        }
    }

    /**
     * Query player list using A2S protocol
     *
     * @param ip   Server IP address
     * @param port Server query port
     * @return List of A2SPlayer
     */
    public List<A2SPlayer> queryPlayers(String ip, int port) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);

            InetAddress address = InetAddress.getByName(ip);
            int queryPort = port + 1;

            // First, get challenge number
            int challenge = getChallenge(socket, address, queryPort);

            // Send A2S_PLAYER request with challenge
            byte[] playerRequest = A2SQuery.buildPlayerRequest(challenge);
            DatagramPacket request = new DatagramPacket(playerRequest, playerRequest.length, address, queryPort);
            socket.send(request);

            // Receive response
            byte[] receiveBuffer = new byte[bufferSize];
            DatagramPacket response = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(response);

            return parsePlayerResponse(response.getData(), response.getLength());
        }
    }

    /**
     * Get challenge number from server
     */
    private int getChallenge(DatagramSocket socket, InetAddress address, int port) throws IOException {
        DatagramPacket challengeRequest = new DatagramPacket(
                A2SQuery.A2S_PLAYER_CHALLENGE,
                A2SQuery.A2S_PLAYER_CHALLENGE.length,
                address,
                port
        );
        socket.send(challengeRequest);

        byte[] buffer = new byte[bufferSize];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        ByteBuffer bb = ByteBuffer.wrap(response.getData(), 0, response.getLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);

        int header = bb.getInt(); // Should be 0xFFFFFFFF
        byte type = bb.get();

        if (type == A2SQuery.A2S_CHALLENGE_RESPONSE) {
            return bb.getInt();
        } else if (type == A2SQuery.A2S_PLAYER_RESPONSE) {
            // Some servers don't require challenge
            return -1;
        }

        throw new IOException("Unexpected response type: " + type);
    }

    /**
     * Parse A2S_INFO response
     */
    private A2SInfo parseInfoResponse(byte[] data, int length, String ip, int port, long ping) {
        ByteBuffer bb = ByteBuffer.wrap(data, 0, length);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        A2SInfo info = new A2SInfo();
        info.setAddress(ip);
        info.setPort(String.valueOf(port));
        info.setPing(String.valueOf(ping));

        // Skip header (4 bytes 0xFFFFFFFF + 1 byte type)
        bb.getInt();
        byte type = bb.get();

        if (type == A2SQuery.A2S_INFO_RESPONSE) {
            // Source format
            info.setProtocol(String.valueOf(bb.get() & 0xFF));
            info.setName(readString(bb));
            info.setMap(readString(bb));
            info.setFolder(readString(bb));
            info.setGame(readString(bb));
            info.setAppId(String.valueOf(bb.getShort() & 0xFFFF));
            info.setPlayers(String.valueOf(bb.get() & 0xFF));
            info.setMaxPlayers(String.valueOf(bb.get() & 0xFF));
            info.setBots(String.valueOf(bb.get() & 0xFF));

            byte serverType = bb.get();
            info.setServerType(serverType == 'd' ? "dedicated" : "listen");

            byte environment = bb.get();
            info.setEnvironment(environment == 'l' ? "linux" : "windows");

            info.setVisibility(String.valueOf(bb.get() & 0xFF));
            info.setVac(String.valueOf(bb.get() & 0xFF));
            info.setVersion(readString(bb));

            // Check for extra data (EDF)
            if (bb.hasRemaining()) {
                byte edf = bb.get();
                if ((edf & 0x80) != 0 && bb.remaining() >= 8) {
                    info.setSteamId(String.valueOf(bb.getLong()));
                }
                if ((edf & 0x10) != 0) {
                    info.setGameDir(readString(bb));
                }
                if ((edf & 0x40) != 0 && bb.remaining() >= 4) {
                    // Skip extended app ID
                    bb.getInt();
                }
                if ((edf & 0x01) != 0) {
                    info.setKeywords(readString(bb));
                }
            }
        }

        return info;
    }

    /**
     * Parse A2S_PLAYER response
     */
    private List<A2SPlayer> parsePlayerResponse(byte[] data, int length) {
        List<A2SPlayer> players = new ArrayList<>();

        ByteBuffer bb = ByteBuffer.wrap(data, 0, length);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        // Skip header
        bb.getInt();
        byte type = bb.get();

        if (type != A2SQuery.A2S_PLAYER_RESPONSE) {
            return players;
        }

        int playerCount = bb.get() & 0xFF;

        for (int i = 0; i < playerCount && bb.hasRemaining(); i++) {
            A2SPlayer player = new A2SPlayer();
            player.setIndex(String.valueOf(bb.get() & 0xFF));
            player.setName(readString(bb));
            player.setScore(String.valueOf(bb.getInt() & 0xFFFFFFFFL));
            player.setDuration(String.valueOf(bb.getFloat()));
            players.add(player);
        }

        return players;
    }

    /**
     * Read null-terminated string from buffer
     */
    private String readString(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (b == 0) break;
            sb.append((char) b);
        }
        return sb.toString();
    }
}
