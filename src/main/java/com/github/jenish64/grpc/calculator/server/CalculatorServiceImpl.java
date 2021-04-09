package com.github.jenish64.grpc.calculator.server;

import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceImplBase;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceImplBase {

  @Override
  public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
    SumResponse response = SumResponse.newBuilder()
        .setSum(request.getFirst() + request.getSecond())
        .build();

    responseObserver.onNext(response);

    responseObserver.onCompleted();
  }

  @Override
  public void decomposePrimeNumber(PrimeNumberDecompositionRequest request,
      StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {

    int number = request.getNumber();
    int divisor = 2;

    while (number > 1) {
      if (number % divisor == 0) {
        number = number / divisor;
        responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
            .setPrimeFactor(divisor)
            .build());
      } else {
        divisor += 1;
      }
    }

    responseObserver.onCompleted();
  }
}
