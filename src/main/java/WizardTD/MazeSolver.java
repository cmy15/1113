package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;

public class MazeSolver {

    private static final int SIZE = 20;
    private char[][] map;
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};


    public MazeSolver(String configPath, PApplet pApplet) {
        JSONObject config = pApplet.loadJSONObject(configPath);
        String layoutFile = config.getString("layout");
        loadLayout(layoutFile);
    }

    private void loadLayout(String layoutFile) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(layoutFile));
            map = new char[SIZE][SIZE];

            for (int i = 0; i < SIZE; i++) {
                String inputLine = lines.get(i);
                if (inputLine.length() < SIZE) {
                    inputLine = String.format("%-" + SIZE + "s", inputLine);
                }

                for (int j = 0; j < SIZE; j++) {
                    if (inputLine.charAt(j) == 'X' || inputLine.charAt(j) == 'W') {
                        map[i][j] = inputLine.charAt(j);
                    } else {
                        map[i][j] = ' ';
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<List<int[]>> findPaths() {
        List<List<int[]>> allPaths = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((i == 0 || i == SIZE - 1 || j == 0 || j == SIZE - 1) && map[i][j] == 'X') {
                    LinkedList<int[]> path = bfs(new int[]{i, j});
                    if (path != null) {
                        allPaths.add(path);
                    }
                }
            }
        }
        return allPaths;
    }
    private LinkedList<int[]> bfs(int[] start) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(start);
        visited[start[0]][start[1]] = true;

        int[][] parent = new int[SIZE * SIZE][2];
        for (int i = 0; i < SIZE * SIZE; i++) {
            parent[i] = new int[]{-1, -1};
        }

        while (!queue.isEmpty()) {
            int[] current = queue.poll();

            if (map[current[0]][current[1]] == 'W') {
                LinkedList<int[]> path = new LinkedList<>();
                int[] node = current;
                while (node[0] != -1 && node[1] != -1) {
                    path.addFirst(node);
                    node = parent[node[0] * SIZE + node[1]];
                }
                return path;
            }

            for (int[] dir : DIRECTIONS) {
                int newX = current[0] + dir[0];
                int newY = current[1] + dir[1];

                if (isValid(newX, newY) && !visited[newX][newY] && map[newX][newY] != ' ') {
                    queue.offer(new int[]{newX, newY});
                    visited[newX][newY] = true;
                    parent[newX * SIZE + newY] = current;
                }
            }
        }

        return null;
    }
    private boolean isValid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
}
