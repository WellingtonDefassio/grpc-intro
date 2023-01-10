package com.grpc.client.rpctypes;

import br.grpc.models.TransferRequest;
import br.grpc.models.TransferServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferClientTest {


    private TransferServiceGrpc.TransferServiceStub transferServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.transferServiceStub = TransferServiceGrpc.newStub(managedChannel);

    }

    @Test
    public void transfer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TransferStreamingResponse response = new TransferStreamingResponse(latch);
        StreamObserver<TransferRequest> requestStreamObserver = this.transferServiceStub.transfer(response);

        for (int i = 0; i < 1000; i++) {
            TransferRequest transferRequest = TransferRequest.newBuilder()
                    .setFromAccount(ThreadLocalRandom.current().nextInt(1, 11))
                    .setToAccount(ThreadLocalRandom.current().nextInt(1, 11))
                    .setAmount(ThreadLocalRandom.current().nextInt(1, 51))
                    .build();

            requestStreamObserver.onNext(transferRequest);
        }
        requestStreamObserver.onCompleted();
        latch.await();
    }


}
