package com.example.aimhustermap.util;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class CipherUtil {

    /*
     * 常量
     */
    public final static int CIPH_ECOUNT = 5;
    
    /*
     *  十六进制下数字到字符的映射数组
     */
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    
    /**
     * 把inputString加密
     * @param inputString
     * @return String
     */
    public static String encryptString(String inputString) {
        return encodeBySHA(inputString);
    }
    
    /**
     * 把inputString按指定次数加密
     * @param inputString
     * @param count
     * @return String
     */
    public static String encryptString(String inputString, int count)  {
        if (count < 2) {
            return encryptString(inputString);
        } else {
            return encryptString(encryptString(inputString), count - 1);
        }
    }
    
    
    /**
     * 生成密匙
     * @param keyString
     * @return SecretKey
     * @throws FailException
     */
    public static SecretKey generateKeyByAES(String keyString)  {
        SecretKey skey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(keyString.getBytes());
            keyGen.init(128, random);
//            keyGen.init(128);
            skey = keyGen.generateKey();
        } catch (Exception e) {

        }
        byte[] keyByte = skey.getEncoded();
        String keysString = new String(keyByte);
        System.out.println("keysssssss----->"+keysString);
        
        return skey;
    }
    
    
    
    /**
     * 加密文件infilename,输出加密后的文件outfilename
     * @param infilename
     * @param outfilename
     * @param k
     * @throws FailException
     * @throws IOException 
     */
    public static void encryptByAES(String infilename, String outfilename, SecretKey k) throws  IOException {
        InputStream in = null;
        DataOutputStream out = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            in = new FileInputStream(infilename);
            out = new DataOutputStream(new FileOutputStream(outfilename));
            cipher.init(Cipher.ENCRYPT_MODE, k);
            crypt(in, out, cipher);
        } catch (Exception e) {
//            throw new FailException(e.getMessage());
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            infilename = null;
            outfilename = null;
            k = null;
            cipher = null;
        }
    }
    
    
    
    /**
     * 用密钥k解密文件infilename,输出明文文件outfilename
     * @param infilename
     * @param outfilename
     * @param k
     * @throws FailException
     * @throws IOException 
     */
    public static void decryptByAES(String infilename, String outfilename, SecretKey k) throws  IOException {
        OutputStream out = null;
        DataInputStream in = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            out = new FileOutputStream(outfilename);
            in = new DataInputStream(new FileInputStream(infilename));
            cipher.init(Cipher.DECRYPT_MODE, k);
            crypt(in, out, cipher);
        } catch (Exception e) {
//            throw new FailException(e.getMessage());
        	e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            infilename = null;
            outfilename = null;
            k = null;
            cipher = null;
        }
    }
    
    
    
    /**
     * 加密,解密函数
     * @param in
     * @param cipher
     * @return String
     * @throws FailException
     */
    public static String crypt(String in, Cipher cipher)  {
        StringBuffer out = new StringBuffer();
        try {
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(blockSize);
            byte[] inBytes = new byte[blockSize];
            byte[] outBytes = new byte[outputSize];
            int inLength = 0;
            boolean more = true;
            while (more) {
                for (int i = 0; i < in.getBytes().length; i++) {
                    if (in.getBytes().length >= i * blockSize) {
                        cipher.update(inBytes, 0, blockSize, outBytes);
                        out.append(outBytes);
                    } else {
                        inLength = in.getBytes().length - i * blockSize;
                        more = false;
                    }
                }
            }
            if (inLength > 0) {
                outBytes = cipher.doFinal(inBytes, 0, inLength);
            } else {
                outBytes = cipher.doFinal();
            }
            out.append(outBytes);
        } catch (Exception e) {
//            throw new FailException(e.getMessage());
        }
        return out.toString();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 自己定义的加密,解密函数
     * @param in
     * @param out
     * @param cipher
     * @throws FailException
     * @throws IOException 
     */
    private static void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException {
        try {
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(blockSize);
            byte[] inBytes = new byte[blockSize];
            byte[] outBytes = new byte[outputSize];
            int inLength = 0;
            boolean more = true;
            while (more) {
                inLength = in.read(inBytes);
                if (inLength == blockSize) {
                    int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
                    out.write(outBytes, 0, outLength);
                } else {
                    more = false;
                }
            }
            if (inLength > 0) {
                outBytes = cipher.doFinal(inBytes, 0, inLength);
            } else {
                outBytes = cipher.doFinal();
            }
            out.write(outBytes);
            
        } catch (Exception e) {
//            throw new FailException(e.getMessage());
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * 对字符串进行SHA-256加密
     * @param originString
     * @return String
     */
    private static String encodeBySHA(String originString)  {
        if (originString != null) {
            try {
                // 创建具有指定算法名称的信息摘要
                MessageDigest md = MessageDigest.getInstance("SHA-512");
//                MessageDigest md = MessageDigest.getInstance("MD5");
                // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
                byte[] results = md.digest(originString.getBytes());
                // 将得到的字节数组变成字符串返回
                String resultString = byteArrayToHexString(results);
                return resultString.toUpperCase();
            } catch (Exception e) {
//                throw new FailException(e.getMessage());
            }
        }
        return null;
    }
    
    
    
    
    
    
    /**
     * 转换字节数组为十六进制字符串
     * @param b
     * @return String
     */
    private static String byteArrayToHexString(byte[] b)  {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }
    
    /**
     * 将一个字节转化成十六进制形式的字符串
     * @param b
     * @return String
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    
   
    
    
    
    
    
}