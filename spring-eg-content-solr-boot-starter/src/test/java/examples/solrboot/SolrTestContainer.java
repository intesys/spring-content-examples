package examples.solrboot;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.testcontainers.containers.SolrContainer;
import org.testcontainers.utility.DockerImageName;

public class SolrTestContainer extends SolrContainer {

    private static final String CONNECTION_URL = "http://%s:%d/solr/solr";
    private static final DockerImageName IMAGE_NAME = DockerImageName.parse("solr:8.11.4").asCompatibleSubstituteFor("solr");

    private SolrTestContainer() {
        super(IMAGE_NAME);
        setStartupAttempts(2);
        start();

        try {
            org.testcontainers.containers.Container.ExecResult result =
                    execInContainer("solr", "create_collection", "-c", "solr", "-d", "sample_techproducts_configs");
            if (result.getExitCode() != 0) {
                throw new RuntimeException("Failed to create solr collection (exit " + result.getExitCode() + "):\n"
                        + result.getStdout() + "\n" + result.getStderr());
            }
            // sample_techproducts_configs has the /update/extract handler but lacks the _text_ catch-all
            // field that Spring Content queries; add it plus a copyField so extracted content is searchable.
            org.testcontainers.containers.Container.ExecResult schema = execInContainer("sh", "-c",
                    "curl -s -X POST -H 'Content-type:application/json' http://localhost:8983/solr/solr/schema -d '"
                    + "{\"add-field\":{\"name\":\"_text_\",\"type\":\"text_general\",\"multiValued\":true,\"indexed\":true,\"stored\":false},"
                    + "\"add-copy-field\":{\"source\":\"*\",\"dest\":\"_text_\"}}'");
            if (schema.getExitCode() != 0) {
                throw new RuntimeException("Failed to update solr schema:\n" + schema.getStdout() + "\n" + schema.getStderr());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to setup solr container", e);
        }
    }

    public static SolrClient getSolrClient() {
        return new HttpSolrClient.Builder(solrUrl()).build();
    }

    public static String solrUrl() {
        return String.format(
                CONNECTION_URL,
                Singleton.INSTANCE.getContainerIpAddress(),
                Singleton.INSTANCE.getMappedPort(SolrContainer.SOLR_PORT));
    }

    @SuppressWarnings("unused") // Serializable safe singleton usage
    protected SolrTestContainer readResolve() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SolrTestContainer INSTANCE = new SolrTestContainer();
    }
}
