package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Board {

    public static final int BOARD_WIDTH = 20;
    public Tile[][] tiles = new Tile[BOARD_WIDTH][BOARD_WIDTH];

    public Board(String configPath, PApplet pApplet) {
        JSONObject config = pApplet.loadJSONObject(configPath);
        String layoutFile = config.getString("layout");
        loadLayout(layoutFile);
    }

    private void loadLayout(String layoutFile) {
        String[] layout = new String[BOARD_WIDTH];
        try (BufferedReader br = new BufferedReader(new FileReader(layoutFile))) {
            for (int y = 0; y < BOARD_WIDTH; y++) {
                layout[y] = br.readLine();

                if (layout[y].length() != BOARD_WIDTH) {
                    while (layout[y].length() < BOARD_WIDTH) {
                        layout[y] += " ";
                    }
                }
            }

            for (int y = 0; y < BOARD_WIDTH; y++) {
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    char c = layout[y].charAt(x);
                    switch (c) {
                        case 'S':
                            tiles[x][y] = Tile.SHRUB;
                            break;
                        case 'X':
                            tiles[x][y] = determinePathType(x, y, layout);
                            break;
                        case ' ':
                            tiles[x][y] = Tile.GRASS;
                            break;
                        case 'W':
                            tiles[x][y] = Tile.WIZARD_HOUSE;
                            break;

                        default:
                            System.err.println("Unknown tile character at (" + x + "," + y + "): " + c);
                            tiles[x][y] = Tile.GRASS;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Tile determinePathType(int x, int y, String[] layout) {
        boolean left = x > 0 && layout[y].charAt(x - 1) == 'X';
        boolean right = x < BOARD_WIDTH - 1 && layout[y].charAt(x + 1) == 'X';
        boolean up = y > 0 && layout[y - 1].charAt(x) == 'X';
        boolean down = y < BOARD_WIDTH - 1 && layout[y + 1].charAt(x) == 'X';

        if (left && right && up && down) {
            return Tile.PATH_CROSSROAD;
        } else if (left && right && up) {
            return Tile.PATH_T_UP;
        } else if (left && right && down) {
            return Tile.PATH_T_DOWN;
        } else if (up && down && left) {
            return Tile.PATH_T_LEFT;
        } else if (up && down && right) {
            return Tile.PATH_T_RIGHT;
        } else if (left && up) {
            return Tile.PATH_CORNER_LEFT_UP;
        } else if (right && up) {
            return Tile.PATH_CORNER_RIGHT_UP;
        } else if (left && down) {
            return Tile.PATH_CORNER_LEFT_DOWN;
        } else if (right && down) {
            return Tile.PATH_CORNER_RIGHT_DOWN;
        } else if (left || right) {
            return Tile.PATH_HORIZONTAL;
        } else {
            return Tile.PATH_VERTICAL;
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }


    public void setTile(int x, int y, Tile tile) {
        tiles[x][y] = tile;
    }


    public enum Tile {
        GRASS, SHRUB, PATH_HORIZONTAL, PATH_CORNER_LEFT_DOWN, PATH_T_DOWN, PATH_CROSSROAD,
        PATH_VERTICAL, PATH_CORNER_RIGHT_DOWN, PATH_CORNER_LEFT_UP, PATH_CORNER_RIGHT_UP,
        PATH_T_UP, PATH_T_LEFT, PATH_T_RIGHT, TOWER, TOWER1, TOWER2, WIZARD_HOUSE, TRAP
    }


}
