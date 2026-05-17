package university.models;

import java.io.Serializable;
import university.enums.RewardType;

public class Reward implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private RewardType rewardType;
    private int amount;
    
    public Reward(RewardType rewardType, int amount) {
        this.rewardType = rewardType;
        this.amount = amount;
    }
    
    public void apply(StudentProgress progress) {
        switch (rewardType) {
            case COIN -> progress.addCoins(amount);
            case DIAMOND -> progress.addDiamonds(amount);
        }
    }
    
    // Getters
    public RewardType getRewardType() { return rewardType; }
    public int getAmount() { return amount; }
}
