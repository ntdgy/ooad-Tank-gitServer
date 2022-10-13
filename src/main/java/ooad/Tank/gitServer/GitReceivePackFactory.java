package ooad.Tank.gitServer;

import lombok.extern.slf4j.Slf4j;
import ooad.Tank.gitServer.backend.AuthManager;
import ooad.Tank.gitServer.backend.Backend;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.PreReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
public class GitReceivePackFactory implements ReceivePackFactory<HttpServletRequest> {

    @Override
    public ReceivePack create(HttpServletRequest req, Repository db) throws ServiceNotEnabledException, ServiceNotAuthorizedException {
        if (!AuthManager.checkWritePermission(req, (Backend.Repo) req.getAttribute(AuthManager.REPO))) throw new ServiceNotAuthorizedException();
        var pack = new ReceivePack(db);
        pack.setPostReceiveHook((rp, commands) -> {
            var repo = rp.getRepository();
            try {
                if (repo.exactRef(repo.exactRef("HEAD").getLeaf().getName()) == null) {
                    for(var cmd : commands) {
                        if (cmd.getType() == ReceiveCommand.Type.CREATE && cmd.getRefName().startsWith("refs/heads/")) {
                            var newRef = cmd.getRefName();
                            repo.updateRef("HEAD").link(newRef);
                            System.out.println("update HEAD to " + newRef);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return pack;
    }
}
