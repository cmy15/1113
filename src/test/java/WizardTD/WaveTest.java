package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WaveTest {

    private PApplet pAppletStub;
    private MazeSolver solverStub;
    private Mana playerManaStub;
    private JSONObject waveConfig;

    @BeforeEach
    public void setUp() {
        pAppletStub = new PApplet();
        solverStub = new MazeSolver("config.json", pAppletStub) {
            @Override
            public List<List<int[]>> findPaths() {
                return List.of(Arrays.asList(new int[]{0, 0}, new int[]{1, 1})); // Dummy path
            }
        };
        playerManaStub = new Mana("config.json", pAppletStub); // Assuming Mana has a default constructor or create one for testing

        // Sample wave configuration
        waveConfig = new JSONObject();
        waveConfig.setFloat("pre_wave_pause", 2.0f);
        waveConfig.setFloat("duration", 5.0f);

        JSONObject monsterConfig = new JSONObject();
        monsterConfig.setString("type", "gremlin");
        monsterConfig.setInt("hp", 100);
        monsterConfig.setFloat("speed", 1.5f);
        monsterConfig.setFloat("armour", 0.5f);
        monsterConfig.setInt("mana_gained_on_kill", 5);
        monsterConfig.setInt("quantity", 2);

        JSONArray monstersConfig = new JSONArray();
        monstersConfig.append(monsterConfig);

        waveConfig.setJSONArray("monsters", monstersConfig);
    }

    @Test
    public void testWaveInitialization() {
        Wave wave = new Wave(waveConfig, pAppletStub, solverStub, playerManaStub);
        List<Monster> monsters = wave.getMonsters();

        assertEquals(2, monsters.size()); // Check if 2 monsters are initialized as per the config
    }

    @Test
    public void testStartWave() {
        Wave wave = new Wave(waveConfig, pAppletStub, solverStub, playerManaStub);
        assertFalse(wave.isWaveStarted());

        wave.startWave(pAppletStub);
        assertTrue(wave.isWaveStarted());
    }

    @Test
    public void testTimeSinceWaveStarted() {
        Wave wave = new Wave(waveConfig, pAppletStub, solverStub, playerManaStub);
        wave.startWave(pAppletStub);

        float initialTime = wave.getTimeSinceWaveStarted(pAppletStub);
        try {
            Thread.sleep(500); // Simulating a wait time
        } catch (InterruptedException ignored) {}
        float timeAfterDelay = wave.getTimeSinceWaveStarted(pAppletStub);

        assertTrue(timeAfterDelay - initialTime >= 500);
    }

    @Test
    public void testWaveFinished() {
        Wave wave = new Wave(waveConfig, pAppletStub, solverStub, playerManaStub);
        assertFalse(wave.isWaveFinished(pAppletStub));

        wave.startWave(pAppletStub);
        wave.getMonsters().clear(); // Simulating all monsters being cleared/defeated
        assertTrue(wave.isWaveFinished(pAppletStub));
    }
}
