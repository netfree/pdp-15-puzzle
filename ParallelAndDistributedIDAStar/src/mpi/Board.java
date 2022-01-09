package mpi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {

    private static final int[] di = new int[]{0,-1,0,1};
    private static final int[] dj = new int[]{-1,0,1,0};
    private static final String[] movesStr = new String[]{"left", "up", "right", "down"};

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
        for (int k =0 ; k<4;k++){
            if (freeRowPos + di[k]>=0 && freeRowPos + di[k] < 4 && freeColPos + dj[k]>=0 && freeColPos + dj[k]<4 ){
                int movedRowPos = freeRowPos + di[k];
                int movedColPos = freeColPos + dj[k];
                if (previousBoard != null && movedRowPos == previousBoard.freeRowPos && movedColPos == previousBoard.freeColPos){
                    continue;
                }
                byte[][] movedBoardPieces = Arrays.stream(boardPieces).map(
                        byte[]::clone
                ).toArray(byte[][]::new);
                movedBoardPieces[freeRowPos][freeColPos] = movedBoardPieces[movedRowPos][movedColPos];
                movedBoardPieces[movedRowPos][movedRowPos] = 0;
                moves.add(new Board(movedBoardPieces,movedRowPos,movedColPos,nrOfSteps+1,this, movesStr[k]));
            }
        }
        return moves;
    }

    public boolean equals(Object obj){
        if (this==obj) return true;
        if (obj==null || getClass() != obj.getClass()) return false;
        Board board = (Board) obj;
        boolean bool = true;
        for (int i =0 ; i<4;i++)
            bool = bool && Arrays.equals(boardPieces[i], board.boardPieces[i]);
        return bool;
    }

    @Override
    public String toString(){
        Board board = this;
        List<String> resultStr = new ArrayList<>();
        while(board!=null){
            StringBuilder result = new StringBuilder();
            result.append("\n");
            result.append(board.move);
            result.append("\n");
            Arrays.stream(board.boardPieces).forEach(row->result.append(Arrays.toString(row)).append("\n"));
            result.append("\n");
            resultStr.add(result.toString());
            board = board.previousBoard;
        }
        Collections.reverse(resultStr);
        return "MOVES " + String.join("",resultStr) + "NR OF STEPS=" + nrOfSteps;
    }
}
