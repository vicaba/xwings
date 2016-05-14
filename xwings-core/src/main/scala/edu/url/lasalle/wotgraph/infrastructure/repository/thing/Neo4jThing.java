package edu.url.lasalle.wotgraph.infrastructure.repository.thing;


import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "Thing")
public class Neo4jThing {

    final static private String CHILD_RELATION = "CHILD";

    @GraphId
    private Long id;

    public String _id;

    public Neo4jThing() {

    }

    public Neo4jThing(String _id) {
        this._id = _id;
    }

    public Neo4jThing(String _id, Set<Neo4jThing> children) {
        this._id = _id;
        this.children = children;
    }

    public Neo4jThing(String _id, Set<Neo4jThing> children, Long neo4jId) {
        this._id = _id;
        this.children = children;
        this.setNeo4jId(neo4jId);
    }


    @Relationship(type = Neo4jThing.CHILD_RELATION)
    public Set<Neo4jThing> children = new HashSet<>();

    public void child(Neo4jThing neo4jThing) {
        children.add(neo4jThing);
    }

    public Neo4jThing setNeo4jId(Long id) {
        if (id == -1) {
            this.id = null;
        } else {
            this.id = id;
        }
        return this;
    }

    public Long getNeo4jId() {
        return this.id;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Neo4jThing) {
            int comp = ((Neo4jThing)arg0)._id.compareTo(this._id);
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
