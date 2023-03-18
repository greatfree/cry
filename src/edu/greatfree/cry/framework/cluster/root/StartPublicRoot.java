package edu.greatfree.cry.framework.cluster.root;

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
import edu.greatfree.cry.framework.cluster.ClusterConfig;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class StartPublicRoot
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Cluster root starting up ...");
		try
		{
			PublicClusterRoot.CRY().start(ClusterConfig.ROOT_PORT, ClusterConfig.ROOT_NAME, ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new CoordinatorTask(), MulticastConfig.PLAIN);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		catch (IPNotExistedException e)
		{
			e.printStackTrace();
		}
		System.out.println("Cluster root started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
