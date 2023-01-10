package com.grpc.server.rpctypes;

import br.grpc.models.Balance;
import br.grpc.models.Money;
import io.grpc.stub.StreamObserver;

public class PixStreamingRequest implements StreamObserver<Money> {

    StreamObserver<Balance> responseObserver;

    static Integer total = 0;

    public PixStreamingRequest(StreamObserver<Balance> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(Money money) {
        total += money.getValue();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder().setAmount(total).build();
        this.responseObserver.onNext(balance);
        this.responseObserver.onCompleted();
        total = 0;
    }
}
