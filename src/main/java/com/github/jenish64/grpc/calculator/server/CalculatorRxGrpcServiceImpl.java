package com.github.jenish64.grpc.calculator.server;

import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.RxCalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class CalculatorRxGrpcServiceImpl extends RxCalculatorServiceGrpc.CalculatorServiceImplBase {

  @Override
  public Single<SumResponse> sum(Single<SumRequest> request) {
    return request.map(protoRequest -> SumResponse.newBuilder()
        .setSum(protoRequest.getFirst() + protoRequest.getSecond())
        .build());
  }

  @Override
  public Flowable<PrimeNumberDecompositionResponse> decomposePrimeNumber(Single<PrimeNumberDecompositionRequest> request) {
    final FlowableOnSubscribe<PrimeNumberDecompositionResponse> flowableOnSubscribe = new FlowableOnSubscribe<PrimeNumberDecompositionResponse>() {
      @Override
      public void subscribe(FlowableEmitter<PrimeNumberDecompositionResponse> emitter)
          throws Exception {
        int number = request.blockingGet().getNumber();
        int divisor = 2;

        while (number > 1) {
          if (number % divisor == 0) {
            number = number / divisor;
            System.out.println(divisor);
            emitter.onNext(PrimeNumberDecompositionResponse.newBuilder()
                .setPrimeFactor(divisor)
                .build());
          } else {
            divisor += 1;
          }
        }

        if (!emitter.isCancelled()) {
          emitter.onComplete();
        }
      }
    };
    return Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
  }
}
