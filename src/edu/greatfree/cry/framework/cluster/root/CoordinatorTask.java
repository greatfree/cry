package edu.greatfree.cry.framework.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.greatfree.cry.cluster.RootTask;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.cluster.message.ClusterAppID;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class CoordinatorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.cluster.root");

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getClusterAppID())
		{
			case ClusterAppID.STOP_ROOT_NOTIFICATION:
				log.info("STOP_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					PublicClusterRoot.CRY().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| InterruptedException | RemoteReadException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException
						| SymmetricKeyUnavailableException | IOException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ClusterAppID.STOP_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					PublicClusterRoot.CRY().stopCluster();
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				catch (RemoteIPNotExistedException e)
				{
					log.info(e.toString());
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					log.info(e.toString());
					e.printStackTrace();
				}
				break;

				/*
			case CryAppID.CLUSTER_CRYPTO_OPTION_NOTIFICATION:
				log.info("CLUSTER_CRYPTO_OPTION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				break;
				*/
		}
		
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
