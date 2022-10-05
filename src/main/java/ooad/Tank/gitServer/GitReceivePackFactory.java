package ooad.Tank.gitServer;

import ooad.Tank.gitServer.backend.AuthManager;
import ooad.Tank.gitServer.backend.Backend;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

import javax.servlet.http.HttpServletRequest;

public class GitReceivePackFactory implements ReceivePackFactory<HttpServletRequest> {

    @Override
    public ReceivePack create(HttpServletRequest req, Repository db) throws ServiceNotEnabledException, ServiceNotAuthorizedException {
        if (!AuthManager.checkWritePermission(req, (Backend.Repo) req.getAttribute(AuthManager.REPO))) throw new ServiceNotAuthorizedException();
        return new ReceivePack(db);
    }
}
