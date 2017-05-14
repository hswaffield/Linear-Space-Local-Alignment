
import java.util.LinkedList;
import java.util.Scanner;


//* Global alignment in linear space:

public class Hirschberg {
	//goal:
	// From source to middle ... returns all the lengths of paths ending in middle row...
	// to sink ... 

	public static int[] fromSource(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int searchLen = search.length();
		int refLen = reference.length();
		
		//getting the middle:
		int midCol = ((refLen + 1) / 2);
		
		//System.out.println("midcol: " + midCol);
		
		//only need to store O(searchLen) values:
		LinkedList<int[]> currentScores = new LinkedList<>();
		
		//initialization causing trouble!
		
		int[] baseCaseCol = new int[searchLen + 1];
		
		for (int i = 0; i <= searchLen; i++) {
			baseCaseCol[i] = 0 - (indelPenalty * i);
		}
		
		currentScores.add(baseCaseCol);
		
		for (int i = 0; i < midCol; i++) {
			
			int[] nextCol = new int[searchLen+1];
			int[] lastCol = currentScores.removeFirst();
			
			//represents taking lots of insertions:
			nextCol[0] = 0 - (indelPenalty * (i+1));	
			
			//char being considered at this iteration, in the reference
			char refChar = reference.charAt(i);
			
			for (int j = 1; j <= searchLen; j++) {
				int insertionScore = nextCol[j - 1] - indelPenalty;
				int deletionScore = lastCol[j] - indelPenalty;
			
				int matchScore = lastCol[j-1] + Scoring.getScore(scoringMatrix, refChar, search.charAt(j-1));
				
				nextCol[j] = Math.max(Math.max(insertionScore, deletionScore), matchScore);
			}
			
			//Shifting the column information: "Circular Buffer"
			//only store 1 column of information at a time.
			//currentScores.removeFirst();
			currentScores.addLast(nextCol);
			
		}
		
		// currentScores at this point holds the middle row scores:
		return currentScores.removeLast();
	}
	
	public static int[] toSink(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int searchLen = search.length();
		int refLen = reference.length();
		
		//TODO: consider what to do when the string is of odd length
		//odd + even = odd worked
		//even + even = even did not...
		//odd + odd = even did not...
		//odd + even came up short...
		
		
		
		//getting the middle:
		int midCol = refLen - ((refLen + 1) / 2);
		
		//System.out.println("midcol: " + midCol);
		
		//cut in half the part to be reversed:
		String refReverse = new StringBuilder(reference.substring(midCol -1)).reverse().toString();
		//TODO: see if this works ^
		
		
		//String refReverse = new StringBuilder(reference).reverse().toString();
		
		String searchReverse = new StringBuilder(search).reverse().toString();
		
//		System.out.println(reference.substring(0, midCol));
//		System.out.println(refReverse);
		
		//only need to store O(searchLen) values:
		LinkedList<int[]> currentScores = new LinkedList<>();
		
		currentScores.add(new int[searchLen+1]);
		
		for (int i = 0; i < midCol; i++) {
			
			int[] nextCol = new int[searchLen+1];
			int[] lastCol = currentScores.getLast();
			
			//represents taking lots of insertions:
			nextCol[0] = 0 - (indelPenalty * (i+1));	
			
			//char being considered at this iteration, in the reference
			char refChar = refReverse.charAt(i);
			
			for (int j = 1; j <= searchLen; j++) {
				int insertionScore = nextCol[j - 1] - indelPenalty;
				int deletionScore = lastCol[j] - indelPenalty;
			
				int matchScore = lastCol[j-1] + Scoring.getScore(scoringMatrix, refChar, searchReverse.charAt(j-1));
				
				//0 represents a taxi ride from (0, 0):
				nextCol[j] = Math.max(Math.max(insertionScore, deletionScore), matchScore);
			
				//Shifting the column information: "Circular Buffer"
				//only store 1 column of information at a time.
				currentScores.removeFirst();
				currentScores.addLast(nextCol);
			}
		}
		
		// currentScores at this point holds the middle row scores:
		return currentScores.removeFirst();
	}
	
	//This uses fromSource and toSink to find the index of the middle node:
	public static int middleNode(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int[] middNode = new int[2];
		
		int[] fromSourceScores = fromSource(scoringMatrix, indelPenalty, reference, search);
		int[] toSinkScores = toSink(scoringMatrix, indelPenalty, reference, search);
		
		if (fromSourceScores.length != toSinkScores.length) {
			System.out.println("lengths not equal!");
		}
		
		int maxNodePathScore = Integer.MIN_VALUE;
		int searchIndex = -1;
		
		int j = 0;
		for (int i = toSinkScores.length-1; i >= 0; i--) {
			//System.out.println(toSinkScores[i] + fromSourceScores[j]);
			int maxPathThroughNode = toSinkScores[i] + fromSourceScores[j];
			if (maxPathThroughNode > maxNodePathScore) {
				maxNodePathScore = maxPathThroughNode;
				searchIndex = j;
			}
			
			j++;
		}
		
		System.out.println("maxScore: " + maxNodePathScore);
		return searchIndex;
	}
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();
		
		
		int[] fromSourceScores = fromSource(Scoring.BLOSUM62, 5, reference, search);
		
		
//		for (int e : fromSourceScores) {
//			System.out.println(e);
//		}
		
		int[] toSinkScores = toSink(Scoring.BLOSUM62, 5, reference, search);
		
		int midd = middleNode(Scoring.BLOSUM62, 5, reference, search);
		
		System.out.println(midd);
		
//		for (int i = toSinkScores.length -1; i >= 0; i--) {
//			System.out.println(toSinkScores[i]);
//		}
//		
//		System.out.println("summing now:" + toSinkScores.length + "   " + fromSourceScores.length);
//		
//		int j = 0;
//		for (int i = toSinkScores.length-1; i >= 0; i--) {
//			System.out.println(toSinkScores[i] + fromSourceScores[j]);
//			j++;
//		}
	}
}