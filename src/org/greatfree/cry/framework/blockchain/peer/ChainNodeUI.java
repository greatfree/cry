package org.greatfree.cry.framework.blockchain.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

import com.google.gson.GsonBuilder;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
class ChainNodeUI
{
	private ChainNodeUI()
	{
	}

	private static ChainNodeUI instance = new ChainNodeUI();
	
	public static ChainNodeUI CHAIN()
	{
		if (instance == null)
		{
			instance = new ChainNodeUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void printMenu()
	{
		System.out.println(ChainNodeMenu.MENU_HEAD);
		System.out.println(ChainNodeMenu.JOIN_CHAIN);
		System.out.println(ChainNodeMenu.TRAVERSE_CHAIN);
		System.out.println(ChainNodeMenu.VALIDATE_CHAIN);
		System.out.println(ChainNodeMenu.QUIT);
		System.out.println(ChainNodeMenu.MENU_TAIL);
		System.out.println(ChainNodeMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	{
		switch (option)
		{
			case MenuOptions.JOIN_CHAIN:
				ChainNode.CHAIN().joinChain();
				break;
				
			case MenuOptions.TRAVERSE_CHAIN:
				List<String> chains = ChainNode.CHAIN().traverse();
				for (String entry : chains)
				{
					System.out.println(entry);
				}
				break;
				
			case MenuOptions.VALIDATE_CHAIN:
				System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ChainNode.CHAIN().validate()));
				break;
		}
	}
}
