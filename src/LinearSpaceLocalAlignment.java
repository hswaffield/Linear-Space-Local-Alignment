import java.util.LinkedList;
import java.util.Scanner;

//Algorithm which computes a local alignment with linear space
public class LinearSpaceLocalAlignment {
	
	// this returns the coordinates and score of the end of the local alignment
	// performed in linear space:
	public static int[] findLocalBoundaries(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		// this will hold the data to be returned:
		int[] coordinateData = new int[5];
		int refIndex = 0;
		int searchIndex = 0;
		int maxScoreValue = -1;
		
		int refIndexFirst = 0;
		int searchIndexFirst = 0;
		
		int searchLen = search.length();
		int refLen = reference.length();
		
		//only need to store O(searchLen) values:
		LinkedList<int[]> currentScores = new LinkedList<>();
		
		//this stores the beginning of every local sequence, holds first boundary indexes:
		LinkedList<int[][]> currentTaxiPredecessors = new LinkedList<>();
				
		currentScores.add(new int[searchLen+1]);
		
		currentTaxiPredecessors.add(new int[searchLen+1][2]);
		
		for (int i = 0; i < refLen; i++) {
			
			int[] nextCol = new int[searchLen+1];
			int[] lastCol = currentScores.getLast();
			
			int[][] nextPredecessors = new int[searchLen+1][2];
			int[][] lastPredecessors = currentTaxiPredecessors.getLast();
			
			//char being considered at this iteration, in the reference
			char refChar = reference.charAt(i);
			
			for (int j = 1; j <= searchLen; j++) {
				int insertionScore = nextCol[j - 1] - indelPenalty;
				int deletionScore = lastCol[j] - indelPenalty;

				int[] currentTaxiPred = new int[2];
								
				int matchScore = lastCol[j-1] + Scoring.getScore(scoringMatrix, refChar, search.charAt(j-1));
				
				//0 represents a taxi ride from (0, 0):
				nextCol[j] = Math.max(Math.max(insertionScore, deletionScore), Math.max(matchScore, 0));
				
				// update the current taxi predecessor, just like the score
				if (nextCol[j] == 0) {
					currentTaxiPred[0] = i;
					currentTaxiPred[1] = j-1; 
				} else if (nextCol[j] == matchScore) {
					//we took a match/mismatch
					currentTaxiPred = lastPredecessors[j-1];
					
					// add 1 if the match was from a taxi region?
					if (lastCol[j-1] == 0) {
						currentTaxiPred[0] += 1;
						currentTaxiPred[1] += 1;
					}
				} else if (nextCol[j] == insertionScore) {
					currentTaxiPred = nextPredecessors[j - 1];
				} else if (nextCol[j] == deletionScore) {
					currentTaxiPred = lastPredecessors[j];
				}

				nextPredecessors[j] = currentTaxiPred;
				
				//comparing to observed max:
				if (nextCol[j] >= maxScoreValue) {
					maxScoreValue = nextCol[j];
					refIndex = i;
					searchIndex = j - 1;
					
					refIndexFirst = currentTaxiPred[0];
					searchIndexFirst = currentTaxiPred[1];
				}
			}
			
			//Shifting the column information: "Circular Buffer"
			//only store 1 column of information at a time.
			currentScores.removeFirst();
			currentScores.addLast(nextCol);
			
			//similarly shifting predecessors
			currentTaxiPredecessors.removeFirst();
			currentTaxiPredecessors.addLast(nextPredecessors);
		}
		
		//this variable holds the three data points to be returned:
		coordinateData[0] = maxScoreValue;
		coordinateData[1] = refIndexFirst;
		coordinateData[2] = searchIndexFirst;
		coordinateData[3] = refIndex;
		coordinateData[4] = searchIndex;
		
		return coordinateData;
	}
	
	//linear Space local Align:
	public static String[] linearSpaceLocalAlign(int[][] scoringMatrix, int indelPenalty, String reference, String search ) {
		int[] gridInfo = findLocalBoundaries(scoringMatrix, indelPenalty, reference, search);
		
		String refLocal = reference.substring(gridInfo[1], gridInfo[3]+1);
		String searchLocal = search.substring(gridInfo[2], gridInfo[4]+1);
	
		String[] alignment = GlobalAlignment_old.globalAlignExternal(scoringMatrix, indelPenalty, refLocal, searchLocal);
		
		return alignment;
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();
		
		//Timing our new version:
		
		long startTime = System.nanoTime();
		
		String[] align = linearSpaceLocalAlign(Scoring.PAM250, 5, reference, search);
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		
		
		System.out.println();
		
		System.out.println(align[0]);
		System.out.println(align[1]);
		System.out.println("new runtime duration (nano): " + duration);
		System.out.println("new runtime duration: " + (duration + 0.0) / 1000000000.0);
		
		
		System.out.println();
		
		//Timing our old version:
		
		long startTime_old = System.nanoTime();
		
		String[] align_old = LocalAlignment_old.localAlignExternal(Scoring.PAM250, 5, reference, search);
		
		long endTime_old = System.nanoTime();
		
		long duration_old = (endTime_old - startTime_old);

		
		System.out.println(align_old[0]);
		System.out.println(align_old[1]);
		System.out.println("old runtime duration (nano): " + duration_old);
		System.out.println("old runtime duration: " + (duration_old + 0.0) / 1000000000.0);
		
		System.out.println();
		
		System.out.println("time increased by a factor of: " + (duration + 0.0) / (duration_old + 0.0));
	}
	
	
	// println shortcut:
	private static <T> void p(T thing) {
		System.out.println(thing);
	}

	// print shortcut:
	private static <T> void p2(T thing) {
		System.out.print(thing);
	}
}
