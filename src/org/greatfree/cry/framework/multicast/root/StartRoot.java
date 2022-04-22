package org.greatfree.cry.framework.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.multicast.MultiAppConfig;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class StartRoot
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		System.out.println("Multicasting root is starting ...");
//		RootPeer.ROOT().start(MultiAppConfig.ROOT_NAME, MultiAppConfig.ROOT_PORT, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.ASYM);
		RootPeer.ROOT().start(MultiAppConfig.ROOT_NAME, MultiAppConfig.ROOT_PORT, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.PLAIN);
		System.out.println("Multicasting root is started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
