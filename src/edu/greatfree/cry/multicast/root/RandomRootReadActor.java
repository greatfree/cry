package edu.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class RandomRootReadActor<Dispatcher extends CryptoCSDispatcher> extends AsyncMulticastor<PrimitiveMulticastRequest, Dispatcher>
{

//	public RandomRootReadActor(RootSyncMulticastor multicastor)
	public RandomRootReadActor(RootSyncMulticastor<Dispatcher> multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void notify(PrimitiveMulticastRequest request)
	{
		try
		{
//			super.getMulticastor().randomRead(request, super.getCryptoOption());
			super.getMulticastor().randomRead(request);
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
				| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
				| PublicKeyUnavailableException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		catch (IPNotExistedException e)
		{
//			log.info(e.toString());
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(MulticastRequest request, int cryptoOption)
	{
	}
	*/

}
