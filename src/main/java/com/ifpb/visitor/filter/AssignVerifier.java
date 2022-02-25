package com.ifpb.visitor.filter;

import java.util.HashMap;

public class AssignVerifier {

    private HashMap primitive;
    private String superClass;

    public AssignVerifier() {
        populatePrimitives();
    }

    public AssignVerifier(String superClass) {
        this.superClass = superClass;
        populatePrimitives();
    }

    public boolean isAssignable(String className) {
        if (className.contains("[]"))
            return isAssignable(className.replaceAll("\\[]", ""));

        if (primitive.containsKey(className))
            return false;

        try {
            Class superClass = Class.forName(this.superClass);
            Class classVerified = Class.forName(className);
            return superClass.isAssignableFrom(classVerified);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void populatePrimitives() {
        primitive = new HashMap<String, Object>();
        primitive.put("int", int.class);
        primitive.put("long", long.class);
        primitive.put("double", double.class);
        primitive.put("char", char.class);
        primitive.put("short", short.class);
        primitive.put("byte", byte.class);
        primitive.put("boolean", boolean.class);
        primitive.put("void", void.class);
    }
}
