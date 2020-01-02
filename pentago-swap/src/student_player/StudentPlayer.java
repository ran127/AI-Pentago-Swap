package student_player;

import boardgame.Move;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import boardgame.Board;
import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    public StudentPlayer() {
        super("260716696");
    }
	
	
	//Class movevalue will have one value and one pentagoMove
	public class MoveValue {
	    	public double value;
	    	public PentagoMove move;
	    	public MoveValue(double valuenum, PentagoMove movenum) {
	    		value = valuenum;
	    		move = movenum;
	    	}
	}

    //evaluate function
    private double evaluateFunction(PentagoBoardState boardState){
    	//case when we already win
    	if(boardState.getWinner() == player_id){
    		return Integer.MAX_VALUE-1;
    	}
    	//case the opponent win
    	else if(boardState.getWinner() == 1-player_id){
    		return Integer.MIN_VALUE+1;
    	}
    	//case in a draw
    	else if(boardState.getWinner() == Board.DRAW){
    		return 0.0;
    	}
    	
    	//else we will evaluate the current move
    	double[][] totalResult = MyTools.checkState(boardState);
    	double[] whiteResult = totalResult[0];
    	double[] blackResult = totalResult[1];
    	double evaluatedscore =0.0;
    	
    	//if we move first
    	if(this.player_id== 0){
    		evaluatedscore = whiteResult[5]*10 + whiteResult[3]*7 + whiteResult[4]*5 + whiteResult[2]*3 + whiteResult[0]*1 + whiteResult[1]*2;
    		evaluatedscore = evaluatedscore - 0.8*(blackResult[5]*10+ blackResult[3]*7 + blackResult[4]*5 + blackResult[2]*3 + blackResult[0]*1 + blackResult[1]*2);
    		//evaluatedscore = whiteResult[5]*10 + whiteResult[3]*5 + whiteResult[4]*3 + whiteResult[2]*0.5 + (whiteResult[0]+whiteResult[1])*3;
    		//evaluatedscore = evaluatedscore - 0.8*(blackResult[5]*10+ blackResult[3]*5 + blackResult[4]*3 + blackResult[2]*0.5 + (blackResult[0]+blackResult[1])*3);
    	}
    	
    	//if we move last
    	else{
    		evaluatedscore = blackResult[5]*10 + blackResult[3]*7 + blackResult[4]*5 + blackResult[2]*3 + blackResult[0]*1 + blackResult[1]*2;
    		evaluatedscore = evaluatedscore - 2*(whiteResult[5]*10 + whiteResult[3]*7 + whiteResult[4]*5 + whiteResult[2]*0.5 + whiteResult[0]*1 + whiteResult[1]*1);
    		//evaluatedscore = whiteResult[5]*10 + whiteResult[3]*5 + whiteResult[4]*3 + whiteResult[2]*0.5 + (whiteResult[0]+whiteResult[1])*1;
    		//evaluatedscore = 2.5*(blackResult[5]*10+ blackResult[3]*5 + blackResult[4]*3 + blackResult[2]*0.5 + (blackResult[0]+blackResult[1])*1) - evaluatedscore ;
    	
    	}
    	return evaluatedscore;
    }
    
    
    //function MiniMax that return one move   
    public MoveValue MiniMax(int depth, int maxDepth, double alpha, double beta, PentagoBoardState boardState) {
    	
    	if (depth == maxDepth) return new MoveValue(evaluateFunction(boardState), null);
    	
    	ArrayList<PentagoMove> moveoptions = boardState.getAllLegalMoves();
    	MoveValue bestMove = null;
    	//max player
    	if (boardState.getTurnPlayer() == player_id) {	//max player
    		
    		for (PentagoMove move : moveoptions) {
    			//apply the move
    			PentagoBoardState cloneState = (PentagoBoardState)boardState.clone();
    			cloneState.processMove(move);
    			
    			if (cloneState.getWinner() == player_id) {
    				return new MoveValue(Integer.MAX_VALUE-1, move);
    			}
    			
    			//if we couldn't get a winner then apply this to minimax recursively
    			MoveValue resultMove = MiniMax(depth+1, maxDepth, alpha, beta, cloneState);
    			
    			// if the best value so far is null we assign the first return value to it
    			if (bestMove == null || (resultMove.value > bestMove.value)) {
    				bestMove = resultMove;
    				bestMove.move = move;
    			}
    			
    			
    			if (alpha < resultMove.value) {
    				//update the best move
    				alpha = resultMove.value;
    				bestMove = resultMove;
    			}
    			
    			if (alpha >= beta) {	// pruning
    				bestMove.value = beta;
    				bestMove.move = null;
    				return bestMove;
    			}
    		}
    		return bestMove;
    	}
    	else {
    		//min player
    		for (PentagoMove move : moveoptions) {
    			//apply this move 
    			PentagoBoardState cloneState = (PentagoBoardState)boardState.clone();
    			cloneState.processMove(move);
    			
    			if (cloneState.getWinner() == 1 - player_id) {
    				return new MoveValue(Integer.MIN_VALUE+1, move);
    			}
    			MoveValue result = MiniMax(depth+1, maxDepth, alpha, beta, cloneState);    			
    			
    			if (bestMove == null || (result.value < bestMove.value)) {
    				bestMove = result;
    				bestMove.move = move;
    			}
    			
    			if (beta > result.value) {
    				//update the best move
    				beta = result.value;
    				bestMove = result;
    			}
    			
    			if (alpha >= beta) {
    				bestMove.value = alpha;
    				bestMove.move = null;
    				return bestMove;
    			}

    		}
    		return bestMove;
    	}
 }
    
  
    
    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    
    public Move chooseMove(PentagoBoardState boardState) {
    	int[][] centerCoord = {{1,1},{1,4},{4,1},{4,4}};
    	int[][] centerCoordDiag = {{2, 2}, {2, 3}, {3, 2}, {3, 3}};
    	long startTime = System.currentTimeMillis();
    	final LinkedList<MoveValue> possibleMoves = new LinkedList<MoveValue>();
    	int timeOut;
    	if(boardState.getTurnNumber() == 0){
    		timeOut = 29950;
    	}
    	else{
    		timeOut = 1700;
    	}
    	
    	Move myMove = null;
    	Piece ourColour;
    	if(this.player_id ==0){
    		ourColour = Piece.WHITE;
    	}
    	else{
    		ourColour = Piece.BLACK;
    	}
    	
    	//if this is the first 3 moves for our player when our player is white
    	if (boardState.getTurnNumber() < 3 && ourColour ==Piece.WHITE) {
    		for (int i=0; i<centerCoord.length; i++) {
    			if (boardState.getPieceAt(centerCoord[i][0], centerCoord[i][1]) == Piece.EMPTY) {
    				myMove = new PentagoMove(centerCoord[i][0], centerCoord[i][1], Quadrant.TL, Quadrant.TR, player_id);
    			}
    		}
    		//Count whether the centerCoordDiag is occupied
    		int count = 0;
    		for (int i=0; i<centerCoordDiag.length; i++) {
    			if (boardState.getPieceAt(centerCoordDiag[i][0], centerCoordDiag[i][1]) == Piece.EMPTY) {
    				count++;
    			}
    		}
    		
    		//if we play the first and the centerCoordDiag is empty and it's turn 2
        	if (count==4 &&boardState.getTurnNumber() ==2 && ourColour==Piece.WHITE){
        		//Then we first want to figure out which two center coordinates that we placed on
        		int first=-1;
        		int second=-1;
        		for (int i=0; i<centerCoord.length; i++) {
        			if (boardState.getPieceAt(centerCoord[i][0], centerCoord[i][1]) == ourColour ) {
        					if(first ==-1){
        						first = i;
        					}
        					else{
        						second =i;
        					}
        			}
        		}
        		//Then depend on the first two move to place the third move
        		if(first ==0){
        			if(second==1){
        				myMove = new PentagoMove(centerCoordDiag[first][0], centerCoordDiag[first][1], Quadrant.TR, Quadrant.BR, player_id);
        			}
        			else if(second ==2){
        				myMove = new PentagoMove(centerCoordDiag[first][0], centerCoordDiag[first][1], Quadrant.BL, Quadrant.BR, player_id);
        			}
        			else{
        				myMove = new PentagoMove(centerCoordDiag[first][0], centerCoordDiag[first][1], Quadrant.TR, Quadrant.BL, player_id);
        			}
        		}
        		else if(first ==1){
        			if(second ==2){
        				myMove = new PentagoMove(centerCoordDiag[first][0], centerCoordDiag[first][1], Quadrant.TL, Quadrant.BR, player_id);
        			}
        			else{
        				myMove = new PentagoMove(centerCoordDiag[first][0], centerCoordDiag[first][1], Quadrant.BL, Quadrant.BR, player_id);
        			}
        		}
        		else{
        		myMove = new PentagoMove(centerCoordDiag[first][0], centerCoordDiag[first][1], Quadrant.TR, Quadrant.BR, player_id);
        		}
        	}
        	
    		

    	}
    	
    	//if our player is black and if we are not in the first two turns
    	else {
    		if (boardState.getTurnNumber() < 2){
    			for (int i=0; i<centerCoord.length; i++) {
        			if (boardState.getPieceAt(centerCoord[i][0], centerCoord[i][1]) == Piece.EMPTY) {
        				myMove = new PentagoMove(centerCoord[i][0], centerCoord[i][1], Quadrant.TL, Quadrant.TR, player_id);
        			}
        		}
    		}
    		if(myMove == null){
    		Callable<Object> IterativeDeepening = new Callable<Object> () {

				@Override
				public Object call() throws Exception {
						//We start from when depth is 2
						int depth = 2;
						MoveValue optimal;
						while (!Thread.currentThread().isInterrupted()) {
							if (depth == 2) {
								optimal = MiniMax(0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, boardState);
							}
							else {
								optimal = MiniMax(0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, boardState);
							}
							// the first time we set the optimal as the second
					        if (depth == 2) {
					        	possibleMoves.add(optimal);
					        	possibleMoves.add(optimal);
					        }
					        else {	
					        		possibleMoves.set(1, possibleMoves.get(0));
					        		possibleMoves.set(0, optimal);
					        }
					        depth++;
					}
					return 0;
				}
    		
    		};
    		
    		ExecutorService executor = Executors.newSingleThreadExecutor();
    		
    		final Future<Object> futureEvent = executor.submit(IterativeDeepening);
    		try {
				futureEvent.get(timeOut, TimeUnit.MILLISECONDS);
			} 
    		catch (TimeoutException e) {
				//this is when time out
    			if (possibleMoves.size() > 0) {
    				myMove = possibleMoves.get(0).move;
	    			//System.out.println("Value of the final move: " + possibleMoves.get(0).value);
    			}
			}
    		catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		finally {
    			executor.shutdownNow();
    		}		
    		myMove = possibleMoves.get(0).move;        
    	}
    	}
    	
    	long endTime = System.currentTimeMillis();
    	long timeperiod = endTime - startTime;

        // Return your move to be processed by the server.
        return myMove;
    }
}