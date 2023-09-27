package com.bobocode;

import javax.annotation.Nonnull;

/**
 * {@link LinkedQueue} implements FIFO {@link Queue}, using singly linked nodes. Nodes are stores in instances of nested
 * class Node. In order to perform operations {@link LinkedQueue#add(Object)} and {@link LinkedQueue#poll()}
 * in a constant time, it keeps to references to the head and tail of the queue.
 *
 * @param <T> a generic parameter
 */
public class LinkedQueue<T> implements Queue<T> {
    static class Node<T> {

        T value;
        Node<T> next = null;
        Node<T> prev = null;

        public Node(T value) {
            this.value = value;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Adds an element to the end of the queue.
     *
     * @param element the element to add
     */
    public void add(@Nonnull  T element) {

        var newNode = new Node<>(element);
        if (tail == null) {
            tail = newNode;
            head = newNode;
        } else {
            newNode.next = tail;
            tail.prev = newNode;
            tail = newNode;
        }
        size++;
    }


    /**
     * Retrieves and removes queue head.
     *
     * @return an element that was retrieved from the head or null if queue is empty
     */
    public T poll() {

        if (isEmpty()) {
            return null;
        }

        var temp = head.value;
        var oneLeft = head == tail;
        if (oneLeft) {
            head = null;
            tail = null;
        } else {
            head.prev.next = null;
            head = head.prev;
        }
        size--;

        return temp;
    }

    /**
     * Returns a size of the queue.
     *
     * @return an integer value that is a size of queue
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return {@code true} if the queue is empty, returns {@code false} if it's not
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
