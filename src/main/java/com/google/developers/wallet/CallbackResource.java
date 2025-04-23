package com.google.developers.wallet;

import com.google.crypto.tink.apps.paymentmethodtoken.GooglePaymentsPublicKeysManager;
import com.google.crypto.tink.apps.paymentmethodtoken.PaymentMethodTokenRecipient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/callback")
public class CallbackResource {

    private static final String PUBLIC_KEY_URL = "https://pay.google.com/gp/m/issuer/keys";

    private static final String SENDER_ID = "GooglePayPasses";

    private static final String PROTOCOL = "ECv2SigningOnly";

    private static final GooglePaymentsPublicKeysManager keysManager = new GooglePaymentsPublicKeysManager.Builder()
            .setKeysUrl(PUBLIC_KEY_URL)
            .build();

    @ConfigProperty(name = "issuer-id")
    String issuerId;

    @POST
    public Response callback(String signedMessage) throws Exception {
        PaymentMethodTokenRecipient recipient =
                new PaymentMethodTokenRecipient.Builder()
                        .protocolVersion(PROTOCOL)
                        .fetchSenderVerifyingKeysWith(keysManager)
                        .senderId(SENDER_ID)
                        .recipientId(issuerId)
                        .build();

        String serializedJsonMessage = recipient.unseal(signedMessage);
        JsonObject jsonObject = new Gson().fromJson(serializedJsonMessage, JsonObject.class);

        System.out.println(jsonObject);

        return Response.ok().build();
    }

}
