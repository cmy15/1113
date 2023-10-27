package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;
    public static int FPS = 60;
    public String configPath = "config.json";
    private Board board;
    private WaveManager waveManager;
    private Mana mana;

    HashMap<String, PImage> towers = new HashMap<>();
    HashMap<String, PImage> gremlins = new HashMap<>();
    HashMap<String, PImage> paths = new HashMap<>();
    HashMap<String, PImage> others = new HashMap<>();

    int btnWidth = 40;
    int btnHeight = 40;
    int startX = 650;
    int startY = 50;
    int spacing = 20;
    float currentMana;
    float manaCap;

    String[] btnLabels = {"FF", "P", "T", "U1", "U2", "U3", "M", "U4"};
    String[] btnDescriptions = {
            "2x speed",
            "PAUSE",
            "Build \ntower",
            "Upgrade \nrange",

            "Upgrade \nspeed",
            "Upgrade \ndamage",
            "Mana \n pool",
            "Trap \n cost: 500"
    };

    int[] btnCosts = {0, 0, 100, 0, 0, 0, 400};
    int manaBarWidth = 320;
    int manaBarHeight = 30;
    int manaBarX = 430;
    int manaBarY = 5;
    boolean[] btnPressed = new boolean[8];
    boolean overBox = false;
    int Current_Over;
    int currentWaveIndex = 0;
    List<int[]> wizardHouses = new ArrayList<>();
    ArrayList<Tower> myTowers;
    ArrayList<Trap> myTraps;
    char lastKeyPressed = ' ';
    int consecutiveKeyPressCount = 0;
    int doubleFps = FPS * 2;
    boolean isDoubleFpsMode = false;
    boolean isGamePaused = false;
    boolean isGameWon = false;
    boolean mouseWasPressed = false;
    float gameSpeedMultiplier = 1.0f;




    @Override
    public void settings() {size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */

    @Override
    public void setup() {
        mana = new Mana(configPath, this);
        currentMana = mana.getCurrentMana();
        manaCap = mana.getManaCap();
        frameRate(FPS);
        board = new Board(configPath, this);
        MazeSolver solver = new MazeSolver(configPath, this);
        waveManager = new WaveManager(configPath, this, solver, mana);
        myTowers = new ArrayList<>();
        myTraps = new ArrayList<>();

        File dir = new File("./src/main/resources/WizardTD/");
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String filename = file.getName();
                    PImage img = loadImage(file.getPath());

                    // categorise by name
                    if (filename.startsWith("tower")) {
                        towers.put(filename, img);
                    } else if (filename.startsWith("gremlin")) {
                        gremlins.put(filename, img);
                    } else if (filename.startsWith("path")) {
                        paths.put(filename, img);
                    } else {
                        // 其他图像（如beetle、fireball、grass等）
                        others.put(filename, img);
                    }
                }
            }
        }

    }

    @Override
    public void keyPressed(){

        if (key == 'F' || key == 'f') {
            if (lastKeyPressed == 'F' || lastKeyPressed == 'f') {
                consecutiveKeyPressCount++;
            } else {
                consecutiveKeyPressCount = 1;
            }

            if (consecutiveKeyPressCount == 2) {
                if (gameSpeedMultiplier == 1.0f) {
                    gameSpeedMultiplier = 2.0f;
                } else {
                    gameSpeedMultiplier = 1.0f;
                }
                toggleFpsMode();
                btnPressed[0] = !btnPressed[0];
                consecutiveKeyPressCount = 0;
            }
        } else {

        if (key == 'P' || key == 'p') {
            isGamePaused = !isGamePaused;
            btnPressed[1] = !btnPressed[1];
        }

        if (key == 'T' || key == 't') {
            btnPressed[2] = !btnPressed[2];
        }

        if (key == '1') {
            btnPressed[3] = !btnPressed[3];
        }

        if (key == '2') {
            btnPressed[4] = !btnPressed[4];
        }

        if (key == '3') {
            btnPressed[5] = !btnPressed[5];
        }

        if (key == '4') {
            btnPressed[7] = !btnPressed[7];
        }

        if (key == 'M' || key == 'm') {
            mana.castManaPoolSpell();
            btnPressed[6] = !btnPressed[6];
        }

        if (key == 'R' || key == 'r') {
            restartGame();
        }

            consecutiveKeyPressCount = 0;
        }

        lastKeyPressed = key;
    }

    @Override
    public void keyReleased(){

    }

    @Override
    public void mouseMoved() {
        overBox = false;
        for (int i = 0; i < 8; i++) {
            if (mouseX > startX && mouseX < startX + btnWidth &&
                    mouseY > startY + i * (btnHeight + spacing) && mouseY < startY + i * (btnHeight + spacing) + btnHeight) {

                if (i == 2 || i == 6) {
                    Current_Over = i;
                    overBox = true;
                }
            }
        }

        int tileX = mouseX / CELLSIZE;
        int tileY = (mouseY - TOPBAR) / CELLSIZE;


        // check if out of board
        if (tileX < 0 || tileX >= BOARD_WIDTH || tileY < 0 || tileY >= BOARD_WIDTH) {
            return;
        }

        Board.Tile tile = board.getTile(tileX, tileY);
        if (tile == Board.Tile.TOWER || tile == Board.Tile.TOWER1 || tile == Board.Tile.TOWER2 ){
            noFill();
            stroke(255,255, 0);

            for (Tower tower : myTowers) {
                if ((tower.getTileX() == tileX && tower.getTileY() == tileY)) {

                    clip(0, 40, 640, 640);

                    ellipse(tower.getCenterX(), tower.getCenterY(), (float) (tower.getRange() * 2), (float) (tower.getRange() * 2));
                    noClip();

                    fill(255);
                    textSize(12);


                    if (btnPressed[3] || btnPressed[4] || btnPressed[5]) {
                        Map<String, Integer> costs = calculateTotalCost();
                        int totalCost = costs.values().stream().mapToInt(Integer::intValue).sum();


                        int boxWidth = 120;
                        int boxHeight = 100;
                        int boxX = width - boxWidth - 10;
                        int boxY = height - boxHeight - 10;


                        fill(255);
                        rect(boxX + 20, boxY, boxWidth, boxHeight);


                        fill(0);
                        textSize(12);


                        int yOffset = 30;
                        text("Upgrade Cost", boxX + 30, boxY + boxHeight - 90);
                        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
                            text(entry.getKey() + ": " + entry.getValue(), boxX + 25, boxY + yOffset);
                            yOffset += 15;
                        }


                        text("Total Cost: " + totalCost, boxX + 30, boxY + boxHeight - 10);
                    }
                }
            }
        }

        // if build tower pressed
        if (!isGamePaused) {
            if (btnPressed[2]) {

                // if on grass tile
                if (tile == Board.Tile.GRASS) {
                    fill(0, 255, 0, 50);
                    rect(tileX * CELLSIZE, tileY * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
                    fill(0, 255, 0, 50);

                } else {
                    fill(255, 0, 0, 50);
                    rect(tileX * CELLSIZE, tileY * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
                }
            }

            if (btnPressed[7]) {
                // if on path_horizontal
                if (tile == Board.Tile.PATH_HORIZONTAL) {
                    fill(0, 255, 0, 50);
                    rect(tileX * CELLSIZE, tileY * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
                } else {
                    fill(255, 0, 0, 50);
                    rect(tileX * CELLSIZE, tileY * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < 8; i++) {
            if (mouseX > startX && mouseX < startX + btnWidth &&
                    mouseY > startY + i * (btnHeight + spacing) && mouseY < startY + i * (btnHeight + spacing) + btnHeight) {
                btnPressed[i] = !btnPressed[i];
                if (i == 0) {
                    toggleFpsMode();
                }

                if (i == 1) {
                    isGamePaused = !isGamePaused;
                }

                if (i == 6) {
                    mana.castManaPoolSpell();

                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void draw() {



        if (isGameWon) {
            displayWinScreen();
        }
        else {
            if (mana.isGameOver()) {
                displayGameOver();
                return;
            }
            if (isGamePaused) {
                textSize(80);
                fill(255, 0, 0);
                textAlign(CENTER, CENTER);
                text("GAME PAUSED", (float) WIDTH / 2, (float) HEIGHT / 2);
            }else {
                float deltaTime = (float) (1.0 / frameRate) * gameSpeedMultiplier;
                mana.update(deltaTime);

                background(204, 153, 255);
                Game_Board();
                Game_Text();
                Game_Buttons();
                Game_Monster();
                Game_WizardHouse();
                Game_Tower();
                for (Tower tower : myTowers) {
                    tower.display();
                }
                for (Trap trap : myTraps) {
                    trap.display();
                }
                mouseMoved();
                Game_Text();
                currentMana = mana.getCurrentMana();
                manaCap = mana.getManaCap();

            }
        }









    }

    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }



    /**
     * Source: <a href="https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java">...</a>
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((double) (newWidth - w) / 2, (double) (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }

    public void Game_Board(){
        // write Board
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_WIDTH; y++) {

                int renderX = x * CELLSIZE;
                int renderY = y * CELLSIZE + TOPBAR;

                Board.Tile tile = board.getTile(x, y);

                PImage path0 = paths.get("path0.png"); // -
                PImage path1 = paths.get("path1.png"); // left down turn
                PImage path2 = paths.get("path2.png"); // T down
                PImage path3 = paths.get("path3.png"); // +

                switch (tile) {
                    case GRASS:
                        image(others.get("grass.png"), renderX, renderY);
                        break;
                    case SHRUB:
                        image(others.get("shrub.png"), renderX, renderY);
                        break;
                    case PATH_HORIZONTAL:
                        image(path0, renderX, renderY);
                        break;
                    case PATH_VERTICAL:
                        image(rotateImageByDegrees(path0, 90), renderX, renderY);
                        break;
                    case PATH_CORNER_LEFT_DOWN:
                        image(path1, renderX, renderY);
                        break;
                    case PATH_CORNER_RIGHT_DOWN:
                        image(rotateImageByDegrees(path1, -90), renderX, renderY);
                        break;
                    case PATH_CORNER_LEFT_UP:
                        image(rotateImageByDegrees(path1, 90), renderX, renderY);
                        break;
                    case PATH_CORNER_RIGHT_UP:
                        image(rotateImageByDegrees(path1, 270), renderX, renderY);
                        break;
                    case PATH_T_DOWN:
                        image(path2, renderX, renderY);
                        break;
                    case PATH_T_UP:
                        image(rotateImageByDegrees(path2, 180), renderX, renderY);
                        break;
                    case PATH_T_LEFT:
                        image(rotateImageByDegrees(path2, 90), renderX, renderY);
                        break;
                    case PATH_T_RIGHT:
                        image(rotateImageByDegrees(path2, 270), renderX, renderY);
                        break;
                    case PATH_CROSSROAD:
                        image(path3, renderX, renderY);
                        break;
                    case TOWER2:
                        image(towers.get("tower2.png"), renderX, renderY);
                        break;
                    case TOWER1:
                        image(towers.get("tower1.png"), renderX, renderY);
                        break;
                    case TOWER:
                        image(towers.get("tower0.png"), renderX, renderY);
                        break;
                    case WIZARD_HOUSE:
                        image(others.get("grass.png"), renderX, renderY);
                        wizardHouses.add(new int[]{x, y});
                        break;
                    default:
                        break;
                }
            }




        }
        for (int[] position : wizardHouses) {
            int x = position[0];
            int y = position[1];
            int offsetX = (CELLSIZE - 48) / 2;
            int offsetY = (CELLSIZE - 48) / 2;
            image(others.get("wizard_house.png"), x * CELLSIZE + offsetX, y * CELLSIZE + offsetY + TOPBAR);
        }
    }

    public void Game_Text() {
        fill(255);
        rect(manaBarX, manaBarY, manaBarWidth, manaBarHeight);
        float manaRatio = currentMana / manaCap;
        fill(0, 0, 255);
        rect(manaBarX, manaBarY, manaBarWidth * manaRatio, manaBarHeight);

        fill(0);
        textSize(30);

        // get and display next wave
        if (waveManager != null && (currentWaveIndex + 1) < waveManager.getWaves().size()) {
            Wave currentWave = waveManager.getWaves().get(currentWaveIndex);

            float timeElapsedSinceCurrentWaveStarted = currentWave.getTimeSinceWaveStarted(this);

            // + currentWave's duration
            float totalTimeForCurrentWave = currentWave.duration * 1000 + currentWave.preWavePause * 1000;
            float timeRemainingForNextWave = totalTimeForCurrentWave - timeElapsedSinceCurrentWaveStarted;

            if (timeRemainingForNextWave < 0) timeRemainingForNextWave = 0;  // clamp to zero if negative

            text("Wave " + (currentWaveIndex + 2) + "starts: " + (int)(timeRemainingForNextWave / 1000) + "s", 20, 20);
        }


        textSize(30);
        text("MANA:  " + Math.round(currentMana) + " / " + manaCap, 320, 18);

        if (Current_Over == 2 || Current_Over == 6) {

            if(overBox) {
                stroke(0);
                fill(255);
                rect(startX - 160, startY + Current_Over * (btnHeight + spacing) + btnHeight - 30, 140, 25);

                fill(0);
                if (Current_Over == 6){
                    btnCosts[6] = (int) mana.getManaPoolSpellCost();


                }
                text("Cost: " + btnCosts[Current_Over], startX - 150, startY + Current_Over * (btnHeight + spacing) + btnHeight - 20);
            }
        }
    }

    public void Game_Buttons(){
        for (int i = 0; i < 8; i++) {
            if (btnPressed[i]) {
                fill(0, 255, 0);
            } else {
                noFill();
            }
            rect(startX, startY + i * (btnHeight + spacing), btnWidth, btnHeight);
            fill(0);
            textSize(18);
            textAlign(CENTER, CENTER);
            text(btnLabels[i], startX + (float) btnWidth /2, startY + i * (btnHeight + spacing) + (float) btnHeight /2);


            textAlign(LEFT, CENTER);
            textSize(15);
            text(btnDescriptions[i], startX + btnWidth + 10, startY + i * (btnHeight + spacing) + (float) btnHeight /2);


        }

    }

    public void Game_Monster() {
        List<Wave> waves = waveManager.getWaves();

        // check if null
        if (waves == null || waves.isEmpty()) return;

        if (currentWaveIndex == 0 && !waves.get(0).isWaveStarted()) {
            waves.get(0).startWave(this);
            waves.get(0).setWaveStarted(true);
        }

        // each wave
        for (int waveIndex = 0; waveIndex <= currentWaveIndex && waveIndex < waves.size(); waveIndex++) {
            Wave wave = waves.get(waveIndex);

            if (wave != null && wave.isWaveStarted()) {
                float elapsedTimeSinceWaveStart = wave.getTimeSinceWaveStarted(this);

                for (Monster monster : wave.getMonsters()) {
                    if (elapsedTimeSinceWaveStart >= monster.spawnTime && monster.isAlive()) {
                        mana.decrease(monster.move());
                        monster.display(this);
                    }
                }
            }
        }

        // if all generated,，and next preWavePause is done, new wave start
        if (currentWaveIndex + 1 < waves.size()) {
            Wave currentWave = waves.get(currentWaveIndex);
            Wave nextWave = waves.get(currentWaveIndex + 1);

            float elapsedTimeSinceCurrentWaveStarted = currentWave.getTimeSinceWaveStarted(this);

            // since this wave start total time.
            float totalElapsedTimeForCurrentWave = elapsedTimeSinceCurrentWaveStarted - currentWave.preWavePause * 1000;

            // all generated and preWavePause is done, next wave begin
            if (totalElapsedTimeForCurrentWave >= currentWave.duration * 1000 && !nextWave.isWaveStarted()) {
                currentWaveIndex++;  // next wave
                nextWave.startWave(this);
                nextWave.setWaveStarted(true);
            }
        }
        if (currentWaveIndex == waves.size() - 1) {
            Wave lastWave = waves.get(currentWaveIndex);
            boolean allMonstersProcessed = true;
            for (Monster monster : lastWave.getMonsters()) {
                if (monster.isAlive()) {
                    allMonstersProcessed = false;
                    break;
                }
            }
            if (allMonstersProcessed) {
                isGameWon = true;
            }
        }

    }

    public void Game_WizardHouse(){
        // draw WIZARD_HOUSE
        for (int[] position : wizardHouses) {
            int x = position[0];
            int y = position[1];
            int offsetX = (CELLSIZE - 48) / 2;
            int offsetY = (CELLSIZE - 48) / 2;
            image(others.get("wizard_house.png"), x * CELLSIZE + offsetX, y * CELLSIZE + offsetY + TOPBAR);
        }

    }

    public void Game_Tower() {
        int tileX = mouseX / CELLSIZE;
        int tileY = (mouseY - TOPBAR) / CELLSIZE;
        // check if it's out of bounds
        if (tileX < 0 || tileX >= BOARD_WIDTH || tileY < 0 || tileY >= BOARD_WIDTH) {
            return;
        }
        // if the "Build tower" button is pressed and the mouse is pressed on a grass tile

        Board.Tile tile = board.getTile(tileX, tileY);

        if (btnPressed[2] && mousePressed && tile == Board.Tile.GRASS) {
            int rangeLevel = btnPressed[3] ? 1 : 0;
            int speedLevel = btnPressed[4] ? 1 : 0;
            int damageLevel = btnPressed[5] ? 1 : 0;

            // build new tower
            Tower newTower = new Tower(tileX, tileY, this, rangeLevel, speedLevel, damageLevel, this.configPath, board, mana);

            // get new build cost
            int totalCost = newTower.getCost();

            // check if mana enough
            if (currentMana >= totalCost) {
                myTowers.add(newTower);
                newTower.display();
                mana.decrease(totalCost);
            }
        }

        if (btnPressed[7] && mousePressed && tile == Board.Tile.PATH_HORIZONTAL) {
            Trap newTrap = new Trap(tileX, tileY, this, board);
            int totalCost = 500;
            if (currentMana >= totalCost) {
                myTraps.add(newTrap);
                newTrap.display();
                mana.decrease(totalCost);
            }
        }

        if (!mouseWasPressed && mousePressed && (btnPressed[3] || btnPressed[4]|| btnPressed[5]) && (tile == Board.Tile.TOWER||tile == Board.Tile.TOWER1||tile == Board.Tile.TOWER2)) {
            int[] desiredUpgrades = {
                    btnPressed[3] ? 1 : 0, // range
                    btnPressed[4] ? 1 : 0, // speed
                    btnPressed[5] ? 1 : 0  // damage
            };

            for (Tower tower : myTowers) {
                if (tower.getTileX() == tileX && tower.getTileY() == tileY) {
                    List<Integer> results = tower.handleUpgrade(desiredUpgrades);
                    int totalCost = 0;
                    for (int i = 0; i < results.size(); i++) {
                        if (results.get(i) == 1) {
                            totalCost += tower.getUpgradeCost(i + 1);
                        }
                    }

                    mana.decrease(totalCost);
                    break;
                }
            }
        }

        for (Tower myTower : myTowers) {
            // let each tower shoot at monsters from all active waves
            for (Wave wave : waveManager.getWaves()) {
                if (wave.isWaveStarted() && !wave.isWaveFinished(this)) {
                    myTower.shoot((ArrayList<Monster>) wave.getMonsters());
                }
            }

            // update and display fireballs
            for (int x = 0; x < myTower.fireballs.size(); x++) {
                myTower.fireballs.get(x).update();
                myTower.fireballs.get(x).display(this);
            }
        }
        for (Trap myTrap : myTraps) {
            // let each tower shoot at monsters from all active waves
            for (Wave wave : waveManager.getWaves()) {
                if (wave.isWaveStarted() && !wave.isWaveFinished(this)) {
                    myTrap.checkAndDamageMonsters((ArrayList<Monster>)wave.getMonsters()); // assuming Wave class has a getMonsters() method
                }
            }
        }
        mouseWasPressed = mousePressed;
    }

    void toggleFpsMode() {
        if (isDoubleFpsMode) {
            frameRate(FPS);  // normal FPS
            isDoubleFpsMode = false;
        } else {
            frameRate(doubleFps);
            isDoubleFpsMode = true;
        }

    }

    private Map<String, Integer> calculateTotalCost() {
        Map<String, Integer> costs = new HashMap<>();
        if (myTowers.isEmpty()) {
            return costs;  // an empty map if no towers exist
        }

        Tower referenceTower = myTowers.get(0);  // first tower as a reference for costs
        List<Integer> upgradeCosts = referenceTower.getUpgradeCosts();

        if (btnPressed[3] && !upgradeCosts.isEmpty()) costs.put("Range Upgrade", upgradeCosts.get(0));
        if (btnPressed[4] && upgradeCosts.size() > 1) costs.put("Speed Upgrade", upgradeCosts.get(1));
        if (btnPressed[5] && upgradeCosts.size() > 2) costs.put("Damage Upgrade", upgradeCosts.get(2));

        return costs;
    }

    void displayGameOver() {
        fill(255, 0, 0);
        textSize(80);
        textAlign(CENTER, CENTER);
        text("GAME OVER", (float) width /2, ((float) height /2) - 40);
        text("Press 'R' to restart", (float) width /2, (float) height /2 + 40);
    }
    public void displayWinScreen() {
        fill(255, 0, 0);
        textSize(80);
        textAlign(CENTER, CENTER);
        text("WIN", (float) width /2, ((float) height /2) - 40);
    }

    public void restartGame() {
        mana = new Mana(configPath, this);
        currentMana = mana.getCurrentMana();
        manaCap = mana.getManaCap();
        board = new Board(configPath, this);
        MazeSolver solver = new MazeSolver(configPath, this);
        waveManager = new WaveManager(configPath, this, solver, mana);
        myTowers.clear();
        currentWaveIndex = 0;
        isDoubleFpsMode = false;
        isGamePaused = false;
        btnPressed = new boolean[8];
    }

}



