package edu.greatfree.cry.framework.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.cluster.ClusterConfig;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 * 05/17/2022
 *
 */
final class StartChildOfPrivateRoot
{
	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, CheatingException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Cluster child starting up ...");
//		ClusterChildOfPublicRoot.CRY().start(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ClusterChildTask(), MulticastConfig.PLAIN, ClusterConfig.ROOT_KEY, true);
		try
		{
			ClusterChildOfPublicRoot.CRY().start(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ClusterChildTask(), MulticastConfig.PLAIN, ClusterConfig.ROOT_KEY);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		System.out.println("Cluster child started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}
}
