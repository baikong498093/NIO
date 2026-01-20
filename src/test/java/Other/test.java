package Other;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class test {
    public static void main(String[] args) {
        try {
            inputRuiYuan();
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private static String RuiYuanKey="Toq7m3ojdJNwBFimGCnokY7lzykkSoD9";
    private static String RuiYuanSecret="8c35271b992a896ed5dc24607c8f4fc6";
    public static void inputRuiYuan() throws Exception {
        byte [] keyBytes = RuiYuanSecret.substring(RuiYuanSecret.length() - 24)
                .getBytes(StandardCharsets.UTF_8);
        byte [] phoneBytes = "18812345678".getBytes(StandardCharsets.UTF_8);
        // 创建AES密钥规范
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        // 获取AES加密实例，使用ECB模式和PKCS5Padding填充
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // 初始化加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // phone加密后的字节数组
        byte[] encryptedBytes = cipher.doFinal(phoneBytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : encryptedBytes) {
            String hex = Integer.toHexString(0xFF & b).toUpperCase(); // 转换为无符号十六进制
            if (hex.length() == 1) { // 若长度为1则补前导0
                hexString.append('0');
            }
            hexString.append(hex);
        }

        System.out.println("加密后的手机号:"+hexString);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //创建线索json字符串
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessKeyId", RuiYuanKey)
                .put("encryptedPhone", hexString.toString())
                .put("outerId",  72786+"")
                .put("cityName", "沈阳市")
        ;
        // 按字母升序排序JSON键并拼接值为#分隔的字符串
        List<String> sortedKeys = new ArrayList<>(jsonObject.keySet());
        Collections.sort(sortedKeys);
        StringBuilder jsonStr = new StringBuilder();
        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            Object value = jsonObject.get(key);
            if (i > 0) {
                jsonStr.append("#");
            }
            jsonStr.append(key+"="+value);
        }
        System.out.println("jsonStr:"+jsonStr.toString());
        // 对resultString进行MD5 32位加密
        String md5JsonStr = DigestUtils.md5Hex(jsonStr.toString());
        String sign = md5JsonStr+"@"+RuiYuanSecret;
        System.out.println("md5JsonStr:"+md5JsonStr);
        System.out.println("使用@连接:"+sign);
        // 计算sign的SHA1值
        String token = generateSHA1Signature(sign);
        jsonObject.put("token", token);
        System.out.println("token:"+token);
        System.err.println(jsonObject.toString());

    }

    public static String generateSHA1Signature(String param) throws Exception {
        // 创建SHA-1摘要算法对象
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        // 将字符串转换为字节数组
        byte[] bytes = param.getBytes(StandardCharsets.UTF_8);
        // 计算SHA-1摘要
        byte[] digest = md.digest(bytes);
        // 将摘要转换为16进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
