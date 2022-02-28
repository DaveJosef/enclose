package com.ifpb.visitor.filter;

import com.ifpb.enclose.controllers.calls.Call;

import java.util.function.Predicate;

public class FilterClass implements Predicate<Call> {

    private AssignVerifier v;

    public FilterClass(String type) {
        v = new AssignVerifier(type);
    }

    @Override
    public boolean test(Call call) {
        if (call.getTargetMethod() == null)
            return false;

        if (call.getTargetMethod().getReturnType() == null || !call.getTargetMethod().getReturnType().contains("java.util."))
            return false;

        return v.isAssignable(call.getTargetMethod().getReturnType().split("<")[0]);
    }
}
