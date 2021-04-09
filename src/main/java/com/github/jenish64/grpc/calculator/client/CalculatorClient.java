package com.github.jenish64.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

  public static void main(String[] args) {
    System.out.println("I am a GRPC client!");

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating blocking stub!");
    CalculatorServiceBlockingStub blockingStub = CalculatorServiceGrpc.newBlockingStub(channel);

    SumRequest request = SumRequest.newBuilder()
        .setFirst(123)
        .setSecond(234)
        .build();

    SumResponse response = blockingStub.sum(request);
    System.out.println(String.format("%s+%s=%s", request.getFirst(), request.getSecond(), response.getSum()));

    blockingStub.decomposePrimeNumber(PrimeNumberDecompositionRequest.newBuilder()
        .setNumber(Integer.MAX_VALUE - 3)
        .build()).forEachRemaining(resp -> System.out.println(resp.getPrimeFactor()));

    System.out.println("Shutting down channel!");
    channel.shutdown();
  }
}