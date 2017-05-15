import java.util.Scanner;

public class oldLocalTester {

	//A method to time to old version of LocalAlignment:
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();

		//Timing our old version:
		
		long startTime_old = System.nanoTime();
		
		String[] align_old = LocalAlignment_old.localAlignExternal(Scoring.PAM250, 5, reference, search);
		
		long endTime_old = System.nanoTime();
		
		long duration_old = (endTime_old - startTime_old);

		System.out.println("old runtime duration (nano): " + duration_old);
		System.out.println("old runtime duration: " + (duration_old + 0.0) / 1000000000.0);
		
		System.out.println();
	}

}
