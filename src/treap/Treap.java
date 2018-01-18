package treap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A basic implementation of a Treap: a probabilistically balanced binary search
 * tree.
 * @param <E>
 */
public class Treap<E> extends AbstractSet<E> {

    // Possible improvements:
    // - implement iteration in linear time by letting the right child pointer point to your successor if you have no right child
    // - implement sublists
    // - implement removal in the iterator
    private int size = 0;
    private TreapNode<E> root = null;
    private Comparator<? super E> comparator = null;

    /**
     * Constructs a new, empty treap, sorted according to the natural ordering
     * of its elements.
     */
    public Treap() {
    }

    /**
     * Constructs a new treap containing the elements in the specified
     * collection, sorted according to the natural ordering of its elements.
     *
     * @param c
     */
    public Treap(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * Constructs a new, empty treap, sorted according to the specified
     * comparator.
     *
     * @param comparator
     */
    public Treap(Comparator<? super E> comparator) {
        this();
        this.comparator = comparator;
    }

    /**
     * Constructs a new treap containing the same elements and using the same
     * ordering as the specified sorted set.
     *
     * @param s
     */
    public Treap(SortedSet<E> s) {
        this(s.comparator());
        addAll(s);
    }

    @Override
    public boolean add(E e) {
        // Find the place this value should be inserted
        TreapNode<E> parent = null;
        TreapNode<E> node = root;
        int comparison = 0;

        while (node != null) {
            parent = node;

            comparison = compare(e, node.key);

            if (comparison == 0) {
                // The treap already contains the specified key
                return false;
            } else if (comparison < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        // Create a new node with the specified key and a random priority
        TreapNode<E> newNode = new TreapNode<E>(e, ThreadLocalRandom.current().nextFloat());

        // Insert this node into the treap
        if (parent == null) {
            root = newNode;
        } else {
            // The last comparison compared the parent's key to the specified value,
            // so we can reuse this to see if we should insert it as the left or right child.
            if (comparison < 0) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }

            newNode.parent = parent;
        }

        // Fix the heap property by performing rotations
        while (newNode.parent != null && newNode.priority < newNode.parent.priority) {
            if (newNode == newNode.parent.left) {
                rotateRight(newNode.parent);
            } else {
                assert newNode == newNode.parent.right;
                rotateLeft(newNode.parent);
            }
        }

        size++;

        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (comparator != null) {
            return containsWithComparator(o);
        }

        if (o == null) {
            throw new NullPointerException();
        }

        @SuppressWarnings("unchecked")
        Comparable<? super E> key = (Comparable<? super E>) o;

        TreapNode<E> node = root;
        int comparison;

        while (node != null) {
            comparison = key.compareTo(node.key);

            if (comparison < 0) {
                node = node.left;
            } else if (comparison > 0) {
                node = node.right;
            } else {
                return true;
            }
        }

        return false;
    }

    private boolean containsWithComparator(Object o) {
        @SuppressWarnings("unchecked")
        E key = (E) o;

        TreapNode<E> node = root;
        int comparison;

        while (node != null) {
            comparison = comparator.compare(key, node.key);

            if (comparison < 0) {
                node = node.left;
            } else if (comparison > 0) {
                node = node.right;
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean remove(Object o) {
        // Find the node corresponding to the given key
        @SuppressWarnings("unchecked")
        E key = (E) o;

        TreapNode<E> node = root;

        while (node != null) {
            int comparison = compare(key, node.key);

            if (comparison == 0) {
                break;
            } else if (comparison < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (node == null) {
            return false;
        }

        remove(node);

        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int nextIndex = 0;
            private TreapNode<E> currentNode = null;
            private TreapNode<E> nextNode = firstNode();

            @Override
            public boolean hasNext() {
                return nextIndex < size();
            }

            @Override
            public E next() {
                currentNode = nextNode;
                nextNode = higherNode(currentNode.key);
                nextIndex++;
                return currentNode.key;
            }

            @Override
            public void remove() {
                /* TODO: implement lowerNode
                TreapNode<E> previousNode = lowerNode(currentNode);
                Treap.this.remove(currentNode);
                currentNode = previousNode;
                nextIndex--;
                 */
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    public E lower(E e) {
        TreapNode<E> node = root;
        E lower = null;

        while (node != null) {
            if (compare(node.key, e) < 0) {
                if (lower == null || compare(node.key, lower) > 0) {
                    lower = node.key;
                }

                node = node.right;
            } else {
                node = node.left;
            }
        }

        return lower;
    }

    public E higher(E e) {
        TreapNode<E> node = root;
        E higher = null;

        while (node != null) {
            if (compare(node.key, e) > 0) {
                if (higher == null || compare(node.key, higher) < 0) {
                    higher = node.key;
                }

                node = node.left;
            } else {
                node = node.right;
            }
        }

        return higher;
    }

    public E floor(E e) {
        TreapNode<E> node = root;
        E lower = null;

        while (node != null) {
            int comparison = compare(node.key, e);

            if (comparison == 0) {
                return e;
            } else if (comparison < 0) {
                if (lower == null || compare(node.key, lower) > 0) {
                    lower = node.key;
                }

                node = node.right;
            } else {
                node = node.left;
            }
        }

        return lower;
    }

    public E ceiling(E e) {
        TreapNode<E> node = root;
        E higher = null;

        while (node != null) {
            int comparison = compare(node.key, e);

            if (comparison == 0) {
                return e;
            } else if (comparison > 0) {
                if (higher == null || compare(node.key, higher) < 0) {
                    higher = node.key;
                }

                node = node.left;
            } else {
                node = node.right;
            }
        }

        return higher;
    }

    public E pollFirst() {
        if (root == null) {
            return null;
        } else {
            TreapNode<E> first = root;

            while (first.left != null) {
                first = first.left;
            }

            remove(first);

            return first.key;
        }
    }

    public E pollLast() {
        if (root == null) {
            return null;
        } else {
            TreapNode<E> last = root;

            while (last.right != null) {
                last = last.right;
            }

            remove(last);

            return last.key;
        }
    }

    public Comparator<? super E> comparator() {
        return comparator;
    }

    public E first() {
        if (root == null) {
            throw new NoSuchElementException("First element was queried, while the treap was empty.");
        } else {
            TreapNode<E> node = root;

            while (node.left != null) {
                node = node.left;
            }

            return node.key;
        }
    }

    public E last() {
        if (root == null) {
            throw new NoSuchElementException("Last element was queried, while the treap was empty.");
        } else {
            TreapNode<E> node = root;

            while (node.right != null) {
                node = node.right;
            }

            return node.key;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else if (a != null) {
            return ((Comparable<E>) a).compareTo(b);
        } else if (b != null) {
            return -((Comparable<E>) b).compareTo(a);
        } else {
            // Both a and b are null, so they are equal
            return 0;
        }
    }

    private void remove(TreapNode<E> node) {
        // Perform rotations until this node is a leaf
        while (node.left != null || node.right != null) {
            rotateDown(node);
        }

        // Actually remove the node
        if (node == root) {
            root = null;
        } else {
            if (node == node.parent.left) {
                node.parent.left = null;
            } else {
                assert node == node.parent.right;
                node.parent.right = null;
            }
        }

        size--;
    }

    /**
     * Rotate the subtree rooted at the specified node to the right, making the
     * left child of the specified node the new root.
     *
     * @param node
     */
    private void rotateRight(TreapNode<E> node) {
        TreapNode<E> child = node.left;

        if (child == null) {
            throw new InternalError("rotateRight called on node without left child.");
        } else {
            // Store temporary values
            TreapNode<E> parent = node.parent;
            TreapNode<E> middleSubtreap = child.right;

            // Switch the child pointers
            if (parent == null) {
                root = child;
            } else {
                if (node == parent.left) {
                    parent.left = child;
                } else {
                    assert node == parent.right;
                    parent.right = child;
                }
            }

            node.left = middleSubtreap;
            child.right = node;

            // Fix the parent pointers
            node.parent = child;
            child.parent = parent;

            if (middleSubtreap != null) {
                middleSubtreap.parent = node;
            }
        }
    }

    /**
     * Rotate the subtree rooted at the specified node to the left, making the
     * right child of the specified node the new root.
     *
     * @param node
     */
    private void rotateLeft(TreapNode<E> node) {
        TreapNode<E> child = node.right;

        if (child == null) {
            throw new InternalError("rotateLeft called on node without right child.");
        } else {
            // Store temporary values
            TreapNode<E> parent = node.parent;
            TreapNode<E> middleSubtreap = child.left;

            // Switch the child pointers
            if (parent == null) {
                root = child;
            } else {
                if (node == parent.left) {
                    parent.left = child;
                } else {
                    assert node == parent.right;
                    parent.right = child;
                }
            }

            node.right = middleSubtreap;
            child.left = node;

            // Fix the parent pointers
            node.parent = child;
            child.parent = parent;

            if (middleSubtreap != null) {
                middleSubtreap.parent = node;
            }
        }
    }

    /**
     * Rotates the subtree rooted at the specified node to move this node down
     * the tree. Must not be called on a leaf.
     *
     * @param node
     */
    private void rotateDown(TreapNode<E> node) {
        if (node.left == null) {
            assert node.right != null;
            rotateLeft(node);
        } else if (node.right == null) {
            rotateRight(node);
        } else if (node.left.priority < node.right.priority) {
            rotateRight(node);
        } else {
            rotateLeft(node);
        }
    }

    private TreapNode<E> firstNode() {
        if (root == null) {
            return null;
        } else {
            TreapNode<E> node = root;

            while (node.left != null) {
                node = node.left;
            }

            return node;
        }
    }

    private TreapNode<E> higherNode(E e) {
        TreapNode<E> node = root;
        TreapNode<E> higherNode = null;

        while (node != null) {
            if (compare(node.key, e) > 0) {
                if (higherNode == null || compare(node.key, higherNode.key) < 0) {
                    higherNode = node;
                }

                node = node.left;
            } else {
                node = node.right;
            }
        }

        return higherNode;
    }

    private class TreapNode<E> {

        E key;
        float priority;
        TreapNode<E> parent, left, right;

        public TreapNode(E key, float priority) {
            this.key = key;
            this.priority = priority;
            parent = null;
            left = null;
            right = null;
        }
    }
}
