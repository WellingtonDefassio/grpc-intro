package com.grpc.client.rpctypes;

import br.grpc.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);

    }

    @Test
    public void balanceTest() {
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(9)
                .build();

        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
        System.out.println("Received: " + balance.getAmount());
    }

    @Test
    public void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(7)
                .setAmount(40)
                .build();
        this.blockingStub.withdraw(withdrawRequest)
                .forEachRemaining(money -> System.out.println("Received: " + money.getValue()));
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(10)
                .setAmount(60)
                .build();

        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> depositRequestStreamObserver = this.bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder().setAccountNumber(8).setAmount(10).build();
            depositRequestStreamObserver.onNext(depositRequest);
        }
        depositRequestStreamObserver.onCompleted();
        latch.await();
    }
    @Test
    public void pixStreamingRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<Money> moneyStreamObserver = this.bankServiceStub.multiPix(new PixStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            Money money = Money.newBuilder().setValue(new Random().nextInt(100)).build();
            moneyStreamObserver.onNext(money);
        }

        moneyStreamObserver.onCompleted();
        latch.await();
    }
}
