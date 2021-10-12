/*
	This is a hack to get rid of XSteam in Unit tests
 */
package hudson;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.collect.Lists;

import hudson.util.Secret;


public class TestProxyConfiguration implements Serializable {
    public final String name;
    public final int port;

    /**
     * Possibly null proxy user name.
     */
    private final String userName;

    /**
     * List of host names that shouldn't use proxy, as typed by users.
     *
     * @see #getNoProxyHostPatterns()
     */
    public final String noProxyHost;

    /**
     * encrypted password
     */
    private Secret secretPassword;
    
    private String testUrl;

    public TestProxyConfiguration(String name, int port) {
        this(name,port,null,null);
    }

    public TestProxyConfiguration(String name, int port, String userName, String password) {
        this(name,port,userName,password,null);
    }

    public TestProxyConfiguration(String name, int port, String userName, String password, String noProxyHost) {
        this(name,port,userName,password,noProxyHost,null);
    }

    @DataBoundConstructor
    public TestProxyConfiguration(String name, int port, String userName, String password, String noProxyHost, String testUrl) {
        this.name = Util.fixEmptyAndTrim(name);
        this.port = port;
        this.userName = Util.fixEmptyAndTrim(userName);
        this.secretPassword = Secret.fromString(password);
        this.noProxyHost = Util.fixEmptyAndTrim(noProxyHost);
        this.testUrl =Util.fixEmptyAndTrim(testUrl);
    }

    public String getUserName() {
        return userName;
    }

//    This method is public, if it was public only for jelly, then should make it private (or inline contents)
//    Have left public, as can't tell if anyone else is using from plugins
    /**
     * @return the password in plain text
     */
    public String getPassword() {
        return Secret.toString(secretPassword);
    }

    public String getEncryptedPassword() {
        return (secretPassword == null) ? null : secretPassword.getEncryptedValue();
    }

    public String getTestUrl() {
        return testUrl;
    }

    /**
     * Returns the list of properly formatted no proxy host names.
     */
    public List<Pattern> getNoProxyHostPatterns() {
        return getNoProxyHostPatterns(noProxyHost);
    }

    /**
     * Returns the list of properly formatted no proxy host names.
     */
    public static List<Pattern> getNoProxyHostPatterns(String noProxyHost) {
        if (noProxyHost==null)  return Collections.emptyList();

        List<Pattern> r = Lists.newArrayList();
        for (String s : noProxyHost.split("[ \t\n,|]+")) {
            if (s.length()==0)  continue;
            r.add(Pattern.compile(s.replace(".", "\\.").replace("*", ".*")));
        }
        return r;
    }


    public Proxy createProxy(String host) {
        return createProxy(host, name, port, noProxyHost);
    }

    public static Proxy createProxy(String host, String name, int port, String noProxyHost) {
        if (host!=null && noProxyHost!=null) {
            for (Pattern p : getNoProxyHostPatterns(noProxyHost)) {
                if (p.matcher(host).matches())
                    return Proxy.NO_PROXY;
            }
        }
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(name,port));
    }

    private static final long serialVersionUID = 1L;

}
