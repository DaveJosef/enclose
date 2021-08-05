class Call {
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
        return this.call.substring(1, call.length() -1 ).split(", ")[1];
    }
    
    public String collection() {
        return this.call.substring(1, call.length() -1 ).split(", ")[2];
    }
    
    public String clientClass() {
        return this.call.substring(1, call.length() -1 ).split(", ")[3];
    }
    
    public String clientMethod() {
        return this.call.substring(1, call.length() -1 ).split(", ")[4];
    }
    
    public String collectionMethod() {
        return this.call.substring(1, call.length() -1 ).split(", ")[5];
    }
}
