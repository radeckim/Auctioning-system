import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.io.IOException;

public class Test{	
	
	public static void main (String args[]){
				
		UserInterface x = new UserInterface();
					
		x.modeChoice();
		x.play();		
		
		System.out.println("Thank you for using our auctioning system. ");
	}
}

