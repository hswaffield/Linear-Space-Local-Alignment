
import java.util.LinkedList;
import java.util.Scanner;


//* Global alignment in linear space:

public class Hirschberg {
	//goal:
	// From source to middle ... returns all the lengths of paths ending in middle row...
	// to sink ... 
	
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
		//String refReverse = new StringBuilder(reference.substring(midCol -1)).reverse().toString();
		//TODO: see if this works ^
		String refReverse = new StringBuilder(reference).reverse().toString();
		
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
	public static int[] middleNode(int[][] scoringMatrix, int indelPenalty, String reference, String search) {
		int[] middNode = new int[2];
		
		if(reference.length() == 1 && search.length() == 1) {
			int[] simple = new int[2];
			simple[0] = 1;
			simple[1] = 1;
			
			// not 0, 0?
			
			return simple;
		}
		
		System.out.println("inMiddleNode: + " + reference + "  search: " + search);
		
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
		
		//(refLen + 1) / 2 - maybe return that?
		
		middNode[0] = searchIndex;
		middNode[1] = (reference.length() + 1) / 2;
		
		System.out.println("maxScore: " + maxNodePathScore);
		System.out.println("maxScore Row: " + middNode[0]);
		System.out.println("maxScore Col: " + middNode[1]);
		
		return middNode;
	}
	
	
	//does the recursive magic:
	
	//TODO: optimize a lot of stuff in this code...
	public static LinkedList<int[]> hirschbergInner(int[][] scoringMatrix, int indelPenalty, String reference, String search, int refOffset, int searchOffset) {
		LinkedList<int[]> middleNodes = new LinkedList<>();
		
		int refLen = reference.length();
		int searchLen = search.length();
		
		
		//: what if one is length 1, and the other is not...
		if (refLen <= 1 && searchLen <= 1) {
		//if (refLen <= 1) {
			
			//nt[] middleNode = new int[2];
			LinkedList<int[]> baseCase = new LinkedList<>();
			//middleNode[1] = refOffset;
			
			int[] middleNode = middleNode(scoringMatrix, indelPenalty, reference, search);
			
			middleNode[0] += searchOffset;
			middleNode[1] += refOffset;
			
			baseCase.add(middleNode);
			
			return baseCase;
			
		} 
		
		else if (refLen <= 1 && searchLen > 1) {
			LinkedList<int[]> baseCase2 = new LinkedList<>();
			
			
			for (int i = 0; i < searchLen; i++) {
				//do all of them...
				int[] next = new int[2];
				next[0] = searchOffset + i;
				next[1] = refOffset;
				baseCase2.add(next);
			}
			
			return baseCase2;
		} else if (refLen > 1 && searchLen <= 1) {
			//third base case...
			LinkedList<int[]> baseCase3 = new LinkedList<>();
			
		//	int[] middleNode = middleNode(scoringMatrix, indelPenalty, reference, search);
			//^1,1
			
			//reflen still 2...
			
			//int[] middleNode2 = middleNode(scoringMatrix, indelPenalty, reference.substring(1), search);
			
			for (int i = 0; i < refLen; i++) {
				//do all of them...
				int[] next = new int[2];
				next[0] = searchOffset;
				next[1] = refOffset + i;
				
				baseCase3.add(next);
			}
			
//			baseCase3.add(middleNode);
//			baseCase3.add(middleNode2);
			
			return baseCase3;
			
		} 
		
		
//		else if (refLen == 2 && searchLen == 2) {
//		
//			//nt[] middleNode = new int[2];
//			LinkedList<int[]> baseCase = new LinkedList<>();
//			//middleNode[1] = refOffset;
//			
//			int[] middleNode = middleNode(scoringMatrix, indelPenalty, reference, search);
//			
//			middleNode[0] += searchOffset;
//			middleNode[1] += refOffset;
//			
//			baseCase.add(middleNode);
//			
//			return baseCase;
//		}
		
		//basecase for length 3 and 2...
		
		else {
			int[] middleNode = middleNode(scoringMatrix, indelPenalty, reference, search);
			
			int middleSearch = middleNode[0];
			int middleRef = middleNode[1];
			
			
//			System.out.println("MIDDLEREF: " + middleRef);
//			System.out.println("MIDDLESEARCH: " + middleSearch);
			
			//existence of column... first column... the one that does not have any ref char ... 
			
			//maybe suntract 1... from midd ref...
			
			System.out.println("Calling recursively: on: " + reference.substring(0, middleRef) + " " + search.substring(0, middleSearch));
			
			middleNodes.addAll(0, hirschbergInner(scoringMatrix, indelPenalty, reference.substring(0, middleRef), search.substring(0, middleSearch), 
					refOffset, searchOffset));
			
			middleNode[0] += searchOffset;
			middleNode[1] += refOffset;
			
			//middlenode of currentProblem:
			middleNodes.add(middleNode);
			
			//-1?
			
			System.out.println("Calling recursively: on: " + reference.substring(middleRef) + " " + search.substring(middleSearch));
			
			middleNodes.addAll(hirschbergInner(scoringMatrix, indelPenalty, reference.substring(middleRef), search.substring(middleSearch),
					refOffset + middleRef, searchOffset + middleSearch));
		
			return middleNodes;
		}
		
	}
	
	
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
		
		//TODO: why do some paths not have 0,0 to begin with?
		
		int[] first = newPath.getFirst();
		//System.out.println("PROCESSING>>> FIRST: BEFORE CHECK " + first[0] + " , " + first[1]);
		
		if(first[0] != 0 || first[1] != 0) {
			
			int[] origin = new int[2];
			
			newPath.addFirst(origin);
		}
		
		//System.out.println("PROCESSING>>> FIRST: " + newPath.getFirst()[0] + " , " + newPath.getFirst()[1]);
		
		
		return newPath;
		
	}
	
	
	public static String[] pathToAlignment(LinkedList<int[]> path, String reference, String search) {
		String[] alignment = new String[2];
		int alignmentSize = path.size();
		
		char[] refAlign = new char[alignmentSize];
		char[] searchAlign = new char[alignmentSize];
		
		
		int refProgress = 0;
		int searchProgress = 0;
		
		System.out.println("SIZE" + alignmentSize + "last elt: " + path.get(alignmentSize - 1)[0] + " " + path.get(alignmentSize - 1)[1]);
		
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
				//we have an insertion:
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
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		 
		String reference = s.nextLine();
		String search = s.nextLine();
		s.close();
		
		
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
		
		for (int[] n : path) {
			
			System.out.print("path: " + n[0] + " col: " + n[1] );
			
			System.out.println();
		}
	
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
		for (String al : alignments) {
			System.out.println(al);
		}
		
	}
}