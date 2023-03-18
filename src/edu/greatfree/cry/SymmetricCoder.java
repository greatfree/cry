package edu.greatfree.cry;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
public final class SymmetricCoder
{
	/*
	 * The method is invoked by the symmetric cryptography as a relatively long term shared key. 02/05/2022, Bing Li
	 */
//	public static SymmetricCrypto generateCrypto(String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength) throws NoSuchAlgorithmException
	public static SymmetricCrypto generateCrypto(String sourcePeerKey, String destinationKey, String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength) throws NoSuchAlgorithmException
	{
		KeyGenerator kGen = KeyGenerator.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		kGen.init(cipherKeyLength);
		SecretKey cipherKey = kGen.generateKey();
		kGen.init(ivKeyLength);
		SecretKey ivKey = kGen.generateKey();
//		String sessionKey = Tools.generateUniqueKey();
//		return new SymmetricCrypto(sessionKey, cipherKey, ivKey, cipherSpec);
		return new SymmetricCrypto(sourcePeerKey, destinationKey, cipherKey, ivKey, cipherSpec);
	}

	/*
	 * The method is invoked at the receiver side to encrypt responses. 04/18/2022, Bing Li
	 */
	public static SymmetricCrypto generateCrypto(String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength) throws NoSuchAlgorithmException
	{
		KeyGenerator kGen = KeyGenerator.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		kGen.init(cipherKeyLength);
		SecretKey cipherKey = kGen.generateKey();
		kGen.init(ivKeyLength);
		SecretKey ivKey = kGen.generateKey();
//		String sessionKey = Tools.generateUniqueKey();
//		return new SymmetricCrypto(sessionKey, cipherKey, ivKey, cipherSpec);
		return new SymmetricCrypto(Tools.generateUniqueKey(), Tools.generateUniqueKey(), cipherKey, ivKey, cipherSpec);
//		return new SymmetricCrypto(cipherKey, ivKey, cipherSpec);
	}

	/*
	 * The method is moved to AsymmetricCoder. 04/18/2022, Bing Li
	 * 
	 * The method is invoked by the asymmetric cryptography as the nonce value, i.e., a one-time shared key. 02/05/2022, Bing Li
	 */
	/*
	public static SymmetricCrypto generateCrypto(String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength, String peerKey) throws NoSuchAlgorithmException
	{
		KeyGenerator kGen = KeyGenerator.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		kGen.init(cipherKeyLength);
		SecretKey cipherKey = kGen.generateKey();
		kGen.init(ivKeyLength);
		SecretKey ivKey = kGen.generateKey();
		return new SymmetricCrypto(cipherKey, ivKey, cipherSpec, peerKey);
	}
	*/

	/*
	public static SecretKey generateKey(String cipherAlgorithm, String cipherSpec, int keyLength) throws NoSuchAlgorithmException
	{
		KeyGenerator kGen = KeyGenerator.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		kGen.init(keyLength);
		return kGen.generateKey();
	}
	*/

	public static byte[] encryptMessage(ServerMessage message, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		byte[] input = Tools.serialize(message);
		cipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		return cipher.doFinal(input);
	}

	/*
	 * The ivKey length is 128. 01/05/2022, Bing Li
	 */
//	public static byte[] encryptNotification(Object object, SecretKey key, SecretKey ivKey, String cipherApproach) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	public static byte[] encryptNotification(Notification notification, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		byte[] input = Tools.serialize(notification);
		cipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		return cipher.doFinal(input);
	}

	public static byte[] encryptRequest(Request request, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		byte[] input = Tools.serialize(request);
		cipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		return cipher.doFinal(input);
	}

	public static byte[] encryptObject(Object obj, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		byte[] input = Tools.serialize(obj);
		cipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		return cipher.doFinal(input);
	}

	public static byte[] encryptResponse(ServerMessage response, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		byte[] input = Tools.serialize(response);
		cipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		return cipher.doFinal(input);
	}

	public static ServerMessage decryptMessage(byte[] encryptedData, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		byte[] finalOutput = new byte[cipher.getOutputSize(encryptedData.length)];
		int len = cipher.update(encryptedData, 0, encryptedData.length, finalOutput, 0);
		len += cipher.doFinal(finalOutput, len);
		return (ServerMessage)Tools.deserialize(finalOutput);
	}

	/*
	 * The ivKey length is 128. 01/05/2022, Bing Li
	 */
	public static Notification decryptNotification(byte[] encryptedData, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		byte[] finalOutput = new byte[cipher.getOutputSize(encryptedData.length)];
		int len = cipher.update(encryptedData, 0, encryptedData.length, finalOutput, 0);
		len += cipher.doFinal(finalOutput, len);
		return (Notification)Tools.deserialize(finalOutput);
	}

	public static Request decryptRequest(byte[] encryptedData, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		byte[] finalOutput = new byte[cipher.getOutputSize(encryptedData.length)];
		int len = cipher.update(encryptedData, 0, encryptedData.length, finalOutput, 0);
		len += cipher.doFinal(finalOutput, len);
		return (Request)Tools.deserialize(finalOutput);
	}

	public static Object decryptObject(byte[] encryptedData, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		byte[] finalOutput = new byte[cipher.getOutputSize(encryptedData.length)];
		int len = cipher.update(encryptedData, 0, encryptedData.length, finalOutput, 0);
		len += cipher.doFinal(finalOutput, len);
		return Tools.deserialize(finalOutput);
	}

	public static <T> T decryptObject(byte[] encryptedData, SecretKey cipherKey, SecretKey ivKey, String cipherSpec, Class<T> c) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		byte[] finalOutput = new byte[cipher.getOutputSize(encryptedData.length)];
		int len = cipher.update(encryptedData, 0, encryptedData.length, finalOutput, 0);
		len += cipher.doFinal(finalOutput, len);
		Object obj = Tools.deserialize(finalOutput);
		if (c.isInstance(obj))
		{
			return c.cast(obj);
		}
		return null;
	}

	public static ServerMessage decryptResponse(byte[] encryptedData, SecretKey cipherKey, SecretKey ivKey, String cipherSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherSpec, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivKey.getEncoded()));
		byte[] finalOutput = new byte[cipher.getOutputSize(encryptedData.length)];
		int len = cipher.update(encryptedData, 0, encryptedData.length, finalOutput, 0);
		len += cipher.doFinal(finalOutput, len);
		return (ServerMessage)Tools.deserialize(finalOutput);
	}
}
