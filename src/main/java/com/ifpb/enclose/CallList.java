package com.ifpb.enclose;

import java.util.ArrayList;
import java.util.List;

public class CallList {
    private List<Call> list;

    public CallList() {
        this.list = new ArrayList<Call>();
        this.list.add(new Call());
        this.list.add(new Call("<java.utils.List<A>, set[int, A], boolean, C, m[], void, null>"));
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
