package org.greatfree.cry.framework.cluster.root;

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
import org.greatfree.cry.framework.cluster.ClusterConfig;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class StartRoot
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		System.out.println("Cluster root starting up ...");
		ClusterRoot.CRY().start(ClusterConfig.ROOT_PORT, ClusterConfig.ROOT_NAME, ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new CoordinatorTask(), MulticastConfig.PLAIN);
		System.out.println("Cluster root started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
