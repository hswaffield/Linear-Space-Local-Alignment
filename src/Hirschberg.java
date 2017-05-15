import java.util.LinkedList;
import java.util.Scanner;

//* Global alignment in linear space... not yet completely functional... produces isolated errors in otherwise correct paths
// 13+ hours in, output is close, but not completely correct, in every case.

// a real world benefit of using this algorithm as part of local alignment is most felt, when the local alignment portion 
// of the problem, those subsequences within the "local alignment boundaries" is largest
public class Hirschberg {
	
	//computes a global alignment score for two strings in linear space
	public static int scoreGlobally(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int searchLen = search.length();
		int refLen = reference.length();
		
		//only need to store O(searchLen) values:
		LinkedList<int[]> currentScores = new LinkedList<>();
		
		//initialization causing trouble!
		
		int[] baseCaseCol = new int[searchLen + 1];
		
		for (int i = 0; i <= searchLen; i++) {
			baseCaseCol[i] = 0 - (indelPenalty * i);
		}
		
		currentScores.add(baseCaseCol);
		
		for (int i = 0; i < refLen; i++) {
			
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
		return currentScores.removeLast()[searchLen];
	}
	
	// Computes and returns the array of values for the longest path going through each middle node from source
	public static int[] fromSource(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int searchLen = search.length();
		int refLen = reference.length();
		
		//getting the middle:
		int midCol = ((refLen + 1) / 2);
		
		//only need to store O(searchLen) values:
		LinkedList<int[]> currentScores = new LinkedList<>();
		
		//initializing, like in standard global alignment
		int[] baseCaseCol = new int[searchLen + 1];
		
		for (int i = 0; i <= searchLen; i++) {
			baseCaseCol[i] = 0 - (indelPenalty * i);
		}
		
		currentScores.add(baseCaseCol);
		
		for (int i = 0; i < midCol; i++) {
			
			int[] nextCol = new int[searchLen+1];
			int[] lastCol = currentScores.removeFirst();
			
			//represents taking lots of insertions (top row base cases):
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
	
	//Similar to fromSource, however it computes the scores from the sink to the middle nodes, 
	// using a similar algorithm, but with reversed input strings.
	public static int[] toSink(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int searchLen = search.length();
		int refLen = reference.length();
		
		//getting the middle:
		int midCol = refLen - ((refLen + 1) / 2);
	
		String refReverse = new StringBuilder(reference).reverse().toString();
		
		String searchReverse = new StringBuilder(search).reverse().toString();

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
	//The middle node is the highest index value, with a maximum i-path, 
	//the sum of its respective fromSource and toSink values, the length
	//of the longest path through that node.
	public static int[] middleNode(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int[] middNode = new int[2];
		
		if(reference.length() == 1 && search.length() == 1) {
			int[] simple = new int[2];
			simple[0] = 1;
			simple[1] = 1;
			return simple;
		}
		
		int[] fromSourceScores = fromSource(scoringMatrix, indelPenalty, reference, search);
		int[] toSinkScores = toSink(scoringMatrix, indelPenalty, reference, search);
		
		if (fromSourceScores.length != toSinkScores.length) {
			System.out.println("lengths not equal!");
		}
		
		int maxNodePathScore = Integer.MIN_VALUE;
		int searchIndex = -1;
		
		int j = 0;
		for (int i = toSinkScores.length-1; i >= 0; i--) {
			int maxPathThroughNode = toSinkScores[i] + fromSourceScores[j];
			if (maxPathThroughNode > maxNodePathScore) {
				maxNodePathScore = maxPathThroughNode;
				searchIndex = j;
			}
			
			j++;
		}
		
		middNode[0] = searchIndex;
		middNode[1] = (reference.length() + 1) / 2;
		
//		System.out.println("maxScore: " + maxNodePathScore);
//		System.out.println("maxScore Row: " + middNode[0]);
//		System.out.println("maxScore Col: " + middNode[1]);
		
		return middNode;
	}

	// hirschbergInner: recursively breaks problems down into left and right subproblems, where middle nodes, those which can
	// be determined to exist in a correct optimal path, are added, to a path, which is later processed/
	public static LinkedList<int[]> hirschbergInner(int[][] scoringMatrix, int indelPenalty, 
			String reference, String search, int refOffset, int searchOffset) {
		LinkedList<int[]> middleNodes = new LinkedList<>();
		
		int refLen = reference.length();
		int searchLen = search.length();
		
		if (refLen <= 1 && searchLen <= 1) {
			LinkedList<int[]> baseCase = new LinkedList<>();
			
			int[] middleNode = middleNode(scoringMatrix, indelPenalty, reference, search);
			
			middleNode[0] += searchOffset;
			middleNode[1] += refOffset;
			
			baseCase.add(middleNode);
			
			return baseCase;
		} 
		
		else if (refLen <= 1 && searchLen > 1) {
			LinkedList<int[]> baseCase2 = new LinkedList<>();
			
			for (int i = 0; i < searchLen; i++) {
				int[] next = new int[2];
				next[0] = searchOffset + i;
				next[1] = refOffset;
				baseCase2.add(next);
			}
			
			return baseCase2;
		} else if (refLen > 1 && searchLen <= 1) {
			//third base case...
			LinkedList<int[]> baseCase3 = new LinkedList<>();
			for (int i = 0; i < refLen; i++) {
				//do all of them...
				int[] next = new int[2];
				next[0] = searchOffset;
				next[1] = refOffset + i;
				
				baseCase3.add(next);
			}
			
			return baseCase3;
		} 
		
		else {
			int[] middleNode = middleNode(scoringMatrix, indelPenalty, reference, search);
			
			int middleSearch = middleNode[0];
			int middleRef = middleNode[1];
			
			//System.out.println("Calling recursively: on: " + reference.substring(0, middleRef) + " " + search.substring(0, middleSearch));
			
			middleNodes.addAll(0, hirschbergInner(scoringMatrix, indelPenalty, reference.substring(0, middleRef), search.substring(0, middleSearch), 
					refOffset, searchOffset));
			
			middleNode[0] += searchOffset;
			middleNode[1] += refOffset;
			
			//middlenode of currentProblem:
			middleNodes.add(middleNode);

			//System.out.println("Calling recursively: on: " + reference.substring(middleRef) + " " + search.substring(middleSearch));
			
			middleNodes.addAll(hirschbergInner(scoringMatrix, indelPenalty, reference.substring(middleRef), search.substring(middleSearch),
					refOffset + middleRef, searchOffset + middleSearch));
		
			return middleNodes;
		}
		
	}
	
	//getPathCoords processes the path output from hirshbergInner, removing redudant information, and adding the beginning and 
	// end nodes, if necessary
	public static LinkedList<int[]> getPathCoords(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		LinkedList<int[]> pathToProcess = hirschbergInner(scoringMatrix, indelPenalty, reference, search, 0, 0);
		int numToCheck = pathToProcess.size() - 1;

		LinkedList<int[]> newPath = new LinkedList<>();
		for (int i = 0; i < numToCheck; i++) {
			int[] current = pathToProcess.removeFirst();
			
			int[] next = pathToProcess.getFirst();
			
			if (current[0] != next[0] || current[1] != next[1]) {
				newPath.add(current);
			}
		}
		
		int[] last = newPath.getLast();
		int[] next = pathToProcess.getFirst();
		if(last[0] != next[0] || last[1] != next[1]) {
			newPath.add(next);
		}
		
		int[] first = newPath.getFirst();
	
		if(first[0] != 0 || first[1] != 0) {
			
			int[] origin = new int[2];
			
			newPath.addFirst(origin);
		}
		
		return newPath;	
	}
	
	// pathToAlignment translastes a processed coordinate path, to an alignment, outputing strings
	public static String[] pathToAlignment(LinkedList<int[]> path, String reference, String search) {
		String[] alignment = new String[2];
		int alignmentSize = path.size();
		
		char[] refAlign = new char[alignmentSize];
		char[] searchAlign = new char[alignmentSize];
		
		int refProgress = 0;
		int searchProgress = 0;
		
		for (int i = 0; i < alignmentSize - 1; i++) {
			int[] current = path.removeFirst();
			int[] next = path.getFirst();
			
			//Three possible moves to be made:
			
			if(current[0] + 1 == next[0] && current[1] + 1 == next[1]) {
				//we have a match/mismatch...
				refAlign[i] = reference.charAt(refProgress);
				searchAlign[i] = search.charAt(searchProgress);
				
				refProgress++;
				searchProgress++;
				
			} else if (current[0] + 1 == next[0] && current[1] == next[1]) {
				//there is an insertion:
				refAlign[i] = '-';
				searchAlign[i] = search.charAt(searchProgress);
				
				searchProgress++;
			} else if (current[1] + 1 == next[1] && current[0] == next[0]) {
				//there is a deletion:
				refAlign[i] = reference.charAt(refProgress);
				searchAlign[i] = '-';
				
				refProgress++;
			} else {
				System.out.println("ERRROROROROOROR");
			}
		}
		
		alignment[0] = String.valueOf(refAlign);
		alignment[1] = String.valueOf(searchAlign);
		return alignment;
	}
	
	//Reads in inputs, will run various functions on them:
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();
		
		//TRY: PLEASANTLY, MEANLY
		
//		int globalScore = scoreGlobally(Scoring.BLOSUM62, 5, reference, search);
//		
//		System.out.println("score: " + globalScore);
//		
//		int[] fromSourceScores = fromSource(Scoring.BLOSUM62, 5, reference, search);
//		
//		for (int e : fromSourceScores) {
//			System.out.println("fromSource: " + e);
//		}
//		
//		int[] toSinkScores = toSink(Scoring.BLOSUM62, 5, reference, search);
//		
//		int midd[] = middleNode(Scoring.BLOSUM62, 5, reference, search);
//		
//		for (int i = toSinkScores.length -1; i >= 0; i--) {
//			System.out.println("toSink: " + toSinkScores[i]);
//		}
//		
//		for (int e : midd) {
//			System.out.println("middle: " + e);
//		}
		
		LinkedList<int[]> path = hirschbergInner(Scoring.BLOSUM62, 5, reference, search, 0, 0);
		
//		for (int[] n : path) {
//			
//			System.out.print("path: " + n[0] + " col: " + n[1] );
//			
//			System.out.println();
//		}
	
		LinkedList<int[]> pathProcessed = getPathCoords(Scoring.BLOSUM62, 5, reference, search);
//
//		for (int[] n : pathProcessed) {
//			
//			System.out.print("processed: " + n[0] + " col: " + n[1] );
//			
//			System.out.println();
//		}
//		
//		
		String[] alignments = pathToAlignment(pathProcessed, reference, search);
//		
//
//		
//		
		System.out.println(scoreGlobally(Scoring.BLOSUM62, 5, reference, search));
		
		for (String al : alignments) {
			System.out.println(al);
		}
		
	}
}