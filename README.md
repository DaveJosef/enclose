# enclose
Ferramenta para avaliar violações de Lei de Demeter.

// BEFORE
// target class
class A {
  private List<A> elements;
  public List<A> getElements() {
    return this.elements;
  }
}
  
// client class
class C {
  private A a;
  public void m() {
    a.getElements().add(new A());
  }
}

// AFTER
// target class
class A {
  private List<A> elements;
  
  public List<A> getElements() {
    return this.elements;
  }
  
  public boolean yourNewMethod(A yourNewParameter1) {
    return this.getElements().add(yourNewParameter1);
  }
}
  
// client class
class C {
  private A a;
  public void m() {
    a.yourNewMethod(new A());
  }
}
