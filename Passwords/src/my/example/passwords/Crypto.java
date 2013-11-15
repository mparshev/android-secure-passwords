package my.example.passwords;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class Crypto {

	public static final String TAG = Crypto.class.getSimpleName();
	public static final String DELIMITER = ":";
	public static final String CIPHER = "AES/CBC/PKCS5Padding";

	static int iterationCount = 999;
	static int keyLength = 256;
	static int saltLength = keyLength / 8;
	
	public static String encrypt(String plaintext, String password) {
		try {
			
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[saltLength];
			random.nextBytes(salt);
			
			SecretKey secretKey = deriveKey(password, salt);			
			
			Cipher cipher = Cipher.getInstance(CIPHER);
			byte[] iv = new byte[cipher.getBlockSize()];
			random.nextBytes(iv);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
			byte[] cipherBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));

			return toBase64(salt) + DELIMITER + toBase64(iv) + DELIMITER + toBase64(cipherBytes);
			
		} catch(Throwable tr) {
			Log.e(TAG,tr.getMessage());
			return null;
			//throw new RuntimeException(tr);
		}
	}
	
	public static String decrypt(String ciphertext, String password) {
		try {
			String[] fields = ciphertext.split(DELIMITER);
			byte[] salt = fromBase64(fields[0]);
			byte[] iv = fromBase64(fields[1]);
			byte[] cipherBytes = fromBase64(fields[2]);
			
			SecretKey secretKey = deriveKey(password, salt);
			
			Cipher cipher = Cipher.getInstance(CIPHER);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
			
			return new String(cipher.doFinal(cipherBytes),"UTF-8");
			
		} catch(Exception tr) {
			Log.e(TAG,tr.getMessage());
			return null;
			//throw new RuntimeException(tr);
		}
	}
	
	private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		return new SecretKeySpec(keyBytes, "AES");
	}
	
	public static String toBase64(byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.NO_WRAP);
	}
	
	public static byte[] fromBase64(String str) {
		return Base64.decode(str, Base64.NO_WRAP);
	}

	public static String generate(int length, String[] classes) {
		String result = "";
		SecureRandom random = new SecureRandom();
		boolean[] used = new boolean[classes.length];
		for(int i=0; i<classes.length; i++) used[i] = false;
		while(result.length()<length) {
			String source = "";
			int left = 0;
			for(int i=0; i<classes.length; i++) {
				if(!used[i]) ++left;
			}
			for(int i=0; i<classes.length; i++) {
				if((!used[i]) || ((length-result.length()) > left)) source += classes[i];
			}
			
			char ch = source.charAt(random.nextInt(source.length()));
			
			for(int i=0; i<classes.length; i++) {
				if(classes[i].contains(""+ch)) used[i] = true; 
			}

			result += ch;
		}
		
		return result;
	}
	
}
