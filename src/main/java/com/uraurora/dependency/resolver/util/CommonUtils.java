package com.uraurora.dependency.resolver.util;

import java.util.Arrays;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-28 15:03
 * @description :
 */
public class CommonUtils {

    static int[] tmp = new int[100];

    static void mergeSort(int[] q, int l, int r) {
        if (l >= r) return;
        int mid = l + r >> 1;
        mergeSort(q, l, mid);
        mergeSort(q, mid + 1, r);

        int i = l, j = mid + 1, k = 0;
        while (i <= mid && j <= r) {
            if (q[i] < q[j]) tmp[k++] = q[i++];
            else tmp[k++] = q[j++];
        }
        while (i <= mid) tmp[k++] = q[i++];
        while (j <= r) tmp[k++] = q[j++];

        for (i = l, j = 0; i <= r; i++, j++) q[i] = tmp[j];
    }

    static void quickSort(int[] q, int l, int r) {
        if (l >= r) return;
        int i = l - 1, j = r + 1, x = q[l];
        while (i < j) {
            do i++; while (q[i] < x);
            do j--; while (q[j] > x);
            if (i < j) swap(q, i, j);
        }
        quickSort(q, l, i);
        quickSort(q, j + 1, r);
    }

    static void heapSort(int[] q, int l, int r) {

    }

    static void heapify(int q, int i) {

    }

    static void swap(int[] q, int i, int j) {
        int t = q[i];
        q[i] = q[j];
        q[j] = t;
    }

    static double sqrt(int x) {
        double l = 0, r = 10000.0;
        while (r - l > 1E-8) {
            double mid = (l + r) / 2.0;
            if (mid * mid >= x) r = mid;
            else l = mid;
        }
        return l;
    }

    static int[] plus(int[] a, int[] b) {
        int size = Integer.max(a.length, b.length);
        int[] res = new int[size + 10];
        int c = 0, k = 0;
        for (int i =0; i<a.length || i < b.length;i++){
            if(i < a.length) c+= a[i];
            if(i < b.length) c+= b[i];
            res[k++] = c%10;
            c = c/10;
        }
        if(c!=0) res[k] = c;
        return res;
    }

    public static void main(String[] args) {
        int[] a = new int[]{10, 5, 7, 9, -1, 3, 2, 4, 0};
        quickSort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
        System.out.println(sqrt(3));
        int[] a1 = new int[]{2,3,9};
        int[] b1 = new int[]{8,6,2};
        System.out.println(Arrays.toString(plus(a1, b1)));
    }

}
