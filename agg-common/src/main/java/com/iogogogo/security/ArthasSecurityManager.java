package com.iogogogo.security;

import java.io.FileDescriptor;
import java.io.IOException;
import java.security.Permission;

/**
 * Created by tao.zeng on 2020/6/10.
 */
public class ArthasSecurityManager extends SecurityManager {


    public static void main(String[] args) throws IOException {
        SecurityManager securityManager = System.getSecurityManager();

        securityManager = new ArthasSecurityManager(securityManager);
        System.setSecurityManager(securityManager);

        System.in.read();
    }

    private SecurityManager delegate;

    public ArthasSecurityManager(SecurityManager securityManager) {
        this.delegate = securityManager;
    }

    @Override
    public void checkPermission(Permission perm) {
        System.out.println("checkPermission, perm: " + perm);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkPermission(perm);
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        System.out.println("checkPermission, perm: " + perm);
        if (this.delegate == null) {
            return;
        }

        this.delegate.checkPermission(perm, context);
    }

    @Override
    public void checkCreateClassLoader() {
        System.out.println("checkCreateClassLoader");
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkCreateClassLoader();
    }

    @Override
    public void checkAccess(Thread t) {
        System.out.println("checkAccess, thread: " + t);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkAccess(t);
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        System.out.println("checkAccess, ThreadGroup: " + g);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkAccess(g);
    }

    @Override
    public void checkExit(int status) {
        System.out.println("checkExit, status: " + status);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkExit(status);
    }

    @Override
    public void checkExec(String cmd) {
        System.out.println("checkExec, cmd: " + cmd);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkExec(cmd);
    }

    @Override
    public void checkLink(String lib) {
        System.out.println("checkLink, checkLink: " + lib);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkLink(lib);
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        System.out.println("checkRead, fd: " + fd);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkRead(fd);
    }

    @Override
    public void checkRead(String file) {
        System.out.println("checkRead, file: " + file);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkRead(file);
    }

    @Override
    public void checkRead(String file, Object context) {
        System.out.println("checkRead, file: " + file);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkRead(file, context);
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        System.out.println("checkWrite, fd: " + fd);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkWrite(fd);
    }

    @Override
    public void checkWrite(String file) {
        System.out.println("checkWrite, file: " + file);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkWrite(file);
    }

    @Override
    public void checkDelete(String file) {
        System.out.println("checkDelete, file: " + file);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkDelete(file);
    }

    @Override
    public void checkConnect(String host, int port) {
        System.out.println("checkConnect, host: " + host + ", port: " + port);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkConnect(host, port);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        System.out.println("checkConnect, host: " + host + ", port: " + port);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkConnect(host, port, context);
    }

    @Override
    public void checkListen(int port) {
        System.out.println("checkListen, port: " + port);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkListen(port);
    }

    @Override
    public void checkAccept(String host, int port) {
        System.out.println("checkAccept, host: " + host + " port: " + port);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkAccept(host, port);
    }

    @Override
    public void checkPropertiesAccess() {
        System.out.println("checkPropertiesAccess");
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkPropertiesAccess();
    }

    @Override
    public void checkPropertyAccess(String key) {
        System.out.println("checkPropertyAccess, key: " + key);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkPropertyAccess(key);
    }

    @Override
    public void checkPrintJobAccess() {
        System.out.println("checkPrintJobAccess");
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkPrintJobAccess();
    }

    @Override
    public void checkPackageAccess(String pkg) {
        System.out.println("checkPackageAccess, pkg: " + pkg);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkPackageAccess(pkg);
    }

    @Override
    public void checkPackageDefinition(String pkg) {
        System.out.println("checkPackageDefinition, pkg: " + pkg);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkPackageDefinition(pkg);
    }

    @Override
    public void checkSetFactory() {
        System.out.println("checkSetFactory");
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkSetFactory();
    }

    @Override
    public void checkSecurityAccess(String target) {
        System.out.println("checkSecurityAccess, target: " + target);
        if (this.delegate == null) {
            return;
        }
        this.delegate.checkSecurityAccess(target);
    }

}
