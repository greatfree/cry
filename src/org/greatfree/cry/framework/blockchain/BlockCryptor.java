package org.greatfree.cry.framework.blockchain;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;
import org.greatfree.cry.CryConfig;
import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Output;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import com.google.gson.GsonBuilder;

/**
 * 
 * @author libing
 * 
 *         01/26/2022, Bing Li
 *
 */
public class BlockCryptor
{
	public static String applySHA256(String input)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance(CryConfig.SHA_256);

			// Applies sha256 to our input,
			byte[] hash = digest.digest(input.getBytes(UtilConfig.UTF_8));

			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++)
			{
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append(CryConfig.ZERO_CHAR);
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// Short hand helper to turn Object into a json string
	public static String getJson(Object o)
	{
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}

	// Returns difficulty string target, to compare to hash. eg difficulty of 5 will
	// return "00000"
	public static String getDifficultyString(int difficulty)
	{
		return new String(new char[difficulty]).replace(CryConfig.NULL_CHAR, CryConfig.ZERO_CHAR);
	}

//	public static String calculateFingerPrint(String previousFingerPrint, long timeStamp, int nonce, Map<String, List<Transaction>> transactions) throws IOException
	public static String calculateFingerPrint(String previousFingerPrint, long timeStamp, int nonce, List<Transaction> transactions) throws IOException
	{
		return BlockCryptor.applySHA256(previousFingerPrint + Long.toString(timeStamp) + Integer.toString(nonce) + Hex.toHexString(Tools.serialize(transactions)));
	}

	public static String calculateFingerPrint(String previousFingerPrint, long timeStamp, int nonce, List<Transaction> transactions, Map<String, List<Input>> inputs, Map<String, List<Output>> outputs) throws IOException
	{
		return BlockCryptor.applySHA256(previousFingerPrint + Long.toString(timeStamp) + Integer.toString(nonce) + Hex.toHexString(Tools.serialize(transactions)) + Hex.toHexString(Tools.serialize(inputs)) + Hex.toHexString(Tools.serialize(outputs)));
	}

	public static String calculateCoin(String coinHash, long timeStamp, int nonce)
	{
		return BlockCryptor.applySHA256(coinHash + Long.toString(timeStamp) + Integer.toString(nonce));
	}
}
