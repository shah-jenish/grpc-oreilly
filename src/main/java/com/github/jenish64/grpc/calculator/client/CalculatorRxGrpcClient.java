package com.github.jenish64.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.RxCalculatorServiceGrpc;
import com.proto.calculator.RxCalculatorServiceGrpc.RxCalculatorServiceStub;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class CalculatorRxGrpcClient {

  public static void main(String[] args) {
    System.out.println("I am a GRPC client!");

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating blocking stub!");
    RxCalculatorServiceStub blockingStub = RxCalculatorServiceGrpc.newRxStub(channel);

    SumRequest request = SumRequest.newBuilder()
        .setFirst(123)
        .setSecond(234)
        .build();

    Single<SumRequest> rxRequest = Single.just(request);
    final Integer sum = rxRequest.as(blockingStub::sum).map(SumResponse::getSum).blockingGet();
    System.out.println(String.format("%s+%s=%s", request.getFirst(), request.getSecond(), sum));

    final PrimeNumberDecompositionRequest primeRequest =
        PrimeNumberDecompositionRequest.newBuilder()
        .setNumber(512)
        .build();

    // Create request
    final Single<PrimeNumberDecompositionRequest> rxPrimeRequest = Single.just(primeRequest);
    // Define response and error handlers
    final Consumer<PrimeNumberDecompositionResponse> responseConsumer =
        resp -> System.out.println(resp.getPrimeFactor());
    final Consumer<Throwable> responseErrorHandler = System.err::println;
    // Make the call, returns Flowable responses
    final Flowable<PrimeNumberDecompositionResponse> flowableResponses =
        rxPrimeRequest.as(blockingStub::decomposePrimeNumber);
    // Subscribe to the Flowable responses
    final Disposable disposableSubscribe = flowableResponses.subscribe(responseConsumer, responseErrorHandler);
    if (disposableSubscribe.isDisposed()) {
      disposableSubscribe.dispose();
    }

    System.out.println(Instant.now());
    System.out.println("Shutting down channel!");
    channel.shutdown();
  }
}