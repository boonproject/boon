package org.boon.di;

import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContextImpl implements Context {

    private Set<Module> modules = new HashSet<> (  );

    public ContextImpl (Module... modules) {
        for (Module module : modules)  {
            this.modules.add ( module );
        }
    }

    @Override
    public <T> T get( Class<T> type ) {

        Object object = null;
        for (Module module : modules) {

            if (module.has ( type )) {
                object = module.get ( type );
                break;
            }
        }

        if (object!=null) {
            Map<String,FieldAccess> fields = Reflection.getAllAccessorFields ( object.getClass (), true );
            for ( FieldAccess field : fields.values()) {
                if (field.hasAnnotation ( "Inject" )) {
                    field.setObject (object,  get(field.getType ()) );
                }
            }
        }

        return (T) object;
    }
}
