package com.mojang.authlib.yggdrasil;

import java.net.*;

import com.google.common.base.Charsets;
import com.mojang.util.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import com.google.common.cache.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.*;
import java.security.*;
import java.security.spec.*;
import com.mojang.authlib.yggdrasil.request.*;
import com.mojang.authlib.properties.*;
import com.mojang.authlib.exceptions.*;
import java.util.*;
import com.mojang.authlib.minecraft.*;
import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.*;
import com.google.gson.*;
import com.mojang.authlib.yggdrasil.response.*;
import com.mojang.authlib.*;
import org.apache.logging.log4j.*;
import com.google.common.collect.*;

public class YggdrasilMinecraftSessionService extends HttpMinecraftSessionService
{
    private static final Logger LOGGER;
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/";
    private static final URL JOIN_URL;
    private static final URL CHECK_URL;
    private final PublicKey publicKey;
    private final Gson gson;
    private final LoadingCache<GameProfile, GameProfile> insecureProfiles;
    
    protected YggdrasilMinecraftSessionService(final YggdrasilAuthenticationService authenticationService) {
        super(authenticationService);
        this.gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
        this.insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6L, TimeUnit.HOURS).build(new CacheLoader<GameProfile, GameProfile>() {
            @Override
            public GameProfile load(final GameProfile key) throws Exception {
                return YggdrasilMinecraftSessionService.this.fillGameProfile(key, false);
            }
        });
        try {
            final X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")));
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
        }
        catch (Exception e) {
            throw new Error("Missing/invalid yggdrasil public key!");
        }
    }
    
    @Override
    public void joinServer(final GameProfile profile, final String authenticationToken, final String serverId) throws AuthenticationException {
        final JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
        request.accessToken = authenticationToken;
        request.selectedProfile = profile.getId();
        request.serverId = serverId;
        this.getAuthenticationService().makeRequest(YggdrasilMinecraftSessionService.JOIN_URL, request, Response.class);
    }
    
    @Override
    public GameProfile hasJoinedServer(final GameProfile user, final String serverId) throws AuthenticationUnavailableException {
        final Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("username", user.getName());
        arguments.put("serverId", serverId);
        final URL url = HttpAuthenticationService.concatenateURL(YggdrasilMinecraftSessionService.CHECK_URL, HttpAuthenticationService.buildQuery(arguments));
        try {
            final HasJoinedMinecraftServerResponse response = this.getAuthenticationService().makeRequest(url, null, HasJoinedMinecraftServerResponse.class);
            if (response != null && response.getId() != null) {
                final GameProfile result = new GameProfile(response.getId(), user.getName());
                if (response.getProperties() != null) {
                    result.getProperties().putAll(response.getProperties());
                }
                return result;
            }
            return null;
        }
        catch (AuthenticationUnavailableException e) {
            throw e;
        }
        catch (AuthenticationException e2) {
            return null;
        }
    }
    
    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(final GameProfile profile, final boolean requireSecure) {
        final Property textureProperty = Iterables.getFirst((profile.getProperties()).get("textures"), null);
        if (textureProperty == null) {
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        if (requireSecure) {
            if (!textureProperty.hasSignature()) {
                YggdrasilMinecraftSessionService.LOGGER.error("Signature is missing from textures payload");
                throw new InsecureTextureException("Signature is missing from textures payload");
            }
            if (!textureProperty.isSignatureValid(this.publicKey)) {
                YggdrasilMinecraftSessionService.LOGGER.error("Textures payload has been tampered with (signature invalid)");
                throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
            }
        }
        MinecraftTexturesPayload result;
        try {
            final String json = new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
            result = this.gson.fromJson(json, MinecraftTexturesPayload.class);
        }
        catch (JsonParseException e) {
            YggdrasilMinecraftSessionService.LOGGER.error("Could not decode textures payload", e);
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        return (result.getTextures() == null) ? new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>() : result.getTextures();
    }
    
    @Override
    public GameProfile fillProfileProperties(final GameProfile profile, final boolean requireSecure) {
        if (profile.getId() == null) {
            return profile;
        }
        if (!requireSecure) {
            return this.insecureProfiles.getUnchecked(profile);
        }
        return this.fillGameProfile(profile, true);
    }
    
    protected GameProfile fillGameProfile(final GameProfile profile, final boolean requireSecure) {
        try {
            URL url = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(profile.getId()));
            url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !requireSecure);
            final MinecraftProfilePropertiesResponse response = this.getAuthenticationService().makeRequest(url, null, MinecraftProfilePropertiesResponse.class);
            if (response == null) {
                YggdrasilMinecraftSessionService.LOGGER.debug("Couldn't fetch profile properties for " + profile + " as the profile does not exist");
                return profile;
            }
            final GameProfile result = new GameProfile(response.getId(), response.getName());
            result.getProperties().putAll(response.getProperties());
            profile.getProperties().putAll(response.getProperties());
            YggdrasilMinecraftSessionService.LOGGER.debug("Successfully fetched profile properties for " + profile);
            return result;
        }
        catch (AuthenticationException e) {
            YggdrasilMinecraftSessionService.LOGGER.warn("Couldn't look up profile properties for " + profile, e);
            return profile;
        }
    }
    
    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService)super.getAuthenticationService();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        JOIN_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/join");
        CHECK_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/hasJoined");
    }
}
