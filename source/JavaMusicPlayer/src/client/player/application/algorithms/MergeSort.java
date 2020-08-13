package client.player.application.algorithms;

import java.util.Comparator;

public class MergeSort<T extends Comparable<T>> {

    private Comparator comparator;
    private boolean ascending;
    // true = ASC | false = DESC

    // Default constructor
    public MergeSort() { }

    // Overloaded constructor, sets the Comparator and ASC/DESC
    public MergeSort(Comparator c, boolean ascending) {
        setComparator(c);
        setAscending(ascending);
    }

    // Parameters:  lowest index    int
    //              midPoint index  int
    //              num indices     int
    void merge(T[] array, int low, int mid, int n) {
        // get sizes of the two sub arrays
        int leftSize = mid - low + 1;
        int rightSize = n - mid;

        // Create temporary sub-arrays
        T[] leftSubArray = (T[]) new Comparable[leftSize];
        T[] rightSubArray = (T[]) new Comparable[rightSize];

        // copy array into sub arrays
        for (int i = 0; i < leftSize; ++i)
            leftSubArray[i] = array[low + i];
        for (int j = 0; j < rightSize; ++j)
            rightSubArray[j] = array[mid + 1 + j];

        // merge the arrays
        int left = 0, right = 0;

        int current = low;
        while (left < leftSize && right < rightSize) {
            int cmp = comparator.compare(leftSubArray[left], rightSubArray[right]);

            if (!ascending)
                cmp = -cmp;

            if (cmp <= 0) {
                // i comes BEFORE j, or is equal
                array[current] = (T) leftSubArray[left];
                left++;
            } else {
                // i comes AFTER j
                array[current] = (T) rightSubArray[right];
                right++;
            }
            current++;
        }

        // copy remaining elements to L
        while (left < leftSize) {
            array[current++] = leftSubArray[left++];
        }

        while (right < rightSize) {
            try {
                array[current++] = rightSubArray[right++];
            } catch (Exception e) {
                System.out.println(e.getMessage() + "\ncurrent:" + current + " j:" + right);
            }
        }
    }


    // Parameters:  array implements Comparable
    //              lowest index    int
    //              num elements    int
    // usage:
    // sort(array, 0, array.length - 1)
    void sort(T array[], int low, int n) {
        if (comparator != null) {
            if (low < n) {
                int mid = (low + n) / 2;

                // sort first half
                sort(array, low, mid);
                // sort second half
                sort(array, mid + 1, n);

                // merge the sorted halves
                merge(array, low, mid, n);
            }
        } else {
            return;
        }
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
