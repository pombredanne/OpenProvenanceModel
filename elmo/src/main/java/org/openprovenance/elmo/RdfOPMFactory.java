package org.openprovenance.elmo;
import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.openprovenance.model.HasAccounts;
import org.openprovenance.model.AccountRef;
import org.openprovenance.model.OPMGraph;
import org.openprovenance.model.Annotation;
import org.openprovenance.model.EmbeddedAnnotation;
import org.openprovenance.model.Property;

import org.openprovenance.rdf.AnnotationOrEdgeOrNodeOrOPMGraphOrRole;
import org.openprovenance.rdf.AnnotationOrEdgeOrNode;

public class RdfOPMFactory extends org.openprovenance.model.OPMFactory {


    public RdfOPMFactory(org.openprovenance.model.ObjectFactory o) {
        super(o);
    }

    public void addProperty(Annotation ann, Property p) {
       HasFacade facade=(HasFacade) ann;
       Object o=facade.findMyFacade();
       org.openprovenance.rdf.Annotation ann2=(org.openprovenance.rdf.Annotation) o;
       org.openprovenance.rdf.Property p2=(org.openprovenance.rdf.Property) ((HasFacade)p).findMyFacade();
       ann2.getProperties().add(p2);
    }

    public void addAccounts(HasAccounts element, Collection<AccountRef> accounts) {
        if (element instanceof AnnotationOrEdgeOrNodeOrOPMGraphOrRole) {
            HasFacade facade=(HasFacade) element;
            Object o=facade.findMyFacade();
            AnnotationOrEdgeOrNode el=(AnnotationOrEdgeOrNode) o;

            Set set=new HashSet();
            for (AccountRef accr: accounts) {
                set.add((org.openprovenance.rdf.Account)accr.getRef());
            }
            el.getAccounts().addAll(set);
            //el.setAccounts(set);
        }
    }

    public void addAnnotation(org.openprovenance.model.Annotable annotable,
                               JAXBElement<? extends org.openprovenance.model.EmbeddedAnnotation> ann) {
        super.addAnnotation(annotable,ann);
        System.out.println("Annotations  !!");
    }

    public void addAnnotation(org.openprovenance.model.Annotable annotable,
                              org.openprovenance.model.EmbeddedAnnotation ann) {
        super.addAnnotation(annotable,ann);
        System.out.println("Annotations  !!!");

        HasFacade facade=(HasFacade) annotable;
        Object o=facade.findMyFacade();
        AnnotationOrEdgeOrNodeOrOPMGraphOrRole annotable2=(AnnotationOrEdgeOrNodeOrOPMGraphOrRole) o;
        org.openprovenance.rdf.Annotation ann2=(org.openprovenance.rdf.Annotation) ((HasFacade)ann).findMyFacade();
        annotable2.getAnnotations().add(ann2);

    }

    public OPMGraph newOPMGraph(String id,
                                Collection<org.openprovenance.model.Account> accs,
                                Collection<org.openprovenance.model.Overlaps> ops,
                                Collection<org.openprovenance.model.Process> ps,
                                Collection<org.openprovenance.model.Artifact> as,
                                Collection<org.openprovenance.model.Agent> ags,
                                Collection<Object> lks,
                                Collection<org.openprovenance.model.Annotation> anns)
    {
        OPMGraph graph=super.newOPMGraph(id,accs,ops,ps,as,ags,lks,anns);
        HasFacade facade=(HasFacade) graph;
        org.openprovenance.rdf.OPMGraph rdfGraph=(org.openprovenance.rdf.OPMGraph) facade.findMyFacade();

        Set<org.openprovenance.rdf.Account> accounts=new HashSet();
        for (org.openprovenance.model.Account p: accs) {
            org.openprovenance.rdf.Account pp=(org.openprovenance.rdf.Account) ((HasFacade)p).findMyFacade();
            accounts.add(pp);
        }
        rdfGraph.setHasAccount(accounts);


        Set<org.openprovenance.rdf.Process> processes=new HashSet();
        for (org.openprovenance.model.Process p: ps) {
            org.openprovenance.rdf.Process pp=(org.openprovenance.rdf.Process) ((HasFacade)p).findMyFacade();
            processes.add(pp);
        }
        rdfGraph.setHasProcess(processes);


        Set<org.openprovenance.rdf.Artifact> artifacts=new HashSet();
        for (org.openprovenance.model.Artifact p: as) {
            org.openprovenance.rdf.Artifact pp=(org.openprovenance.rdf.Artifact) ((HasFacade)p).findMyFacade();
            artifacts.add(pp);
        }
        rdfGraph.setHasArtifact(artifacts);


        Set<org.openprovenance.rdf.Agent> agents=new HashSet();
        for (org.openprovenance.model.Agent p: ags) {
            org.openprovenance.rdf.Agent pp=(org.openprovenance.rdf.Agent) ((HasFacade)p).findMyFacade();
            agents.add(pp);
        }
        rdfGraph.setHasAgent(agents);

        Set<org.openprovenance.rdf.Edge> dependency=new HashSet();
        for (Object o: lks) {
            org.openprovenance.rdf.Edge pp=(org.openprovenance.rdf.Edge) ((HasFacade)o).findMyFacade();
            dependency.add(pp);
        }
        rdfGraph.setHasDependency(dependency);



        return graph;
        
    }


}
