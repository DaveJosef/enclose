package com.ifpb.calls;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CallMethodElement {
    private String returnType;
    private List<String> params;
    private String methodName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallMethodElement that = (CallMethodElement) o;
        return Objects.equals(returnType, that.returnType) &&
                Objects.equals(params, that.params) &&
                Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnType, params, methodName);
    }

    public CallMethodElement(String returnType, List<String> params, String methodName) {
        this.returnType = returnType;
        this.params = params;
        this.methodName = methodName;
    }

    public CallMethodElement() {
        this(null, null, null);
    }

    public CallMethodElement(String targetMethodAsString) {
        String[] nameAndParams = targetMethodAsString.split("\\[");

        String methodName = nameAndParams[0];
        String methodParams = nameAndParams[1].substring(0, nameAndParams[1].length() - 1);
        List<String> methodParamsList = Arrays.asList(methodParams.split(","));
        methodParamsList.forEach(param -> param = param.trim());

        this.params = methodParamsList;
        this.methodName = methodName;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(methodName)
                .append(params == null ? "["+params+"]" : params)
                .toString();
    }
}
