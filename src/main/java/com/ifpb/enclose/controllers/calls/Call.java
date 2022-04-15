package com.ifpb.enclose.controllers.calls;

import java.util.Arrays;
import java.util.Objects;

public class Call {
    String targetClass = null;
    String clientClass = null;
    CallMethodElement clientMethod;
    CallMethodElement targetMethod;
    CallMethodElement collectionMethod;

    public Call() {
        this.clientMethod = new CallMethodElement();
        this.targetMethod = new CallMethodElement();
        this.collectionMethod = new CallMethodElement();
    }

    public Call(String targetClass, CallMethodElement targetMethod, String clientClass, CallMethodElement clientMethod, CallMethodElement collectionMethod) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.clientClass = clientClass;
        this.clientMethod = clientMethod;
        this.collectionMethod = collectionMethod;
    }

    public Call from(String line) {
        if (line.isEmpty()) return from("<com.ifpb.A, getElements[], java.utils.List<A>, com.ifpb.C, m[], void, add[A]>");

        String[] pieces = line.substring(1, line.length() - 1).split(",");
        Arrays.asList(pieces).forEach(piece -> piece = piece.trim());

        this.targetClass = pieces[0];

        if (!(pieces.length > 1)) return this;
        this.targetMethod = new CallMethodElement(pieces[1]);

        if (!(pieces.length > 2)) return this;
        this.targetMethod.setReturnType(pieces[2]);

        if (!(pieces.length > 3)) return this;
        this.clientClass = pieces[3];

        if (!(pieces.length > 4)) return this;
        this.clientMethod = new CallMethodElement(pieces[4]);

        if (!(pieces.length > 5)) return this;
        this.clientMethod.setReturnType(pieces[5]);

        if (!(pieces.length > 6)) return this;
        this.collectionMethod = new CallMethodElement(pieces[6]);

        return this;
    }

    public String getClientClass() {
        return clientClass;
    }

    public CallMethodElement getCollectionMethod() {
        return collectionMethod;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public CallMethodElement getClientMethod() {
        return clientMethod;
    }

    public CallMethodElement getTargetMethod() {
        return targetMethod;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public void setClientClass(String clientClass) {
        this.clientClass = clientClass;
    }

    public void setClientMethod(CallMethodElement clientMethod) {
        this.clientMethod = clientMethod;
    }

    public void setTargetMethod(CallMethodElement targetMethod) {
        this.targetMethod = targetMethod;
    }

    public void setCollectionMethod(CallMethodElement collectionMethod) {
        this.collectionMethod = collectionMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return Objects.equals(targetClass, call.targetClass) &&
                Objects.equals(clientClass, call.clientClass) &&
                Objects.equals(clientMethod, call.clientMethod) &&
                Objects.equals(targetMethod, call.targetMethod) &&
                Objects.equals(collectionMethod, call.collectionMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetClass, clientClass, clientMethod, targetMethod, collectionMethod);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("<")
                .append(targetClass).append("; ")
                .append(targetMethod).append("; ")
                .append(targetMethod.getReturnType()).append("; ")
                .append(clientClass).append("; ")
                .append(clientMethod).append("; ")
                .append(clientMethod.getReturnType()).append("; ")
                .append(collectionMethod)
                .append(">")
                .toString();
    }
}
