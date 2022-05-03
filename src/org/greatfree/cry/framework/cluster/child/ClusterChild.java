package org.greatfree.cry.framework.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cry.cluster.child.ClusterChildContainer;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClusterChild
{
	private ClusterChildContainer child;

	private ClusterChild()
	{
	}
	
	private static ClusterChild instance = new ClusterChild();
	
	public static ClusterChild CRY()
	{
		if (instance == null)
		{
			instance = new ClusterChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.child.stop(timeout);
	}

	public void start(String registryIP, int registryPort, ChildTask task, int cryptoOption, String rootKey) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, DistributedNodeFailedException
	{
		this.child = new ClusterChildContainer(registryIP, registryPort, task, cryptoOption);
		this.child.start(rootKey);
	}
	
}
