package ARLib.utils;

public class recipePart {
    public recipePart(String id, int num, float p) {
        this.id = id;
        this.num = num;
        this.p = p;
    }

    public String id;       // id/tag
    public int num;         // how often to 'roll the dice' / max num
    public float p;         // probability to produce / consume
    public int actual_num;  // how much is actually consumed / produced after 'rolling the dice'

    public void roll() {
        actual_num = 0;
        // Roll the dice `num` times if `p < 1`
        for (int i = 0; i < num; i++) {
            if (p >= 1 || Math.random() < p) {
                actual_num++;
            }
        }
    }
}
