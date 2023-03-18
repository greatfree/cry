package edu.greatfree.cry.framework.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.multicast.MultiAppConfig;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class StartRoot
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Multicasting root is starting ...");
//		RootPeer.ROOT().start(MultiAppConfig.ROOT_NAME, MultiAppConfig.ROOT_PORT, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.ASYM);
		try
		{
			RootPeer.ROOT().start(MultiAppConfig.ROOT_NAME, MultiAppConfig.ROOT_PORT, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.PLAIN);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		catch (IPNotExistedException e)
		{
			e.printStackTrace();
		}
		System.out.println("Multicasting root is started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
