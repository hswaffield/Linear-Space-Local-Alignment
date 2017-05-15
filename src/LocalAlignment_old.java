//contains a basic local alignment algorithm, runs with O(n^2) time and memory

import java.util.LinkedList;
import java.util.Scanner;

public class LocalAlignment_old {

	//the chars that are ultimately printed:
	public static LinkedList<Character> alignReference = new LinkedList<Character>();
	public static LinkedList<Character> alignSearch = new LinkedList<Character>();
	
	//prints the alignments, which are in static class variables
	public static void printAlignment() {
		 for(char c : alignReference) {
			 System.out.print(c);
		 }
		 
		 System.out.println();
		 
		 for(char c : alignSearch) {
			 System.out.print(c);
		 }
	}
	
	
	//Performs local alignment, using specified scoring matrix:
	public static void localAlignment(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		//top and leftmost rows reflect no substring, not even first letter...
		 int width = reference.length() + 1;
		 int height = search.length() + 1;
		 
		 int[][] alignment = new int[height][width];
		 
		 //< back left	 ^ up	 / match	 T taxi to 0,0
		 char[][] backtrack = new char[height][width];
		 
		 //initiliazing base cases
		 for (int i = 0; i < width; i++) {
			 backtrack[0][i] = 'T';
		 }
		 
		 for (int i = 0; i < height; i++) {
			 backtrack[i][0] = 'T';
		 }
		 
		 //to enable second taxi ride:
		 int maxScore = -1;
		 int maxScoreRow = -1;
		 int maxScoreCol = -1;
		 
		 //Computing the score:
		 for (int i = 1; i < height; i++) {
			 for (int j = 1; j < width; j++) {
				 //Picking which move to make:
				 int maxIndel;
				 
				 //stores the move:
				 char indelBack;
				 
				 if (alignment[i-1][j] >= alignment[i][j-1]) {
					 maxIndel = alignment[i-1][j] - indelPenalty;
					 indelBack = '^';
				 } else {
					 maxIndel = alignment[i][j-1] - indelPenalty;
					 indelBack = '<';
				 }
				 
				 int matchScore = alignment[i-1][j-1] +  Scoring.getScore(scoringMatrix, reference.charAt(j-1), search.charAt(i-1));
												 
				 if (maxIndel > matchScore && maxIndel > 0) {
					 //take the maxIndel...
					 alignment[i][j] = maxIndel;
					 backtrack[i][j] = indelBack;
				 } else if (matchScore > 0) {
					 alignment[i][j] = matchScore;
					 backtrack[i][j] = '/';
				 } else {
					 //Taxi...
					 alignment[i][j] = 0;
					 backtrack[i][j] = 'T';
				 }
				 
				 //updating max score seen
				 if (alignment[i][j] >= maxScore) {
					 maxScore = alignment[i][j];
					 maxScoreRow = i;
					 maxScoreCol = j;
				 }				 
			 }
		 }
		 
		//printing the final score:
		System.out.println(maxScore);

		//gets actual chars:
		followLocalBacktrack(reference, search, backtrack, maxScoreRow, maxScoreCol);
	}
	

	//Example reads in input strings and will score them using the PAM250 matrix:
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		 String reference = s.nextLine();
		 String search = s.nextLine();
		 s.close();
		 
		 localAlignment(Scoring.PAM250, 5, reference, search);
		 printAlignment();
	}
	
	// A helper
	private static String[] returnAlignment() {
		 int len = alignReference.size();
	
		 char[] first = new char[len];
		 char[] second = new char[len];
		 
		 for (int i = 0; i < len; i++) {
			 first[i] = alignReference.removeFirst();
			 second[i] = alignSearch.removeFirst();
		 }
		 
		 String[] alignment = new String[2];
		 alignment[0] = String.valueOf(first);
		 alignment[1] = String.valueOf(second);
		 
		 return alignment;
	 }


	 // for other classes to use:
	 public static String[] localAlignExternal(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		 localAlignment(scoringMatrix, indelPenalty, reference, search);
		 return returnAlignment();
	 } 
	
	//produces the backtrack sequences:
	private static void followLocalBacktrack(String reference, String search, char[][] backtrack, int row, int col) {
		
		//< back left	 ^ up	 / match	 T taxi to 0,0
					
	 	//here is where we add the chars:
		if (backtrack[row][col] == '<') {
			alignReference.addFirst(reference.charAt(col-1));
			alignSearch.addFirst('-');
			followLocalBacktrack(reference, search, backtrack, row, col-1);
		} else if (backtrack[row][col] == '^') {
			alignReference.addFirst('-');
			alignSearch.addFirst(search.charAt(row-1));
			followLocalBacktrack(reference, search, backtrack, row-1, col);
		} else if (backtrack[row][col] == 'T') {
			return;
		} else {
			//there was a match/mismatch:
			alignReference.addFirst(reference.charAt(col-1));
			alignSearch.addFirst(search.charAt(row-1));
			followLocalBacktrack(reference, search, backtrack, row-1, col-1);
		}
	}
}
