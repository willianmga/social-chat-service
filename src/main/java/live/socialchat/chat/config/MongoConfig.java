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
import org.springframework.beans.factory.annotation.Value;
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
    public MongoDatabase mongoDatabase(final MongoClient mongoClient,
                                       @Value("${mongo.chat.database}") String mongoDatabase) {
        
        return mongoClient.getDatabase(environment.getProperty(CHAT_MONGO_DATABASE, mongoDatabase));
    }
    
    @Bean
    public MongoClient mongoClient(CodecRegistry codecRegistry,
                                   @Value("${mongo.server}") String mongoServer,
                                   @Value("${mongo.username}") String mongoUsername,
                                   @Value("${mongo.password}") String mongoPassword,
                                   @Value("${mongo.connection.string}") String mongoConnectionString,
                                   @Value("${mongo.auth.database}") String mongoAuthDatabase,
                                   @Value("${mongo.chat.database}") String mongoChatDatabase) {
    
        final String server = environment.getProperty(CHAT_MONGO_SERVER, mongoServer);
        final String username = environment.getProperty(CHAT_MONGO_USERNAME, mongoUsername);
        final String password = environment.getProperty(CHAT_MONGO_PASSWORD, mongoPassword);
        final String authDatabase = environment.getProperty(CHAT_MONGO_AUTH_DATABASE, mongoAuthDatabase);
        final String chatDatabase = environment.getProperty(CHAT_MONGO_DATABASE, mongoChatDatabase);
        final String connection = environment.getProperty(CHAT_MONGO_CONNECTION_STRING, mongoConnectionString);
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
    
    @Bean
    public CodecRegistry codecRegistry() {
        return
            fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

}
