// Daniel Bergman, TCSS 435 Winter 2016. All Rights Reserved. 


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// The Pentago game class
public class Pentago {

	public static final int ASCIIOFFSET = 48;
	private char gameBoard[][];
	private char computerColor = 'b'; //set to black by default.
	private Scanner myLineScanner;
	
	private boolean gameOver;
	private moveNode currentMove;
	public static final String[] ROTATIONS = {"1R","1L","2R","2L","3R","3L","4R","4L"};
	private static final int LOOKFORWARD = 3; // a constant for how many moves (black and white) the game should look forward. 
	                                          // Must be odd, so that the computer ends with a human move. Otherwise it won't realize a human winning move can't be undone.
	
	// Initiates the fields and draws the game board. Won't start the game without a separate call to startGame().
	public Pentago() {
		gameOver = false;
		
		myLineScanner = new Scanner(System.in);
		
		gameBoard = new char[8][8];
		
		for (int i = 0; i < 8; i++) {
			
			gameBoard[i][0] = '|';
			//gameBoard[i][4] = '|';
			gameBoard[i][7] = '|';
			gameBoard[0][i] = '_';
			if (i != 7) gameBoard[7][i] = '_';
			
		}
		
		for (int i = 1; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				gameBoard[i][j] = '.';
			}
			
		}
		currentMove = new moveNode("white", gameBoard);
		
	}
	
	// Starts the game. Uses a Scanner to continuously take user input, decode the user move and make a computer move, until the game ends or user quits.
	public void startGame() {
		
		printWelcomeInstructions();
		pickColors();
		askFirst();
		printBoard();
		
		while (!gameOver) {
			String nextLine = myLineScanner.nextLine();
			if (nextLine.contains("q") || nextLine.contains("Q")) {
				break;
			}
			try {boolean win = decodeMove(nextLine); 
			if (win) break;
			}
			catch (StringIndexOutOfBoundsException e) {
				System.out.println("Illegal input. Please enter try again to enter input in proper format.");
			}
			
		}
		
		myLineScanner.close();
		
		
		
		
	}
	
	// Allows the user to choose to be white or black.
	private void pickColors() {
		
		System.out.println("Would you like to be white or black? Reply 'w' for white, 'b' for black.");
		String reply = myLineScanner.nextLine();
		Boolean valid = false;
		while (!valid) {
			if (reply.charAt(0) == 'w' || reply.charAt(0) == 'W') {
				valid = true;
			} else if (reply.charAt(0) == 'b' || reply.charAt(0) == 'B') {
				valid = true;
				computerColor = 'w';
				
			} else {
				System.out.println("I didn't understand your answer. Please respond with w or b.");
				reply = myLineScanner.nextLine();
			}
		}
		
	}

	// asks if the user would like to go first or not. If not, the computer is given a default first move.
	private void askFirst() {
		
		System.out.println("Would you like the computer to make the first move? Reply yes or no."); 
		String reply = myLineScanner.nextLine();
		Boolean valid = false;
		while (!valid) {
			if (reply.charAt(0) == 'y' || reply.charAt(0) == 'Y') {
				if (computerColor == 'b') {
					gameBoard[2][2] = 'b'; 
				} else {
					gameBoard[2][2] = 'w';	
				}
				valid = true;
			} else if (reply.charAt(0) == 'n' || reply.charAt(0) == 'N') {
				System.out.println("OK, you go first");
				valid = true;
			} else {
				System.out.println("I didn't understand your answer. Please respond yes or no.");
				reply = myLineScanner.nextLine();
			}
			
		}
		
	}

	// A helper method for printing the entire board, adding separators between the different quadrants
	private void printBoard() {
		
		for (int i = 0; i < 4; i++) {
			
			for (int j = 0; j < 4; j++) {
			
				System.out.print(gameBoard[i][j]);
				
			}
			if (i == 0) {System.out.print('_');}
			else {System.out.print('|');}
			for (int j = 4; j < 8; j++) {
				
				System.out.print(gameBoard[i][j]);
				
			}
			System.out.println();
		}
		
		for (int i = 0; i < 9; i++) {
			if (i == 0 || i == 4 || i == 8) {System.out.print('|');}
			else {System.out.print('_');}
		}
		System.out.println();
		
		for (int i = 4; i < 8; i++) {
			
			for (int j = 0; j < 4; j++) {
			
				System.out.print(gameBoard[i][j]);
				
			}
			System.out.print('|');
			for (int j = 4; j < 8; j++) {
				
				System.out.print(gameBoard[i][j]);
				
			}
			System.out.println();
		}
		
		
	}

	/*
	 * Decodes and implements the player input for this turn as a move, checks for a winner, calls a computer move
	 * and checks for a winner again. After each player or computer move, reprints the board.
	 */	
	private boolean decodeMove(String nextLine) {
		
		int x = nextLine.charAt(0) - ASCIIOFFSET;
		int y = nextLine.charAt(2) - ASCIIOFFSET;
		int block = nextLine.charAt(4) - ASCIIOFFSET;
		char rotation = nextLine.charAt(5);
		
		try { 
			if (gameBoard[y][x] == '.') {
			
				if (computerColor == 'b') {
					gameBoard[y][x] = 'w';
				} else {
					gameBoard[y][x] = 'b';
				}
			} else {
				System.out.println("Illegal move. Space at " + x + "/"+ y + " is already occupied. You lose your turn.");	
					
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Illegal move. Space at " + (x-1) + "/"+ (y-1) + " is off the board. You lose your turn.");
		}
		
		rotate(block, rotation, gameBoard);
		
		moveNode move;
		if (computerColor == 'b') {
			move = new moveNode("white", gameBoard);
		} else {
			move = new moveNode("black", gameBoard);
		}
		//System.out.println("Your move had a weight of " + move.getWeight());
		if (move.getWeight() > 4) {
			printBoard();
			System.out.println("You Win!");
			return true;
		}
		if (move.getWeight() < -4) {
			System.out.println("Computer wins! Better luck next time.");
			return true;
		}
		currentMove = move;
		printBoard();
		System.out.println("Computer's move!");
		currentMove = computerMove();
		gameBoard = currentMove.getBoard();
		//System.out.println("Computer's move weight equals: " + currentMove.getWeight());
		printBoard();
		if (currentMove.getWeight() < -4) {
			System.out.println("Computer wins! Better luck next time.");
			return true;
		}
		return false;
	}

	// rotates a block on the board clockwise if the rotaion parameter is 'r',
	// and counter clockwise if it is left. uses the block parameter to determine which block should be rotated.
	private void rotate(int block, char rotation, char[][] board) {
		
		if (block == 1) {rotateUpperLeft(rotation, board);}
		else if (block == 2) {rotateUpperRight(rotation, board);}
		else if (block == 3) {rotateLowerRight(rotation, board);}
		else if (block == 4) {rotateLowerLeft(rotation, board);}
		
	}

	private void rotateLowerLeft(char rotation, char[][] board) {
		
		if (rotation == 'r' || rotation == 'R') {
			
			char temp = board[6][1]; 
			board[6][1] = board[6][3];
			board[6][3] = board[4][3];
			board[4][3] = board[4][1];
			board[4][1] = temp;
			
			temp = board[5][1];
			board[5][1] = board[6][2];
			board[6][2] = board[5][3];
			board[5][3] = board[4][2];
			board[4][2] = temp;
			
		} else { 
		
			char temp = board [4][3];
			board[4][3] = board [6][3];
			board[6][3] = board [6][1];
			board[6][1] = board[4][1];
			board[4][1] = temp;
			
			temp = board[4][2];
			board[4][2] = board[5][3];
			board[5][3] = board[6][2];
			board[6][2] = board[5][1];
			board[5][1] = temp;
		}
	}

	private void rotateLowerRight(char rotation, char[][] board) {

		if (rotation == 'r' || rotation == 'R') {
			
			char temp = board[6][4]; 
			board[6][4] = board[6][6];
			board[6][6] = board[4][6];
			board[4][6] = board[4][4];
			board[4][4] = temp;
			
			temp = board[5][4];
			board[5][4] = board[6][5];
			board[6][5] = board[5][6];
			board[5][6] = board[4][5];
			board[4][5] = temp;
			
		} else { 
		
			char temp = board[4][6];
			board[4][6] = board[6][6];
			board[6][6] = board[6][4];
			board[6][4] = board[4][4];
			board[4][4] = temp;
			
			temp = board[4][5];
			board[4][5] = board[5][6];
			board[5][6] = board[6][5];
			board[6][5] = board[5][4];
			board[5][4] = temp;
		}
	}

	private void rotateUpperRight(char rotation, char[][] board) {
		if (rotation == 'r' || rotation == 'R') {
			
			char temp = board[3][4]; 
			board[3][4] = board[3][6];
			board[3][6] = board[1][6];
			board[1][6] = board[1][4];
			board[1][4] = temp;
			
			temp = board[2][4];
			board[2][4] = board[3][5];
			board[3][5] = board[2][6];
			board[2][6] = board[1][5];
			board[1][5] = temp;
			
		} else { 
		
			char temp = board[1][6];
			board[1][6] = board[3][6];
			board[3][6] = board[3][4];
			board[3][4] = board[1][4];
			board[1][4] = temp;
			
			temp = board[1][5];
			board[1][5] = board[2][6];
			board[2][6] = board[3][5];
			board[3][5] = board[2][4];
			board[2][4] = temp;
		}
	}

	private void rotateUpperLeft(char rotation, char[][] board) {

		if (rotation == 'r' || rotation == 'R') {
			
			char temp = board[3][1]; 
			board[3][1] = board[3][3];
			board[3][3] = board[1][3];
			board[1][3] = board[1][1];
			board[1][1] = temp;
			
			temp = board[2][1];
			board[2][1] = board[3][2];
			board[3][2] = board[2][3];
			board[2][3] = board[1][2];
			board[1][2] = temp;
			
		} else { 
		
			char temp = board [1][3];
			board[1][3] = board [3][3];
			board[3][3] = board [3][1];
			board[3][1] = board[1][1];
			board[1][1] = temp;
			
			temp = board[1][2];
			board[1][2] = board[2][3];
			board[2][3] = board[3][2];
			board[3][2] = board[2][1];
			board[2][1] = temp;
		}
	}

	// begins the recursive calling of black and white moves with counter set to 1, then returns the chosen move "next"
	private moveNode computerMove() {

		moveNode next;
		if (computerColor == 'b') {
			next = findBlackMove(currentMove, 1);
		} else {
			next = findWhiteMove(currentMove, 1);
		}
		//filter(next);
		
		return next;
	}

	// recursively alternates with findWhiteMove(), by finding all possible moves, then calling findWhiteMove on all of them
	// and choosing a best value from white's choices. If the counter has reached a maximum as per the LOOKFORWARD constant,
	// will pick from the available moves without first passing them to findWhiteMove()
	private moveNode findBlackMove(moveNode theCurrentNode, int i) {
		i++;
		moveNode next = null;
		moveNode whiteLow = null;
		
		if (i == LOOKFORWARD) {
			List <moveNode> possible = getEmptySpaces(theCurrentNode);
			next = getLowestWeight(possible);
		} else {

			List<moveNode> possible = getEmptySpaces(theCurrentNode);
			//List<moveNode> whiteMoves = new ArrayList<moveNode>();
			
			for (moveNode m : possible) {
				
				moveNode whiteMove = findWhiteMove(m, i);
				if (whiteLow == null || whiteMove.getWeight() < whiteLow.getWeight()) {
	
					whiteLow = whiteMove;
					next = m;	
				}
			}
			//next = getLowestWeight(whiteMoves);
		}
		//filter(next);
		return next;
	}

	// Same as findBlackMove() but returns a white move and preferring higher rather than lower weights
	private moveNode findWhiteMove(moveNode theCurrentNode, int i) {
		i++;
		moveNode next = null;
		moveNode blackHigh = null;
		
		if (i == LOOKFORWARD) {
			List <moveNode> possible = getEmptySpaces(theCurrentNode);
			next = getHighestWeight(possible);
		} else {

			List<moveNode> possible = getEmptySpaces(theCurrentNode);
			
			for (moveNode m : possible) {
				moveNode blackChoice =  findBlackMove(m, i);
				if (blackHigh == null || blackChoice.getWeight() > blackHigh.getWeight()) {
					
					blackHigh = blackChoice;
					next = m;
				}
			}
		}
	
	return next;

	}

	// picks from a List of moveNodes the node with the highest weight.
	private moveNode getHighestWeight(List<moveNode> moves) {
		moveNode Highest = null;
		int high =  -1000;
		
		for (moveNode m : moves) {
			if (m.getWeight() > high) {
				
				Highest = m;
				high = m.getWeight();
				
			}
			
		}
		
		return Highest;
	}


	// picks from a List of moveNodes the node with the lowest weight.
	private moveNode getLowestWeight(List<moveNode> moves) {
		moveNode lowest = null;
		int low =  1000;
		
		for (moveNode m : moves) {
			if (m.getWeight() < low) {
				
				lowest = m;
				low = m.getWeight();
				
			}
			
		}
		
		return lowest;
	}

	// finds all the empty spaces remaining a on a moveNodes game board for possible next moves by the opposite color.
	// for each space, returns one moveNode possibility for each of the eight possible board rotations. when complete,
	// a list of possible moveNodes is returned. 
	private List<moveNode> getEmptySpaces(moveNode theCurrentMove) {

		List<moveNode> possible = new ArrayList<moveNode>();
		
		for (int i  = 1; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				if (theCurrentMove.getBoard()[j][i] == '.') {
					
					if (theCurrentMove.getColor().equals("white")) {
						for (int l = 0; l < ROTATIONS.length; l++) {
							char[][] board = new char[8][8];
							for (int k = 0; k < 8; k++) {
								board[k] = Arrays.copyOf(theCurrentMove.getBoard()[k], 8);
							}
							board[j][i] = 'b';
							rotate(ROTATIONS[l].charAt(0) - ASCIIOFFSET, ROTATIONS[l].charAt(1), board);
							possible.add(new moveNode("black", board));
						}
					} else {
						char[][] board = new char[8][8];
						for (int l = 0; l < ROTATIONS.length; l++) {
							
							for (int k = 0; k < 8; k++) {
								board[k] = Arrays.copyOf(theCurrentMove.getBoard()[k], 8);
							}
							board[j][i] = 'w';
							rotate(ROTATIONS[l].charAt(0) - ASCIIOFFSET, ROTATIONS[l].charAt(1), board);
							possible.add(new moveNode("white", board));
						}
					}
					
				}
			}
		}
		
		return possible;
	}

	// prints the welcome instructions
	private void printWelcomeInstructions() {
		System.out.println("Welcome to Pentago, by Dovi Bergman. All Rights Reserved.");
		System.out.println("Enter your next move in x/y format, followed by a space.");
		System.out.println("Then enter the number of the square you want to rotate");
		System.out.println("(upperleft is 1, then 2-4 procede clockwise)");
		System.out.println("and r to rotate right(clockwise) or l for left(counterclockwise)."); 
		System.out.println("Enter q at any time to quit");
	}

	public char[][] getGameBoard() {
		return Arrays.copyOf(gameBoard, gameBoard.length);
	}

	public void setGameBoard(char[][] theNewGameBoard) {
		gameBoard = Arrays.copyOf(theNewGameBoard, theNewGameBoard.length);
	}

		
	
}
