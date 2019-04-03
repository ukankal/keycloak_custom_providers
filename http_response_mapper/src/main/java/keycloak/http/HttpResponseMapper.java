package keycloak.http;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
// import org.keycloak.models.ClientSessionContext;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.representations.IDToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/*
 * Our own example protocol mapper.
 */
public class HttpResponseMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    /*
     * A config which keycloak uses to display a generic dialog to configure the token.
     */
    private static final List<ProviderConfigProperty> configProperties = new ArrayList();
    private static String targetUrl;
    private static String tokenClaimName;
    HttpClient httpClient;

    /*
     * The ID of the token mapper. Is public, because we need this id in our data-setup project to
     * configure the protocol mapper in keycloak.
     */
    public static final String PROVIDER_ID = "http-response-mapper";

    static {
        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, HttpResponseMapper.class);
    }

    public void HelloWorldMapper(KeycloakSession session) {
        this.httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
    }

    @Override
    public void init(Config.Scope config) {
        targetUrl = config.get("targetUrl");
        tokenClaimName = config.get("tokenClaimName");
    }

    @Override
    public String getDisplayCategory() {
        return "Token mapper";
    }

    @Override
    public String getDisplayType() {
        return "Http Response Mapper";
    }

    @Override
    public String getHelpText() {
        return "Adds response from the target url to the claim";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    // @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession) {
        // adds our data to the token. Uses the parameters like the claim name which were set by the user
        // when this protocol mapper was configured in keycloak. Note that the parameters which can
        // be configured in keycloak for this protocol mapper were set in the static intializer of this class.
        //
        // Sets a static "Hello world" string, but we could write a dynamic value like a group attribute here too.
        HttpPost httpPost = new HttpPost(this.targetUrl);
        httpPost.setHeader("Content-Type", "application/json");
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("userId", userSession.getId()));
        postParameters.add(new BasicNameValuePair("userRealm", userSession.getRealm().getName()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = this.httpClient.execute(httpPost);
            OIDCAttributeMapperHelper.mapClaim(token, mappingModel, response);
        }
        catch (IOException e) {
            // Do something here
        }

    }


    public static ProtocolMapperModel create(String name, String claimType,
                                      boolean accessToken, boolean idToken) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<String, String>();
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, tokenClaimName);
        config.put(targetUrl, targetUrl);
        mapper.setConfig(config);
        return mapper;
    }

}
