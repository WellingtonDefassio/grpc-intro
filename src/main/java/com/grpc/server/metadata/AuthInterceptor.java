package com.grpc.server.metadata;

import io.grpc.*;

import java.util.Objects;

import static com.grpc.server.metadata.ServerConstants.TOKEN;
import static com.grpc.server.metadata.ServerConstants.USER_TOKEN;



public class AuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String secret = metadata.get(USER_TOKEN);
        if (validateToken(secret)) {
            System.out.println("Authenticado com sucesso!");
            UserRole userRole = extractUserRole(secret);
            Context context = Context.current().withValue(ServerConstants.CTX_USER_ROLE, userRole);
            return Contexts.interceptCall(context, serverCall, metadata,serverCallHandler);
//            return serverCallHandler.startCall(serverCall, metadata);
        } else {
            Status status = Status.UNAUTHENTICATED.withDescription("invalid token");
            System.out.println("Erro de authenticação");
            serverCall.close(status, metadata);
        }
        return new ServerCall.Listener<ReqT>() {
        };
    }

    private boolean validateToken(String token) {
        return Objects.nonNull(token) && token.startsWith("user-secret-3") || token.startsWith("user-secret-2");
    }

    private UserRole extractUserRole(String jwt) {
        return jwt.endsWith("prime") ? UserRole.PRIME : UserRole.STANDARD;
    }
}
