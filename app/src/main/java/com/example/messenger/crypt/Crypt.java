package com.example.messenger.crypt;

import java.util.TreeMap;

public class Crypt {
    public String key;
    public static final int LENGTH = 6;

    public Crypt(String key) {
        this.key = key;
    }

    public TreeMap<String, String> generateKey(boolean isKeyFirst) {
        int n = 0, c;
        TreeMap<String, String> res = new TreeMap<>();
        String s;
        while (n < key.length()) {
            s = Integer.toBinaryString(n + 1);
            c = LENGTH - s.length();
            s = "";
            for (int i = 0; i < c; i++) {
                s += "0";
            }
            s += Integer.toBinaryString(n + 1);
            if (isKeyFirst) res.put(key.charAt(n) + "", s);
            else res.put(s, key.charAt(n) + "");
            n += 1;
        }
        return res;
    }

    public String encode(String s) {
        TreeMap<String, String> generatedKey = generateKey(true);
        StringBuilder res = new StringBuilder();
        String key;
        for (int i = 0; i < s.length(); i++) {
            key = s.charAt(i) + "";
            res.append(generatedKey.get(key));
        }
        return res.toString();
    }

    public String decode(String n) {
        TreeMap<String, String> generatedKey = generateKey(false);
        String res = "";
        if (n.length() % LENGTH != 0) {
            int l = LENGTH - (n.length() % LENGTH);
            for (int i = 0; i < l; i++) {
                res += "0";
            }
        }
        res += n;
        String s, finalRes = "";
        for (int i = 0; i < res.length(); i += LENGTH) {
            s = res.substring(i, i + LENGTH);
            finalRes += generatedKey.get(s);
        }
        return finalRes;
    }
}

