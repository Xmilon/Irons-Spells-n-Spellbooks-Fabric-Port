package io.redspace.ironslib.patreon.data;

import java.util.UUID;

public record ProfileResult(String username, UUID uuid, PatreonPermissions permissions) {
   public static final ProfileResult NULL = new ProfileResult("", null, PatreonPermissions.None);
}
