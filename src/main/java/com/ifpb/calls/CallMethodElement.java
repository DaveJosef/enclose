package com.ifpb.calls;

import java.util.Arrays;
import java.util.List;

public class CallMethodElement {
    private String returnType;
    private List<String> params;
    private String methodName;

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
