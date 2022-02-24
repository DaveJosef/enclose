package com.ifpb.tests;

import com.ifpb.calls.Call;
import com.ifpb.calls.CallList;
import com.ifpb.calls.CallMethodElement;
import com.ifpb.enclose.RefactorIntention;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.ArrayList;
import java.util.Arrays;

public class PluginTests {
    public static void testsCallsInstantiation() {
        System.out.println("Testando Call:");
        CallMethodElement el1 = new CallMethodElement("com.ifpb.C", Arrays.asList("com.ifpb.A", "com.ifpb.C"), "m");
        CallMethodElement el2 = new CallMethodElement("com.ifpb.A", Arrays.asList("com.ifpb.C"), "m1");
        CallMethodElement el3 = new CallMethodElement("boolean", Arrays.asList("com.ifpb.C"), "add");

        Call c = new Call("com.igpb.A", el1, "com.ifpb.C", el2, el3);
        System.out.println(c); // Must print <com.igpb.A, m[com.ifpb.A, com.ifpb.C], com.ifpb.C, com.ifpb.C, m1[com.ifpb.C], com.ifpb.A, add[com.ifpb.C]>

        System.out.println(new Call()); // Must print <null, null[null], null, null, null[null], null, null[null]>

        System.out.println(new Call().from("<com.ifpb.A, getElements[], java.utils.List<A>, C, m[], void, set[int, com.ifpb.A]>"));
    }

    public static void testsCallsListInstantiation() {
        System.out.println("Testando callList:");
        System.out.println(new CallList());
    }

    public static void testsCallListContains() {

        CallMethodElement el1 = new CallMethodElement("com.ifpb.C", Arrays.asList("com.ifpb.A", "com.ifpb.C"), "m");
        CallMethodElement el2 = new CallMethodElement("com.ifpb.A", Arrays.asList("com.ifpb.C"), "m1");
        CallMethodElement el3 = new CallMethodElement("boolean", Arrays.asList("com.ifpb.C"), "add");
        Call call = new Call("com.igpb.A", el1, "com.ifpb.C", el2, el3);

        CallList callList = new CallList(Arrays.asList(call, call));

        System.out.println("Testando se a calllist contém a call:");
        System.out.println(callList.contains(call));
        System.out.println("CallList:");
        System.out.println(callList);
        System.out.println("Call:");
        System.out.println(call);
    }

}
