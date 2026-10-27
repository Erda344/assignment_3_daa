import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int[][] open = Graphs.open(w, broken);
        int n = w.cells();
        int exit = n - 1;

        int deadEnds = 0;
        for (int[] neighbours : open) {
            if (neighbours.length == 1) deadEnds++;
        }

        boolean[] seen = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] stack = new int[n];
        int[] nextDirection = new int[n];
        int top = 0;
        int visited = 1;
        stack[0] = 0;
        seen[0] = true;

        while (top >= 0) {
            int cell = stack[top];
            if (cell == exit) break;

            if (nextDirection[top] == 4) {
                top--;
                continue;
            }

            int direction = nextDirection[top]++;
            int neighbour = neighbour(cell, direction, w.rows(), w.cols());
            if (neighbour < 0 || seen[neighbour] || !isOpen(open[cell], neighbour)) continue;

            seen[neighbour] = true;
            parent[neighbour] = cell;
            visited++;
            stack[++top] = neighbour;
            nextDirection[top] = 0;
        }

        if (!seen[exit]) return new Result(new int[0], visited, deadEnds);

        int length = 1;
        for (int cell = exit; cell != 0; cell = parent[cell]) length++;
        int[] path = new int[length];
        int cell = exit;
        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }
        return new Result(path, visited, deadEnds);
    }

    private static int neighbour(int cell, int direction, int rows, int cols) {
        int row = cell / cols;
        int col = cell % cols;
        if (direction == 0) return row > 0 ? cell - cols : -1;
        if (direction == 1) return col + 1 < cols ? cell + 1 : -1;
        if (direction == 2) return row + 1 < rows ? cell + cols : -1;
        return col > 0 ? cell - 1 : -1;
    }

    private static boolean isOpen(int[] neighbours, int wanted) {
        for (int neighbour : neighbours) {
            if (neighbour == wanted) return true;
        }
        return false;
    }
}
