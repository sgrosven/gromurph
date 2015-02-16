

package org.gromurph.xml;

import java.io.IOException;

public interface IDocumentWriter {
    
    public PersistentNode createRootNode(String rootTag);

    public void saveObject(PersistentNode root, boolean elementOnly) throws DocumentException, IOException;
    
}
