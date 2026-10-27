import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    private record Edge(int to, int cost) {}

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet sets = new DisjointSet(w.cells());
        int[] result = new int[Math.max(0, w.cells() - 1)];
        int broken = 0;

        for (int i = 0; i < w.count() && broken < result.length; i++) {
            if (sets.union(w.a()[i], w.b()[i])) {
                result[broken++] = i;
            }
        }

        return result;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (x, y) -> Integer.compare(w.cost()[x], w.cost()[y]));

        DisjointSet sets = new DisjointSet(w.cells());
        int[] result = new int[Math.max(0, w.cells() - 1)];
        int broken = 0;

        for (int wall : order) {
            if (sets.union(w.a()[wall], w.b()[wall])) {
                result[broken++] = wall;
                if (broken == result.length) break;
            }
        }

        return result;
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int[] degree = new int[n];
        for (int i = 0; i < w.count(); i++) {
            degree[w.a()[i]]++;
            degree[w.b()[i]]++;
        }

        Edge[][] graph = new Edge[n][];
        for (int i = 0; i < n; i++) graph[i] = new Edge[degree[i]];
        int[] filled = new int[n];
        for (int i = 0; i < w.count(); i++) {
            int a = w.a()[i];
            int b = w.b()[i];
            int cost = w.cost()[i];
            graph[a][filled[a]++] = new Edge(b, cost);
            graph[b][filled[b]++] = new Edge(a, cost);
        }

        boolean[] used = new boolean[n];
        PriorityQueue<Edge> queue = new PriorityQueue<>((x, y) -> Integer.compare(x.cost(), y.cost()));
        queue.add(new Edge(0, 0));

        long total = 0;
        int added = 0;
        while (!queue.isEmpty() && added < n) {
            Edge current = queue.remove();
            if (used[current.to()]) continue;

            used[current.to()] = true;
            total += current.cost();
            added++;

            for (Edge edge : graph[current.to()]) {
                if (!used[edge.to()]) queue.add(edge);
            }
        }
        return total;
    }
}
