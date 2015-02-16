package org.gromurph.xml.jaxb;

import org.w3c.dom.Element;

public class ElementNode extends AbstractNode {

	public ElementNode( Element e) {
		super(e);
	}
	
	public Element element() {
		return (Element) node;
	}

	@Override
	public String getName() {
		return element().getNodeName();
	}

	@Override
	public String getText() {
		Element e = element();
		String x = e.getNodeValue();
		String y = e.getTextContent();
		return (x == null) ? y : x;

	}

	@Override
	public void setText(String value) {
		element().setNodeValue(value);
		element().setTextContent(value);
	}

}
