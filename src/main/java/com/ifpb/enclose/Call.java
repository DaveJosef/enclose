package com.ifpb.enclose;

public class Call {
    private String[] callSubsections;
    private String call;
    private String collection;
    private String collectionMethod;
    private String collectionMethodReturnType;
    private String clientClass;
    private String clientMethod;
    private String clientMethodReturnType;
    private String nullIdentifier;
    private String[] clientMethodParametersTypes;

    public Call() {
        this.call = "<java.utils.List<A>, add[A], boolean, C, m[], void, null>";
        this.callSubsections = call.substring(1, call.length() - 1).split("\\[");
        this.collectionMethod = callSubsections[0].split(", ")[callSubsections[0].split(", ").length - 1];
        this.collection = callSubsections[0].split(", ")[callSubsections[0].split(", ").length - 2];
        this.clientMethod = callSubsections[1].split(", ")[callSubsections[1].split(", ").length - 1];
        this.clientClass = callSubsections[1].split(", ")[callSubsections[1].split(", ").length - 2];
        this.clientMethodReturnType = callSubsections[2].split(", ")[callSubsections[2].split(", ").length - 2];
	}

	public Call(String call) {
		this.call = call;
        this.callSubsections = call.substring(1, call.length() - 1).split("\\[");
        this.collectionMethod = callSubsections[0].split(", ")[callSubsections[0].split(", ").length - 1];
        this.collection = callSubsections[0].split(", ")[callSubsections[0].split(", ").length - 2];
        this.clientMethod = callSubsections[1].split(", ")[callSubsections[1].split(", ").length - 1];
        this.clientClass = callSubsections[1].split(", ")[callSubsections[1].split(", ").length - 2];
        this.clientMethodReturnType = callSubsections[2].split(", ")[callSubsections[2].split(", ").length - 2];
	}

	public Call(String collection, String collectionMethod, String collectionMethodReturnType, String clientClass, String clientMethod, String[] clientMethodParametersTypes, String clientMethodReturnType, String nullIdentifier) {
	    this.collection = collection;
	    this.collectionMethod = collectionMethod.split("\\[")[0];
	    this.collectionMethodReturnType = collectionMethodReturnType;
	    this.clientClass = clientClass;
	    this.clientMethod = clientMethod.split("\\[")[0];
	    this.clientMethodParametersTypes = clientMethodParametersTypes;
	    this.clientMethodReturnType = clientMethodReturnType;
	    this.nullIdentifier = nullIdentifier;
    }

    public String collection() {
		return this.collection;
    }
    
    public String clientClass() {
        return this.clientClass;
    }
    
    public String clientMethod() {
		return this.clientMethod;
    }
    
    public String collectionMethod() {
        return this.collectionMethod;
    }

    public String[] clientMethodParametersTypes() {
        return this.clientMethodParametersTypes;
    }

    public String clientMethodReturnType() {
        return clientMethodReturnType;
    }
}
