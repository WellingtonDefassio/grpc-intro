package com.grpc.server.loadbalance;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServerBalance2 {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9091)
                .addService(new BankService())
                .build();
        System.out.println("GRPC SERVER INICIADO NA PORTA : 9091");
        server.start();
        server.awaitTermination();
    }


}
