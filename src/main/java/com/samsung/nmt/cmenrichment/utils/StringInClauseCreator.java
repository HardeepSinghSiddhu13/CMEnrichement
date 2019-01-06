package com.samsung.nmt.cmenrichment.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StringInClauseCreator {

    public static final boolean ALLOW_DUPLICATE = true;

    private StringBuilder stringBuilder = null;
    private boolean isFirstElementAdded = false;
    private static final String SINGLE_QUOTE = "'";
    private static final String SEP = ",";
    private int i = 0;
    Set<String> ele;
    private boolean allowDuplicate;

    public StringInClauseCreator() {
        allowDuplicate = false;
    }

    public StringInClauseCreator(boolean allowDuplicate) {
        allowDuplicate = true;
    }

    public void add(String param) {
        if (allowDuplicate) {
            addDuplicate(param);
        } else {
            addUnique(param);
        }
    }

    private void addUnique(String param) {
        if (isFirstElementAdded == false) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(SINGLE_QUOTE).append(param).append(SINGLE_QUOTE);
            ele = new HashSet<>();
            ele.add(param);
            isFirstElementAdded = true;
            i++;
        } else if (ele.contains(param) == false) {
            stringBuilder.append(SEP).append(SINGLE_QUOTE).append(param).append(SINGLE_QUOTE);
            i++;
        }
        ele.add(param);
    }

    private void addDuplicate(String param) {
        if (isFirstElementAdded == false) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(SINGLE_QUOTE).append(param).append(SINGLE_QUOTE);
            isFirstElementAdded = true;
        } else {
            stringBuilder.append(SEP).append(SINGLE_QUOTE).append(param).append(SINGLE_QUOTE);
        }
        i++;
    }

    public int length() {
        return i;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        StringInClauseCreator inClauseCreator = new StringInClauseCreator();
        inClauseCreator.add("a");
        inClauseCreator.add("b");
        System.out.println(inClauseCreator.toString());

        List<String> sts = new LinkedList<>();
        sts.add("abc");
        System.out.println(sts.get(0));
        ;
    }

}
