import java.util.Scanner;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64.Encoder;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.*;



/**
 * A utility class used to validate a user input.
 * The class is able to check if the user input is:
 *
 * - a positive integer
 * - an integer in a set <1, 2, 3>
 * - a string
 *
 */

public class InputValidator{
	
	private Scanner reader;

	/**
	 * Create input validator and initialize a scanner.
	 *
	 */
	
	public InputValidator(){

		reader = new Scanner(System.in);
	}

	/**
	 * Check if input is a positive integer.
	 *
	 * @param message displayed when the user input
	 * @return if the number is finally correct - return it
	 */
	
	public int integerValidation(String message){

		//the user input
		int n;
		
		do{ 
		
			System.out.println(message);

			//iterate until the number is int
			while(!reader.hasNextInt()){
				
				System.out.println("Please enter a positive integer value.");
				reader.next();
			} 
			
			n = reader.nextInt();
			reader.nextLine();

		//iterate till the input is positive
		} while (n < 0);

		//correct input
		return n;
	}

	/**
	 * Check if input is a valid user choice - it's either 1 or 2 or 3
	 *
	 * @return if the number is finally correct - return it.
	 */
	
	public int userChoiceValidator(){

		//the user input
		int n;
		
		do{ 
		
			System.out.println("Please enter a number from the list: ");

			//iterate until the number is int
			while(!reader.hasNextInt()){
				
				System.out.println("Your choice should be an integer. Please, try again: ");
				reader.next();
			} 
			
			n = reader.nextInt();

		//n stands for possible choicec - user can choice either 1 or 2 or 3
		} while (!(n == 1 || n == 2 || n == 3)); 


		//correct input
		return n;
	}

	/**
	 * Check if an input is a string
	 *
	 * @param message displayed when the user input
	 * @return input string
	 */

	public String basicString(String message){	
		
		System.out.println(message);				
        String s = reader.nextLine();	
		
		return s;
	}	
	
	
	public String generateSafeToken() {
		
		SecureRandom secureR = new SecureRandom();
		byte array[] = new byte[20];
		secureR.nextBytes(array);
		Encoder enc = Base64.getUrlEncoder().withoutPadding();
		String token = enc.encodeToString(array);
		
		return token;
	}
	
	public PublicKey readKey(String path) throws Exception{
		
		byte[] bytes = Files.readAllBytes(Paths.get(path));

		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pub = kf.generatePublic(ks);	
		
		return pub;
	}
	
	public PrivateKey readPrivKey(String path) throws Exception{
		
		byte[] bytes = Files.readAllBytes(Paths.get(path));

		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pvt = kf.generatePrivate(ks);
		
		return pvt;
	}
}