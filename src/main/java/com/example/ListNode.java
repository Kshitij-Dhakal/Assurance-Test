package com.example;

public class ListNode<T> {
    public ListNode(T value) {
        this.value = value;
    }

    public T value;
    public ListNode<T> next;
}
