package live.socialchat.chat.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class MongoConfig {
    
    private static final String CHAT_MONGO_SERVER = "CHAT_MONGO_SERVER";
    private static final String CHAT_MONGO_USERNAME = "CHAT_MONGO_USERNAME";
    private static final String CHAT_MONGO_PASSWORD = "CHAT_MONGO_PASSWORD";
    private static final String CHAT_MONGO_CONNECTION_STRING = "CHAT_MONGO_CONNECTION_STRING";
    private static final String CHAT_MONGO_DATABASE = "CHAT_MONGO_DATABASE";
    private static final String CHAT_MONGO_AUTH_DATABASE = "CHAT_MONGO_AUTH_DATABASE";
    
    @Autowired
    private Environment environment;
    
    @Bean
    public MongoDatabase mongoDatabase(final MongoClient mongoClient) {
        return mongoClient
            .getDatabase(getEnvOrLocalProperty(CHAT_MONGO_DATABASE, "mongo.chat.database"));
    }
    
    @Bean
    public MongoClient mongoClient(CodecRegistry codecRegistry) {
        
        final String server = getEnvOrLocalProperty(CHAT_MONGO_SERVER, "mongo.server");
        final String username = getEnvOrLocalProperty(CHAT_MONGO_USERNAME, "mongo.username");
        final String password = getEnvOrLocalProperty(CHAT_MONGO_PASSWORD, "mongo.password");
        final String authDatabase = getEnvOrLocalProperty(CHAT_MONGO_AUTH_DATABASE, "mongo.auth.database");
        final String chatDatabase = getEnvOrLocalProperty(CHAT_MONGO_DATABASE, "mongo.chat.database");
        final String connection = getEnvOrLocalProperty(CHAT_MONGO_CONNECTION_STRING, "mongo.connection.string");
        final String connectionString = String.format(connection, username, password, server, chatDatabase);
        
        final MongoCredential credential = MongoCredential
            .createCredential(username, authDatabase, password.toCharArray());
        
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .credential(credential)
            .codecRegistry(codecRegistry)
            .applyConnectionString(new ConnectionString(connectionString))
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
    
    public String getEnvOrLocalProperty(final String envName, final String propertyName) {
        final String env = environment.getProperty(envName);
        return (env != null)
            ? env
            : environment.getProperty(propertyName);
    }
    
    @Bean
    public CodecRegistry codecRegistry() {
        return
            fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

}
