package mpi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Board implements Serializable {

    private static final int[] di = new int[]{0,-1,0,1};
    private static final int[] dj = new int[]{-1,0,1,0};
    private static final String[] moves = new String[]{"up","down","left","right"};

    private final byte[][] boardPieces;

    private final Board previousBoard;

    private final int minNrOfSteps;

    private final int nrOfSteps;

    private final int freeColPos;

    private final int freeRowPos;

    private final int estimation;

    private final int manhattanDistance;

    private final String move;

    private final int hashValue;

    public Board(byte[][] boardPieces, int freeRowPos, int freeColPos, int nrOfSteps, Board previousBoard, String move){
        this.boardPieces=boardPieces;
        this.freeColPos=freeColPos;
        this.freeRowPos=freeRowPos;
        this.nrOfSteps=nrOfSteps;
        this.previousBoard=previousBoard;
        this.move=move;
        this.manhattanDistance=calculateManhattanDistance();
        this.minNrOfSteps=nrOfSteps+manhattanDistance;
        this.estimation=nrOfSteps+manhattanDistance;
        this.hashValue=hashingSimulation();

    }

    public int getManhattanDistance(){
        return manhattanDistance;
    }

    private int hashingSimulation(){
        int hashResult = 0 ;
        for (int row =0 ; row<4;row++){
            hashResult += Arrays.hashCode(boardPieces[row]);
        }
        return hashResult;
    }

    private int calculateManhattanDistance() {
        int result = 0;
        for (int row = 0 ; row<4; row++){
            for ( int col =0 ; col < 4 ; col ++ )
            {
                if (boardPieces[row][col] != 0 ){
                    int finalRowPos = (boardPieces[row][col] - 1 ) / 4;
                    int finalColPos = (boardPieces[row][col] - 1 ) % 4;
                    result = Math.abs(row - finalRowPos) + Math.abs(col - finalColPos);
                }
            }
        }
        return result;
    }
    
    public static Board readBoard() throws IOException{
        byte[][] boardPieces = new byte[4][4];
        int freeRowPos = -1;
        int freeColPos = -1;
        Scanner scanner = new Scanner(new BufferedReader(new FileReader("board.in")));
        for (int row=0;row<4;row++){
            for(int col=0;col<4;col++){
                boardPieces[row][col]=Integer.valueOf(scanner.nextInt()).byteValue();
                if (boardPieces[row][col]==0){
                    freeRowPos=row;
                    freeColPos=col;
                }
            }
        }
        return new Board(boardPieces,freeRowPos,freeColPos,0,null,"");
    }

    public List<Board> generateMoves(){
        List<Board> moves = new ArrayList<>();
        return moves;
    }
}
