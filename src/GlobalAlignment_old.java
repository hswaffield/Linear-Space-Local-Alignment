// This contains code for a O(n^2) space solution for a basic alignment algorithm

import java.util.LinkedList;
import java.util.Scanner;


public class GlobalAlignment_old {
	//the chars that are ultimately printed:
	public static LinkedList<Character> alignFirst = new LinkedList<Character>();
	public static LinkedList<Character> alignSecond = new LinkedList<Character>();
	
	 //This computes the actual global alignment:
	//include an indel penalty param
	 public static void globalAlignment(int[][] scoringMatrix, String reference, String search) {
		 //top and leftmost rows reflect no substring, not even first letter...
		 int width = reference.length() + 1;
		 int height = search.length() + 1;
		 
		 int[][] alignment = new int[height][width];		 
		 char[][] backtrack = new char[height][width];
		 
		 //initiliazing base cases
		 for (int i = 0; i < width; i++) {
			 backtrack[0][i] = '<';
			 alignment[0][i] = -5 * i;
		 }
		 
		 for (int i = 0; i < height; i++) {
			 backtrack[i][0] = '^';
			 alignment[i][0] = -5 * i;
		 }
			 
		 backtrack[0][0] = '/';
		 
		 //Computing the score:
		 for (int i = 1; i < height; i++) {
			 for (int j = 1; j < width; j++) {
				 //Picking which move to make:
				 int maxIndel;
				 
				 //stores the move:
				 char indelBack;
				 
				 if (alignment[i-1][j] >= alignment[i][j-1]) {
					 maxIndel = alignment[i-1][j] - 5;
					 indelBack = '^';
				 } else {
					 maxIndel = alignment[i][j-1] - 5;
					 indelBack = '<';
				 }
				 
				 int matchScore = alignment[i-1][j-1] + Scoring.getScore(scoringMatrix, reference.charAt(j-1), search.charAt(i-1));
												 
				 if (maxIndel > matchScore) {
					 //take the maxIndel...
					 alignment[i][j] = maxIndel;
					 backtrack[i][j] = indelBack;
				 } else {
					 alignment[i][j] = matchScore;
					 backtrack[i][j] = '/';
				 }
				 
				 alignment[i][j] = Math.max(maxIndel, matchScore);
			 }
		 }
			 
		 //printing the final score:
		 System.out.println(alignment[height-1][width-1]);
		 
		 //computing the backtrack path:
		 followBacktrack(reference, search, backtrack, search.length(), reference.length());
	 }
	

	 //Accesses static linkedlist variables, and prints the alignment:
	 public static void printAlignment(){
		 for(char c : alignFirst) {
			 System.out.print(c);
		 }
		 
		 System.out.println();
		 
		 for(char c : alignSecond) {
			 System.out.print(c);
		 }
	 }
	 
	 //This main method will read in two strings and perform a global alignment
	 public static void main(String[] args) {
		 Scanner s = new Scanner(System.in);
		 
		 String first = s.nextLine();
		 String second = s.nextLine();
		 s.close();
		 
		 globalAlignment(Scoring.BLOSUM62, first, second);
		 printAlignment();
	 }
	 
	
	//Follows the backtrack matrix, and adds characters to actual sequences
	 private static void followBacktrack(String str1, String str2, char[][] backtrack, int row, int col ){
		if (row == 0 && col == 0) {
				return;
		}
		 			
	 	//here is where we add the chars
		if (backtrack[row][col] == '<') {
			alignFirst.addFirst(str1.charAt(col-1));
			alignSecond.addFirst('-');
			followBacktrack(str1, str2, backtrack, row, col-1);
		} else if (backtrack[row][col] == '^') {
			alignFirst.addFirst('-');
			alignSecond.addFirst(str2.charAt(row-1));
			followBacktrack(str1, str2, backtrack, row-1, col);
		} else {
			//there was a match/mismatch:
			alignFirst.addFirst(str1.charAt(col-1));
			alignSecond.addFirst(str2.charAt(row-1));
			followBacktrack(str1, str2, backtrack, row-1, col-1);
		}
	 }
}
