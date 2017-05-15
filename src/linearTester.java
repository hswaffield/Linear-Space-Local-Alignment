import java.util.Scanner;

public class linearTester {
	
	//A Method to test the new version of local Alignment
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();
		
		//Timing our new version:
		
		long startTime = System.nanoTime();
		
		String[] align = LinearSpaceLocalAlignment.linearSpaceLocalAlign(Scoring.PAM250, 5, reference, search);
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		
		System.out.println();

		System.out.println("new runtime duration (nano): " + duration);
		System.out.println("new runtime duration: " + (duration + 0.0) / 1000000000.0);
	}
}
