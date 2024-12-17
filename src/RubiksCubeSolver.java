import java.util.*;

class RubiksCube {
    private int[][][] cube;
    private int size ;
    private static final String[] MOVES = {"U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'"};

    public RubiksCube(int [][][] cube,int size) {
        this.size = size;
        this.cube = cube;

    }

    public boolean isSolved() {
        for (int face = 0; face < 6; face++) {
            int color = cube[face][0][0];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (cube[face][i][j] != color) return false;
                }
            }
        }
        return true;
    }

    public RubiksCube applyMove(String move) {
        RubiksCube newCube = this.copy();

        switch (move) {
            case "U":
                // Rotate top face clockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][size - 1 - i][0];
                    newCube.cube[0][size - 1 - i][0] = newCube.cube[1][size - 1][i];
                    newCube.cube[1][size - 1][i] = newCube.cube[5][size - 1 - i][size - 1];
                    newCube.cube[5][size - 1 - i][size - 1] = newCube.cube[4][0][i];
                    newCube.cube[4][0][i] = temp;
                }
                break;
            case "U'":
                // Rotate top face counterclockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][size - 1 - i][0];
                    newCube.cube[0][size - 1 - i][0] = newCube.cube[4][0][i];
                    newCube.cube[4][0][i] = newCube.cube[5][size - 1 - i][size - 1];
                    newCube.cube[5][size - 1 - i][size - 1] = newCube.cube[1][size - 1][i];
                    newCube.cube[1][size - 1][i] = temp;
                }
                break;
            case "D":
                // Rotate bottom face clockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[2][size - 1 - i][size - 1];
                    newCube.cube[2][size - 1 - i][size - 1] = newCube.cube[3][size - 1][i];
                    newCube.cube[3][size - 1][i] = newCube.cube[5][0][i];
                    newCube.cube[5][0][i] = newCube.cube[4][size - 1 - i][0];
                    newCube.cube[4][size - 1 - i][0] = temp;
                }
                break;
            case "D'":
                // Rotate bottom face counterclockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[2][size - 1 - i][size - 1];
                    newCube.cube[2][size - 1 - i][size - 1] = newCube.cube[4][size - 1 - i][0];
                    newCube.cube[4][size - 1 - i][0] = newCube.cube[5][0][i];
                    newCube.cube[5][0][i] = newCube.cube[3][size - 1][i];
                    newCube.cube[3][size - 1][i] = temp;
                }
                break;
            case "L":
                // Rotate left face clockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][i][0];
                    newCube.cube[0][i][0] = newCube.cube[2][i][0];
                    newCube.cube[2][i][0] = newCube.cube[5][size - 1 - i][0];
                    newCube.cube[5][size - 1 - i][0] = newCube.cube[1][i][size - 1];
                    newCube.cube[1][i][size - 1] = temp;
                }
                break;
            case "L'":
                // Rotate left face counterclockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][i][0];
                    newCube.cube[0][i][0] = newCube.cube[1][i][size - 1];
                    newCube.cube[1][i][size - 1] = newCube.cube[5][size - 1 - i][0];
                    newCube.cube[5][size - 1 - i][0] = newCube.cube[2][i][0];
                    newCube.cube[2][i][0] = temp;
                }
                break;
            case "R":
                // Rotate right face clockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][i][size - 1];
                    newCube.cube[0][i][size - 1] = newCube.cube[1][i][0];
                    newCube.cube[1][i][0] = newCube.cube[5][size - 1 - i][size - 1];
                    newCube.cube[5][size - 1 - i][size - 1] = newCube.cube[2][i][size - 1];
                    newCube.cube[2][i][size - 1] = temp;
                }
                break;
            case "R'":
                // Rotate right face counterclockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][i][size - 1];
                    newCube.cube[0][i][size - 1] = newCube.cube[2][i][size - 1];
                    newCube.cube[2][i][size - 1] = newCube.cube[5][size - 1 - i][size - 1];
                    newCube.cube[5][size - 1 - i][size - 1] = newCube.cube[1][i][0];
                    newCube.cube[1][i][0] = temp;
                }
                break;
            case "F":
                // Rotate front face clockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][size - 1][i];
                    newCube.cube[0][size - 1][i] = newCube.cube[3][0][i];
                    newCube.cube[3][0][i] = newCube.cube[2][size - 1 - i][size - 1];
                    newCube.cube[2][size - 1 - i][size - 1] = newCube.cube[1][0][i];
                    newCube.cube[1][0][i] = temp;
                }
                break;
            case "F'":
                // Rotate front face counterclockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][size - 1][i];
                    newCube.cube[0][size - 1][i] = newCube.cube[1][0][i];
                    newCube.cube[1][0][i] = newCube.cube[2][size - 1 - i][size - 1];
                    newCube.cube[2][size - 1 - i][size - 1] = newCube.cube[3][0][i];
                    newCube.cube[3][0][i] = temp;
                }
                break;
            case "B":
                // Rotate back face clockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][0][i];
                    newCube.cube[0][0][i] = newCube.cube[1][size - 1][i];
                    newCube.cube[1][size - 1][i] = newCube.cube[2][0][i];
                    newCube.cube[2][0][i] = newCube.cube[3][size - 1][i];
                    newCube.cube[3][size - 1][i] = temp;
                }
                break;
            case "B'":
                // Rotate back face counterclockwise
                for (int i = 0; i < size; i++) {
                    int temp = newCube.cube[0][0][i];
                    newCube.cube[0][0][i] = newCube.cube[3][size - 1][i];
                    newCube.cube[3][size - 1][i] = newCube.cube[2][0][i];
                    newCube.cube[2][0][i] = newCube.cube[1][size - 1][i];
                    newCube.cube[1][size - 1][i] = temp;
                }
                break;
            default:
                break;
        }

        return newCube;
    }

    public List<String> getPossibleMoves() {
        return Arrays.asList(MOVES);
    }

    public RubiksCube copy() {
        RubiksCube newCube = new RubiksCube(new int[6][size][size], size);
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < size; i++) {
                System.arraycopy(this.cube[face][i], 0, newCube.cube[face][i], 0, size);
            }
        }
        return newCube;
    }

    // Simplified heuristic: returns the number of faces not fully solved.
    public int heuristic() {
        int score = 0;
        for (int face = 0; face < 6; face++) {
            int baseColor = cube[face][0][0];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (cube[face][i][j] != baseColor) {
                        score++;
                    }
                }
            }
        }
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RubiksCube that = (RubiksCube) obj;
        return Arrays.deepEquals(cube, that.cube);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(cube);
    }
}

class Node implements Comparable<Node> {
    RubiksCube state;
    String move;
    int g; // Cost from start
    int h; // Heuristic estimate to goal
    Node parent;

    Node(RubiksCube state, String move, int g, int h, Node parent) {
        this.state = state;
        this.move = move;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.g + this.h, other.g + other.h);
    }
}

public class RubiksCubeSolver {
    public static void main(String[] args) {
        int randomCube[][][] = new int[6][3][3];
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    randomCube[i][j][k] = rand.nextInt(6);
//                    System.out.print(randomCube[i][j][k]+" ");
                }
//                System.out.println();
            }
//            System.out.println("===============================");
        }
        RubiksCube cube = new RubiksCube(randomCube,3);
        List<String> solution = aStar(cube);
        if (solution != null) {
            System.out.println("Solution found in " + solution.size() + " moves.");
            System.out.println("Moves: " + String.join(" ", solution));
        } else {
            System.out.println("No solution found.");
        }
    }

    public static List<String> aStar(RubiksCube initialState) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<RubiksCube> closedList = new HashSet<>();

        openList.add(new Node(initialState, null, 0, initialState.heuristic(), null));


        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            System.out.println("================>"+ currentNode.h);
            if (currentNode.state.isSolved()) {
                return extractSolution(currentNode);
            }

            closedList.add(currentNode.state);

            for (String move : currentNode.state.getPossibleMoves()) {
                RubiksCube newState = currentNode.state.applyMove(move);
                if (closedList.contains(newState)) continue;

                int g = currentNode.g + 1;
                int h = newState.heuristic();

                Node newNode = new Node(newState, move, g, h, currentNode);

                openList.add(newNode);
            }
        }

        return null;
    }

    private static List<String> extractSolution(Node node) {
        List<String> moves = new ArrayList<>();
        while (node != null && node.move != null) {
            moves.add(0, node.move); // Add moves in reverse order
            node = node.parent;
        }
        return moves;
    }
}
