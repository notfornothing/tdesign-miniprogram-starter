package com.rustserver.a2s;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class A2SQuery {

    // A2S_INFO request
    public static final byte[] A2S_INFO_REQUEST = new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0x54, // A2S_INFO header
            'S', 'o', 'u', 'r', 'c', 'e', ' ', 'E', 'n', 'g', 'i', 'n', 'e', ' ', 'Q', 'u', 'e', 'r', 'y', 0
    };

    // A2S_PLAYER challenge request
    public static final byte[] A2S_PLAYER_CHALLENGE = new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0x55, // A2S_PLAYER header
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };

    // A2S_RULES challenge request
    public static final byte[] A2S_RULES_CHALLENGE = new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0x56, // A2S_RULES header
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };

    // Response headers
    public static final byte A2S_INFO_RESPONSE = 0x49; // 'I'
    public static final byte A2S_INFO_RESPONSE_SOURCE = 0x6D; // 'm' for source engine
    public static final byte A2S_PLAYER_RESPONSE = 0x44; // 'D'
    public static final byte A2S_RULES_RESPONSE = 0x45; // 'E'
    public static final byte A2S_CHALLENGE_RESPONSE = 0x41; // 'A'

    /**
     * Build A2S_PLAYER request with challenge number
     */
    public static byte[] buildPlayerRequest(int challenge) {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0x55);
        buffer.putInt(challenge);
        return buffer.array();
    }

    /**
     * Build A2S_RULES request with challenge number
     */
    public static byte[] buildRulesRequest(int challenge) {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0xFF);
        buffer.put((byte) 0x56);
        buffer.putInt(challenge);
        return buffer.array();
    }
}
