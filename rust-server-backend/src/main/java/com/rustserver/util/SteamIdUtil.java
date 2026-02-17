package com.rustserver.util;

import java.math.BigInteger;

/**
 * Utility class for Steam ID conversions
 */
public class SteamIdUtil {

    private static final BigInteger STEAM_ID_BASE = new BigInteger("76561197960265728");

    /**
     * Convert SteamID64 to SteamID32 (account id)
     */
    public static String toSteamId32(String steamId64) {
        try {
            BigInteger id = new BigInteger(steamId64);
            BigInteger accountId = id.subtract(STEAM_ID_BASE);
            return accountId.toString();
        } catch (NumberFormatException e) {
            return steamId64;
        }
    }

    /**
     * Convert SteamID32 to SteamID64
     */
    public static String toSteamId64(String steamId32) {
        try {
            BigInteger accountId = new BigInteger(steamId32);
            BigInteger steamId64 = accountId.add(STEAM_ID_BASE);
            return steamId64.toString();
        } catch (NumberFormatException e) {
            return steamId32;
        }
    }

    /**
     * Convert SteamID64 to SteamID3 format (e.g., [U:1:12345678])
     */
    public static String toSteamId3(String steamId64) {
        try {
            String accountId = toSteamId32(steamId64);
            return "[U:1:" + accountId + "]";
        } catch (Exception e) {
            return steamId64;
        }
    }

    /**
     * Get Steam profile URL from SteamID64
     */
    public static String getProfileUrl(String steamId64) {
        return "https://steamcommunity.com/profiles/" + steamId64;
    }

    /**
     * Get Steam avatar URL from SteamID64
     */
    public static String getAvatarUrl(String steamId64) {
        return "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/"
                + steamId64.substring(steamId64.length() - 2) + "/"
                + steamId64 + "_full.jpg";
    }

    /**
     * Validate SteamID64 format
     */
    public static boolean isValidSteamId64(String steamId64) {
        if (steamId64 == null || steamId64.length() != 17) {
            return false;
        }
        try {
            BigInteger id = new BigInteger(steamId64);
            return id.compareTo(BigInteger.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
