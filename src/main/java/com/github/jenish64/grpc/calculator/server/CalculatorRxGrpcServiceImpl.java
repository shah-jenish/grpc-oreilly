package com.github.jenish64.grpc.calculator.server;

import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.RxCalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;

/**
 * 1. Try rxjava 3 -
 * https://github.com/salesforce/reactive-grpc/pull/219 RxJava 3 PR which is currently not even supported by sales force
 *    What are the implications???
 *
 * 2. Expand the emitter to have onError, OnDispose....
 *
 * 3. Take the FE mock feed service and use the rxjava service stubs
 *
 * Feed Sdk options:
 * Option 1: Feed service change to generate salesforce reactive grpc stubs
 * Option 2: Modify Feed Sdk to convert the List<FeedEvent> to Flowable<FeedEvent>
 *            - No service change
 * Option 3: No Feed Sdk change...Just add a new Example showing how to use RxJava
 *
 */
public class CalculatorRxGrpcServiceImpl extends RxCalculatorServiceGrpc.CalculatorServiceImplBase {

  @Override
  public Single<SumResponse> sum(Single<SumRequest> request) {
    return request.map(protoRequest -> SumResponse.newBuilder()
        .setSum(protoRequest.getFirst() + protoRequest.getSecond())
        .build());
  }

  @Override
  public Flowable<PrimeNumberDecompositionResponse> decomposePrimeNumber(Single<PrimeNumberDecompositionRequest> request) {

    final FlowableOnSubscribe<PrimeNumberDecompositionResponse> flowableOnSubscribe = emitter -> {
      int number = request.blockingGet().getNumber();
      int divisor = 2;

      while (number > 1) {
        if (emitter.isCancelled()) {
          System.err.println("Is Cancelled Invoked!!!");
          break;
        }
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
      emitter.onComplete();
    };

    return Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
  }
}
