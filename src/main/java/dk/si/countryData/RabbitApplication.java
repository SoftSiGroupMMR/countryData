package dk.si.countryData;

/*
 * Message Consumer
 *
 * 1) Creates  a queue, if it is not yet created
 * 2) Registers for notification of messages sent to its ID
 */
import com.rabbitmq.client.*;
import dk.si.countryData.countryData.CountryDataClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitApplication
{
    private final static String QUEUE_NAME = "countryData";

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(RabbitApplication.class, args);
        connectQueue();
    }

    
    public static void connectQueue() throws Exception {
        CountryDataClient countryDataClient = new CountryDataClient();
        // Same as the producer: tries to create a queue, if it wasn't already created
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("188.166.16.16");
        factory.setUsername("mmmrj1");
        factory.setPassword("mmmrj1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Register for a queue
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // Get notified, if a message for this receiver arrives
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            countryDataClient.countryDataHandler(message);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}

