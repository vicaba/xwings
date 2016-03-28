package edu.url.lasalle.wotgraph.application;

import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

abstract public class Thing {

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

    public Set<Thing> children = new HashSet<>();

    public Set<Thing> actions = new HashSet<>();
}
