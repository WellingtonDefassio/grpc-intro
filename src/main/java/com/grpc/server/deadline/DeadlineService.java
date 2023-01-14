package com.grpc.server.deadline;

import br.grpc.models.*;
import com.google.common.util.concurrent.Uninterruptibles;
import com.grpc.server.rpctypes.AccountDataBase;
import com.grpc.server.rpctypes.CashStreamingRequest;
import com.grpc.server.rpctypes.PixStreamingRequest;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class DeadlineService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDataBase.getBalance(accountNumber))
                .build();
        //simulate time
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        responseObserver.onNext(balance);
        responseObserver.onCompleted();

    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount();
        int balance = AccountDataBase.getBalance(accountNumber);


        if(balance< amount) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Not balance enough to draw. balance: " + balance + " amount: " + amount);
            responseObserver.onError(status.asRuntimeException());
            return;
        }
        //all validations passed
        for (int i = 0; i < (amount/10); i++) {
            Money money = Money.newBuilder().setValue(10).build();
            //simulate time
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
            if(!Context.current().isCancelled()) {
                responseObserver.onNext(money);
                System.out.println("Delivery $10");
                AccountDataBase.deductBalance(accountNumber, 10);
            } else {
                break;
            }
        }
        System.out.println("Completed");
        responseObserver.onCompleted();

    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
    }

    @Override
    public StreamObserver<Money> multiPix(StreamObserver<Balance> responseObserver) {
        return new PixStreamingRequest(responseObserver);
    }
}
