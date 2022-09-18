package ooad.Tank.gitServer;

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

public class GitHttpResolver implements RepositoryResolver<HttpServletRequest> {
    @Override
    public Repository open(HttpServletRequest request, String name) throws RepositoryNotFoundException, ServiceNotAuthorizedException, ServiceNotEnabledException, ServiceMayNotContinueException {
        System.out.println("name: " + name + ", action:" + request.getRequestURI());
        if (!AuthManager.checkReadPermission(request, name)) throw new ServiceNotAuthorizedException();
        try {
            File storeDirectory = new File("../repo-store/");
            File repoDirectory = new File(storeDirectory, name);
            var repo = new FileRepository(repoDirectory.getCanonicalPath());
            if (!repoDirectory.exists()) {
                repoDirectory.mkdirs();
                repo.create(true);
                System.out.println("Create Repo for " + repoDirectory.getCanonicalPath());
            }
            System.out.println("repo: " + repo);
            return repo;
//            return new FileRepository("../ooad-Tank-backend/repo/" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
