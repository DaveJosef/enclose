# enclose
Ferramenta para avaliar violações de Lei de Demeter.

package com.ifpb.examples;

// Client Class
class C{
    private A a;
    public void m(){
        a.getElements().add(new A());
        String arroz = true ? "" : "oiu";
        a.yourNewMethod(new A());
    }
}

package com.ifpb.examples;

// Client Class
class C{
    private A a;
    public void m(){
        a.yourNewMethod(new A());
        String arroz = true ? "" : "oiu";
        a.yourNewMethod(new A());
        }
}