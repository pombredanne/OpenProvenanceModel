package org.openprovenance.model;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;


/** This class provides a set of indexes over information contained in
 * an OPMGraph, facilitating its navigation.  Its constructor takes an
 * OPMGraph builds an index for it.  Of course, for the index to be
 * maintained, one cannot access, say the list of edges, and mutate
 * it. Instead, one has to use the add methods provided.
 *<p>
 * Note that code is not thread-safe. */

public class IndexedOPMGraph extends OPMGraph {

    OPMUtilities u=new OPMUtilities();
    final OPMFactory oFactory;

    private HashMap<String,Account>  accountMap=new HashMap();
    private HashMap<String,Artifact> artifactMap=new HashMap();
    private HashMap<String,Process>  processMap=new HashMap();

    /* Collection of Used edges that have a given process as an
     * effect. */
    private HashMap<String,Collection<Used>> processUsedMap=new HashMap();

    /* Collection of Used edges that have a given artifact as a
     * cause. */
    private HashMap<String,Collection<Used>> artifactUsedMap=new HashMap();
    private Collection<Used> allUsed=new LinkedList();

    /* Collection of WasGeneratedBy edges that have a given process as a
     * cause. */
    private HashMap<String,Collection<WasGeneratedBy>> processWasGeneratedByMap=new HashMap();

    /* Collection of WasGeneratedBy edges that have a given artifact as an
     * effect. */
    private HashMap<String,Collection<WasGeneratedBy>> artifactWasGeneratedByMap=new HashMap();
    private Collection<WasGeneratedBy> allWasGeneratedBy=new LinkedList();

    /* Collection of WasDerivedFrom edges that have a given artifact as a cause. */
    private HashMap<String,Collection<WasDerivedFrom>> artifactCauseWasDerivedFromMap=new HashMap();

    /* Collection of WasDerivedFrom edges that have a given artifact as an
     * effect. */
    private HashMap<String,Collection<WasDerivedFrom>> artifactEffectWasDerivedFromMap=new HashMap();
    private Collection<WasDerivedFrom> allWasDerivedFrom=new LinkedList();

    /* Collection of WasControlledBy edges that have a given process as an
     * effect. */
    private HashMap<String,Collection<WasControlledBy>> processWasControlledByMap=new HashMap();

    /* Collection of WasControlledBy edges that have a given agent as a
     * cause. */
    private HashMap<String,Collection<WasControlledBy>> agentWasControlledByMap=new HashMap();
    private Collection<WasControlledBy> allWasControlledBy=new LinkedList();

    /* Collection of WasTriggeredBy edges that have a given process as a cause. */
    private HashMap<String,Collection<WasTriggeredBy>> processCauseWasTriggeredByMap=new HashMap();

    /* Collection of WasTriggeredBy edges that have a given process as an
     * effect. */
    private HashMap<String,Collection<WasTriggeredBy>> processEffectWasTriggeredByMap=new HashMap();
    private Collection<WasTriggeredBy> allWasTriggeredBy=new LinkedList();


    /** Return all used edges for this graph. */
    public Collection<Used> getUsed() {
        return allUsed;
    }
    /** Return all used edges with process p as an effect. */
    public Collection<Used> getUsed(Process p) {
        return processUsedMap.get(p.getId());
    }

    /** Return all used edges with artifact a as a cause. */
    public Collection<Used> getUsed(Artifact p) {
        return artifactUsedMap.get(p.getId());
    }


    protected ObjectFactory of=new ObjectFactory();

    Account addAccount(String name, Account account) {
        Account existing=accountMap.get(name);
        if (existing!=null) {
            return existing;
        } else {
            accountMap.put(name,account);
            Accounts accounts=getAccounts();
            if (accounts==null) {
                accounts=of.createAccounts();
                setAccounts(accounts);
            }
            accounts.getAccount().add(account);
            return account;
        }
    }
    Account addAccount(Account account) {
        return addAccount(account.getId(),account);
    }

    Artifact addArtifact(Artifact artifact) {
        return addArtifact(artifact.getId(),artifact);
    }
    Artifact addArtifact(String name, Artifact artifact) {
        Artifact existing=artifactMap.get(name);
        if (existing!=null) {
            return existing;
        } else {
            artifactMap.put(name,artifact);
            Artifacts artifacts=getArtifacts();
            if (artifacts==null) {
                artifacts=of.createArtifacts();
                setArtifacts(artifacts);
            }
            artifacts.getArtifact().add(artifact);
            return artifact;
        }
    }

    Process addProcess(Process process) {
        return addProcess(process.getId(),process);
    }

    Process addProcess(String name, Process process) {
        Process existing=processMap.get(name);
        if (existing!=null) {
            return existing;
        } else {
            processMap.put(name,process);
            Processes processes=getProcesses();
            if (processes==null) {
                processes=of.createProcesses();
                setProcesses(processes);
            }
            processes.getProcess().add(process);
            return process;
        }
    }
            
    public IndexedOPMGraph(OPMFactory oFactory, OPMGraph graph) {
        this.oFactory=oFactory;
        if (graph.getAccounts()!=null) {
            for (Account acc: graph.getAccounts().getAccount()) {
                addAccount(acc);
            }
        }

        if (graph.getArtifacts()!=null) {
            for (Artifact acc: graph.getArtifacts().getArtifact()) {
                addArtifact(acc);
            }
        }

        if (graph.getProcesses()!=null) {
            for (Process acc: graph.getProcesses().getProcess()) {
                addProcess(acc);
            }
        }

        if (graph.getCausalDependencies()!=null) {
            if (getCausalDependencies()==null) {
                setCausalDependencies(of.createCausalDependencies());
            }
            List<Edge> edges=u.getEdges(graph);

            for (Edge edge: edges) {
                if (edge instanceof Used) {
                    /* clone to ensure that original graph is not mutated. */
                    addUsed(oFactory.newUsed((Used) edge));  
                }
                if (edge instanceof WasGeneratedBy) {
                    /* clone to ensure that original graph is not mutated. */
                    addWasGeneratedBy(oFactory.newWasGeneratedBy((WasGeneratedBy) edge));  
                }
                if (edge instanceof WasDerivedFrom) {
                    /* clone to ensure that original graph is not mutated. */
                    addWasDerivedFrom(oFactory.newWasDerivedFrom((WasDerivedFrom) edge));  
                }
                if (edge instanceof WasControlledBy) {
                    /* clone to ensure that original graph is not mutated. */
                    addWasControlledBy(oFactory.newWasControlledBy((WasControlledBy) edge));  
                }
                if (edge instanceof WasTriggeredBy) {
                    /* clone to ensure that original graph is not mutated. */
                    addWasTriggeredBy(oFactory.newWasTriggeredBy((WasTriggeredBy) edge));  
                }
            }
        }
    }

    /** Only adds to acc1s elements of acc2s that are not already in acc1s. */
    void addNewAccounts(Collection<AccountId> acc1s, Collection<AccountId> acc2s) {
        for (AccountId acc2: acc2s) {
            if (!(acc1s.contains(acc2))) {
                acc1s.add(acc2);
            }
        }
    }

    /** Add a used edge to the graph. Update processUsedMap and
        artifactUsedMap accordingly.  By doing so, aggregate all used
        edges (p,r,a) with different accounts in a single edge.
        Return the used edge itself (if it had not been encountered
        before), or the instance encountered before.*/

    public Used addUsed(Used used) {
        ProcessId pid=used.getEffect();
        Process p=(Process)(pid.getId());
        ArtifactId aid=used.getCause();
        Artifact a=(Artifact)(aid.getId());
        Role r=used.getRole();
        Collection<AccountId> accs=used.getAccount();

        Used result=used;

        boolean found=false;
        Collection<Used> ucoll=processUsedMap.get(p.getId());
        if (ucoll==null) {
            ucoll=new LinkedList();
            ucoll.add(used);
            processUsedMap.put(p.getId(),ucoll);
        } else {

            for (Used u: ucoll) {
                
                if (aid.equals(u.getCause())
                    &&
                    r.equals(u.getRole())) {
                    addNewAccounts(u.getAccount(),accs);
                    result=u;
                    found=true;
                }
            }
            if (!found) {
                ucoll.add(used);
            }
        }

        ucoll=artifactUsedMap.get(a.getId());
        if (ucoll==null) {
            ucoll=new LinkedList();
            ucoll.add(used);
            artifactUsedMap.put(p.getId(),ucoll);
        } else {
            if (!found) {
                // if we had not found it in the first table, then we
                // have to add it here too
                ucoll.add(used);
            }
        }

        if (!found) {
            allUsed.add(used);
            getCausalDependencies().getUsedOrWasGeneratedByOrWasTriggeredBy().add(used);
        }
        return result;
   }




    /** Add a wasGeneratedBy edge to the graph. Update processWasGeneratedByMap and
        artifactWasGeneratedByMap accordingly.  By doing so, aggregate all wasGeneratedBy
        edges (a,r,p) with different accounts in a single edge.
        Return the wasGeneratedBy edge itself (if it had not been encountered
        before), or the instance encountered before.*/

    public WasGeneratedBy addWasGeneratedBy(WasGeneratedBy wasGeneratedBy) {
        ProcessId pid=wasGeneratedBy.getCause();
        Process p=(Process)(pid.getId());
        ArtifactId aid=wasGeneratedBy.getEffect();
        Artifact a=(Artifact)(aid.getId());
        Role r=wasGeneratedBy.getRole();
        Collection<AccountId> accs=wasGeneratedBy.getAccount();

        WasGeneratedBy result=wasGeneratedBy;

        boolean found=false;
        Collection<WasGeneratedBy> gcoll=processWasGeneratedByMap.get(p.getId());
        if (gcoll==null) {
            gcoll=new LinkedList();
            gcoll.add(wasGeneratedBy);
            processWasGeneratedByMap.put(p.getId(),gcoll);
        } else {

            for (WasGeneratedBy u: gcoll) {
                
                if (aid.equals(u.getEffect())
                    &&
                    r.equals(u.getRole())) {
                    addNewAccounts(u.getAccount(),accs);
                    result=u;
                    found=true;
                }
            }
            if (!found) {
                gcoll.add(wasGeneratedBy);
            }
        }

        gcoll=artifactWasGeneratedByMap.get(a.getId());
        if (gcoll==null) {
            gcoll=new LinkedList();
            gcoll.add(wasGeneratedBy);
            artifactWasGeneratedByMap.put(p.getId(),gcoll);
        } else {
            if (!found) {
                // if we had not found it in the first table, then we
                // have to add it here too
                gcoll.add(wasGeneratedBy);
            }
        }

        if (!found) {
            allWasGeneratedBy.add(wasGeneratedBy);
            getCausalDependencies().getUsedOrWasGeneratedByOrWasTriggeredBy().add(wasGeneratedBy);
        }
        return result;
   }


    /** Add a wasDerivedFrom edge to the graph. Update processWasDerivedFromMap and
        artifactWasDerivedFromMap accordingly.  By doing so, aggregate all wasDerivedFrom
        edges (p,r,a) with different accounts in a single edge.
        Return the wasDerivedFrom edge itself (if it had not been encountered
        before), or the instance encountered before.*/

    public WasDerivedFrom addWasDerivedFrom(WasDerivedFrom wasDerivedFrom) {
        ArtifactId aid2=wasDerivedFrom.getEffect();
        Artifact a2=(Artifact)(aid2.getId());
        ArtifactId aid1=wasDerivedFrom.getCause();
        Artifact a1=(Artifact)(aid1.getId());
        Collection<AccountId> accs=wasDerivedFrom.getAccount();

        WasDerivedFrom result=wasDerivedFrom;

        boolean found=false;
        Collection<WasDerivedFrom> dcoll=artifactCauseWasDerivedFromMap.get(a1.getId());
        if (dcoll==null) {
            dcoll=new LinkedList();
            dcoll.add(wasDerivedFrom);
            artifactCauseWasDerivedFromMap.put(a1.getId(),dcoll);
        } else {

            for (WasDerivedFrom d: dcoll) {
                
                if (aid1.equals(d.getCause())) {
                    addNewAccounts(d.getAccount(),accs);
                    result=d;
                    found=true;
                }
            }
            if (!found) {
                dcoll.add(wasDerivedFrom);
            }
        }

        dcoll=artifactEffectWasDerivedFromMap.get(a2.getId());
        if (dcoll==null) {
            dcoll=new LinkedList();
            dcoll.add(wasDerivedFrom);
            artifactEffectWasDerivedFromMap.put(a2.getId(),dcoll);
        } else {
            if (!found) {
                // if we had not found it in the first table, then we
                // have to add it here too
                dcoll.add(wasDerivedFrom);
            }
        }

        if (!found) {
            allWasDerivedFrom.add(wasDerivedFrom);
            getCausalDependencies().getUsedOrWasGeneratedByOrWasTriggeredBy().add(wasDerivedFrom);
        }
        return result;
   }


    /** Add a wasControlledBy edge to the graph. Update processWasControlledByMap and
        agentWasControlledByMap accordingly.  By doing so, aggregate all wasControlledBy
        edges (p,r,a) with different accounts in a single edge.
        Return the wasControlledBy edge itself (if it had not been encountered
        before), or the instance encountered before.*/

    public WasControlledBy addWasControlledBy(WasControlledBy wasControlledBy) {
        ProcessId pid=wasControlledBy.getEffect();
        Process p=(Process)(pid.getId());
        AgentId aid=wasControlledBy.getCause();
        Agent a=(Agent)(aid.getId());
        Role r=wasControlledBy.getRole();
        Collection<AccountId> accs=wasControlledBy.getAccount();

        WasControlledBy result=wasControlledBy;

        boolean found=false;
        Collection<WasControlledBy> ccoll=processWasControlledByMap.get(p.getId());
        if (ccoll==null) {
            ccoll=new LinkedList();
            ccoll.add(wasControlledBy);
            processWasControlledByMap.put(p.getId(),ccoll);
        } else {

            for (WasControlledBy u: ccoll) {
                
                if (aid.equals(u.getCause())
                    &&
                    r.equals(u.getRole())) {
                    addNewAccounts(u.getAccount(),accs);
                    result=u;
                    found=true;
                }
            }
            if (!found) {
                ccoll.add(wasControlledBy);
            }
        }

        ccoll=agentWasControlledByMap.get(a.getId());
        if (ccoll==null) {
            ccoll=new LinkedList();
            ccoll.add(wasControlledBy);
            agentWasControlledByMap.put(p.getId(),ccoll);
        } else {
            if (!found) {
                // if we had not found it in the first table, then we
                // have to add it here too
                ccoll.add(wasControlledBy);
            }
        }

        if (!found) {
            allWasControlledBy.add(wasControlledBy);
            getCausalDependencies().getUsedOrWasGeneratedByOrWasTriggeredBy().add(wasControlledBy);
        }
        return result;
   }

    /** Add a wasTriggeredBy edge to the graph. Update processWasTriggeredByMap and
        artifactWasTriggeredByMap accordingly.  By doing so, aggregate all wasTriggeredBy
        edges (p,r,a) with different accounts in a single edge.
        Return the wasTriggeredBy edge itself (if it had not been encountered
        before), or the instance encountered before.*/

    public WasTriggeredBy addWasTriggeredBy(WasTriggeredBy wasTriggeredBy) {
        ProcessId pid2=wasTriggeredBy.getEffect();
        Process p2=(Process)(pid2.getId());
        ProcessId pid1=wasTriggeredBy.getCause();
        Process p1=(Process)(pid1.getId());
        Collection<AccountId> accs=wasTriggeredBy.getAccount();

        WasTriggeredBy result=wasTriggeredBy;

        boolean found=false;
        Collection<WasTriggeredBy> dcoll=processCauseWasTriggeredByMap.get(p1.getId());
        if (dcoll==null) {
            dcoll=new LinkedList();
            dcoll.add(wasTriggeredBy);
            processCauseWasTriggeredByMap.put(p1.getId(),dcoll);
        } else {

            for (WasTriggeredBy d: dcoll) {
                
                if (pid1.equals(d.getCause())) {
                    addNewAccounts(d.getAccount(),accs);
                    result=d;
                    found=true;
                }
            }
            if (!found) {
                dcoll.add(wasTriggeredBy);
            }
        }

        dcoll=processEffectWasTriggeredByMap.get(p2.getId());
        if (dcoll==null) {
            dcoll=new LinkedList();
            dcoll.add(wasTriggeredBy);
            processEffectWasTriggeredByMap.put(p2.getId(),dcoll);
        } else {
            if (!found) {
                // if we had not found it in the first table, then we
                // have to add it here too
                dcoll.add(wasTriggeredBy);
            }
        }

        if (!found) {
            allWasTriggeredBy.add(wasTriggeredBy);
            getCausalDependencies().getUsedOrWasGeneratedByOrWasTriggeredBy().add(wasTriggeredBy);
        }
        return result;
   }

}