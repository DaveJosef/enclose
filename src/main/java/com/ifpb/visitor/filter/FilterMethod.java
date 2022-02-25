package com.ifpb.visitor.filter;

import com.ifpb.calls.Call;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterMethod implements Predicate<Call> {

    private List<String> names;

    public FilterMethod() {
        this.loadNames();
    }

    private void loadNames() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("methods/multatingmethods.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        this.names = reader.lines()
                .map(s -> Arrays.asList(s.replace(" ", "").split(",")))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public boolean test(Call call) {
        return names.contains(call.getCollectionMethod().getMethodName());
    }
}
