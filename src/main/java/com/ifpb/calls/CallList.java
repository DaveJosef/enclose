package com.ifpb.calls;

import com.ifpb.calls.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallList {
    private List<Call> list;

    public CallList() {
        this.list = new ArrayList<Call>();
/*
        CallMethodElement el1 = new CallMethodElement("java.utils.List<com.ifpb.A>", Arrays.asList(""), "getElements");
        CallMethodElement el2 = new CallMethodElement("void", Arrays.asList(""), "m");
        CallMethodElement el3 = new CallMethodElement("boolean", Arrays.asList("com.ifpb.A"), "add");

        Call c = new Call("com.ifpb.A", el1, "com.ifpb.C", el2, el3);
        this.list.add(c);

        el1 = new CallMethodElement("java.utils.List<com.ifpb.A>", Arrays.asList(""), "getElements");
        el2 = new CallMethodElement("void", Arrays.asList(""), "m");
        el3 = new CallMethodElement("boolean", Arrays.asList("int", "com.ifpb.A"), "set");

        c = new Call("com.ifpb.A", el1, "com.ifpb.C", el2, el3);
        this.list.add(c);
*/
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        list.forEach(call -> builder.append(call).append("\n"));

        return builder.toString();
    }

}
