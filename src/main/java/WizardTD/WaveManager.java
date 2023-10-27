package WizardTD;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    Mana playerMana;
    private final List<Wave> waves = new ArrayList<>();

    public WaveManager(String configPath, PApplet pApplet, MazeSolver solver, Mana playerMana) {
        this.playerMana = playerMana;


        JSONObject config = pApplet.loadJSONObject(configPath);
        JSONArray wavesConfig = config.getJSONArray("waves");
        for (int i = 0; i < wavesConfig.size(); i++) {

            waves.add(new Wave(wavesConfig.getJSONObject(i), pApplet, solver, playerMana));
        }
    }

    public List<Wave> getWaves() {
        return waves;
    }

}
