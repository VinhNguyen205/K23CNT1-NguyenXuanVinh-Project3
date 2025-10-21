package com.devmaster.lesson02.tight_loosely_coupling;


@FunctionalInterface
public interface SortAlgorithm {
    void sort(int[] array);
}