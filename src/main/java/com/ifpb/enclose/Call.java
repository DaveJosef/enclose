package com.ifpb.enclose;

public class Call {
    private final String call;

	public Call() {
		this.call = "<A, getElements[], java.util.List<A>, C, m[], add[A]>";
	}

	public Call(String call) {
		this.call = call;
	}
    
    public String trgtClass() {
        return this.call.substring(1, call.length() -1 ).split(", ")[0];
    }
    
    public String trgtMethod() {
		String callElement = this.call.substring(1, call.length() -1 ).split(", ")[1];
        return callElement.substring(0, callElement.length() -2);
    }
    
    public String collection() {
		String callElement = this.call.substring(1, call.length() -1 ).split(", ")[2];
        return callElement.substring(0, callElement.length() -3);
    }
    
    public String clientClass() {
        return this.call.substring(1, call.length() -1 ).split(", ")[3];
    }
    
    public String clientMethod() {
		String callElement = this.call.substring(1, call.length() -1 ).split(", ")[4];
        return callElement.substring(0, callElement.length() -2);
    }
    
    public String collectionMethod() {
		String callElement = this.call.substring(1, call.length() -1 ).split(", ")[5];
        return callElement.substring(0, callElement.length() -3);
    }
}
