package com.ifpb.enclose;

import java.util.List;

public class CallList {
    private List<Call> list;

    public CallList() {
        this.list.add(new Call());
        this.list.add(new Call("<A, getElements[], java.util.List<A>, C, m[], set[A]>"));
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
}
