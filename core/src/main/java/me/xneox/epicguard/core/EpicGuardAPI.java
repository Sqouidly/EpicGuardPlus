/*
 * EpicGuardPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EpicGuardPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.xneox.epicguard.core;

import java.util.Collection;
import java.util.Set;
import me.xneox.epicguard.core.manager.AttackManager;
import me.xneox.epicguard.core.manager.BypassManager;
import me.xneox.epicguard.core.manager.GeoManager;
import me.xneox.epicguard.core.storage.AddressMeta;
import me.xneox.epicguard.core.storage.StorageManager;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A singleton API class which can be safely used by other projects.
 */
public class EpicGuardAPI {

  public static final EpicGuardAPI INSTANCE = new EpicGuardAPI();
  private EpicGuard epicGuard;

  /**
   * The {@link GeoManager} class contains methods that you may find useful if you want to check
   * country/city of an address.
   *
   * @return An instance of {@link GeoManager}.
   */
  @NotNull
  public GeoManager geoManager() {
    checkAvailability();
    return this.epicGuard.geoManager();
  }

  /**
   * @return The storage manager, which is used for various things regarding address data.
   */
  @NotNull
  public StorageManager storageManager() {
    checkAvailability();
    return this.epicGuard.storageManager();
  }

  /**
   * @return The attack manager which contains methods for managing the attack status.
   */
  public AttackManager attackManager() {
    checkAvailability();
    return this.epicGuard.attackManager();
  }

  /**
   * @return An immutable Collection which contains whitelisted addresses.
   */
  @NotNull
  public Collection<String> whitelistedAddresses() {
    checkAvailability();
    return this.epicGuard.storageManager().viewAddresses(AddressMeta::whitelisted);
  }

  /**
   * @return An immutable Collection which contains blacklisted addresses.
   */
  @NotNull
  public Collection<String> blacklistedAddresses() {
    checkAvailability();
    return this.epicGuard.storageManager().viewAddresses(AddressMeta::blacklisted);
  }

  /**
   * @return The bypass manager for managing runtime bypass lists.
   */
  @NotNull
  public BypassManager bypassManager() {
    checkAvailability();
    return this.epicGuard.bypassManager();
  }

  // ========================
  //    BYPASS METHODS
  // ========================

  /**
   * Adds an IP address to the runtime bypass list.
   * Players from this address will skip all checks.
   *
   * @param address The IP address to bypass.
   */
  public void addBypassAddress(@NotNull String address) {
    checkAvailability();
    this.epicGuard.bypassManager().addBypassAddress(address);
  }

  /**
   * Removes an IP address from the runtime bypass list.
   *
   * @param address The IP address to remove.
   */
  public void removeBypassAddress(@NotNull String address) {
    checkAvailability();
    this.epicGuard.bypassManager().removeBypassAddress(address);
  }

  /**
   * Checks if an IP address is in the runtime bypass list.
   *
   * @param address The IP address to check.
   * @return true if the address is bypassed.
   */
  public boolean isAddressBypassed(@NotNull String address) {
    checkAvailability();
    return this.epicGuard.bypassManager().isAddressBypassed(address);
  }

  /**
   * Adds a nickname to the runtime bypass list.
   * Players with this nickname will skip all checks.
   *
   * @param nickname The nickname to bypass.
   */
  public void addBypassNickname(@NotNull String nickname) {
    checkAvailability();
    this.epicGuard.bypassManager().addBypassNickname(nickname);
  }

  /**
   * Removes a nickname from the runtime bypass list.
   *
   * @param nickname The nickname to remove.
   */
  public void removeBypassNickname(@NotNull String nickname) {
    checkAvailability();
    this.epicGuard.bypassManager().removeBypassNickname(nickname);
  }

  /**
   * Checks if a nickname is in the runtime bypass list.
   *
   * @param nickname The nickname to check.
   * @return true if the nickname is bypassed.
   */
  public boolean isNicknameBypassed(@NotNull String nickname) {
    checkAvailability();
    return this.epicGuard.bypassManager().isNicknameBypassed(nickname);
  }

  /**
   * @return An unmodifiable set of all runtime-bypassed addresses.
   */
  @NotNull
  public Set<String> bypassedAddresses() {
    checkAvailability();
    return this.epicGuard.bypassManager().bypassedAddresses();
  }

  /**
   * @return An unmodifiable set of all runtime-bypassed nicknames.
   */
  @NotNull
  public Set<String> bypassedNicknames() {
    checkAvailability();
    return this.epicGuard.bypassManager().bypassedNicknames();
  }

  // ========================
  //  WHITELIST / BLACKLIST
  // ========================

  /**
   * Adds an address to the whitelist. Whitelisted addresses skip all checks.
   *
   * @param address The IP address to whitelist.
   */
  public void whitelistAddress(@NotNull String address) {
    checkAvailability();
    this.epicGuard.storageManager().addressMeta(address).whitelisted(true);
  }

  /**
   * Removes an address from the whitelist.
   *
   * @param address The IP address to un-whitelist.
   */
  public void unwhitelistAddress(@NotNull String address) {
    checkAvailability();
    this.epicGuard.storageManager().addressMeta(address).whitelisted(false);
  }

  /**
   * Checks if an address is whitelisted.
   *
   * @param address The IP address to check.
   * @return true if the address is whitelisted.
   */
  public boolean isWhitelisted(@NotNull String address) {
    checkAvailability();
    return this.epicGuard.storageManager().addressMeta(address).whitelisted();
  }

  /**
   * Adds an address to the blacklist. Blacklisted addresses are always blocked.
   *
   * @param address The IP address to blacklist.
   */
  public void blacklistAddress(@NotNull String address) {
    checkAvailability();
    this.epicGuard.storageManager().addressMeta(address).blacklisted(true);
  }

  /**
   * Removes an address from the blacklist.
   *
   * @param address The IP address to un-blacklist.
   */
  public void unblacklistAddress(@NotNull String address) {
    checkAvailability();
    this.epicGuard.storageManager().addressMeta(address).blacklisted(false);
  }

  /**
   * Checks if an address is blacklisted.
   *
   * @param address The IP address to check.
   * @return true if the address is blacklisted.
   */
  public boolean isBlacklisted(@NotNull String address) {
    checkAvailability();
    return this.epicGuard.storageManager().addressMeta(address).blacklisted();
  }

  // ========================
  //    PROXY / GEO CHECK
  // ========================

  /**
   * Checks if an IP address is a VPN/proxy using the configured proxy services.
   *
   * @param address The IP address to check.
   * @return true if the address is detected as a proxy/VPN.
   */
  public boolean isProxy(@NotNull String address) {
    checkAvailability();
    return this.epicGuard.proxyManager().isProxy(address);
  }

  /**
   * Gets the country code for an IP address.
   *
   * @param address The IP address to lookup.
   * @return The ISO country code, or null if unavailable.
   */
  @Nullable
  public String getCountry(@NotNull String address) {
    checkAvailability();
    return this.epicGuard.geoManager().countryCode(address);
  }

  /**
   * Gets the city name for an IP address.
   *
   * @param address The IP address to lookup.
   * @return The city name, or null if unavailable.
   */
  @Nullable
  public String getCity(@NotNull String address) {
    checkAvailability();
    return this.epicGuard.geoManager().city(address);
  }

  // ========================
  //    ATTACK STATUS
  // ========================

  /**
   * @return true if the server is currently under a bot attack.
   */
  public boolean isUnderAttack() {
    checkAvailability();
    return this.epicGuard.attackManager().isUnderAttack();
  }

  /**
   * @return The current connections per second.
   */
  public int connectionCounter() {
    checkAvailability();
    return this.epicGuard.attackManager().connectionCounter();
  }

  /**
   * @return The platform name and version EpicGuardPlus is currently running on.
   */
  @NotNull
  public String platformVersion() {
    return this.epicGuard.platform().platformVersion();
  }

  /**
   * @return The instance of EpicGuardPlus's core. Use at your own risk.
   */
  public EpicGuard instance() {
    return this.epicGuard;
  }

  /**
   * This method is used during initialization to set an instance. Can't be used twice.
   *
   * @param instance Instance of {@link EpicGuard} class.
   */
  protected void instance(@NotNull EpicGuard instance) {
    if (this.epicGuard == null) {
      this.epicGuard = instance;
    } else {
      throw new UnsupportedOperationException("Instance already set.");
    }
  }

  /**
   * Checks if the EpicGuard has been initialized already.
   */
  public void checkAvailability() {
    Validate.notNull(this.epicGuard, "Can't acces EpicGuardAPI because the plugin is not initialized. Have you set is as dependency?.");
  }
}
