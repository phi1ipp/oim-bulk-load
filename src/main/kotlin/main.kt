import oracle.iam.platform.OIMClient
import java.util.*

fun main(args: Array<String>) {
    val ctxFactory = "weblogic.jndi.WLInitialContextFactory"
    val serverURL = args[0]
    val username = "xelsysadm"
    val password = args[1]
    val configPath = args[2]

    val env = Hashtable<String,String>()

    env[OIMClient.JAVA_NAMING_FACTORY_INITIAL] = ctxFactory;
    env[OIMClient.JAVA_NAMING_PROVIDER_URL] = serverURL;
    System.setProperty("java.security.auth.login.config", configPath);
    System.setProperty("APPSERVER_TYPE", "wls");
    val oimClient = OIMClient(env);
    oimClient.login(username, password.toCharArray());
    println("connected")
}
