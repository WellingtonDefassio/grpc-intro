package com.grpc.server.metadata;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcMetadataServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9096)
//                .intercept(new AuthInterceptor())
                .addService(new MetadataService())
                .build();
        System.out.println("GRPC SERVER INICIADO NA PORTA : 9096");
        server.start();
        server.awaitTermination();
    }


}
