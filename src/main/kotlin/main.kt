import oracle.iam.identity.orgmgmt.api.OrganizationManager
import oracle.iam.identity.orgmgmt.api.OrganizationManagerConstants
import oracle.iam.platform.OIMClient
import java.util.*
import oracle.iam.identity.usermgmt.api.UserManager
import oracle.iam.identity.usermgmt.vo.User
import oracle.iam.platform.entitymgr.vo.SearchCriteria
import java.io.File

fun main(args: Array<String>) {
    val ctxFactory = "weblogic.jndi.WLInitialContextFactory"
    val serverURL = args[0]
    val username = "xelsysadm"
    val password = args[1]
    val configPath = args[2]

    if (args.size < 4) {
        println("Usage: serverURL password authwl.config_path file_to_load")
        return
    }

    val env = Hashtable<String, String>()

    env[OIMClient.JAVA_NAMING_FACTORY_INITIAL] = ctxFactory;
    env[OIMClient.JAVA_NAMING_PROVIDER_URL] = serverURL;
    System.setProperty("java.security.auth.login.config", configPath);
    System.setProperty("APPSERVER_TYPE", "wls");

    val oimClient = OIMClient(env).also {
        it.login(username, password.toCharArray());
    }
    println("connected")

    val um = oimClient.getService(UserManager::class.java)
    println("got UM")

    val om = oimClient.getService(OrganizationManager::class.java)
    println("got OM")

    File(args[3]).forEachLine {
        val (login, fname, lname, email, orgName) = it.split(",")

        val orgKey =
            om.search(
                SearchCriteria(
                    OrganizationManagerConstants.AttributeName.ORG_NAME.id,
                    orgName,
                    SearchCriteria.Operator.EQUAL
                ),
                null, null
            )

        val u = User(
            null,
            hashMapOf(
                Pair("First Name", fname),
                Pair("Last Name", lname),
                Pair("Email", email),
                Pair("User Login", login),
                Pair("act_key", orgKey[0].entityId.toLong()),
                Pair("Xellerate Type", "End-User"),
                Pair("Role", "EMP")
            )
        )

        try {
            val umr = um.create(u)

            println("Creating user $login - ${umr.status}")
        } catch (e: Exception) {
            println("Creating user $login - EXCEPTION! ${e.message}")
        }
    }
}
