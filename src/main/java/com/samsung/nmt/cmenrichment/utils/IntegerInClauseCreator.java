package com.samsung.nmt.cmenrichment.utils;

import java.util.HashSet;
import java.util.Set;

public class IntegerInClauseCreator {

    private StringBuilder stringBuilder = null;
    private boolean isFirstElementAdded = false;
    private static final String SEP = ",";
    Set<Integer> ele;
    private int i = 0;

    public IntegerInClauseCreator() {

    }

    public void add(Integer param) {
        if (isFirstElementAdded == false) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(param);
            ele = new HashSet<>();
            ele.add(param);
            isFirstElementAdded = true;
            i++;
        } else if (ele.contains(param) == false) {
            stringBuilder.append(SEP).append(param);
            i++;
        }

        ele.add(param);

    }

    public int length() {
        return i;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        IntegerInClauseCreator inClauseCreator = new IntegerInClauseCreator();
        inClauseCreator.add(1);
        //inClauseCreator.add(2);
        System.out.println(inClauseCreator.toString());
    }

}
