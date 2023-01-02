package com.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9090)
                .addService(new BankService())
                .addService(new TransferService())
                .build();
        System.out.println("GRPC SERVER INICIADO NA PORTA : 9090");
        server.start();
        server.awaitTermination();
    }


}
