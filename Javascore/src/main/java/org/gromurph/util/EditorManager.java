package org.gromurph.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class EditorManager {

	static Map<Class<? extends BaseObjectModel>, Class<? extends BaseEditor>> editors = 
			new HashMap<Class<? extends BaseObjectModel>, Class<? extends BaseEditor>>();

	public static void put( Class<? extends BaseObjectModel> modelClass, Class<? extends BaseEditor> editorClass) {
		editors.put( modelClass, editorClass);
	}
	
	public static Class lookupEditorClass( BaseObjectModel object) {
		if (object == null) return null;
		return lookupEditorClass( object.getClass());
	}
	
	public static Class lookupEditorClass(Class objectClass) {
		if (objectClass == null) return null;
		Class c = objectClass;
		while (c != null) {
			Class ec = editors.get( c);
			if (ec != null) return ec;
			Class pc = c.getSuperclass();
			if (pc == c) return null;
			c = pc;
		}
		return null;
	}

	public static BaseEditor lookupEditor(BaseObjectModel obj, BaseEditorContainer editorContainer) {
		return lookupEditor( obj.getClass(), editorContainer);
	}
	
	public static BaseEditor lookupEditor(Class objectClass, BaseEditorContainer editorContainer) {
		if (editors == null) return null;
		Class c = objectClass;
		Class ec = lookupEditorClass( objectClass);
		if (ec != null) try {
			Constructor ctor = ec.getConstructor( new Class[] {BaseEditorContainer.class});
			BaseEditor e = (BaseEditor) ctor.newInstance(editorContainer);
			return e;
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	

}
