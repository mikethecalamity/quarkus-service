package my.project.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.Startup;
import io.vertx.mutiny.sqlclient.Pool;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@UnlessBuildProfile("prod")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MyDataDBInit {

    private final Pool client;

    @Startup
    public void init() {
//        client.query("CREATE SCHEMA IF NOT EXISTS data").execute().flatMap(r -> client.query("""
//                CREATE TABLE IF NOT EXISTS data.data1 (
//                        id UUID NOT NULL,
//                        timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
//                        source TEXT NOT NULL,
//                        data JSONB NOT NULL,
//                        PRIMARY KEY(id, timestamp, source)
//                )
//                """).execute()).await().indefinitely();
    }
}
