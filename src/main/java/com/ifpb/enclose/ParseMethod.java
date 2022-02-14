package com.ifpb.enclose;

import com.ifpb.visitor.MethodVisitor;

public interface ParseMethod extends ParseStrategy {
    public ParseMethod visitor(MethodVisitor visitor);
}
