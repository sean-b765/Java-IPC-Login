package client.player.application.algorithms;

import java.util.Comparator;

public class QuickSort<T extends Comparable<T>> {

    private Comparator comparator;
    private boolean ascending;
    // true = ASC | false = DESC

    // Default constructor
    public QuickSort() { }

    // Overloaded constructor, sets the Comparator and ASC/DESC
    public QuickSort(Comparator c, boolean ascending) {
        setComparator(c);
        setAscending(ascending);
    }


    // Recursive QuickSort function
    // Parameters:  array
    //              lowest index    int
    //              num elements    int
    // usage:
    // sort(array, 0, array.length - 1)
    void sort(T array[], int low, int n) {
        if (low < n) {
            // get the partition index using partition() method
            int partIndex = partition(array, low, n);

            sort(array, low, partIndex - 1);
            sort(array, partIndex + 1, n);
        }
    }

    private int partition(T array[], int low, int n) {
        T pivot = array[n];
        int i = (low - 1);

        for (int j = low; j < n; j++) {
            int cmp = comparator.compare(array[j], pivot);
            // switch the comparator if descending
            if (!ascending)
                cmp = -cmp;

            if (cmp <= 0) {
                i++;
                T swap = array[i];
                array[i] = array[j];
                array[j] = swap;
            }
        }

        T swap = array[i + 1];
        array[i + 1] = array[n];
        array[n] = swap;

        return i + 1;
    }

    public void sort(T array[], Comparator comparator, boolean ascending) {
        setAscending(ascending);
        setComparator(comparator);
        sort(array, 0, array.length - 1);
    }

    void setComparator(Comparator c) {
        this.comparator = c;
    }

    void setAscending(boolean asc) {
        ascending = asc;
    }

}
