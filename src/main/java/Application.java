import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException, GitAPIException {
        String repositoryURI = "https://gist.github.com/qphsmt/2fa836ba13e96a81b691acbf3576e02d";

        // delete local dir and clone gist to local dir
        String localRepositoryPath = "/tmp/repository";
        File localRepository = new File(localRepositoryPath);
        FileUtils.deleteDirectory(localRepository);
        localRepository.mkdir();
        Git.cloneRepository().setURI(repositoryURI).setDirectory(new File(localRepositoryPath)).call();

        // add new shop to file
        String shopFile = localRepositoryPath + "/sample.md";
        ShopEntity shopEntity = new ShopEntity();
        shopEntity.setName("sample2");
        shopEntity.setUrl("http://local.com");
        FileWriter filewriter = new FileWriter(new File(shopFile), true);
        filewriter.write("| " + shopEntity.getName() + " | " + shopEntity.getUrl() + " |\n");
        filewriter.close();

        // git add command and commit command
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repository = repositoryBuilder.setGitDir(new File(localRepositoryPath + "/.git"))
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build();
        Git git = new Git(repository);
        git.add().addFilepattern("sample.md").call();
        git.commit().setMessage("add new shop").call();

        // git push
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider( "using_accesstoken", "" );
        git.push().setCredentialsProvider(credentialsProvider).call();
    }
}