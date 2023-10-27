package WizardTD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Wave {

    private final List<Monster> monsters = new ArrayList<>();
    private boolean waveStarted = false;
    private float waveStartTime;
    PImage[] deathFrames = new PImage[5];
    float preWavePause;
    float duration;



    public Wave(JSONObject waveConfig, PApplet pApplet, MazeSolver solver, Mana playerMana) {

        this.preWavePause = waveConfig.getFloat("pre_wave_pause");
        this.duration = waveConfig.getFloat("duration");



        JSONArray monstersConfig = waveConfig.getJSONArray("monsters");

        int totalMonstersInWave = 0;

        for (int i = 0; i < monstersConfig.size(); i++) {
            totalMonstersInWave += monstersConfig.getJSONObject(i).getInt("quantity");
        }

        float timeBetweenMonsters = duration * 1000 / totalMonstersInWave;

        float currentSpawnTime = 0;

        for (int i = 0; i < monstersConfig.size(); i++) {
            JSONObject monsterConfig = monstersConfig.getJSONObject(i);

            String type = monsterConfig.getString("type");
            int hp = monsterConfig.getInt("hp");
            float speed = monsterConfig.getFloat("speed");
            float armour = monsterConfig.getFloat("armour");
            int manaOnKill = monsterConfig.getInt("mana_gained_on_kill");
            int quantity = monsterConfig.getInt("quantity");

            PImage sprite = pApplet.loadImage("/src/main/resources/WizardTD/" + type + ".png");
            deathFrames[0] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin1.png");
            deathFrames[1] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin2.png");
            deathFrames[2] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin3.png");
            deathFrames[3] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin4.png");
            deathFrames[4] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin5.png");
            List<List<int[]>> paths = solver.findPaths();

            for (int j = 0; j < quantity; j++) {

                Random rand = new Random();
                List<int[]> path = paths.get(rand.nextInt(paths.size()));
                monsters.add(new Monster(type, hp, speed, armour, manaOnKill, path, sprite, currentSpawnTime,deathFrames, playerMana));
                currentSpawnTime += timeBetweenMonsters;
            }
        }


    }


    public boolean isWaveStarted()
    {
        return waveStarted;
    }
    public boolean isWaveFinished(PApplet pApplet) {
        return waveStarted && (monsters.isEmpty());
    }
    public void setWaveStarted(boolean isWaveStarted)
    {
        waveStarted = isWaveStarted;
    }

    public void startWave(PApplet pApplet) {
        this.waveStartTime = pApplet.millis();
    }


    public float getTimeSinceWaveStarted(PApplet pApplet) {
        return pApplet.millis() - waveStartTime;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

}
