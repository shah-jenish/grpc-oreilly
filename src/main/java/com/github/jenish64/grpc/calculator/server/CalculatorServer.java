package com.github.jenish64.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class CalculatorServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("Calculator Server!");

    Server server = ServerBuilder.forPort(50051)
        .addService(new CalculatorServiceImpl())
        .addService(new CalculatorRxGrpcServiceImpl())
        .build();

    server.start();
    System.out.println("Running...");

    Runtime.getRuntime().addShutdownHook(new Thread( () -> {
      System.out.println("Received shutdown request!");
      server.shutdown();
      System.out.println("Successfuly shutdown server!");
    }));

    server.awaitTermination();
  }
}