package com.github.jenish64.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.proto.greet.GreetServiceGrpc.GreetServiceStub;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

  public static void main(String[] args) throws InterruptedException {
    System.out.println("I am a GRPC client!");

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating blocking stub!");
    GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);

    System.out.println("Creating non-blocking stub!");
    GreetServiceStub nonBlockingStub = GreetServiceGrpc.newStub(channel);

    GreetRequest request = GreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder()
            .setFirstName("J")
            .setLastName("S")
            .build())
        .build();

    GreetResponse response = blockingStub.greet(request);
    System.out.println(response.getResult());

    blockingStub.greetManyTimes(request).forEachRemaining(
        resp -> System.out.println(resp.getResult())
    );

    final boolean[] gotResponse = {false};
    final StreamObserver<GreetRequest> requestStreamObserver = nonBlockingStub
        .longGreet(new StreamObserver<GreetResponse>() {
          @Override
          public void onNext(GreetResponse resp) {
            System.out.println(resp.getResult());
            gotResponse[0] = true;
          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onCompleted() {

          }
        });

    for (int i = 0; i < 20; i++) {
      request = GreetRequest.newBuilder()
          .setGreeting(Greeting.newBuilder()
              .setFirstName("J" + i)
              .setLastName("S" + i)
              .build())
          .build();
      requestStreamObserver.onNext(request);
    }

    requestStreamObserver.onCompleted();

    while (!gotResponse[0]) {
      TimeUnit.SECONDS.sleep(1);
    }

    System.out.println("Shutting down channel!");
    channel.shutdown();
  }
}