package com.github.jenish64.grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc.GreetServiceImplBase;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class GreetServiceImpl extends GreetServiceImplBase {

  @Override
  public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
    final Greeting greeting = request.getGreeting();

    // Build the response
    GreetResponse response = GreetResponse.newBuilder().setResult(
        "Hello, " + greeting.getFirstName() + " "+ greeting.getLastName()
    ).build();

    // Send the response
    responseObserver.onNext(response);

    // Complete the RPC call
    responseObserver.onCompleted();
  }

  @Override
  public void greetManyTimes(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
    final Greeting greeting = request.getGreeting();

    for (int i = 0; i < 20; i++) {
      // Build the response
      GreetResponse response = GreetResponse.newBuilder().setResult(
          i + ": Hello, " + greeting.getFirstName() + " " + greeting.getLastName()
      ).build();

      // Send the response
      responseObserver.onNext(response);
      try {
        TimeUnit.SECONDS.sleep(1L);
      } catch (InterruptedException e) {
        System.err.println(e.getMessage());
        Thread.currentThread().interrupt();
      }
    }

    // Complete the RPC call
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<GreetRequest> longGreet(StreamObserver<GreetResponse> responseObserver) {

    return new StreamObserver<GreetRequest>() {

      final StringJoiner greetings = new StringJoiner("\n");
      int index = 1;

      @Override
      public void onNext(GreetRequest request) {
        final Greeting greeting = request.getGreeting();
        greetings.add((index++) + ": Hello, " + greeting.getFirstName() + " " + greeting.getLastName());
      }

      @Override
      public void onError(Throwable t) {
        // client sends an error
      }

      @Override
      public void onCompleted() {
        // Build the response
        GreetResponse response = GreetResponse.newBuilder().setResult(greetings.toString()).build();

        // Send the response
        responseObserver.onNext(response);
      }
    };
  }
}
