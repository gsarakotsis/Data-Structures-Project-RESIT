package datastructures;
import java.util.*;


public class CustomHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;


    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }


    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }


    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }


    private int hash(K key) {
        return key == null ? 0 : Math.abs(key.hashCode() % capacity);
    }


    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        // Resize if load factor exceeded
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Entry<K, V> entry = buckets[index];

        // Check if key already exists (update value)
        while (entry != null) {
            if (entry.key.equals(key)) {
                V oldValue = entry.value;
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        // Add new entry at the beginning of the chain
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
    }


    public V get(K key) {
        if (key == null) return null;

        int index = hash(key);
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }


    public void remove(K key) {
        if (key == null) return;

        int index = hash(key);
        Entry<K, V> entry = buckets[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) {
                    // Removing first entry in chain
                    buckets[index] = entry.next;
                } else {
                    // Removing entry in middle/end of chain
                    prev.next = entry.next;
                }
                size--;
                return;
            }
            prev = entry;
            entry = entry.next;
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        int oldCapacity = capacity;

        capacity *= 2;
        buckets = new Entry[capacity];
        size = 0;

        // Rehash all entries
        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> entry = oldBuckets[i];
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }


    public boolean containsKey(K key) {
        return get(key) != null;
    }


    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Entry<K, V> entry : buckets) {
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        return keys;
    }


    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        for (Entry<K, V> entry : buckets) {
            while (entry != null) {
                values.add(entry.value);
                entry = entry.next;
            }
        }
        return values;
    }


    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }


    public int getCapacity() {
        return capacity;
    }


    public double getLoadFactor() {
        return (double) size / capacity;
    }


    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = new Entry[capacity];
        size = 0;
    }


    public String getStatistics() {
        int maxChainLength = 0;
        int nonEmptyBuckets = 0;
        int totalChainLength = 0;

        for (Entry<K, V> entry : buckets) {
            if (entry != null) {
                nonEmptyBuckets++;
                int chainLength = 0;
                while (entry != null) {
                    chainLength++;
                    entry = entry.next;
                }
                maxChainLength = Math.max(maxChainLength, chainLength);
                totalChainLength += chainLength;
            }
        }

        double avgChainLength = nonEmptyBuckets > 0 ? (double) totalChainLength / nonEmptyBuckets : 0;

        return String.format("Hash Table Statistics:\n" +
                        "  Size: %d\n" +
                        "  Capacity: %d\n" +
                        "  Load Factor: %.3f\n" +
                        "  Non-empty Buckets: %d\n" +
                        "  Max Chain Length: %d\n" +
                        "  Avg Chain Length: %.2f",
                size, capacity, getLoadFactor(),
                nonEmptyBuckets, maxChainLength, avgChainLength);
    }
}