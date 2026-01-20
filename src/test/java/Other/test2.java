package Other;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class test2 {
    // 测试示例
    public static void main(String[] args) {
        String mobile = "18638588557";
        String secret = "hfsLveIz0bC3cSaZW68wAm5cwz31smsB";
        String encryptResult = encryptMobile(mobile, secret);
        System.out.println("加密结果：" + encryptResult);
    }

    /**
     * 核心加密方法，完全等价原JS的encryptMobile方法
     * @param mobile 待加密的手机号
     * @param secret 加密秘钥
     * @return 加密后的大写Hex字符串
     */
    public static String encryptMobile(String mobile, String secret) {
        try {
            // 1. 秘钥处理：截取最后24位 + 转小写，和原JS逻辑完全一致
            String keyString = secret.substring(secret.length() - 24).toLowerCase();
            // 2. 处理秘钥为24字节的数组（核心方法，等价pwdHandler）
            byte[] key = pwdHandler(keyString);
            if (key == null) {
                throw new IllegalArgumentException("秘钥处理失败，生成的密钥为空");
            }

            // 3. 构建AES-192-ECB加密器 ECB模式不需要IV向量（原JS传null）
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // 4. 加密：等价JS的cipher.update + cipher.final 拼接逻辑
            byte[] encryptedBytes = cipher.doFinal(mobile.getBytes(StandardCharsets.UTF_8));

            // 5. 加密字节数组转 大写Hex字符串，等价JS的toString('hex').toUpperCase()
            return bytesToHexUpperCase(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("手机号加密失败", e);
        }
    }

    /**
     * 等价原JS的pwdHandler方法，完整对齐所有逻辑
     * @param password 待处理的密钥字符串
     * @return 固定24字节的密钥字节数组，不足补0，非空不会返回null
     */
    public static byte[] pwdHandler(String password) {
        // 对应JS：password === null || password === undefined
        if (password == null) {
            return null;
        }
        // 对应JS：bytes = Buffer.from(password, 'utf8')
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        final int PWD_SIZE = 24;

        // 对应JS：不足24字节则补0，足够则直接返回原数组
        if (bytes.length < PWD_SIZE) {
            byte[] paddedBytes = new byte[PWD_SIZE];
            System.arraycopy(bytes, 0, paddedBytes, 0, bytes.length);
            return paddedBytes;
        } else {
            return bytes;
        }
    }

    /**
     * 字节数组转 大写的十六进制字符串，完美等价 Node.js Buffer.toString('hex').toUpperCase()
     */
    private static String bytesToHexUpperCase(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

}
