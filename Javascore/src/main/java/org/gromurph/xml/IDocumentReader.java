package org.gromurph.xml;

import java.io.IOException;

public interface IDocumentReader
{
    public PersistentNode readDocument() throws DocumentException, IOException;
}
