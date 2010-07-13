package org.openprovenance.reproduce;

import java.util.HashMap;
import java.util.List;

import org.openprovenance.model.Artifact;
import org.openprovenance.model.Process;
import org.openprovenance.model.OPMFactory;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;
import java.io.IOException;


public class Execute {

    final OPMFactory oFactory;

    final Utilities u;

    public Execute(OPMFactory oFactory) throws SAXException, IOException {
        this.oFactory=oFactory;
        this.u=new Utilities(oFactory);
        u.loadLibrary();
    }

    static int count=0;
    String prefix="_r_";

    public String newProcessName() {
        return prefix + "p" + (count++);
    }


    public Document invoke(String procedure,
                           HashMap<String,Artifact> arguments) throws org.jaxen.JaxenException{
        Process p=oFactory.newProcess(newProcessName(),
                                      null,
                                      procedure);

        List<?> procs=u.getDefinitionForUri(procedure);
        if ((procs==null)
            || (procs.size()==0)) {
            throw new NullPointerException();
        }

        Node proc=(Node)procs.get(0);

        Document doc=oFactory.builder.newDocument();

        List<?> ins=u.getInputs(proc);
        List<?> outs=u.getOutputs(proc);



        Element program=doc.createElementNS(Utilities.swift_XML_NS,"swift:program");
        program.setAttribute("xmlns:swift",Utilities.swift_XML_NS);
        program.setAttribute("xmlns",Utilities.swift_XML_NS);
        program.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        program.setAttribute("xmlns:opr","http://openprovenance.org/reproducibility");

        Element types=doc.createElementNS(Utilities.swift_XML_NS,"swift:types");

        Node newProc=doc.importNode(proc,true);
        u.removeRoles(newProc);

        program.appendChild(types);
        program.appendChild(newProc);

        Element call=doc.createElementNS(Utilities.swift_XML_NS,"swift:call");
        call.setAttribute("proc",u.getNameFromUri(procedure));

        for (Object out: outs) {
            String role=u.getRole((Node)out);
            String name=u.getName((Node)out);
            String type=u.getType((Node)out);

            System.out.println(" role " + role + ", name " + name + ", type " + type);
            System.out.println(arguments);
            Artifact a=arguments.get(role);
            generateOutputForArtifact(a,type,call,program,types,doc);
        }

        for (Object in: ins) {
            String role=u.getRole((Node)in);
            String name=u.getName((Node)in);
            String type=u.getType((Node)in);

            System.out.println(" role " + role + ", name " + name + ", type " + type);
            System.out.println(arguments);
            Artifact a=arguments.get(role);
            generateInputForArtifact(a,type,call,program,types,doc);
            
        }
        program.appendChild(call);
        doc.appendChild(program);
        return doc;
    }

    String makeVariable(Artifact a) {
        return "var_" + a.getId();
    }

    void generateInputForArtifact(Artifact a, String varType, Element call, Element program, Element types, Document doc) throws org.jaxen.JaxenException {
        Element input=doc.createElementNS(Utilities.swift_XML_NS,"swift:input");

        String type=oFactory.getType(a);
        if (type.equals("http://openprovenance.org/primitives#File")) {
            Element ref=doc.createElementNS(Utilities.swift_XML_NS,"swift:variableReference");
            ref.appendChild(doc.createTextNode(makeVariable(a)));
            input.appendChild(ref);

            Element var=doc.createElementNS(Utilities.swift_XML_NS,"swift:variable");
            Element file=doc.createElementNS(Utilities.swift_XML_NS,"swift:file");
            var.appendChild(file);
            var.setAttribute("type",varType);
            var.setAttribute("isGlobal","false");
            var.setAttribute("name",makeVariable(a));
            List value=oFactory.getPropertyValues(a,"http://openprovenance.org/primitives#path");
            file.setAttribute("name",makeFilename((String)value.get(0)));
            program.appendChild(var);

            addTypeDeclaration(types,varType,doc);
            
        } else if (type.equals("http://openprovenance.org/primitives#String")) {
            Element v=(Element)oFactory.getValue(a);
            String s=v.getFirstChild().getNodeValue();
            Element cst=doc.createElementNS(Utilities.swift_XML_NS,"swift:stringConstant");
            cst.appendChild(doc.createTextNode(s));
            input.appendChild(cst);
        }
        
        call.appendChild(input);
    }


    void generateOutputForArtifact(Artifact a, String varType, Element call, Element program, Element types, Document doc) throws org.jaxen.JaxenException {
        Element output=doc.createElementNS(Utilities.swift_XML_NS,"swift:output");

        String type=oFactory.getType(a);
        if (type.equals("http://openprovenance.org/primitives#File")) {
            Element ref=doc.createElementNS(Utilities.swift_XML_NS,"swift:variableReference");
            ref.appendChild(doc.createTextNode(makeVariable(a)));
            output.appendChild(ref);

            Element var=doc.createElementNS(Utilities.swift_XML_NS,"swift:variable");
            Element file=doc.createElementNS(Utilities.swift_XML_NS,"swift:file");
            var.appendChild(file);
            var.setAttribute("name",makeVariable(a));
            var.setAttribute("type",varType);
            var.setAttribute("isGlobal","false");
            List value=oFactory.getPropertyValues(a,"http://openprovenance.org/primitives#path");
            file.setAttribute("name",makeFilename((String)value.get(0)));
            program.appendChild(var);

            addTypeDeclaration(types,varType,doc);

        }
        
        call.appendChild(output);
    }

    HashMap<String,Boolean> seen=new HashMap();
    public void  addTypeDeclaration(Element types,String varType, Document doc) throws org.jaxen.JaxenException {
        if (seen.get(varType)==null)  {
            seen.put(varType,true);
            Node n=(Node)u.getLibraryTypeDefinition(varType).get(0);
            Node nn=doc.importNode(n,true);
            types.appendChild(nn);
        }
    }

    public String makeFilename(String path) {
        return "./" + path;
    }

}