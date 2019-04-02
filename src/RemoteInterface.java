import java.rmi.*;
import java.util.HashMap;
import java.security.*;

/**
* An interface which provides a possible implementation of RMI stub in the auctioning system. 
*
*/

public interface RemoteInterface extends Remote{	

    public int createAuction(int startingPrice, String description, int minimumPrice, String userName) throws RemoteException;
    public String deleteAuction(int id, String userName) throws RemoteException;
    public HashMap browseAuctions() throws RemoteException;
    public int bidAuction(int id, int increase, String email, String name) throws RemoteException;
	
	public byte[] verifyConnection(String nonce) throws Exception, InvalidKeyException;	
	public String sendChallenge(String userName) throws RemoteException;
	public boolean verifyAuthent(byte[] toBeChecked, String userName) throws Exception, RemoteException;
	
	//public boolean authentication(String userName, Integer digestPass) throws RemoteException;
}
