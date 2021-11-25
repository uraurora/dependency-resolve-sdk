package com.uraurora.dependency.resolver.util;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-28 15:03
 * @description :
 */
public class CommonUtils {

    static int[] tmp = new int[100];

    static void mergeSort(int[] q, int l, int r) {
        if(l >= r)return;
        int mid = l+r>>1;
        mergeSort(q, l, mid);mergeSort(q, mid+1, r);
        int i = l, j = mid +1, k=0;
        while(i<=mid&&j<=r){
            if(q[i]<q[j])tmp[k++] = q[i++];
            else tmp[k++] = q[j++];
        }
        while(i<=mid)tmp[k++] = q[i++];
        while(j<=r)tmp[k++] = q[j++];
        for(i = l, j = 0; i<=r;i++,j++) q[i] = tmp[j];
    }

    static void quickSort(int[] q, int l, int r) {
        if(l >= r)return;
        int i = l-1, j= r+1,x =q[l+r>>1];
        while(i<j){
            do i++;while(q[i] < x);
            do j--;while (q[j] > x);
            if(i < j) swap(q, i, j);
        }
        quickSort(q, l, j);quickSort(q, j+1, r);
    }

    static void swap(int[] q, int i, int j) {
        int t = q[i];
        q[i] = q[j];
        q[j] = t;
    }

    static double sqrt(int x) {
        double l = 0, r = x;
        while (r - l > 1E-8) {
            double mid = (l + r) / 2;
            if (mid * mid >= x) r = mid;
            else l = mid;
        }
        return l;
    }

    static int[] plus(int[] a, int[] b) {
        int size = Integer.max(a.length, b.length);
        int[] res = new int[size + 10];
        int c = 0, k = 0;
        for (int i = 0; i < a.length || i < b.length; i++) {
            if (i < a.length) c += a[i];
            if (i < b.length) c += b[i];
            res[k++] = c % 10;
            c = c / 10;
        }
        if (c != 0) res[k] = c;
        return res;
    }


    static void prefixSum(int[][] q, int n, int m) {
        int[][] s = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                s[i][j] = s[i - 1][j] + s[i][j - 1] - s[i - 1][j - 1] + q[i - 1][j - 1];
            }
        }
    }


    /**
     * 3 4 3
     * 1 7 2 4
     * 3 6 2 8
     * 2 1 2 3
     * <p>
     * 1 1 2 2
     * 2 1 3 4
     * 1 3 3 4
     * <p>
     * 17
     * 27
     * 21
     */
    private static void testPrefixSum() {
        int[][] s = new int[1010][1010];
        int n, m, r;
        Scanner sc = new Scanner(new BufferedInputStream(System.in));
        n = sc.nextInt();
        m = sc.nextInt();
        r = sc.nextInt();
        int[][] q = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                q[i][j] = sc.nextInt();
            }
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                s[i][j] = s[i - 1][j] + s[i][j - 1] - s[i - 1][j - 1] + q[i][j];
            }
        }
        int x1, y1, x2, y2;
        while (r-- > 0) {
            x1 = sc.nextInt();
            y1 = sc.nextInt();
            x2 = sc.nextInt();
            y2 = sc.nextInt();
            System.out.println(s[x2][y2] + s[x1 - 1][y1 - 1] - s[x2][y1 - 1] - s[x1 - 1][y2]);
        }
    }

    static void testPrefixSum2() {
        int[][] s = new int[100][100];
        Scanner sc = new Scanner(new BufferedInputStream(System.in));
        int n, m, r;
        n = sc.nextInt();
        m = sc.nextInt();
        r = sc.nextInt();
        int[][] a = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                a[i][j] = sc.nextInt();
            }
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                s[i][j] = s[i - 1][j] + s[i][j - 1] - s[i - 1][j - 1] + a[i][j];
            }
        }
        int x1, y1, x2, y2;
        while (r-- > 0) {
            x1 = sc.nextInt();
            y1 = sc.nextInt();
            x2 = sc.nextInt();
            y2 = sc.nextInt();
            System.out.println(s[x2][y2] + s[x1 - 1][y1 - 1] - s[x2][y1 - 1] - s[x1 - 1][y2]);
        }
    }

    private static void testSort() {
        int[] a = new int[]{10, 5, 7, 9, -1, 3, 2, 4, 0};
        mergeSort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
        int[] a1 = new int[]{10, 5, 7, 9, -1, 3, 2, 4, 0};
        quickSort(a1, 0, a1.length - 1);
        System.out.println(Arrays.toString(a1));
    }


    private static void testDiff() {
        int[] d = new int[10010];
        int n, q, l, r, c;
        Scanner sc = new Scanner(new BufferedInputStream(System.in));
        n = sc.nextInt();
        q = sc.nextInt();
        int[] a = new int[n + 1];
        for (int i = 1; i <= n; i++) a[i] = sc.nextInt();
        for (int i = 0; i < n; i++) d[i + 1] = a[i + 1] - a[i];
        while (q-- > 0) {
            l = sc.nextInt();
            r = sc.nextInt();
            c = sc.nextInt();
            d[l + 1] += c;
            d[r] -= c;
        }
        int count = 0;
        for (int i = 1; i <= n; i++) {
            count += d[i];
            System.out.println(count + " ");
        }
    }

    static void testSqrt() {
        System.out.println(sqrt(3));
    }

    public static void main(String[] args) {
        testSort();
        testSqrt();
//        testPrefixSum();
        testDiff();
    }

}
