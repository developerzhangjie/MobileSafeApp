package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sin on 2016/10/15.
 * Description:
 */

public class MD5Utils {
    public static String md5(String msg) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(msg.getBytes());
            for (int i = 0; i < digest.length; i++) {
                int result = digest[i] & 0xff;
                String num = Integer.toHexString(result);
                if (num.length() < 2) {
                    sb.append("0");
                }
                sb.append(num);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
