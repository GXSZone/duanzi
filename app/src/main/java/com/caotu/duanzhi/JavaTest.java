package com.caotu.duanzhi;

import androidx.core.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JavaTest {
    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        for (int i = 0; i < 10; i++) {
            map.put(i + "", i);
        }
        Pair<String,String> pair=new Pair<>("111",null);
        System.out.println(pair.toString());

        System.out.println(entries.size());
    }
}
