package org.apache.qpid.jms.example;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.TextMessage;
import jakarta.jms.Session;

import java.nio.charset.Charset;

import javax.naming.Context;
import javax.naming.InitialContext;

public class Receiver {
    private static final int DEFAULT_COUNT = 20;

    public static void main(String[] args) throws Exception {
        int count = DEFAULT_COUNT;
        if (args.length == 0) {
            System.out.println("Consuming up to " + count + " messages.");
            System.out.println("Specify a message count as the program argument if you wish to consume a different amount.");
        } else {
            count = Integer.parseInt(args[0]);
            System.out.println("Consuming up to " + count + " messages.");
        }

        try {
            // The configuration for the Qpid InitialContextFactory has been supplied in
            // a jndi.properties file in the classpath, which results in it being picked
            // up automatically by the InitialContext constructor.
            Context context = new InitialContext();

            ConnectionFactory factory = (ConnectionFactory) context.lookup("mycf");
            Destination queue = (Destination) context.lookup("q1p1");

            Connection connection = factory.createConnection(); //(System.getProperty("USER"), System.getProperty("PASSWORD"));
            connection.setExceptionListener(new MyExceptionListener());
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer messageConsumer = session.createConsumer(queue);

            long start = System.currentTimeMillis();

            int actualCount = 0;
            boolean deductTimeout = false;
            int timeout = 300;
            for (int i = 1; i <= count; i++, actualCount++) {
                Message message = messageConsumer.receive(timeout);
                if (message == null) {
                    System.out.println("Message " + i + " not received within timeout, stopping.");
                    deductTimeout = true;
                    break;
                }

                try {
                    final TextMessage tMessage = (TextMessage)message;
                    System.out.println("Received message: " + tMessage.getText());
                } catch (JMSException e) {
                    System.out.println("Error attempting to examine message. " + e.getMessage());
                    e.printStackTrace();
                }

                if (i % 100 == 0) {
                    System.out.println("Got message " + i);
                }
            }

            long finish = System.currentTimeMillis();
            long taken = finish - start;
            if (deductTimeout) {
                taken -= timeout;
            }
            System.out.println("Received " + actualCount + " messages in " + taken + "ms");

            connection.close();
        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        @Override
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
