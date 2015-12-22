package com.mygdx.ai;

import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

/**
 * makes the backing Array of nodes available, unlike DefaultIndexedGraph
 * @author elimonent
 *
 */
public class DefaultIndexedGraphWithPublicNodes<N extends IndexedNode<N>> extends DefaultIndexedGraph<N> {
	
	public DefaultIndexedGraphWithPublicNodes(Array<N> nodes) {
		super(nodes);
	}
	public Array<N> getNodes() {
		return this.nodes;
	}
}
