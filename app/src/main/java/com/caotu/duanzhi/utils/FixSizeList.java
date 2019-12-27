package com.caotu.duanzhi.utils;

import java.util.LinkedList;

/**
 * 固定集合长度(集合里内容元素)的集合
 *
 * @param <E>
 */
public class FixSizeList<E> extends LinkedList<E> {
    private int capacity;

    public FixSizeList(int size) {
        capacity = size;
    }

    @Override
    public boolean add(E e) {
        if (size() + 1 > capacity) {
            removeFirst();
        }
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        if (size() > capacity) {
            removeFirst();
        }
    }
}
