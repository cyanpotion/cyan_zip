package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

/**
 * <p>Transition class.</p>
 *
 * @author XenoAmess
 * @version 0.1.1
 */
public class Transition {
    public FenwickTree[][] fenwickTrees = new FenwickTree[ForcastingRangeEncodingEncoder.TRANSITION_NUM][260];

    /**
     * <p>Constructor for Transition.</p>
     */
    public Transition() {
        for (int i = 0; i < ForcastingRangeEncodingEncoder.TRANSITION_NUM; i++) {
            for (int j = 0; j < 260; j++) {
                this.fenwickTrees[i][j] = new FenwickTree();
            }
        }
    }

    /**
     * <p>inc.</p>
     *
     * @param i a int.
     * @param j a int.
     * @param k a int.
     */
    public void inc(int i, int j, int k) {
        this.add(i, j, k, 1);
    }

    /**
     * <p>dec.</p>
     *
     * @param i a int.
     * @param j a int.
     * @param k a int.
     */
    public void dec(int i, int j, int k) {
        this.sub(i, j, k, 1);
    }

    /**
     * <p>add.</p>
     *
     * @param i a int.
     * @param j a int.
     * @param k a int.
     * @param value a int.
     */
    public void add(int i, int j, int k, int value) {
        this.fenwickTrees[i][j].add(k, value);
    }

    /**
     * <p>sub.</p>
     *
     * @param i a int.
     * @param j a int.
     * @param k a int.
     * @param value a int.
     */
    public void sub(int i, int j, int k, int value) {
        this.fenwickTrees[i][j].sub(k, value);
    }

    /**
     * <p>get.</p>
     *
     * @param i a int.
     * @param j a int.
     * @param k a int.
     * @return a long.
     */
    public long get(int i, int j, int k) {
        return this.fenwickTrees[i][j].get(k);
    }

    /**
     * <p>getSum.</p>
     *
     * @param i a int.
     * @param j a int.
     * @param k a int.
     * @return a long.
     */
    public long getSum(int i, int j, int k) {
        return this.fenwickTrees[i][j].getSum(k);
    }

}
