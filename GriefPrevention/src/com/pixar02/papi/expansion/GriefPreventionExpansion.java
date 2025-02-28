package com.pixar02.papi.expansion;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;

public class GriefPreventionExpansion extends PlaceholderExpansion implements Configurable {

    private GriefPrevention plugin;

    /**
     * Since this expansion requires api access to the plugin "SomePlugin" we must
     * check if "SomePlugin" is on the server in this method
     */
    @Override
    public boolean canRegister() {
        return (plugin = (GriefPrevention) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }

    /**
     * The name of the person who created this expansion should go here
     */
    @Override
    public String getAuthor() {
        return "pixar02";
    }

    /**
     * The placeholder identifier should go here This is what tells PlaceholderAPI
     * to call our onPlaceholderRequest method to obtain a value if a placeholder
     * starts with our identifier. This must be unique and can not contain % or _
     */
    @Override
    public String getIdentifier() {
        return "griefprevention";
    }

    /**
     * if an expansion requires another plugin as a dependency, the proper name of
     * the dependency should go here. Set this to null if your placeholders do not
     * require another plugin be installed on the server for them to work. This is
     * extremely important to set if you do have a dependency because if your
     * dependency is not loaded when this hook is registered, it will be added to a
     * cache to be registered when plugin: "getPlugin()" is enabled on the server.
     */
    @Override
    public String getRequiredPlugin() {
        return "GriefPrevention";
    }

    /**
     * This is the version of this expansion
     */
    @Override
    public String getVersion() {
        return "1.8.1";
    }

    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<String, Object>();
        defaults.put("formatting.thousands", "k");
        defaults.put("formatting.millions", "M");
        defaults.put("formatting.billions", "B");
        defaults.put("formatting.trillions", "T");
        defaults.put("formatting.quadrillions", "Q");

        defaults.put("color.enemy", "&4");
        defaults.put("color.trusted", "&a");
        defaults.put("color.neutral", "&7");

        defaults.put("translate.unclaimed", "Unclaimed");
        defaults.put("translate.not-owner", "You don't own this claim!");
        return defaults;
    }

    @Override
    public List<String> getPlaceholders() {
        List<String> placeholders = new ArrayList<>();
        placeholders.add("%griefprevention_muted%");
        placeholders.add("%griefprevention_claims%");
        placeholders.add("%griefprevention_claims_formatted%");
        placeholders.add("%griefprevention_bonusclaims%");
        placeholders.add("%griefprevention_bonusclaims_formatted%");
        placeholders.add("%griefprevention_usedclaimblocks%");
        placeholders.add("%griefprevention_usedclaimblocks_formatted%");
        placeholders.add("%griefprevention_accruedclaims%");
        placeholders.add("%griefprevention_accruedclaims_formatted%");
        placeholders.add("%griefprevention_accruedclaims_limit%");
        placeholders.add("%griefprevention_totalclaims%");
        placeholders.add("%griefprevention_totalclaims_formatted%");
        placeholders.add("%griefprevention_claimedblocks_total%");
        placeholders.add("%griefprevention_claimedblocks_current%");
        placeholders.add("%griefprevention_remainingclaims%");
        placeholders.add("%griefprevention_remainingclaims_formatted%");
        placeholders.add("%griefprevention_currentclaim_player_is_owner%");
        placeholders.add("%griefprevention_currentclaim_player_can_access%");
        placeholders.add("%griefprevention_inclaim%");
        placeholders.add("%griefprevention_currentclaim_ownername%");
        placeholders.add("%griefprevention_currentclaim_ownername_color%");
        return placeholders;
    }

    /**
     * This is the method called when a placeholder with our identifier is found and
     * needs a value We specify the value identifier in this method
     */
    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
        if (!p.isOnline() || p == null) {
            return "";
        }

        Player player = p.getPlayer();

        if (player == null || !player.isOnline()) {
            return "";
        }

        DataStore DataS = plugin.dataStore;
        PlayerData pd = DataS.getPlayerData(player.getUniqueId());

        // %griefprevention_muted%
        if (identifier.equals("muted")){
            if (DataS.isSoftMuted(player.getUniqueId())){
                return ChatColor.translateAlternateColorCodes('&',
                        getString("translate.muted", "Muted!"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("translate.not_muted", "Not Muted!"));
            }
        }

        /*
         %griefprevention_claims%
         %griefprevention_claims_formatted%
        */
        if (identifier.equals("claims")) {
            return String.valueOf(pd.getClaims().size());
        } else if (identifier.equals("claims_formatted")) {
            return fixMoney(pd.getClaims().size());
        }

        // %griefprevention_bonusclaims%
        // %griefprevention_bonusclaims_formatted%
        if (identifier.equals("bonusclaims")) {
            return String.valueOf(pd.getBonusClaimBlocks());
        } else if (identifier.equals("bonusclaims_formatted")) {
            return fixMoney(pd.getBonusClaimBlocks());
        }


         // %griefprevention_usedclaimblocks%
         // %griefprevention_usedclaimblocks_formatted%
        if (identifier.startsWith("usedclaimblocks")) {
            int totalClaims = pd.getClaims().size() + pd.getAccruedClaimBlocks();
            int remainingClaims = pd.getRemainingClaimBlocks();

            int usedClaims = totalClaims - remainingClaims;
            return identifier.endsWith("formatted") ? fixMoney(usedClaims) : String.valueOf(usedClaims);
        }

        /*
         %griefprevention_accruedclaims%
         %griefprevention_accruedclaims_formatted%
        */
        if (identifier.equals("accruedclaims")) {
            return String.valueOf(pd.getAccruedClaimBlocks());
        } else if (identifier.equals("accruedclaims_formatted")) {
            return fixMoney(pd.getAccruedClaimBlocks());
        }

        // %griefprevention_accruedclaims_limit%
        if (identifier.equals("accruedclaims_limit")) {
            return String.valueOf(pd.getAccruedClaimBlocksLimit());
        }

        // %griefprevention_totalclaims%
        // %griefprevention_totalclaims_formatted%
        if (identifier.equals("totalclaims")) {
            return String.valueOf(pd.getAccruedClaimBlocks() + pd.getBonusClaimBlocks());
        } else if (identifier.equals("totalclaims_formatted")) {
            return fixMoney(pd.getAccruedClaimBlocks() + pd.getBonusClaimBlocks());
        }

        //%griefprevention_claimedblocks_total%
        if (identifier.equals("claimedblocks_total")) {
            int blocks = 0;
            for (Claim c : pd.getClaims()) {
                blocks += c.getArea();
            }
            return String.valueOf(blocks);
        }

        //%griefprevention_claimedblocks_current%
        if (identifier.equals("claimedblocks_current")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("translate.unclaimed", "Unclaimed!"));
            } else if (Objects.equals(claim.getOwnerName(), p.getName())) {
                return String.valueOf(claim.getArea());
            }
            return ChatColor.translateAlternateColorCodes('&',
                    getString("translate.not-owner", "You don't own this claim!"));
        }

        /*
         %griefprevention_remainingclaims%
         %griefprevention_remainingclaims_formatted%
        */
        if (identifier.equals("remainingclaims")) {
            return String.valueOf(pd.getRemainingClaimBlocks());
        } else if (identifier.equals("remainingclaims_formatted")) {
            return fixMoney(pd.getRemainingClaimBlocks());
        }

        // %griefprevention_inclaim%
        if (identifier.equals("inclaim")) {
            return String.valueOf(DataS.getClaimAt(player.getLocation(), true, null) != null);
        }

        // %griefprevention_currentclaim_player_is_owner%
        if (identifier.equals("currentclaim_player_is_owner")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return String.valueOf(false);
            }

            return String.valueOf(Objects.equals(claim.getOwnerName(), p.getName()));
        }

        // %griefprevention_currentclaim_player_can_access%
        if (identifier.equals("currentclaim_player_can_access")) {
            try {
                Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
                if (claim == null) {
                    return String.valueOf(false);
                }

                if (claim.getOwnerName().equals(player.getName())) {
                    return String.valueOf(true);

                } else {
                    ClaimPermission claimPermission = claim.getPermission(String.valueOf(player.getUniqueId()));
                    if (claimPermission == null) {
                        return String.valueOf(false);
                    } else {
                        return String.valueOf(claimPermission.isGrantedBy(ClaimPermission.Access) ||
                                claimPermission.isGrantedBy(ClaimPermission.Build) ||
                                claimPermission.isGrantedBy(ClaimPermission.Manage) ||
                                claimPermission.isGrantedBy(ClaimPermission.Edit) ||
                                claimPermission.isGrantedBy(ClaimPermission.Inventory));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        // %griefprevention_currentclaim_ownername_color%
        // %griefprevention_currentclaim_ownername%
        if (identifier.equals("currentclaim_ownername")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("translate.unclaimed", "Unclaimed!"));
            } else {
                return ChatColor.translateAlternateColorCodes('&', claim.getOwnerName());
            }

        } else if (identifier.equals("currentclaim_ownername_color")) {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        getString("color.neutral", "")
                                + getString("translate.unclaimed", "Unclaimed!"));
            } else {

                try {
                    ClaimPermission claimPermission = claim.getPermission(String.valueOf(player.getUniqueId()));
                    if (claimPermission != null && claimPermission.isGrantedBy(ClaimPermission.Access)) {
                        //Trusted
                        return ChatColor.translateAlternateColorCodes('&',
                                getString("color.trusted", "") + claim.getOwnerName());
                    } else {
                        // not trusted
                        return ChatColor.translateAlternateColorCodes('&',
                                getString("color.enemy", "") + String.valueOf(claim.getOwnerName()));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }

        return null;
    }

    private String fixMoney(double d) {
        if (d < 1000L) {
            return format(d);
        }
        if (d < 1000000L) {
            return format(d / 1000L) + getString("formatting.thousands", "k");
        }
        if (d < 1000000000L) {
            return format(d / 1000000L) + getString("formatting.millions", "m");
        }
        if (d < 1000000000000L) {
            return format(d / 1000000000L) + getString("formatting.billions", "b");
        }
        if (d < 1000000000000000L) {
            return format(d / 1000000000000L) + getString("formatting.trillions", "t");
        }
        if (d < 1000000000000000000L) {
            return format(d / 1000000000000000L) + getString("formatting.quadrillions", "q");
        }
        return toLong(d);
    }

    private String toLong(double amt) {
        long send = (long) amt;
        return String.valueOf(send);
    }

    private String format(double d) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(d);
    }
}