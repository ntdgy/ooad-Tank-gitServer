package ooad.Tank.gitServer;

import lombok.extern.slf4j.Slf4j;
import ooad.Tank.gitServer.backend.AuthManager;
import ooad.Tank.gitServer.backend.Backend;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GitHttpResolver implements RepositoryResolver<HttpServletRequest> {
    private static final String storeDirectory = "../repo-store/";

    @Override
    public Repository open(HttpServletRequest request, String name) throws RepositoryNotFoundException, ServiceNotAuthorizedException, ServiceNotEnabledException, ServiceMayNotContinueException {
        System.out.println("name: " + name + ", action:" + request.getRequestURI());
        String[] actions = name.split("/");
        String username = actions[0];
        String repoName = StringUtils.removeEnd(actions[1], ".git");
        Backend.Repo backRepo = Backend.resolveRepo(username, repoName);
        if (!AuthManager.checkReadPermission(request, backRepo)) throw new ServiceNotAuthorizedException();
        request.setAttribute(AuthManager.REPO, backRepo);
        try {
            File repoDirectory = new File(storeDirectory, String.format("%s/%s", backRepo.ownerId(), backRepo.repoId()));
            if (!repoDirectory.exists()) {
                throw new RuntimeException("repo should already created");
            }
            var repo = new FileRepository(repoDirectory.getCanonicalPath());
            System.out.println("repo: " + repo);
            return repo;
//            return new FileRepository("../ooad-Tank-backend/repo/" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
