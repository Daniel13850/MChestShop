package me.daniel1385.mchestshop.objects;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FreeShopInfo {
    private String rank;
    private int delay;

    public FreeShopInfo(String rank, int delay) {
        this.rank = rank;
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public String getRank() {
        return rank;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FreeShopInfo that = (FreeShopInfo) o;
        return delay == that.delay && Objects.equals(rank, that.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, delay);
    }
}
