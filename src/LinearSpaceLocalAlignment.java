import java.util.LinkedList;
import java.util.Scanner;

//Algorithm which computes a local alignment with linear space

public class LinearSpaceLocalAlignment {
	
	// this returns the coordinates and score of the end of the local alignment
	public static int[] findEndBoundary(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		// this will hold the data to be returned:
		int[] coordinateData = new int[3];
		int refIndex = 0;
		int searchIndex = 0;
		int maxScoreValue = -1;
		
		int searchLen = search.length();
		int refLen = reference.length();
		
		//only need to store O(searchLen) values:
		LinkedList<int[]> currentScores = new LinkedList<>();
		
		currentScores.add(new int[searchLen+1]);
		
		for (int i = 0; i < refLen; i++) {
			
			int[] nextCol = new int[searchLen+1];
			int[] lastCol = currentScores.getLast();
			
			//char being considered at this iteration, in the reference
			char refChar = reference.charAt(i);
			
			for (int j = 1; j <= searchLen; j++) {
				int insertionScore = nextCol[j - 1] - indelPenalty;
				int deletionScore = lastCol[j] - indelPenalty;
				
				int maxIndelScore = Math.max(insertionScore, deletionScore);
				
				int matchScore = lastCol[j-1] + Scoring.getScore(scoringMatrix, refChar, search.charAt(j-1));
				
				//0 represents a taxi ride from (0, 0):
				nextCol[j] = Math.max(maxIndelScore, Math.max(matchScore, 0));
				
				//comparing to observed max:
				if (nextCol[j] >= maxScoreValue) {
					maxScoreValue = nextCol[j];
					refIndex = i;
					searchIndex = j - 1;
				}
			}
			
			//Shifting the column information: "Circular Buffer"
			//only store 1 column of information at a time.
			currentScores.removeFirst();
			currentScores.addLast(nextCol);
		}
		
		//this variable holds the three data points to be returned:
		coordinateData[0] = maxScoreValue;
		coordinateData[1] = refIndex;
		coordinateData[2] = searchIndex;
		
		return coordinateData;
	}
		
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();
		
		int[] scores = findEndBoundary(Scoring.PAM250, 5, reference, search);
		
		System.out.println(scores[0]);

		System.out.println(scores[1]);
		System.out.println(scores[2]);
	}
}
