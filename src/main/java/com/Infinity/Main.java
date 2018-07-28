package com.Infinity;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);

        int n = cin.nextInt();
        node[] nodes = new node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new node();
            nodes[i].x = cin.nextInt();
            nodes[i].y = cin.nextInt();
        }

        Arrays.sort(nodes);
        int sum = 0;
        int prex, prey;
        prex = prey = 0;

        for (int i = 0; i < n; i++) {
            sum += (Math.abs(prex - nodes[i].x) + Math.abs(prey - nodes[i].y));
            prex = nodes[i].x;
            prey = nodes[i].y;
        }
        sum += nodes[n - 1].x + nodes[n - 1].y;
        System.out.println(sum);
    }
}

class node implements Comparable<node> {

    int x, y;

    public node() {
    }

    public node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(node o) {
        if (this.x < o.x) {
            return -1;
        } else if (this.x > o.x) {
            return 1;
        } else {
            return this.y - o.y;
        }
    }
}