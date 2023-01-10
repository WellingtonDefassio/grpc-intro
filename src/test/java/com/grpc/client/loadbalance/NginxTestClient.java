package com.grpc.client.loadbalance;

import br.grpc.models.Balance;
import br.grpc.models.BalanceCheckRequest;
import br.grpc.models.BankServiceGrpc;
import br.grpc.models.DepositRequest;
import com.grpc.client.rpctypes.BalanceStreamObserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NginxTestClient {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("192.168.0.109", 8585)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);

    }

    @Test
    public void balanceTest() {
        for (int i = 0; i < 100; i++) {
            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(ThreadLocalRandom.current().nextInt(1,10))
                    .build();

            Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
            System.out.println("Received: " + balance.getAmount());
        }
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

}
