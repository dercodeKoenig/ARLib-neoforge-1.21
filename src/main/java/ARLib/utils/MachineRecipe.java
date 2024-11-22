package ARLib.utils;

import java.util.HashMap;
import java.util.Map;

public class MachineRecipe {
    public Map<String, Integer> inputs = new HashMap<>();
    public int energyPerTick = 0;
    public Map<String, Integer> outputs = new HashMap<>();
    public int ticksRequired = 1;

    public void addInput(String input_id_or_tag, int num) {
        inputs.put(input_id_or_tag, num);
    }

    public void addOutput(String output_id, int num) {
        outputs.put(output_id,num);
    }
}
