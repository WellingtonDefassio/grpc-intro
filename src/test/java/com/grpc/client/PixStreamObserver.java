package com.grpc.client;

import br.grpc.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class PixStreamObserver implements StreamObserver<Balance> {
    private CountDownLatch latch;

    public PixStreamObserver(CountDownLatch latch) {

        this.latch = latch;
    }

    @Override
    public void onNext(Balance balance) {

        //todo poderia ser salvar no banco.
        System.out.println("Total de pix feito: " + balance.getAmount());
        latch.countDown();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        System.out.println("Fim da transferencia..");
        latch.countDown();
    }
}
