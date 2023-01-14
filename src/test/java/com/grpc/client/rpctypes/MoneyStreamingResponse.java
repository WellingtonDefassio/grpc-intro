package com.grpc.client.rpctypes;

import br.grpc.models.Money;
import br.grpc.models.WithdrawalError;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

import static com.grpc.client.metadata.ClientConstants.WITHDRAWAL_ERROR_KEY;

public class MoneyStreamingResponse implements StreamObserver<Money> {

    private CountDownLatch latch;


    public MoneyStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Money money) {
        System.out.println("Received async: " + money.getValue());
    }

    @Override
    public void onError(Throwable throwable) {
        Metadata metadata = Status.trailersFromThrowable(throwable);
        WithdrawalError withdrawalError = metadata.get(WITHDRAWAL_ERROR_KEY);
        System.out.println(withdrawalError.getAmount() + ":" + withdrawalError.getErrorMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("server is Done");
        latch.countDown();
    }

}
