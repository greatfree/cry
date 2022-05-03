package org.greatfree.cry.framework.tncs;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
public class CryptoConfig
{
	public final static int CRYPTO_SERVER_PORT = 8900;
	public final static String AES = "AES";
	public final static String AES_SPEC = "AES/CBC/PKCS5Padding";
	public final static String RSA = "RSA";
	public final static int RSA_LENGTH = 4096;
	public final static String DSA = "DSA";
	public final static String SHA_WITH_RSA = "SHA256withRSA";
	public final static String SIGNATURE_SUFFIX = ".signature";
	public final static int SYMMETRIC_KEY_LENGTH = 256;
	public final static int SYMMETRIC_IV_KEY_LENGTH = 128;
}
