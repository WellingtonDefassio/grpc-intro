package com.grpc.client.metadata;

import br.grpc.models.WithdrawalError;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;

public class ClientConstants {
    public static final Metadata.Key<String> USER_TOKEN = Metadata.Key.of("user-token", Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> USER_ROLE = Metadata.Key.of("user-role", Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<WithdrawalError> WITHDRAWAL_ERROR_KEY = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
    private static final Metadata METADATA = new Metadata();

    static {
        METADATA.put(
             Metadata.Key.of("cliente-token", Metadata.ASCII_STRING_MARSHALLER),
                "secret"
        );

    }
    public static Metadata getClientToken(){
        return METADATA;
    }
}
