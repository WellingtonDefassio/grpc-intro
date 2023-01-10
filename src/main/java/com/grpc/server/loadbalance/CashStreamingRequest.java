package com.grpc.server.loadbalance;

import br.grpc.models.Balance;
import br.grpc.models.DepositRequest;
import com.grpc.server.rpctypes.AccountDataBase;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;

    public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver) {
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext(DepositRequest depositRequest) {

        int accountNumber = depositRequest.getAccountNumber();
        System.out.println("Received cash deposit for " + accountNumber);
        int amount = depositRequest.getAmount();
        this.accountBalance = AccountDataBase.addBalance(accountNumber, amount);

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder().setAmount(this.accountBalance).build();
        this.balanceStreamObserver.onNext(balance);
        this.balanceStreamObserver.onCompleted();
    }
}
