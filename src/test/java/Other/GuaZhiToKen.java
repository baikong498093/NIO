package Other;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuaZhiToKen {
    public static void main(String[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account", "AAAaccount");
        paramMap.put("outerId", 20251106);
        paramMap.put("encryptPhone", "D53AE32DC5FE0C817E1861EE660AFF07");
        paramMap.put("cityId", "1");
        String sign = getGuaZiSignToken(paramMap, "8c35271b992a896ed5dc24607c8f4fc6");
        System.out.println(sign);
    }
    public static String getGuaZiSignToken(Map<String, Object> paramMap, String secret) {
        String result = "";
        try {
            List<Map.Entry<String, Object>> sortList = new ArrayList<>(paramMap.entrySet());
            // 对所有参数按照字段名的ASCIIM 码从小到大排序(字典)
            sortList.sort(Map.Entry.comparingByKey());
            List<String> paramList = new ArrayList<>();
            for (Map.Entry<String, Object> item : sortList) {
                String key = item.getKey();
                String value = item.getValue() == null ? "" : item.getValue().toString();
                paramList.add(key + "=" + value);
            }
            String paramStr = String.join("#", paramList);
            String md5 = md5(paramStr);
            result = sha1(md5 + "@" + secret);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public static String md5(String message) {
        MessageDigest digest;
        String md5String = null;
        try {
            digest = MessageDigest.getInstance("md5");
            md5String = new String(Hex.encodeHex(digest.digest(message.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5String;
    }

    private static String sha1(String message) throws Exception {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = message.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
