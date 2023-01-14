package com.grpc.server.deadline;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcDeadLineServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9095)
                .addService(new DeadlineService())
                .build();
        System.out.println("GRPC SERVER INICIADO NA PORTA : 9095");
        server.start();
        server.awaitTermination();
    }


}
