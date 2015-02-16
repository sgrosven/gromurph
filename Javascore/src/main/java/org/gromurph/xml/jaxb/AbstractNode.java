package org.gromurph.xml.jaxb;

import java.util.ArrayList;

import org.gromurph.xml.PersistentNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractNode implements PersistentNode {

	protected Node node;
	
	public AbstractNode( Node n) {
		node = n;
	}
	
	public Document getOwnerDocument() {
		return node.getOwnerDocument();
	}
	
	public PersistentNode getParent() {
		Node p = node.getParentNode();
		if (p instanceof Element) return new ElementNode( (Element) p);
		else if (p instanceof Attr) return new AttributeNode( (Attr) p);
		return null;
	}
	
	@Override
	public String[] getAttributes() {
		NamedNodeMap nnp = node.getAttributes();
		String[] alist = new String[ nnp.getLength()];
		for (int i = 0; i < nnp.getLength(); i++) {
			alist[i] = nnp.item(i).getNodeName();
		}
		return alist;
	}

	@Override
	public String getAttribute(String name) {
		NamedNodeMap nnp = node.getAttributes();
		for (int i = 0; i < nnp.getLength(); i++) {
			Node n = nnp.item(i);
			if (n.getNodeName().equals( name)) return n.getNodeValue();
		}
		return null;
	}

	@Override
	public void setAttribute(String name, String value) {
		NamedNodeMap nnp = node.getAttributes();
		for (int i = 0; i < nnp.getLength(); i++) {
			Node n = nnp.item(i);
			if (n.getNodeName().equals( name)) {
				n.setNodeValue( value);
				return; // overwrite existing value
			}
		}
		// getting here means a new attribute
		Attr attr = node.getOwnerDocument().createAttribute(name);
		attr.setNodeValue( value);
		nnp.setNamedItem(attr);
	}

	@Override
	public boolean hasAttribute(String name) {
		return (getAttribute(name) != null);
	}

	@Override
	public PersistentNode[] getElements() {
		ArrayList<PersistentNode> elements = new ArrayList<PersistentNode>(10);
		NodeList nnp = node.getChildNodes();
		for (int i = 0; i < nnp.getLength(); i++) {
			Node n = nnp.item(i);
			if (n instanceof Element) {
				elements.add( new ElementNode( (Element) n));
			}
		}
		PersistentNode[] array = new PersistentNode[ elements.size()];
		for ( int i = 0; i < elements.size(); i++) array[i] = elements.get(i);
		return array;
	}

	@Override
	public PersistentNode getElement(String name) {
		NodeList nnp = node.getChildNodes();
		for (int i = 0; i < nnp.getLength(); i++) {
			Node n = nnp.item(i);
			if (n instanceof Element && n.getNodeName().equals(name)) {
				return new ElementNode( (Element) n);
			}
		}
		return null;
	}

	
	@Override
	public PersistentNode createChildElement( String ename) {
		Element e = node.getOwnerDocument().createElement( ename);
		node.appendChild(e);
		PersistentNode en = new ElementNode(e);
		return en;
	}

	@Override
	public boolean hasElement(String name) {
		return getElement(name) != null;
	}

}