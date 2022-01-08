package utils;

import domain.BoardPosition;
import domain.State;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StateUtils {

    public static State generateRandomInitialState(int size){
        List<Integer> integers = IntStream.range(0, size*size).boxed().collect(Collectors.toList());
        Collections.shuffle(integers);
        int [][] matrix = new int[size][size];
        BoardPosition initialPosition = null;
        for(int i = 0; i < size * size; i++) {
            if(integers.get(i) == 0)
                initialPosition = new BoardPosition(size, i / size, i % size);
            matrix[i / size][i % size] = integers.get(i);
        }
        return new State(size, matrix, initialPosition);
    }

    public static State readInitialStateFromFile(String pathname) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(pathname));
        BoardPosition initialPosition = null;
        int boardSize = scanner.nextInt();
        int [][] matrix = new int[boardSize][boardSize];
        for(int i = 0; i < boardSize; i ++)
            for(int j = 0; j < boardSize; j ++){
                int number = scanner.nextInt();
                if(number == 0)
                    initialPosition = new BoardPosition(boardSize, i, j);
                matrix[i][j] = number;
            }
        return new State(boardSize, matrix, initialPosition);
    }
}

