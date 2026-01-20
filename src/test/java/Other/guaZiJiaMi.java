package Other;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.management.openmbean.InvalidKeyException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class guaZiJiaMi {
    public static void main(String[] args) throws Exception {

    }
    static String phone="18812345678";
    static String secret="8c35271b992a896ed5dc24607c8f4fc6";
    public static String encryptPhone() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, java.security.InvalidKeyException {
        // 1. 获取secret后24位并转换为UTF-8字节数组
        String passwordStr = secret.substring(secret.length() - 24);
        byte[] keyBytes = passwordStr.getBytes(StandardCharsets.UTF_8);

        // 2. 将手机号转换为UTF-8字节数组
        byte[] phoneBytes = phone.getBytes(StandardCharsets.UTF_8);

        // 3. 使用AES/ECB/PKCS5Padding模式加密
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(phoneBytes);

        // 将字节数组转换为16进制字符串（补0并转大写）
        StringBuilder hexString = new StringBuilder();
        for (byte b : encryptedBytes) {
            String hex = Integer.toHexString(0xFF & b).toUpperCase();
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();

    }
}
