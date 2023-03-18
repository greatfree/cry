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

import edu.greatfree.cry.cluster.ChildTask;
import edu.greatfree.cry.cluster.child.ClusterChildContainer;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;

/**
 * 
 * The accessibility, public and private, does not make sense to children of clusters since the root determine the attribute. So the class is not invoked. 06/21/2022, Bing Li
 * 
 * @author libing
 * 
 * 05/17/2022
 *
 */
final class ClusterChildOfPrivateRoot
{
	private ClusterChildContainer child;

	private ClusterChildOfPrivateRoot()
	{
	}
	
	private static ClusterChildOfPrivateRoot instance = new ClusterChildOfPrivateRoot();
	
	public static ClusterChildOfPrivateRoot CRY()
	{
		if (instance == null)
		{
			instance = new ClusterChildOfPrivateRoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, SignatureException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.stop(timeout);
	}

//	public void start(String registryIP, int registryPort, ChildTask task, int cryptoOption, String rootKey, boolean isRootPrivate) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException
	public void start(String registryIP, int registryPort, ChildTask task, int cryptoOption, String rootKey) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException
	{
		this.child = new ClusterChildContainer(registryIP, registryPort, task, cryptoOption);
//		this.child.start(rootKey, isRootPrivate);
		this.child.start(rootKey);
	}

}
