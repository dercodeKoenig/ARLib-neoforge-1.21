package ARLib.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MachineRecipe {
    public List<recipePart> inputs = new ArrayList<>();
    public int energyPerTick = 0;
    public List<recipePart> outputs = new ArrayList<>();
    public int ticksRequired = 1;

    public MachineRecipe copy(){
        MachineRecipe r = new MachineRecipe();
        for (recipePart p : inputs)
            r.inputs.add(new recipePart(p.id,p.num,p.p));
        for (recipePart p : outputs)
            r.outputs.add(new recipePart(p.id,p.num,p.p));
        r.ticksRequired = ticksRequired;
        r.energyPerTick = energyPerTick;
        return r;
    }

    public void compute_actual_output_nums(){
        for (recipePart p : inputs)
            p.roll();
        for (recipePart p : outputs)
            p.roll();
    }
    public void addInput(String input_id_or_tag, int num, float p) {
        recipePart part = new recipePart(input_id_or_tag,num,p);
        inputs.add(part);
    }

    public void addOutput(String output_id, int num, float p) {
        recipePart part = new recipePart(output_id,num,p);
        outputs.add(part);
    }
}

