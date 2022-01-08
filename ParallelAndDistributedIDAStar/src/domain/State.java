package domain;

import javax.management.StandardEmitterMBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State {


    private final int size;
    private final int[][] matrix;
    private final BoardPosition freePosition;

    public State(int size, int[][] matrix, BoardPosition freePosition){
        this.size = size;
        this.matrix = matrix;
        this.freePosition = freePosition;
    }

    public int getSize() {
        return size;
    }

    List<State> getListOfCandidates() {
        List<State> candidates = new ArrayList<>();
        List<BoardPosition> freePositionNeighbours = freePosition.getNeighbours();
        for(BoardPosition neighbourPosition : freePositionNeighbours){
            int[][] neighbourBoard = Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
            int temp = neighbourBoard[freePosition.getI()][freePosition.getJ()];
            neighbourBoard[freePosition.getI()][freePosition.getJ()] = neighbourBoard[neighbourPosition.getI()][neighbourPosition.getJ()];
            neighbourBoard[neighbourPosition.getI()][neighbourPosition.getJ()] = temp;
            candidates.add(new State(size, neighbourBoard, neighbourPosition));
        }
        return candidates;
    }

    private int getManhattanDistance(){
        int ans = 0;
        for(int i = 0; i < getSize(); i++)
            for(int j = 0; j < getSize(); j++)
                if(matrix[i][j] != 0)
                    ans += Math.abs(i - (matrix[i][j] - 1) / 4) + Math.abs(i - (matrix[i][j] - 1) % 4);
        return ans;
    }

    int getG() {
        return 0;
    }

    int getH() {
        return getManhattanDistance();
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        for(int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                for(int temp = 10; temp <= 100; temp *= 10)
                    if(matrix[i][j] < temp)
                        ans.append(" ");
                ans.append(matrix[i][j]).append(" ");
            }
            ans.append("\n");
        }
        return ans.toString();
    }
}
