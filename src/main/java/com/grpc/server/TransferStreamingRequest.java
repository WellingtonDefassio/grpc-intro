package com.grpc.server;

import br.grpc.models.Account;
import br.grpc.models.TransferRequest;
import br.grpc.models.TransferResponse;
import br.grpc.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

    private StreamObserver<TransferResponse> response;

    public TransferStreamingRequest(StreamObserver<TransferResponse> response) {
        this.response = response;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        int fromAccount = transferRequest.getFromAccount();
        int toAccount = transferRequest.getToAccount();
        int amount = transferRequest.getAmount();
        int balance = AccountDataBase.getBalance(fromAccount);
        TransferStatus status = TransferStatus.FAILED;
        if (balance >= amount && fromAccount != toAccount) {
            AccountDataBase.deductBalance(fromAccount, amount);
            AccountDataBase.addBalance(toAccount, amount);
            status = TransferStatus.SUCCESS;
        }
        TransferResponse transferResponse = TransferResponse.newBuilder()
                .setStatus(status)
                .addAccounts(Account.newBuilder()
                        .setAccountNumber(fromAccount)
                        .setAmount(AccountDataBase.getBalance(fromAccount))
                        .build()
                )
                .addAccounts(Account.newBuilder()
                        .setAccountNumber(toAccount)
                        .setAmount(AccountDataBase.getBalance(toAccount))
                        .build()).build();

        this.response.onNext(transferResponse);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        AccountDataBase.printAccountDetails();
        this.response.onCompleted();
    }
}
