package mpi;

import mpi.MPI;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException{
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        if (me==0){
            Board board = Board.readBoard();
            masterSearch(board);
        }
        else{
            workerSearch();
        }
        MPI.Finalize();
    }


    private static void masterSearch(Board first){
        int size = MPI.COMM_WORLD.Size();
        int workers = size -1;
        int minBound = first.getManhattanDistance();
        boolean found = false;
        long time = System.currentTimeMillis();
        // generate the starting configurations for the workers

        Queue<Board> queue = new LinkedList<>();
        queue.add(first);
        while (queue.size() + queue.peek().generateMoves().size() - 1 <= workers) {
            Board thisBoard = queue.poll();
            for (Board neighbour : thisBoard.generateMoves()) {
                queue.add(neighbour);
            }
        }

        while (!found){
            // data is sent to all workers

            Queue<Board> temporary = new LinkedList<>();
            temporary.addAll(queue);

            for (int w = 0 ; w < queue.size() ; w++){
                // to each worker is sent the "first"
                Board thisBoard = temporary.poll();
                MPI.COMM_WORLD.Send(new boolean[]{false},0,1,MPI.BOOLEAN,w+1,0);
                MPI.COMM_WORLD.Send(new Object[]{thisBoard},0,1,MPI.OBJECT,w+1,0);
                MPI.COMM_WORLD.Send(new int[]{minBound}, 0, 1, MPI.INT, w + 1, 0);
            }
        }

        Object[] pairs = new Object[size + 5];
        // receive data
        for (int i = 1; i <= queue.size(); i++) {
            MPI.COMM_WORLD.Recv(pairs, i - 1, 1, MPI.OBJECT, i, 0);
        }

        // check if any node found a solution
        int newMinBound = Integer.MAX_VALUE;
        for (int i = 0; i < queue.size(); i++) {
            Pair<Integer, Board> p = (Pair<Integer, Board>) pairs[i];
            //System.out.println(p.toString());
            if (p.getEl1() == -1) {
                // found solution
                System.out.println("Solution found in " + p.getEl2().getNumOfSteps() + " steps");
                System.out.println("Solution is: ");
                System.out.println(p.getEl2());
                System.out.println("Execution time: " + (System.currentTimeMillis() - time) + "ms");
                found = true;
                break;
            } else if (p.getEl1() < newMinBound) {
                newMinBound = p.getEl1();
            }
        }
        if(!found){
            System.out.println("Depth " + newMinBound + " reached in " + (System.currentTimeMillis() - time) + "ms");
            minBound = newMinBound;
        }


        for(int i = 1; i < size; i++) {
        // shut down workers when solution was found
            Board curr = queue.poll();
            MPI.COMM_WORLD.Send(new boolean[]{true}, 0, 1, MPI.BOOLEAN, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{curr}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{minBound}, 0, 1, MPI.INT, i, 0);
            }
    }


    private static void workerSearch() {
        while (true) {
            Object[] Board = new Object[1];
            int[] bound = new int[1];
            boolean[] end = new boolean[1];
            MPI.COMM_WORLD.Recv(end, 0, 1, MPI.BOOLEAN, 0, 0);
            MPI.COMM_WORLD.Recv(Board, 0, 1, MPI.OBJECT, 0, 0);
            MPI.COMM_WORLD.Recv(bound, 0, 1, MPI.INT, 0, 0);
            if (end[0]) { // shut down when solution was found
                //System.out.println("Node " + MPI.COMM_WORLD.Rank() + " is ending its execution");
                return;
            }
            int minBound = bound[0];
            Board current = (Board) Board[0];
            Pair<Integer, Board> result = search(current, current.getNumOfSteps(), minBound);
            MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
        }
    }

    public static Pair<Integer, Board> search(Board current, int numSteps, int bound) {
        int estimation = numSteps + current.getManhattanDistance();
        if (estimation > bound) {
            return new Pair<>(estimation, current);
        }
        if (estimation > 80) {
            return new Pair<>(estimation, current);
        }
        if (current.getManhattanDistance() == 0) {
            return new Pair<>(-1, current);
        }
        int min = Integer.MAX_VALUE;
        Board solution = null;
        for (Board next : current.generateMoves()) {
            Pair<Integer, Board> result = search(next, numSteps + 1, bound);
            int t = result.getEl1();
            if (t == -1) {
                return new Pair<>(-1, result.getEl2());
            }
            if (t < min) {
                min = t;
                solution = result.getEl2();
            }
        }
        return new Pair<>(min, solution);
    }

}



