package org.greatfree.cry;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class AsymmetricCoder
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry");
	
//	public static AsymmetricCrypto generateCrypto(String cipherAlgorithm, int cipherKeyLength, SymmetricCrypto symCrypto) throws NoSuchAlgorithmException
	public static AsymmetricCrypto generateCrypto(String asymCipherAlgorithm, int asymCipherKeyLength, String symCipherAlgorithm, String symCipherSpec, int symCipherKeyLength, int symIVKeyLength) throws NoSuchAlgorithmException
	{
//		KeyPairGenerator generator = KeyPairGenerator.getInstance(cipherAlgorithm);
		KeyPairGenerator generator = KeyPairGenerator.getInstance(asymCipherAlgorithm, new BouncyCastleProvider());
		generator.initialize(asymCipherKeyLength);
		KeyPair kp = generator.generateKeyPair(); 
//		return new AsymmetricCrypto(Tools.generateUniqueKey(), cipherAlgorithm, kp.getPublic(), kp.getPrivate(), symCrypto);
		return new AsymmetricCrypto(Tools.generateUniqueKey(), asymCipherAlgorithm, kp.getPublic(), kp.getPrivate(), symCipherAlgorithm, symCipherSpec, symCipherKeyLength, symIVKeyLength);
	}

	public static AsymmetricCrypto generateCrypto(String asymCipherAlgorithm, int asymCipherKeyLength, String symCipherAlgorithm, String symCipherSpec, int symCipherKeyLength, int symIVKeyLength, String signatureAlgorithm, String signature) throws NoSuchAlgorithmException
	{
		KeyPairGenerator generator = KeyPairGenerator.getInstance(asymCipherAlgorithm, new BouncyCastleProvider());
		generator.initialize(asymCipherKeyLength);
		KeyPair kp = generator.generateKeyPair(); 
//		return new AsymmetricCrypto(Tools.generateUniqueKey(), cipherAlgorithm, kp.getPublic(), kp.getPrivate(), symCrypto);
		return new AsymmetricCrypto(Tools.generateUniqueKey(), asymCipherAlgorithm, kp.getPublic(), kp.getPrivate(), symCipherAlgorithm, symCipherSpec, symCipherKeyLength, symIVKeyLength, signatureAlgorithm, signature);
	}

	/*
	 * The method is invoked by the asymmetric cryptography as the nonce value, i.e., a one-time shared key. 02/05/2022, Bing Li
	 */
	public static AsymCompCrypto generateCompCrypto(String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength, String peerKey) throws NoSuchAlgorithmException
	{
		KeyGenerator kGen = KeyGenerator.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		kGen.init(cipherKeyLength);
		SecretKey cipherKey = kGen.generateKey();
		kGen.init(ivKeyLength);
		SecretKey ivKey = kGen.generateKey();
		return new AsymCompCrypto(cipherKey, ivKey, cipherSpec, peerKey);
	}

//	public static AsymmetricCrypto resetCrypto(String cipherAlgorithm, int cipherKeyLength, AsymmetricCrypto existingOne, SymmetricCrypto symCrypto) throws NoSuchAlgorithmException
	/*
	public static AsymmetricCrypto resetCrypto(String cipherAlgorithm, int cipherKeyLength, AsymmetricCrypto existingOne, String symCipherAlgorithm, String symCipherSpec, int symCipherKeyLength, int symIVKeyLength) throws NoSuchAlgorithmException
	{
		KeyPairGenerator generator = KeyPairGenerator.getInstance(cipherAlgorithm);
		generator.initialize(cipherKeyLength);
		KeyPair kp = generator.generateKeyPair(); 
//		return new AsymmetricCrypto(Tools.generateUniqueKey(), cipherAlgorithm, kp.getPublic(), kp.getPrivate(), existingOne.getPublicCryptos(), symCrypto);
		return new AsymmetricCrypto(Tools.generateUniqueKey(), cipherAlgorithm, kp.getPublic(), kp.getPrivate(), existingOne.getPublicCryptos(), symCipherAlgorithm, symCipherSpec, symCipherKeyLength, symIVKeyLength);
	}
	*/
	
	public static byte[] encrypt(Object object, String cipherAlgorithm, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
//		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		Cipher cipher = Cipher.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] input = Tools.serialize(object);
//		log.info("input length = " + input.length);
		return cipher.doFinal(input);
	}

	/*
	public static byte[] encryptWithBC(Object object, String cipherAlgorithm, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] input = Tools.serialize(object);
		log.info("input length = " + input.length);
		return cipher.doFinal(input);
	}
	*/

	/*
	public static byte[] encryptRequest(Request request, String cipherAlgorithm, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] input = Tools.serialize(request);
		return cipher.doFinal(input);
	}
	
	public static byte[] encryptResponse(ServerMessage response, String cipherAlgorithm, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] input = Tools.serialize(response);
		return cipher.doFinal(input);
	}
	*/

	public static Object decrypt(byte[] encryptedData, String cipherAlgorithm, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
//		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		Cipher cipher = Cipher.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] finalOutput = cipher.doFinal(encryptedData);
//		return (Notification)Tools.deserialize(finalOutput);
		return Tools.deserialize(finalOutput);
	}

	/*
	public static Object decryptWithBC(byte[] encryptedData, String cipherAlgorithm, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherAlgorithm, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] finalOutput = cipher.doFinal(encryptedData);
//		return (Notification)Tools.deserialize(finalOutput);
		return Tools.deserialize(finalOutput);
	}
	*/

	/*
	public static Request decryptRequest(byte[] encryptedData, String cipherAlgorithm, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] finalOutput = cipher.doFinal(encryptedData);
		return (Request)Tools.deserialize(finalOutput);
	}

	public static ServerMessage decryptResponse(byte[] encryptedData, String cipherAlgorithm, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] finalOutput = cipher.doFinal(encryptedData);
		return (ServerMessage)Tools.deserialize(finalOutput);
	}
	*/
	
	public static byte[] sign(String algorithm, PrivateKey privateKey, String info) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException
	{
		Signature signature = Signature.getInstance(algorithm, new BouncyCastleProvider());
		signature.initSign(privateKey);
		signature.update(Tools.serialize(info));
		return signature.sign();
	}
	
	public static boolean verify(String algorithm, PublicKey publicKey, String info, byte[] partnerSignature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException
	{
		Signature signature = Signature.getInstance(algorithm, new BouncyCastleProvider());
		signature.initVerify(publicKey);
		signature.update(Tools.serialize(info));
		return signature.verify(partnerSignature);
	}
}
