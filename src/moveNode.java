// Daniel Bergman, TCSS 435 Winter 2016. All Rights Reserved. 

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


// A node class for a game state ("move") in Pentago. 
public class moveNode {
	
	//private moveNode previous;
	private String color;
	private char[][] board;
	private int weight;
// constructor
	public moveNode(String theColor, char[][] theBoard) {
		
		color = theColor;
		board = theBoard;
		weight = weighMove();
	}
	
// method to copy a node	
	public moveNode copy() {
		moveNode newNode;
		newNode = new moveNode(color, board);
		
		return newNode;
		
	}
	
	// returns the weight of this move, by subtracting the longest sequence on this moves board possessed by Black
	// from the longest possessed by White. adds 100 to the value if white has one, or subtracts 100 if black has won.
	public int weighMove(){
		int weight = 0;
		
		int whiteScore = countLongest("white");
		if (whiteScore >= 5) whiteScore = 100;	// Mark winning move for white
		
		int blackScore = countLongest("black");
		if (blackScore >= 5) blackScore = 100;  // Mark winning move for black
		
		weight = whiteScore-blackScore;
		
		return weight;
	}

	// finds the longest sequence of matching points for a given color in any of the 4 axes.
	// recursively loops over the gameboard. For each spot that contains a matching color, compiles a list 
	// of adjacent points and searches for matches. Once a match is found, the function calls a recursive
	// method checkLength() to continue to check that directions for matches until a non-matching point
	// is found, and then checks in the opposite direction. The longest found sequence is continuously updated
	// until the entire board has been checked.
	private int countLongest(String color) {
		int longest = 0;
		char check = 0;
		if (color == "white") check = 'w';
		if (color == "black") check = 'b';
		
		for (int i = 1; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				
				if (board[i][j] == check) { 
					int longest1 = 1;
				
					Point current = new Point(j,i);
					
					List<Point> nexts = checkAdjacentPoints (current, check);
					for (Point next: nexts) {
						int length = 1;
						String direction = checkDirection(current, next);
						length += checkLength(current, direction, check);
						
						String opposite = getOppositeDir(direction);
						length += checkLength(current, opposite, check);
						if (length > longest1) longest1 = length;
					}
					
					if (longest1 > longest) longest = longest1;
				}
			}
		}
		
		return longest;
	}

	// given a direction, returns the opposite direction.
	private String getOppositeDir(String direction) {
		
		String opposite = null;
		if (direction.equals("N")) opposite = "S";
		else if (direction.equals("S")) opposite = "N";
		else if (direction.equals("E")) opposite = "W";
		else if (direction.equals("W")) opposite = "E";
		else if (direction.equals("NE")) opposite = "SW";
		else if (direction.equals("SE")) opposite = "NW";
		else if (direction.equals("NW")) opposite = "SE";
		else if (direction.equals("SW")) opposite = "NE";
		
		
		return opposite;
	}

	// recursively searches for a next point in the current sequence, and adds 1 to the length value each time
	// until a non-matching next point is found.
	private int checkLength(Point current, String direction, char check) {
		
		int length;
		
			Point next = null;
			if (direction.equals("N")) next = new Point(current.x, current.y-1);
			else if (direction.equals("S")) next = new Point(current.x, current.y+1);
			else if (direction.equals("E")) next = new Point(current.x+1, current.y);
			else if (direction.equals("W")) next = new Point(current.x-1, current.y);
			else if (direction.equals("NE")) next = new Point(current.x+1, current.y-1);
			else if (direction.equals("SE")) next = new Point(current.x+1, current.y+1);
			else if (direction.equals("NW")) next = new Point(current.x-1, current.y-1);
			else if (direction.equals("SW")) next = new Point(current.x-1, current.y+1);
			//System.out.println(board[next.y][next.x]);
			if (isOnBoard(next) && board[next.y][next.x] == check) {
				length = 1;
				length += checkLength(next, direction, check);
			} else {length = 0;} 
			
		 
		
		return length;
	}

	// returns as a String the direction of an adjacent matching point("next") to the point of origin ("current")
	private String checkDirection(Point current, Point next) {
		String direction = null;
		
		int northsouth = current.y - next.y;
		int eastwest = current.x - next.x;
		
		if (northsouth == 0) {
			if (eastwest < 0) {direction = "E";}
			else if (eastwest > 0) direction = "W";
		}
		
		else if (northsouth < 0) {
			if (eastwest < 0) {direction = "SE";}
			else if (eastwest > 0) {direction = "SW";}
			else direction = "S";			
		}
		
		else {
			if (eastwest < 0) {direction = "NE";}
			else if (eastwest > 0) {direction = "NW";}
			else direction = "N";
		}
		
		return direction;
	}


	// checks adjacent points on the board to find chars matching check. Returns the new points, or an empty list 
	// if no new point is found. Will move clockwise or counterclockwise. Handles adjacent points that are off the
	// board array by skipping them.
	private List<Point> checkAdjacentPoints(Point current, char check) {
		List<Point> matches = new ArrayList<Point>();
		List<Point> adjacents = new ArrayList<Point>();
		
		adjacents.add(new Point(current.x, current.y+1));
		adjacents.add(new Point(current.x, current.y-1));
		adjacents.add(new Point(current.x+1, current.y+1));
		adjacents.add(new Point(current.x+1, current.y-1));
		adjacents.add(new Point(current.x+1, current.y));
		adjacents.add(new Point(current.x-1, current.y+1));
		adjacents.add(new Point(current.x-1, current.y-1));
		adjacents.add(new Point(current.x-1, current.y));
		
		for (Point p : adjacents) {
			if (isOnBoard(p) && board[p.y][p.x] == check) {
			
					matches.add(p);
				
			}
		}
		
		return matches;
	}


	// verifies that a given Point is on the playable game board.
	private boolean isOnBoard(Point p) {
		
		if (p.x > 0 && p.x < 7 && p.y > 0 && p.y < 7) return true;
		
		return false;
	}

/*
	public moveNode getPrevious() {
		return previous;
	}

	public void setPrevious(moveNode previous) {
		this.previous = previous;
	}
	public List<moveNode> getNextMoves() {
		return nextMoves;
	}

	public void setNextMoves(List<moveNode> nextMoves) {
		this.nextMoves = nextMoves;
	}
*/

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}


	public char[][] getBoard() {
		return board;
	}


	public void setBoard(char[][] board) {
		this.board = board;
	}


	public int getWeight() {
		return weight;
	}


	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	
}
