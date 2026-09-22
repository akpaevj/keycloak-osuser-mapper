package ru.akpaev.keycloak;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

/**
 * Maps the {@code userPrincipalName} attribute ({@code user@domain.local}) into
 * the {@code osUser} claim in the Windows format required by 1C:Enterprise systems.
 */
public class UpnOsUserMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "upn-osuser-protocol-mapper";

    private static final String OSUSER_CLAIM = "osUser";
    private static final String UPN_ATTRIBUTE = "userPrincipalName";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addAttributeConfig(configProperties, UpnOsUserMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
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
        var osUser = getOsUser(userSession);
        if (!osUser.isEmpty()) {
            OIDCAttributeMapperHelper.mapClaim(token, mappingModel, osUser);
        }
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel,
                                            KeycloakSession session, UserSessionModel userSession,
                                            ClientSessionContext clientSessionCtx) {
        var osUser = getOsUser(userSession);
        if (!osUser.isEmpty()) {
            token.getOtherClaims().put(OSUSER_CLAIM, osUser);
            setClaim(token, mappingModel, userSession, session, clientSessionCtx);
        }
        return token;
    }

    @Override
    public IDToken transformIDToken(IDToken token, ProtocolMapperModel mappingModel,
                                    KeycloakSession session, UserSessionModel userSession,
                                    ClientSessionContext clientSessionCtx) {
        var osUser = getOsUser(userSession);
        if (!osUser.isEmpty()) {
            token.getOtherClaims().put(OSUSER_CLAIM, osUser);
            setClaim(token, mappingModel, userSession, session, clientSessionCtx);
        }
        return token;
    }

    private String getOsUser(UserSessionModel session) {
        var user = session.getUser();
        return user == null ? "" : toOsUser(user.getFirstAttribute(UPN_ATTRIBUTE));
    }

    /**
     * Converts a UPN ({@code user@domain.local}) into a Windows OS user name.
     * Returns an empty string when the input is {@code null} or not a valid UPN.
     */
    static String toOsUser(String userPrincipalName) {
        if (userPrincipalName == null) {
            return "";
        }
        var parts = userPrincipalName.split("@");
        if (parts.length < 2) {
            return "";
        }
        var domain = parts[1].split("\\.")[0];
        return domain + "\\" + parts[0];
    }
}
