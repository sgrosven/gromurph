package org.gromurph.xml.jaxb;

import org.w3c.dom.Attr;

public class AttributeNode extends AbstractNode {

	public AttributeNode( Attr e) {
		super(e);
	}
	
	public Attr attr() {
		return (Attr) node;
	}

	@Override
	public String getName() {
		return attr().getName();
	}

	@Override
	public String getText() {
		return attr().getValue();

	}

	@Override
	public void setText(String value) {
		attr().setValue(value);
	}

}
