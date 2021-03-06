package org.openprovenance.elmo;
import java.util.Set;
import org.openprovenance.rdf.Account;
import org.openprovenance.rdf.Node;

import javax.xml.namespace.QName;
import org.openrdf.elmo.ElmoManager;

public class RdfAccount extends org.openprovenance.model.Account implements HasFacade {

    ElmoManager manager;
    String prefix;
    QName qname;

    public RdfAccount(ElmoManager manager, QName qname) {
        this.manager=manager;
        this.qname=qname;
        this.prefix=qname.getNamespaceURI();
        super.setId(qname.getLocalPart());
    }

    public RdfAccount(ElmoManager manager, String prefix) {
        this.manager=manager;
        this.prefix=prefix;
    }

    public void setId(String value) {
        super.setId(value);
        qname = new QName(prefix, value);
        manager.designate(qname, org.openprovenance.rdf.Account.class);
    }


    public QName getQName() {
        return qname;
    }

    public org.openprovenance.rdf.Account findMyFacade() {
        org.openprovenance.rdf.Account r=(org.openprovenance.rdf.Account)manager.find(getQName());
        return r;
    }

    
}
