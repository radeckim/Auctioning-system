import java.util.Scanner;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.security.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Abstract class, which provide a registry lookup by a name.
 * Using that we can provide a stub for the child classes.
 *
 * Child classes: BuyerClient, SellerClient. *
 */

public abstract class Client{
	
	protected RemoteInterface stub;
	protected static InputValidator input;
	protected String userName;
	private String nonce;
	private PublicKey serverPubKey;
	private PublicKey userPubKey;
	private PrivateKey userPrivKey;

	//name of the service we're looking for in the registry
	private static final String SERVICE_NAME = "55";
	private KeyGen keyGen;
	
	public Client() {
	
		try {
			
			keyGen = new KeyGen();				
			
			//create an input validator - used in a child classes to validate the user input
			input = new InputValidator();

			Registry reg = LocateRegistry.getRegistry(8888);
			stub = (RemoteInterface) reg.lookup(SERVICE_NAME);

			System.out.println("Connected. ");
					
			serverPubKey = input.readKey("\\\\lancs\\homes\\42\\radecki\\My Desktop\\x\\src\\myKeypublic.key");
			
		} catch (Exception e){

			e.printStackTrace();
		}
	}

	public boolean login(){	

		boolean finalResponse = false;
	
		userName = input.basicString("Please enter your userName");			
		nonce = input.generateSafeToken();	
		
		
		try{
			
			userPubKey = input.readKey("\\\\lancs\\homes\\42\\radecki\\My Desktop\\x\\src\\" + userName + "public.key");
			userPrivKey = input.readPrivKey("\\\\lancs\\homes\\42\\radecki\\My Desktop\\x\\src\\" + userName + "private.key");
			serverPubKey = input.readKey("\\\\lancs\\homes\\42\\radecki\\My Desktop\\x\\src\\myKeypublic.key");
			
			byte[] toBeChecked = stub.verifyConnection(nonce);				
			boolean isServer = keyGen.verifySign(nonce, toBeChecked, serverPubKey);			
			if(!isServer) return false;			
			
			String serverNonce = stub.sendChallenge(userName);			
			byte[] signed = keyGen.sign(userPrivKey, serverNonce);				
			finalResponse = stub.verifyAuthent(signed, userName);			
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		return finalResponse;
	}
}	