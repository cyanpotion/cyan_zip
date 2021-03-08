package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

/**
 * <p>FenwickTree class.</p>
 *
 * @author XenoAmess
 * @version 0.1.1
 */
public class FenwickTree {
    public long sum;
    public long[] a;

    /**
     * <p>lowbit.</p>
     *
     * @param x a int.
     * @return a int.
     */
    public static int lowbit(int x) {
        return x & (-x);
    }

    /**
     * <p>Constructor for FenwickTree.</p>
     */
    public FenwickTree() {
        this.init();
    }

    /**
     * <p>init.</p>
     */
    public void init() {
        a = new long[260];
        sum = 0;
    }

    /**
     * <p>add.</p>
     *
     * @param x a int.
     * @param value a long.
     */
    public void add(int x, long value) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i] += value;
        }
        sum += value;
    }

    /**
     * <p>sub.</p>
     *
     * @param x a int.
     * @param value a long.
     */
    public void sub(int x, long value) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i] -= value;
        }
        sum -= value;
    }

    /**
     * <p>inc.</p>
     *
     * @param x a int.
     */
    public void inc(int x) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i]++;
        }
        sum++;
    }

    /**
     * <p>dec.</p>
     *
     * @param x a int.
     */
    public void dec(int x) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i]--;
        }
        sum--;
    }


    /**
     * <p>Getter for the field <code>sum</code>.</p>
     *
     * @param x a int.
     * @return a long.
     */
    public long getSum(int x) {
        x++;

        if (x == 0) {
            return 0;
        }
        long res = 0;
        for (int i = x; i != 0; i -= lowbit(i)) {
            res += a[i];
        }
        return res;
    }

    /**
     * <p>get.</p>
     *
     * @param x a int.
     * @return a long.
     */
    public long get(int x) {
        return this.getSum(x) - this.getSum(x - 1);
    }
}
