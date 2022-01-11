package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class State implements Serializable {
    private final int g;
    private final int size;
    private final int[][] matrix;
    private final BoardPosition freePosition;

    public State(int g, int size, int[][] matrix, BoardPosition freePosition){
        this.g = g;
        this.size = size;
        this.matrix = matrix;
        this.freePosition = freePosition;
    }

    public int getSize() {
        return size;
    }

    public List<State> getListOfCandidates() {
        List<State> candidates = new ArrayList<>();
        List<BoardPosition> freePositionNeighbours = freePosition.getNeighbours();
        for(BoardPosition neighbourPosition : freePositionNeighbours){
            int[][] neighbourBoard = Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
            int temp = neighbourBoard[freePosition.getI()][freePosition.getJ()];
            neighbourBoard[freePosition.getI()][freePosition.getJ()] = neighbourBoard[neighbourPosition.getI()][neighbourPosition.getJ()];
            neighbourBoard[neighbourPosition.getI()][neighbourPosition.getJ()] = temp;
            candidates.add(new State(g + 1, size, neighbourBoard, neighbourPosition));
        }
        return candidates;
    }

    private int getManhattanDistance(){
        int ans = 0;
        for(int i = 0; i < getSize(); i++)
            for(int j = 0; j < getSize(); j++)
                if(matrix[i][j] != 0)
                    ans += Math.abs(i - (matrix[i][j] - 1) / 4) + Math.abs(j - (matrix[i][j] - 1) % 4);
        return ans;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return getManhattanDistance();
    }

    public boolean isGoalState() {
        int expected_number = 1;
        for(int i = 0; i < getSize(); i++)
            for(int j = 0; j < getSize(); j++) {
                if (i == getSize() - 1 && j == getSize() - 1)
                    expected_number = 0;
                if (matrix[i][j] != expected_number)
                    return false;
                expected_number += 1;
            }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        ans.append("g: ").append(getG()).append(" h: ").append(getH()).append("\n");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        if(size != state.size)
            return  false;

        for(int i = 0; i < getSize(); i ++)
            for(int j = 0; j < getSize(); j ++)
                if(matrix[i][j] != state.matrix[i][j])
                    return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(matrix);
        return result;
    }
}
