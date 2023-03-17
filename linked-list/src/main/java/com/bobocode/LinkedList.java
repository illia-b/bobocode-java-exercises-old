package com.bobocode;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link LinkedList} is a list implementation that is based on singly linked generic nodes. A node is implemented as
 * inner static class {@link Node<T>}. In order to keep track on nodes, {@link LinkedList} keeps a reference to a head node.
 *
 * @param <T> generic type parameter
 */
public class LinkedList<T> implements List<T>, Iterable<T> {

    public static final String INDEX_INCORRECT_MSG = "Index is incorrect";

    static class Node<T> {
        T value;
        Node<T> next = null;

        public Node(T value) {
            this.value = value;
        }
    }



    private int size;
    private long version = Long.MIN_VALUE;
    private final Node<T> root = new Node<>(null);

    public Iterator<T> iterator() {
        return new Iterator<>() {

            private final long createdAt = version;
            private Node<T> cursor = root;
            @Override
            public boolean hasNext() {
                checkNotModified();
                return cursor.next != null;
            }

            @Override
            public T next() {
                checkNotModified();
                if (cursor.next == null) {
                    throw new NoSuchElementException();
                }
                cursor = cursor.next;
                return cursor.value;
            }

            private void checkNotModified() {
                if (createdAt != version) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }

    public Stream<T> getStream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size, 0), false);
    }

    /**
     * This method creates a list of provided elements
     *
     * @param elements elements to add
     * @param <T>      generic type
     * @return a new list of elements the were passed as method parameters
     */
    @SafeVarargs
    public static <T> List<T> of(@Nonnull T... elements) {
        var l = new LinkedList<T>();
        Arrays.stream(elements).forEach(l::add);
        return l;
    }

    /**
     * Adds an element to the end of the list
     *
     * @param element element to add
     */
    @Override
    public void add(@Nonnull T element) {
        add(size, element);
    }

    /**
     * Adds a new element to the specific position in the list. In case provided index in out of the list bounds it
     * throws {@link IndexOutOfBoundsException}
     *
     * @param index   an index of new element
     * @param element element to add
     */
    @Override
    public void add(int index, @Nonnull T element) {
        var toTheEnd = index == size;
        if (!(hasElement(index) || toTheEnd))
            throw new IndexOutOfBoundsException(INDEX_INCORRECT_MSG);

        insertAfter(getNode(index - 1), element);
        version++;
        size++;
    }

    /**
     * Changes the value of an list element at specific position. In case provided index in out of the list bounds it
     * throws {@link IndexOutOfBoundsException}
     *
     * @param index   an position of element to change
     * @param element a new element value
     */
    @Override
    public void set(int index, @Nonnull T element) {
        if (!hasElement(index)) {
            throw new IndexOutOfBoundsException(INDEX_INCORRECT_MSG);
        }
        getNode(index).value = element;
    }

    /**
     * Retrieves an elements by its position index. In case provided index in out of the list bounds it
     * throws {@link IndexOutOfBoundsException}
     *
     * @param index element index
     * @return an element value
     */
    @Override
    public T get(int index) {
        if (!hasElement(index)) {
            throw new IndexOutOfBoundsException(INDEX_INCORRECT_MSG);
        }
        return getNode(index).value;
    }

    /**
     * Removes an elements by its position index. In case provided index in out of the list bounds it
     * throws {@link IndexOutOfBoundsException}
     *
     * @param index element index
     */
    @Override
    public void remove(int index) {
        if (!hasElement(index)) {
            throw new IndexOutOfBoundsException(INDEX_INCORRECT_MSG);
        }
        removeAfter(getNode(index - 1));
        version++;
        size--;
    }


    /**
     * Checks if a specific exists in he list
     *
     * @return {@code true} if element exist, {@code false} otherwise
     */
    @Override
    public boolean contains(T element) {
        return getStream().anyMatch(element::equals);
    }

    /**
     * Checks if a list is empty
     *
     * @return {@code true} if list is empty, {@code false} otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the list
     *
     * @return number of elements
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all list elements
     */
    @Override
    public void clear() {
        root.next = null;
        size = 0;
    }

    private Node<T> getNode(int index) {
        var i = -1;
        var node = root;
        while (i < index) {
            node = node.next;
            i++;
        }
        return node;
    }

    private void insertAfter(Node<T> node, T value) {
        var nextnext = node.next;
        node.next = new Node<>(value);
        node.next.next = nextnext;
    }

    private void removeAfter(Node<T> node) {
        node.next = node.next.next;
    }

    private boolean hasElement(int index) {
        boolean nonNegative = index >= 0;
        boolean notOutOfSize = index < size;
        return nonNegative && notOutOfSize;
    }

}
