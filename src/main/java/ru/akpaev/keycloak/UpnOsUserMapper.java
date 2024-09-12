package ru.akpaev.keycloak;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

public class UpnOsUserMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "upn-osuser-protocol-mapper";
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addAttributeConfig(configProperties, UpnOsUserMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return "Token Mapper";
    }

    @Override
    public String getDisplayType() {
        return "UPN to OSUser mapper";
    }

    @Override
    public String getHelpText() {
        return "Map UPN to OSUser format, required by 1C:Enterprise systems";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel,
                            UserSessionModel userSession, KeycloakSession keycloakSession,
                            ClientSessionContext clientSessionCtx) {
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, getOsUser(userSession));
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        token.getOtherClaims().put("osUser", getOsUser(userSession));
        setClaim(token, mappingModel, userSession, session, clientSessionCtx);
        return token;
    }

    @Override
    public IDToken transformIDToken(IDToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        token.getOtherClaims().put("osUser", getOsUser(userSession));
        setClaim(token, mappingModel, userSession, session, clientSessionCtx);
        return token;
    }

    private String getOsUser(UserSessionModel session) {
        var upn = session.getUser().getFirstAttribute("userPrincipalName");
        var data = upn.split("@");
        return data[1].split("\\.")[0] + "\\" + data[0];
    }
}
