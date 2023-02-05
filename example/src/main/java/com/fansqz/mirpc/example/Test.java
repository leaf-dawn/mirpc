package com.fansqz.mirpc.example;

public class Test {
    public static void main(String[] args) {
        System.out.println(new Solution().alternateDigitSum(521));
    }
}
class Solution {
    public int alternateDigitSum(int n) {
        boolean flag = true;
        int answer = 0;
        while (n > 0) {
            int a = n / 10;
            n = n % 10;
            if (flag) {
                answer += a;
            } else {
                answer -= a;
            }
            flag = !flag;
        }
        return answer;
    }
}