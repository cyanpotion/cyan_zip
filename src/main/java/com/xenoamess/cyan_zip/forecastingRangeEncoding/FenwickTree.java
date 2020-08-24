package com.xenoamess.cyan_zip.forecastingRangeEncoding;

public class FenwickTree {
    public long sum;
    public long[] a;

    public static int lowbit(int x) {
        return x & (-x);
    }

    public FenwickTree() {
        this.init();
    }

    public void init() {
        a = new long[260];
        sum = 0;
    }

    public void add(int x, long value) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i] += value;
        }
        sum += value;
    }

    public void sub(int x, long value) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i] -= value;
        }
        sum -= value;
    }

    public void inc(int x) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i]++;
        }
        sum++;
    }

    public void dec(int x) {
        x++;

        for (int i = x; i < 260; i += lowbit(i)) {
            a[i]--;
        }
        sum--;
    }


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

    public long get(int x) {
        return this.getSum(x) - this.getSum(x - 1);
    }
}
