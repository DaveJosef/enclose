package com.ifpb.experiment.actions;

import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.experiment.Directories;
import com.ifpb.experiment.SetupDirectories;
import com.ifpb.visitor.MethodCallVisitor;
import com.ifpb.visitor.filter.FilterClass;
import com.ifpb.visitor.filter.FilterMethod;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import net.sf.cglib.asm.$TypePath;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Collectors;

public class DefaultDataExtractor implements DataExtractor {
    private final PsiManager myPsiManager;
    private final Project _project;
    private final MethodCallVisitor visitor;

    public DefaultDataExtractor(Project project) {
        _project = project;
        myPsiManager = PsiManager.getInstance(project);
        visitor = new MethodCallVisitor();
    }

    @Override
    public Project getProject() {
        return _project;
    }

    @Override
    public void extractOf(Project projeto) {

        extractOf(projeto.getBasePath());
    }

    @Override
    public void extractOfAll(Project projeto) {
        try {
            for (int i = 1; i <= samplesCount; i++) {
                Object analisedProject = SetupDirectories.class.getField("PROJETO" + i);
                analisedProject = ((Field) analisedProject).get(new Directories());

                // extract for project in field
                extractOf((String) analisedProject);
            }
        } catch (Exception exc) {
            System.out.println("Houve um erro no acesso aos campos");
            exc.printStackTrace();
        }
    }

    @Override
    public void extractOf(String Abspath) {
        System.out.println(Abspath);

        // --- COM VISITOR
        Path path = Paths.get(Abspath);
        VirtualFile pastaDoProjeto = VfsUtil.findFile(path, true);
        PsiDirectory dir = myPsiManager.findDirectory(pastaDoProjeto);
        if (dir != null) dir.accept(visitor);

        CallList allCalls = new CallList(visitor.getVisitResult());
        CallList jcfCalls = new CallList(allCalls.calls().stream().filter(new FilterClass("java.util.Collection").or(new FilterClass("java.util.Map"))).collect(Collectors.toList()));
        CallList breakerOnes = new CallList(jcfCalls.calls().stream().filter(new FilterMethod()).collect(Collectors.toList()));

        //String m = "Olá, Mundo!";
        String m = "\nAll calls: ("+allCalls.calls().size()+")\n";
        m += allCalls.toString();
        m += "\nJCF: ("+jcfCalls.calls().size()+")\n";
        m += jcfCalls;
        m += "\nQUEBRAM O CONFINAMENTO: ("+breakerOnes.calls().size()+")\n";
        m += breakerOnes;
        //String m = processor.processToString();

        try {
            FileWriter myWriter = new FileWriter("results_folder\\"+pastaDoProjeto.getName()+(new Date().getTime())+".txt");
            myWriter.write(m);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException exc) {
            System.out.println("An error occurred.");
            exc.printStackTrace();
        }
    }

    @Override
    public void extractOf() {
        extractOf(getProject());
    }

    @Override
    public void extractOfAll() {
        extractOfAll(getProject());
    }
}
