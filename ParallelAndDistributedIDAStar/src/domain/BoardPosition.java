package domain;

import java.util.ArrayList;
import java.util.List;

public class BoardPosition {
    private final int i, j;
    private final int boardSize;
    private static final int[] di = new int[]{0, -1, 0, 1};
    private static final int[] dj = new int[]{-1, 0, 1, 0};

    public BoardPosition(int boardSize, int i, int j) {
        this.i = i;
        this.j = j;
        this.boardSize = boardSize;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    private boolean valid(int i, int j){
        return i >= 0 && i < boardSize && j >= 0 && j < boardSize;
    }

    /**
     * @return list of valid neighbours of the current board postion
     */
    public List<BoardPosition> getNeighbours(){
        List<BoardPosition> neighbours = new ArrayList<>();
        for(int k = 0; k < 4; k ++){
            int i = this.i + di[k];
            int j = this.j + dj[k];
            if(valid(i, j))
                neighbours.add(new BoardPosition(boardSize, i, j));
        }
        return neighbours;
    }
}
