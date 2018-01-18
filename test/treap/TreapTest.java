package treap;

import java.util.Random;
import java.util.TreeSet;
import java.util.Iterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TreapTest {

    private static final Random rand = new Random();
    private Treap<Integer> treap;
    private TreeSet<Integer> set;
    private final int RANGE = 100;
    private final int nTestRepetitions = 2 * RANGE;

    public TreapTest() {
    }

    @Before
    public void setUp() {
        treap = new Treap<Integer>();
        set = new TreeSet<Integer>();

        int n = 100;

        for (int i = 0; i < n; i++) {
            int val = rand.nextInt(RANGE);

            treap.add(val);
            set.add(val);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAdd() {
        System.out.println("add");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            boolean expResult = set.add(val);
            boolean result = treap.add(val);
            assertEquals(expResult, result);
            checkEquality(treap, set);
        }
    }

    @Test
    public void testContains() {
        System.out.println("contains");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            boolean expResult = set.contains(val);
            boolean result = treap.contains(val);
            assertEquals(expResult, result);
            checkEquality(treap, set);
        }
    }

    @Test
    public void testClear() {
        System.out.println("clear");

        treap.clear();
        set.clear();
        checkEquality(treap, set);
    }

    @Test
    public void testRemove() {
        System.out.println("remove");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            boolean expResult = set.remove(val);
            boolean result = treap.remove(val);
            assertEquals(expResult, result);
            checkEquality(treap, set);
        }
    }

    @Test
    public void testIterator() {
        System.out.println("iterator");

        Iterator<Integer> treapIt = treap.iterator();
        Iterator<Integer> setIt = set.iterator();

        while (treapIt.hasNext()) {
            assertEquals(treapIt.hasNext(), setIt.hasNext());
            assertEquals(treapIt.next(), setIt.next());
        }

        assertEquals(treapIt.hasNext(), setIt.hasNext());
    }

    @Test
    public void testSize() {
        System.out.println("size");

        int expResult = set.size();
        int result = treap.size();
        assertEquals(expResult, result);
    }

    @Test
    public void testLower() {
        System.out.println("lower");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            Integer expResult = set.lower(val);
            Integer result = treap.lower(val);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testFloor() {
        System.out.println("floor");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            Integer expResult = set.floor(val);
            Integer result = treap.floor(val);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testCeiling() {
        System.out.println("ceiling");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            Integer expResult = set.ceiling(val);
            Integer result = treap.ceiling(val);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testHigher() {
        System.out.println("higher");

        for (int i = 0; i < nTestRepetitions; i++) {
            int val = rand.nextInt(RANGE);

            Integer expResult = set.higher(val);
            Integer result = treap.higher(val);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testPollFirst() {
        System.out.println("pollFirst");

        for (int i = 0; i < treap.size(); i++) {
            Integer expResult = set.pollFirst();
            Integer result = treap.pollFirst();
            assertEquals(expResult, result);
            checkEquality(treap, set);
        }
    }

    @Test
    public void testPollLast() {
        System.out.println("pollLast");

        for (int i = 0; i < treap.size(); i++) {
            Integer expResult = set.pollLast();
            Integer result = treap.pollLast();
            assertEquals(expResult, result);
            checkEquality(treap, set);
        }
    }

    @Test
    public void testFirst() {
        System.out.println("first");

        int expResult = set.first();
        int result = treap.first();
        assertEquals(expResult, result);
    }

    @Test
    public void testLast() {
        System.out.println("last");

        int expResult = set.last();
        int result = treap.last();
        assertEquals(expResult, result);
    }

    @Test
    public void testPerformance() {
        System.out.println("performance: Treap vs TreeSet");

        int n = 100;
        double[] values = new double[n];

        for (int i= 0; i < n; i++) {
            values[i] = rand.nextDouble();
        }

        long start = System.nanoTime();
        Treap<Double> perfTreap = new Treap<Double>();

        for (int i= 0; i < n; i++) {
            perfTreap.add(values[i]);
        }
        long treapConstruction = System.nanoTime() - start;

        start = System.nanoTime();
        TreeSet<Double> perfSet = new TreeSet<Double>();

        for (int i= 0; i < n; i++) {
            perfSet.add(values[i]);
        }
        long setConstruction = System.nanoTime() - start;

        System.out.println(String.format("  Construction: %f vs %f", treapConstruction * 10e-9, setConstruction * 10e-9));

        start = System.nanoTime();
        for (int i= 0; i < n; i++) {
            perfTreap.contains(values[i]);
        }
        long treapContains = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i= 0; i < n; i++) {
            perfSet.contains(values[i]);
        }
        long setContains = System.nanoTime() - start;

        System.out.println(String.format("  Positive queries: %f vs %f", treapContains * 10e-9, setContains * 10e-9));

        double[] negativeQueryValues = new double[n];

        for (int i= 0; i < n; i++) {
            negativeQueryValues[i] = rand.nextDouble();
        }

        start = System.nanoTime();
        for (int i= 0; i < n; i++) {
            perfTreap.contains(negativeQueryValues[i]);
        }
        long treapContainsNeg = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i= 0; i < n; i++) {
            perfSet.contains(negativeQueryValues[i]);
        }
        long setContainsNeg = System.nanoTime() - start;

        System.out.println(String.format("  Negative queries: %f vs %f", treapContainsNeg * 10e-9, setContainsNeg * 10e-9));

        start = System.nanoTime();
        for (int i= 0; i < n; i++) {
            perfTreap.remove(values[i]);
        }
        long treapRemoval = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i= 0; i < n; i++) {
            perfSet.remove(values[i]);
        }
        long setRemoval = System.nanoTime() - start;

        System.out.println(String.format("  Removal: %f vs %f", treapRemoval * 10e-9, setRemoval * 10e-9));
    }

    private void checkEquality(Treap<Integer> treap, TreeSet<Integer> set) {
        assertEquals(treap.size(), set.size());

        if (!treap.containsAll(set)) {
            throw new AssertionError("Treap does not contain all elements contained in set.");
        }
    }
}
