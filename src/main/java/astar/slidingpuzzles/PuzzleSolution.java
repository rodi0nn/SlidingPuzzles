package astar.slidingpuzzles;

import java.io.*;
import java.util.*;

public class PuzzleSolution {
    
    static int n = 0, m = 0; // The number of rows and columns.
    static int sx=2, sy=0, fx=0, fy=0; // The coordonites of the starting and finish points.
    static ArrayList<Point> solution = new ArrayList<>(); // The array that will be used to store the solution.
    
    // The following method will read and save the size (the number of rows and columns) of the matrix in the variable n and m respectively. 
    static void readMazeSize () {
        try {
            File mazeFile = new File("maze.txt");
            Scanner mazeSize = new Scanner(mazeFile);
            
            if (mazeFile.length()!=0 ) {
                int rowLength = mazeSize.nextLine().length();
                m = rowLength; // The number of columns.
                n = 1; // The number of rows.
                while (mazeSize.hasNextLine()) {
                    rowLength = mazeSize.nextLine().length();
                    if (m!=rowLength) {
                        System.out.println("The format of the maze is not acceptable.");
                        break;
                    }
                    n++;
                }
                mazeSize.close();
            }
            else System.out.println("The file is empty.");
        }
        catch (FileNotFoundException e) {
            System.out.println("The file savedf1data.txt was not found.");
        }
    }
    
    // The following method will reread the file to create a new matrix that is easily implemented into the BFS algorithm.
    static void readMaze(int[][] matrix) {
        try {
            File mazeFile = new File("maze.txt");
            Scanner mazeReader = new Scanner(mazeFile);
            
            if (mazeFile.length() != 0) {
                int i = 0;
                while (mazeReader.hasNextLine()) {
                    String row = mazeReader.nextLine();
                    for (int j=0; j < row.length(); j++) {
                        char c = row.charAt(j);
                        if (c=='.') matrix[i][j] = 0;
                        if (c=='0') matrix[i][j] = 1;
                        if (c=='S') {
                            sx = j;
                            sy = i;
                            matrix[i][j] = 0;
                        }
                        if (c=='F') {
                            fx = j;
                            fy = i;
                            matrix[i][j] = 0;
                        }
                    }
                    i++;
                }
                mazeReader.close();
            }
            else System.out.println("The file is empty.");
        }
        catch (FileNotFoundException e) {
            System.out.println("The file savedf1data.txt was not found.");
        }
    }
    
    // The following method will solve the puzzle and display the result.
    static String solveMaze(int[][] matrix, int startX, int startY, int finishX, int finishY) {
        Point startPoint = new Point(startX, startY);
        
        System.out.println();

        LinkedList<Point> queue = new LinkedList<>();
        Point[][] matrixColors = new Point[n][m];

        queue.addLast(new Point(startX, startY));
        matrixColors[startY][startX] = startPoint;

        while (!queue.isEmpty()) {
            Point currPos = queue.pollFirst();
            // The following loop will traverse the matrix beginning with the starting point to the finish point.
            for (Direction dir : Direction.values()) {
                Point nextPos = movePoint(matrix, matrixColors, currPos, dir);
                if (nextPos != null) {
                    queue.addLast(nextPos);
                    matrixColors[nextPos.getY()][nextPos.getX()] = new Point(currPos.getX(), currPos.getY());
                    if (nextPos.getX() == finishX && nextPos.getY() == finishY) {
                        // The finish point has been found!
                        Point tmp = currPos;  // Counting the number of steps.
                        int count = 0;
                        while (tmp != startPoint) {
                            solution.add(tmp);
                            count++;
                            tmp = matrixColors[tmp.getY()][tmp.getX()];
                        }
                        if (count!=0) {
                            // The following for loop displays the found solution.
                            int j=1;
                            for (int i=solution.size()-1; i>=0; i--) {
                                if(i==solution.size()-1) System.out.println(j + ". Start at (" + solution.get(i).x + "," + solution.get(i).y + ")");
                                else {
                                    if (solution.get(i).x!=solution.get(i+1).x) {
                                        if (solution.get(i).x<solution.get(i+1).x) System.out.println(j + ". Moves LEFT to (" + solution.get(i).x + "," + solution.get(i).y + ")");
                                        else System.out.println(j + ". Moves RIGHT to (" + solution.get(i).x + "," + solution.get(i).y + ")");
                                    }
                                    else {
                                        if (solution.get(i).y<solution.get(i+1).y) System.out.println(j + ". Moves UP to (" + solution.get(i).x + "," + solution.get(i).y + ")");
                                        else System.out.println(j + ". Moves DOWN to (" + solution.get(i).x + "," + solution.get(i).y + ")");
                                    }
                                }
                                j++;
                            }
                            System.out.println(j + ". Finish at (" + fx + "," + fy + ")");   
                        }
                        System.out.println();
                        return "The algorithm solved the puzzle in " + count + " steps.";
                    }
                }
            }
        }    
        return "There was no solution to the puzzle.";
    }

    // The following method helps traverse from one point to another.
    public static Point movePoint(int[][] matrix, Point[][] matrixColors, Point currPos, Direction dir) {
        int x = currPos.getX();
        int y = currPos.getY();

        int diffX = (dir == Direction.LEFT ? -1 : (dir == Direction.RIGHT ? 1 : 0));
        int diffY = (dir == Direction.UP ? -1 : (dir == Direction.DOWN ? 1 : 0));

        int i = 1;
        while (x + i * diffX >= 0
                && x + i * diffX < matrix[0].length
                && y + i * diffY >= 0
                && y + i * diffY < matrix.length
                && matrix[y + i * diffY][x + i * diffX] != 1) {
            i++;
        }

        i--;  // reverse the last step

        
        if (matrixColors[y + i * diffY][x + i * diffX] != null) {
            // we've already seen this point
            return null;
        }

        return new Point(x + i * diffX, y + i * diffY);
    }

    /* 
        The following class Point is used to transform the importnat data (elements of the matrix)
        into a format that saves the coorodnites of the element while being able to be added to the LinkedList
        used in the Breadth First Search of our algorithm.
    */
    public static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        
    }

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public static void main(String[] args) {
        
        System.out.println("Ice Sliding Puzzle");
        System.out.println();
        System.out.println("Please make sure that you have added the maze file in program folder(SlidingPuzzle) under the name maze.txt.");
        
        // The initialisation of the maze.
        readMazeSize();
        int[][] maze = new int[n][m];
        for (int i=0; i<n; i++) 
            for (int j=0; j<m; j++)
                maze[i][j]=0;       
        readMaze(maze);
        
        // The method above solves the puzzle, storing its solution in the ArrayList solution, and returns the number of steps needed to solve the puzzle.
        System.out.println(solveMaze(maze, sx, sy, fx, fy));
        
         
    }    
}