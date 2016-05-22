package io.petical.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * バッチアプリケーション.
 */
@SpringBootApplication
public class BatchApplication implements EnvironmentAware {

    private Environment environment;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BatchApplication.class);
        application.setWebEnvironment(false);
        ApplicationContext context = application.run();
        SpringApplication.exit(context);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
