package com.xenoamess.cyan_zip.forecastingRangeEncoding;

public class Transition {
    public FenwickTree fenwickTrees[][] = new FenwickTree[Encoder.TRANSITION_NUM][260];

    public Transition() {
        for (int i = 0; i < Encoder.TRANSITION_NUM; i++) {
            for (int j = 0; j < 260; j++) {
                this.fenwickTrees[i][j] = new FenwickTree();
            }
        }
    }

    public void inc(int i, int j, int k) {
        this.add(i, j, k, 1);
    }

    public void dec(int i, int j, int k) {
        this.sub(i, j, k, 1);
    }

    public void add(int i, int j, int k, int value) {
        this.fenwickTrees[i][j].add(k, value);
    }

    public void sub(int i, int j, int k, int value) {
        this.fenwickTrees[i][j].sub(k, value);
    }

    public long get(int i, int j, int k) {
        return this.fenwickTrees[i][j].get(k);
    }

    public long getsum(int i, int j, int k) {
        return this.fenwickTrees[i][j].getsum(k);
    }

}
