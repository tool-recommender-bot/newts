package org.opennms.newts.rest;


import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.opennms.newts.api.SampleRepository;
import org.opennms.newts.persistence.cassandra.CassandraSampleRepository;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;


public class NewtsService extends Service<Config> {

    public static void main(String... args) throws Exception {
        new NewtsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.setName("newts");
        bootstrap.addCommand(new InitCommand());
    }

    @Override
    public void run(Config configuration, Environment environment) throws Exception {

        environment.addFilter(CrossOriginFilter.class, "/*");

        String host = configuration.getCassandraHost();
        int port = configuration.getCassandraPort();
        String keyspace = configuration.getCassandraKeyspace();

        SampleRepository repository = new CassandraSampleRepository(keyspace, host, port, null);

        environment.addResource(new MeasurementsResource(repository, configuration.getReports()));
        environment.addResource(new SamplesResource(repository));

        environment.addHealthCheck(new RepositoryHealthCheck(repository));

        environment.addProvider(IllegalArgumentExceptionMapper.class);

    }

}