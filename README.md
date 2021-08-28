# enclose
Ferramenta para avaliar violações de Lei de Demeter.
[versão do JDK: 11]

```
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
```

```
// AFTER
// target class
class A {
  private List<A> elements;
  
  public List<A> getElements() {
    return this.elements;
  }
  
  public boolean newMethod(A newParameter1) {
    return this.getElements().add(newParameter1);
  }
}
  
// client class
class C {
  private A a;
  public void m() {
    a.newMethod(new A());
  }
}
```
