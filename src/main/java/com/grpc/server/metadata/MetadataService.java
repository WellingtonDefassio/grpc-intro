package com.grpc.server.metadata;

import br.grpc.models.*;
import com.grpc.server.rpctypes.AccountDataBase;
import com.grpc.server.rpctypes.CashStreamingRequest;
import com.grpc.server.rpctypes.PixStreamingRequest;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;

public class MetadataService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();
        int amount = AccountDataBase.getBalance(accountNumber);
        UserRole userRole = ServerConstants.CTX_USER_ROLE.get();
        UserRole userRole1 = ServerConstants.CTX_USER_ROLE1.get();

        amount = UserRole.PRIME.equals(userRole) ? amount : (amount - 15);

        Balance balance = Balance.newBuilder()
                .setAmount(amount)
                .build();

        System.out.println(userRole + " : " + userRole1);
        //simulate time
//        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        responseObserver.onNext(balance);
        responseObserver.onCompleted();

    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount();
        int balance = AccountDataBase.getBalance(accountNumber);

        if(amount < 10 || (amount % 10) != 0) {
            Metadata metadata = new Metadata();
            Metadata.Key<WithdrawalError> errorKey = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
            WithdrawalError withdrawalError = WithdrawalError.newBuilder().setAmount(balance).setErrorMessage(ErrorMessage.ONLY_TEN_MULTIPLIES).build();
            metadata.put(errorKey, withdrawalError);
            responseObserver.onError( Status.FAILED_PRECONDITION.asRuntimeException(metadata));
            return;
        }

        if (balance < amount) {
            Metadata metadata = new Metadata();
            Metadata.Key<WithdrawalError> errorKey = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
            WithdrawalError withdrawalError = WithdrawalError.newBuilder().setAmount(balance).setErrorMessage(ErrorMessage.INSUFFICIENT_BALANCE).build();
            metadata.put(errorKey, withdrawalError);
            responseObserver.onError( Status.FAILED_PRECONDITION.asRuntimeException(metadata));
            return;
        }
        //all validations passed
        for (int i = 0; i < (amount / 10); i++) {
            Money money = Money.newBuilder().setValue(10).build();
            responseObserver.onNext(money);
            AccountDataBase.deductBalance(accountNumber, 10);

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
