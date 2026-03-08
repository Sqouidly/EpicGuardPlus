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

package me.xneox.epicguard.core.manager;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Manages runtime bypass lists for addresses and nicknames.
 * External plugins can use this via the EpicGuardAPI to exempt
 * specific players or IPs from all checks.
 */
public class BypassManager {
  private final Set<String> bypassedAddresses = ConcurrentHashMap.newKeySet();
  private final Set<String> bypassedNicknames = ConcurrentHashMap.newKeySet();

  /**
   * Adds an address to the runtime bypass list.
   * Players connecting from this address will skip all checks.
   *
   * @param address The IP address to bypass.
   */
  public void addBypassAddress(@NotNull String address) {
    this.bypassedAddresses.add(address);
  }

  /**
   * Removes an address from the runtime bypass list.
   *
   * @param address The IP address to remove.
   */
  public void removeBypassAddress(@NotNull String address) {
    this.bypassedAddresses.remove(address);
  }

  /**
   * Checks if the given address is in the runtime bypass list.
   *
   * @param address The IP address to check.
   * @return true if the address is bypassed.
   */
  public boolean isAddressBypassed(@NotNull String address) {
    return this.bypassedAddresses.contains(address);
  }

  /**
   * Adds a nickname to the runtime bypass list.
   * Players with this nickname will skip all checks.
   *
   * @param nickname The nickname to bypass.
   */
  public void addBypassNickname(@NotNull String nickname) {
    this.bypassedNicknames.add(nickname);
  }

  /**
   * Removes a nickname from the runtime bypass list.
   *
   * @param nickname The nickname to remove.
   */
  public void removeBypassNickname(@NotNull String nickname) {
    this.bypassedNicknames.remove(nickname);
  }

  /**
   * Checks if the given nickname is in the runtime bypass list.
   *
   * @param nickname The nickname to check.
   * @return true if the nickname is bypassed.
   */
  public boolean isNicknameBypassed(@NotNull String nickname) {
    return this.bypassedNicknames.contains(nickname);
  }

  /**
   * @return An unmodifiable view of all bypassed addresses.
   */
  @NotNull
  public Set<String> bypassedAddresses() {
    return Collections.unmodifiableSet(this.bypassedAddresses);
  }

  /**
   * @return An unmodifiable view of all bypassed nicknames.
   */
  @NotNull
  public Set<String> bypassedNicknames() {
    return Collections.unmodifiableSet(this.bypassedNicknames);
  }
}
