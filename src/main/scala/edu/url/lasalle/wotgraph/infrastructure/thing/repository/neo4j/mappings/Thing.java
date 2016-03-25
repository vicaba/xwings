package edu.url.lasalle.wotgraph.infrastructure.thing.repository.neo4j.mappings;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;
import java.util.UUID;

@NodeEntity(label = "Thing")
public class Thing {

    final static private String CHILD_RELATION = "CHILD";
    final static private String ACTION_RELATION = "ACTION";


    @GraphId
    private Long id;
    public String _id;
    public String hName;
    public String action;

    public Thing() {

    }

    public Thing(String _id, String hName, String action) {
        this._id = _id;
        this.hName = hName;
        this.action = action;
    }

    @Relationship(type = Thing.CHILD_RELATION)
    public Set<Thing> children;

    @Relationship(type = Thing.ACTION_RELATION)
    public Set<Thing> actions;

    public void child(Thing thing) {
        children.add(thing);
    }

    public void action(Thing thing) {
        actions.add(thing);
    }

    public String toString() {
        return _id;
    }

}
