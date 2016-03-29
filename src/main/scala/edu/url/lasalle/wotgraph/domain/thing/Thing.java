package edu.url.lasalle.wotgraph.domain.thing;


import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "Thing")
public class Thing {

    final static private String CHILD_RELATION = "CHILD";
    final static private String ACTION_RELATION = "ACTION";


    @GraphId
    private Long id;
    public String _id;
    public String hName;
    public String action;
    public Metadata metadata;

    public Thing() {

    }

    public Thing(String _id, String hName, String action) {
        this._id = _id;
        this.hName = hName;
        this.action = action;
    }

    @Relationship(type = Thing.CHILD_RELATION)
    public Set<Thing> children = new HashSet<>();

    @Relationship(type = Thing.ACTION_RELATION)
    public Set<Thing> actions = new HashSet<>();

    public void child(Thing thing) {
        children.add(thing);
    }

    public void action(Thing thing) {
        actions.add(thing);
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Thing) {
            int comp = ((Thing)arg0)._id.compareTo(this._id);
            if (comp == 0) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return _id;
    }

}
