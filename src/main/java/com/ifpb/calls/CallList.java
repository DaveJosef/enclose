package com.ifpb.calls;

import java.util.ArrayList;
import java.util.List;

public class CallList {
    private List<Call> list = new ArrayList<>();

    public CallList() {
        this.list = new ArrayList<Call>();
    }

    public CallList(Call c) {
        this.list.add(c);
    }

    public CallList(List<Call> l) {
        this.list = l;
    }

    public List<Call> calls() {
        return list;
    }

    public boolean contains(Call call) {
        return list.contains(call);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        list.forEach(call -> builder.append(call).append("\n"));

        return builder.toString();
    }

}
